/*********************************************************************
* Copyright (c) 10.10.2023 Thomas Zierer
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/

package de.tgmz.zdev.compile;

public class SyslibFactory {
	private static final SyslibFactory INSTANCE = new SyslibFactory();

	private SyslibFactory() {
	}
	
	public static SyslibFactory getInstance() {
		return INSTANCE;
	}
	
	public String createSyslib(String s) {
		String[] split = s.split(";");
		
		StringBuilder result = new StringBuilder(split[0]);
		
		for (int i = 1; i < split.length; i++) {
			result.append(System.lineSeparator());
			result.append("//        DD DISP=SHR,DSN=");  
			result.append(split[i]);  
		}
		
		return result.toString();
	}
}
