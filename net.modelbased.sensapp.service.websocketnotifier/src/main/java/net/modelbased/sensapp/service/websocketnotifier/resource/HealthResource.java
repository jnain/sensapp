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
package net.modelbased.sensapp.service.websocketnotifier.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.HEAD;
import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;

import org.apache.commons.lang.time.StopWatch;

import com.amazonaws.services.dynamodb.AmazonDynamoDB;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.s3.AmazonS3;
import com.google.inject.Inject;

public class HealthResource extends BaseResource {
	@Inject
	AmazonEC2 ec2;

	@Inject
	AmazonS3 s3;

	@Inject
	AmazonDynamoDB dynamoDb;
	
	@HEAD
	@Produces("text/plain")
	@Path("/check")
	public String doHeadOnHealthCheck() {
		return doHealthCheck();
	}
	

	@GET
	@Produces("text/plain")
	@Path("/check")
	public String doHealthCheck() {
		StopWatch stopWatch = new StopWatch();
		
		stopWatch.start();
		
		try {
			ec2.describeInstances();

			s3.listBuckets();

			dynamoDb.listTables();
		} catch (Exception exc) {
			if (logger.isWarnEnabled())
				logger.warn("doHealthCheck() failed", exc);
			
			throw new WebApplicationException(exc, 500);
		}
		
		stopWatch.stop();
		
		long len = stopWatch.getTime() / 1000;
		
		if (logger.isInfoEnabled())
			logger.info("doHealthCheck(): Took {}s", new Object[] { len } );

		return "OK: " + len;
	}

}
