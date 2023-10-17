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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.jzos.MvsConsole;
import com.ibm.jzos.WtoConstants;

/**
 * The main class of the file lock server. Should run as a started task.
 */
public final class Server implements Runnable {
	public static final int DEFAULT_PORT = 3141;

	private static final Logger LOG = LoggerFactory.getLogger(Server.class);
	private final ServerSocket serverSocket;
	private boolean stopped = false;
	private int exceptionCounter = 0;
	// Useful for tests
	private boolean local = false;
	
	private CommandHandler handler = new CommandHandler();

	public Server(int port, boolean local) throws IOException {
		serverSocket = new ServerSocket(port);
		this.local = local;
	}
	
	public static void main(String... args) throws IOException {
		if (args.length > 2) {
			System.err.println("Usage: " + Server.class.getName() + " port (ignored!) <true/false>");
		} else {		
			boolean b = args.length > 1 && Boolean.valueOf(args[1]);
		
			new Thread(new Server(DEFAULT_PORT, b)).start();
		}
	}

	@Override
	public void run() {
		if (!local) {
			MvsConsole.wto("FileLock Server started", WtoConstants.ROUTCDE_PROGRAMMER_INFORMATION , WtoConstants.DESC_JOB_STATUS);
		}
		
		LOG.info("Listening on port {}", serverSocket.getLocalPort());
		
		while (!stopped && exceptionCounter < 9999) {
			try (Socket socket = serverSocket.accept()) {
				
				for (String s : read(socket.getInputStream())) {
					if (!"".equals(s)) {
						String paramString = s.replace("\r\n", "<CLRF>");
						
						LOG.info("Received {}", paramString);
						LOG.info("From {}", socket.getInetAddress().getHostName());

						run(paramString);
					}
				}
			} catch (IOException e) {
				++exceptionCounter;

				LOG.error("Errror reading from socket", e);
			//CHECKSTYLE DISABLE IllegalCatch for 1 lines
			} catch (Exception e) {
				++exceptionCounter;
				
				LOG.error("Errror occurred", e);
			}
		}
		
		LOG.info("Server stopped");
	}

	private void run(String paramString) throws IOException {
		// See CommandHandler for the format of the command string
		String[] parms = paramString.split(";");
			
		switch (parms[0]) {
		case "STATUS":
			break;
		case "STOP":
			stopped = true;
			break;
		default:
			handler.run(Command.valueOf(parms[0]), Arrays.copyOfRange(parms, 1, parms.length));
		}
	}

	private List<String> read(InputStream is) throws IOException {
		List<String> result = new ArrayList<>(); 
		
		try(BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
			for (String s = ""; s != null; s = br.readLine()) {
				result.add(s.trim());
			}
		}
		
		return result;
	}

	public void setLocal(boolean local) {
		this.local = local;
	}
}
