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
 * Copyright (C) 2012-  SINTEF ICT
 * Contact: SINTEF ICT <nicolas.ferry@sintef.no>
 *
 * Module: net.modelbased.sensapp.system.sample
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
package net.modelbased.sensapp.service.sample.specs

// tests
import org.specs2.mutable._
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
// http support 
import cc.spray.client._
import cc.spray.http._
import cc.spray.http.HttpMethods._
import cc.spray.json.DefaultJsonProtocol.listFormat
import cc.spray.typeconversion.SprayJsonSupport
import net.modelbased.sensapp.library.system.HttpSpraySupport
import akka.dispatch.Await
import akka.util.duration.intToDurationInt
// Service-specific
import net.modelbased.sensapp.service.sample.data.{Element}
import net.modelbased.sensapp.service.sample.data.ElementJsonProtocol.format


@RunWith(classOf[JUnitRunner])
class ServiceSpecIT extends SpecificationWithJUnit with SprayJsonSupport with HttpSpraySupport {
  
  override def httpClientName = "raw-database-helper"
    
  // Solve an implicit conflict between Akka 2.0 and Specs2
  override def intToRichLong(v: Int) = new RichLong(v.toLong)
  override def longToRichLong(v: Long) = new RichLong(v)
    
    "Service Specification Unit (Integration)".title
    
  //step(load) // Load the HttpSpraySupport in the context of this test suite
  
  "GET(/sample/elements)" should {
    val conduit = new HttpConduit(httpClient, "localhost",8090) {
      val pipeline = { simpleRequest ~> sendReceive ~> unmarshal[List[Element]] }
    }
    "Retrieve an empty list when no elements are stored" in new EmptyRegistry {
      val future = conduit.pipeline(Get("/sample/elements"))
      val response = Await.result(future, (5 seconds) )
      response must_== List()
    }   
  }
 
  //step(unload) // Unload the HttpSpraySupport
}

trait EmptyRegistry extends Before {
  def before { (new net.modelbased.sensapp.service.sample.data.ElementRegistry) dropAll() }
}
