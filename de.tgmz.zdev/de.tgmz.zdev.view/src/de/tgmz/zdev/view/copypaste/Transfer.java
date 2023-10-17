/*********************************************************************
* Copyright (c) 10.10.2023 Thomas Zierer
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package de.tgmz.zdev.view.copypaste;

import java.util.Arrays;

/**
 * A transfer object.
 */
public class Transfer {
	private String name;
	private byte[] content;
	public Transfer(String name, byte[] content) {
		super();
		this.name = name;

		this.content = content == null ? new byte[0] : Arrays.copyOf(content, content.length);
	}
	public final String getName() {
		return name;
	}
	
	public final byte[] getContent() {
		return content == null ? new byte[0] : Arrays.copyOf(content, content.length);
	}
}
