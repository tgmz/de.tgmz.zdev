/*********************************************************************
* Copyright (c) 06.10.2023 Thomas Zierer
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package de.tgmz.zdev.history;

/**
 * History Exception
 */
public class HistoryException extends Exception {
	private static final long serialVersionUID = -5079235166716364998L;

	public HistoryException(String message, Throwable cause) {
		super(message, cause);
	}

	public HistoryException(Throwable cause) {
		super(cause);
	}
}
