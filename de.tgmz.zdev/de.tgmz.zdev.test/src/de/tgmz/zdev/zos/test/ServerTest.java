/*********************************************************************
* Copyright (c) 10.10.2023 Thomas Zierer
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package de.tgmz.zdev.zos.test;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import de.tgmz.zdev.zos.Server;

public class ServerTest {
	private static final Charset UTF_8 = StandardCharsets.UTF_8;
	@BeforeClass
	public static void setupOnce() throws IOException {
		new Thread(new Server(Server.DEFAULT_PORT, true)).start();
	}
	
	@Test
	public void query() throws IOException {
		write("STATUS");
	}
	
	@Test
	public void dequeue() throws IOException {
		write("DEQ;HLQ.ZDEV.PLI;MI013");
	}

	@Test
	public void enqueue() throws IOException {
		write("ENQ;HLQ.ZDEV.PLI;MI013");
	}

	@AfterClass
	public static void teardownOnce() throws IOException {
		write("STOP");
	}
	
	private static void write(String s) throws IOException {
		try (Socket socket = new Socket("localhost", Server.DEFAULT_PORT);
			OutputStream raus = socket.getOutputStream();
			PrintStream ps = new PrintStream(raus, true, UTF_8.name())) {
			ps.println(s);
		}
	}
}
