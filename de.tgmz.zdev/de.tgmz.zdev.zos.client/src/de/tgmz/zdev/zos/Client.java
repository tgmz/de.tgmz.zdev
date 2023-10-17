/*********************************************************************
* Copyright (c) 10.10.2023 Thomas Zierer
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package de.tgmz.zdev.zos;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import javax.net.SocketFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.cics.zos.model.IZOSConnectable;

import de.tgmz.zdev.connection.ZdevConnectable;
import de.tgmz.zdev.zos.Server;

/**
 * Submits the command to the file lock server through a socket connection. 
 */
public final class Client {
	private static final Logger LOG = LoggerFactory.getLogger(Client.class);
	private static final Client INSTANCE = new Client();
	
	private Client() {
	}
	
	public void enqueue (String parentPath, String memberName) throws IOException, NotConnectedException {
		write("ENQ;" + parentPath + ";" + memberName);
	}
	
	public void dequeue (String parentPath, String memberName) throws IOException, NotConnectedException {
		write("DEQ;" + parentPath + ";" + memberName);
	}
	
	public void stop () throws IOException, NotConnectedException {
		write("STOP");
	}
	
	public static boolean available() throws NotConnectedException {
		LOG.info("Request status");

		try {
			write("STATUS");
		} catch (IOException e) {
			LOG.error("Request status failed", e);
			return false;
		}

		return true;
	}

	private static void write(String s) throws IOException, NotConnectedException {
		IZOSConnectable conn = ZdevConnectable.getConnectable();

		if (!conn.isConnected()) {
			throw new NotConnectedException();
		}

		String host = conn.getConnection().getConfiguration().getHost();

		try (Socket socket = SocketFactory.getDefault().createSocket(host, Server.DEFAULT_PORT)) {
			LOG.debug("Write {} to {}:{}", s, host, Server.DEFAULT_PORT);

			try (PrintStream ps = new PrintStream(socket.getOutputStream(), true, StandardCharsets.UTF_8.name())) {
				ps.println(s);
			}
		}
	}

	public static Client getInstance() {
		return INSTANCE;
	}
}
