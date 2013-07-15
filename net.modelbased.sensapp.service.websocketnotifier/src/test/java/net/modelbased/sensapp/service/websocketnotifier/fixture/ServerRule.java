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
package net.modelbased.sensapp.service.websocketnotifier.fixture;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.webapp.WebAppContext;
import org.junit.rules.ExternalResource;

public class ServerRule extends ExternalResource {
	private int port;

	private final String warPath;

	private final String contextPath;

	private Server server;

	public ServerRule() {
		this(8080, "/");
	}
	
	public ServerRule(int port) {
		this(port, "/");
	}

	public ServerRule(int port, String contextPath) {
		this(port, "src/main/webapp", contextPath);
	}

	public ServerRule(int port, String warPath, String contextPath) {
		this.port = port;
		this.warPath = warPath;
		this.contextPath = contextPath;
	}

	public void start() throws Exception {
		server = new Server();

		WebAppContext wac = new WebAppContext();
		wac.setContextPath(contextPath);
		wac.setWar(warPath);
		server.setHandler(wac);

		SelectChannelConnector scc = new SelectChannelConnector();
		scc.setPort(port);
		server.addConnector(scc);
		server.start();
	}

	public static void main(String[] args) throws Exception {
		ServerRule webStart = new ServerRule();

		webStart.start();

		System.err.println("Running. Hit <Enter> to Stop");

		new BufferedReader(new InputStreamReader(System.in)).readLine();

		webStart.stop();

	}

	public void stop() throws Exception {
		//server.join();
		server.stop();
	}

	@Override
	protected void before() throws Throwable {
		start();
	}

	@Override
	protected void after() {
		try {
			stop();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}