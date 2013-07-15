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
package net.modelbased.sensapp.service.websocketdatabase.raw.resource;

import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;
import org.codehaus.jackson.node.TextNode;

/**
 * Root Resource Class. Represents a single entry-point for the whole REST
 * Application, in order to ease on maintenance
 */
@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class RootResource extends BaseResource {
	@Path("/debug")
	@GET
	public ObjectNode getAllHeaders(@Context HttpHeaders httpHeaders) {
		ObjectNode result = objectMapper.createObjectNode();

		Set<Entry<String, List<String>>> entrySet = httpHeaders
				.getRequestHeaders().entrySet();

		for (Entry<String, List<String>> entry : entrySet) {
			String key = entry.getKey();
			JsonNode value = null;

			if (1 == entry.getValue().size()) {
				value = new TextNode(entry.getValue().get(0));
			} else {
				ArrayNode arrayNode = objectMapper.createArrayNode();

				for (String v : entry.getValue())
					arrayNode.add(v);

				value = arrayNode;
			}

			result.put(key, value);
		}

		return result;
	}
	
	@Path("/remote")
	@GET
	public String getRemoteAddress(@Context HttpServletRequest request) {
		return request.getRemoteHost();
	}
	
	@Path("/health")
	public HealthResource getHealthResource() throws Exception {
		return super.createResource(HealthResource.class);
	}

}
