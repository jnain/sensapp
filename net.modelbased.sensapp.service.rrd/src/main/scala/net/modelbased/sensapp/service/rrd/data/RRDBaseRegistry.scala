/**
 * ====
 *     This file is part of SensApp [ http://sensapp.modelbased.net ]
 *
 *     Copyright (C) 2011-  SINTEF ICT
 *     Contact: SINTEF ICT <nicolas.ferry@sintef.no>
 *
 *     Module: net.modelbased.sensapp
 *
 *     SensApp is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as
 *     published by the Free Software Foundation, either version 3 of
 *     the License, or (at your option) any later version.
 *
 *     SensApp is distributed in the hope that it will be useful, but
 *     WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *     GNU Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General
 *     Public License along with SensApp. If not, see
 *     <http://www.gnu.org/licenses/>.
 * ====
 *
 * This file is part of SensApp [ http://sensapp.modelbased.net ]
 *
 * Copyright (C) 2011-  SINTEF ICT
 * Contact: SINTEF ICT <nicolas.ferry@sintef.no>
 *
 * Module: net.modelbased.sensapp
 *
 * SensApp is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 *
 * SensApp is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General
 * Public License along with SensApp. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package net.modelbased.sensapp.service.rrd.data

/**
 * This file is part of SensApp [ http://sensapp.modelbased.net ]
 *
 * Copyright (C) 2012-  SINTEF ICT
 * Contact: Sebastien Mosser <sebastien.mosser@sintef.no>
 *
 * Module: net.modelbased.sensapp.service.rrd
 *
 * SensApp is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 *
 * SensApp is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General
 * Public License along with SensApp. If not, see
 * <http://www.gnu.org/licenses/>.
 */
import cc.spray.json._
import net.modelbased.sensapp.library.datastore._
import java.util.jar.{JarEntry, JarFile}
import java.io._
import java.lang.StringBuilder
import java.text.SimpleDateFormat
import org.rrd4j.core.{Util, RrdDefTemplate, RrdMongoDBBackendFactory, RrdDb}
import net.modelbased.sensapp.library.senml.MeasurementOrParameter
import org.rrd4j.graph._
import java.awt.{Color, Cursor}
import java.awt.image.BufferedImage

//import org.specs2.internal.scalaz.Validation
import java.net.{URLConnection, URLDecoder, URL}
import org.xml.sax.XMLReader
import com.mongodb._
import org.parboiled.support.Var

import com.mongodb.casbah.Imports._
import java.util.ArrayList

import scala.collection.JavaConversions._

/**
 * Persistence layer associated to the RRDBase class
 * 
 * @author Sebastien Mosser
 */
class RRDBaseRegistry {

  val rrd4jDatabaseName = "sensapp_db"
  val rrd4jCollectionName = "rrd.databases"

  private  lazy val rrd4jcollection = {
    val conn = MongoConnection()
    val db = conn.getDB(rrd4jDatabaseName)
    val col = db.getCollection(rrd4jCollectionName)
    col
  }

  private  lazy val rrd4jfactory = new RrdMongoDBBackendFactory(rrd4jcollection);


  def listRRD4JBases() : ArrayList[String] = {
    // Had to query the DB . No method in the RRD4J APIs.
    val result = new ArrayList[String]()
    val q  = MongoDBObject.empty
    val fields = MongoDBObject("path" -> 1)
    val res = rrd4jcollection.find(q, fields)

    res.toArray.foreach{o =>
        result.add(o.get("path").toString)
    }

    return result
  }

  def deleteRRD4JBase(path : String) = {
    // Had to query the DB . No method in the RRD4J APIs.
    val query = MongoDBObject("path" -> path)
    val rrdObject = rrd4jcollection.findOne(query);
    if (rrdObject != null) {
        rrd4jcollection.remove(rrdObject)
    }
  }

  def createRRD4JBase(path : String, template_url : String) = {
      // TODO: catch the numerous exceptions which could be raised here
      val xml = IOUtils.sendGetRequest(template_url, null);
      if (xml != null) {
        val template = new RrdDefTemplate(xml)
        template.setVariable("PATH", path);
        val rrddef = template.getRrdDef
        rrddef.setPath(path)
        val db = new RrdDb(rrddef, rrd4jfactory)
        db.close
      }
  }

  def importRRD4JBase(path : String, data_url : String) = {
      // TODO: catch the numerous exceptions which could be raised here
      val xmlfile = IOUtils.downloadTmpFile(data_url, null)
      if (xmlfile != null) {
        val db = new RrdDb(path, xmlfile.getAbsolutePath, rrd4jfactory)
        db.close
        xmlfile.delete
      }
  }

  def getRRD4JBase(path : String, ro:Boolean) : RrdDb = {
    val result = new RrdDb(path, ro, rrd4jfactory)
    return result
  }

  def getgetRRD4JBaseDescription(db : RrdDb) : String = {
    val result = new StringBuilder();
    val dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    result.append("Sensapp RRD Database " + db.getPath + " (step = " +db.getRrdDef.getStep+ ")\n")
    result.append("Database Estimated Size: " + db.getRrdDef.getEstimatedSize + "\n")
    result.append("Number of Data Source: " + db.getDsCount + " [ ")
    db.getDsNames.foreach{ n => result.append(n + " ") }
    result.append("]\n")
    result.append("Number of Archives: " + db.getArcCount + "\n")
    result.append("Latest Update: " + dateFormat.format(Util.getCalendar(db.getLastArchiveUpdateTime).getTime) + "\n")
    result.append("Latest Values: [ ")
    db.getLastDatasourceValues.foreach{ v => result.append(v + " ") }
    result.append("]\n")
    result.toString
  }

  def createDefaultGraph(path : String, source: String, start : String, end : String, resolution : String, function : String) : BufferedImage = {
    val db = getRRD4JBase(path, true)
    val query = RRDRequest(function, start, end, resolution)
    val fr = db.createFetchRequest(query.getFunction, query.getStart, query.getEnd, query.getResolution)
    val gd = new RrdGraphDef()
    gd.comment("Generated by Sensapp")
    gd.setWidth(600);
    gd.setHeight(340);
    gd.setTimeSpan(query.getStart, query.getEnd);
    //gd.setTextAntiAliasing(true)
    //gd.setAltAutoscale(true)
    //gd.setFilename("-")  // in memory
    gd.datasource(source, fr.fetchData())
    gd.line(source, Color.red)
    val g = new RrdGraph(gd)
    val img = new BufferedImage(g.getRrdGraphInfo.getWidth,g.getRrdGraphInfo.getHeight,BufferedImage.TYPE_INT_RGB)
    g.render(img.getGraphics)
    img
  }

}
