/*********************************************************************
* Copyright (c) 10.10.2023 Thomas Zierer
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package de.tgmz.zdev.plicomp;

/**
 * Exception on parsing.
 */
public class PlicompException extends Exception {
	private static final long serialVersionUID = 8242050194743145608L;

	public PlicompException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}
}
