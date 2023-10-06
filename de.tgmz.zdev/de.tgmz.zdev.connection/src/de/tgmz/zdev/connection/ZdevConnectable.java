/*********************************************************************
* Copyright (c) 06.10.2023 Thomas Zierer
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package de.tgmz.zdev.connection;

import com.ibm.cics.core.connections.ConnectionsPlugin;
import com.ibm.cics.zos.model.IZOSConnectable;

/**
 * Wrapper for ConnectionsPlugin.getDefault().getConnectionService().
 */
public class ZdevConnectable {
	private static IZOSConnectable connectable = 
			(IZOSConnectable) ConnectionsPlugin.getDefault().getConnectionService().getConnectable("com.ibm.cics.zos.comm.connection");

	private ZdevConnectable() {
	}
	
	public static synchronized IZOSConnectable getConnectable() {
		return connectable;
	}

	public static synchronized void setConnectable(IZOSConnectable connectable) {
		ZdevConnectable.connectable = connectable;
	}
}
