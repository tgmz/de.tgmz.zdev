/*********************************************************************
* Copyright (c) 10.10.2023 Thomas Zierer
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package de.tgmz.zdev.restore.compare;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * CompareItem based on a history version.
 */
public class HistoryCompareItem extends ZdevCompareItem {
	public HistoryCompareItem(String name, byte[] contents) {
		super(name);
		
		setContent(contents);
	}

	@Override
	public InputStream getContents() {
		return new ByteArrayInputStream(contents);
	}
}
