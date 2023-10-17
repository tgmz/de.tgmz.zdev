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

import de.tgmz.zdev.xinfo.generated.FILE;
import de.tgmz.zdev.xinfo.generated.PACKAGE;

/**
 * Some utility functions for dealing with {@link PACKAGE}.
 *
 */
public final class PlicompUtil {
	private PlicompUtil() {
	}

	public static String getFileNameFromFileNumber(String i, PACKAGE p) {
		if (i == null) {
			return null;
		}
		
		for (FILE f : p.getFILEREFERENCETABLE().getFILE()) {
			if (i.equals(f.getFILENUMBER())) {
				return f.getFILENAME();
			}
		}
		
		return null;
	}
}
