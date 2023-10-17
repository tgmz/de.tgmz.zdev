/*********************************************************************
* Copyright (c) 10.10.2023 Thomas Zierer
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package de.tgmz.zdev.crypto;

/**
 * Generic crypto exception.
 */
public class ZdevCryptoException extends RuntimeException {
	private static final long serialVersionUID = 6052142121658423134L;
	
	public ZdevCryptoException(String message, Throwable cause) {
		super(message, cause);
	}
}
