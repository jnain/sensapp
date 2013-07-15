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
 * Module: net.modelbased.sensapp.service.notifier
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
package net.modelbased.sensapp.service.notifier

import akka.dispatch.Await
import akka.util.duration._
import cc.spray.client._
import cc.spray.json._
import cc.spray.typeconversion.SprayJsonSupport
import cc.spray.typeconversion.DefaultUnmarshallers._
import net.modelbased.sensapp.library.system._
import net.modelbased.sensapp.library.senml._
import net.modelbased.sensapp.library.senml.export.JsonProtocol._ 
import data.SubscriptionRegistry
import net.modelbased.sensapp.service.notifier.protocols.ProtocolFactory

object Helper {
  
  /**
   * Assumption: Root is canonized.
   */
  def doNotify(root: Root, sensor: String, reg: SubscriptionRegistry) {
    val subscription = reg pull(("sensor", sensor))

    val protocol = ProtocolFactory.createProtocol(subscription.get.protocol.toString)
    protocol.send(subscription, root, sensor)
  }
}