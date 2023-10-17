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

import org.junit.Test;

import de.tgmz.zdev.zos.Client;
import de.tgmz.zdev.zos.NotConnectedException;

/**
 * Dummy-Test to call the client at least one time.
 */
public class ClientTest {
	
	@Test(expected=NotConnectedException.class)
	public void available() throws NotConnectedException {
		Client.available();
	}
	
	@Test(expected=NotConnectedException.class)
	public void dequeue() throws NotConnectedException, IOException {
		Client.getInstance().dequeue("", "");
	}
	
	@Test(expected=NotConnectedException.class)
	public void enqueue() throws NotConnectedException, IOException {
		Client.getInstance().enqueue("", "");
	}
	
	@Test(expected=NotConnectedException.class)
	public void stop() throws NotConnectedException, IOException {
		Client.getInstance().stop();
	}
}
