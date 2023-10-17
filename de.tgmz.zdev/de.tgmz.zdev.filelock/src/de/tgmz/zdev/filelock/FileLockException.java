/*********************************************************************
* Copyright (c) 10.10.2023 Thomas Zierer
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package de.tgmz.zdev.filelock;

/**
 * Exception for FileLockClient
 */
public class FileLockException extends Exception {
	private static final long serialVersionUID = 174333460931186110L;
	public FileLockException() {
		super();
	}
	public FileLockException(Throwable e) {
		super(e);
	}
}
