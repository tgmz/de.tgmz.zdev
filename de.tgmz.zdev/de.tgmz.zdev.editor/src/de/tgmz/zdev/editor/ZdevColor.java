/*********************************************************************
* Copyright (c) 10.10.2023 Thomas Zierer
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package de.tgmz.zdev.editor;

import org.eclipse.swt.graphics.RGB;

/**
 * Colors for syntax highlighting.
 */
public enum ZdevColor {
	BUILTIN(0, 0, 255),
	COMMENT(0, 112, 112),
	KEYWORD(0, 0, 255),
	DEFAULT(0, 0, 0),
	LITERAL(128, 0, 128),
	NUMERIC(128, 0, 0),
	PREPROC(0, 0, 128),
//	SQL(112, 112, 0),
//	MULTILINE_STRING(128, 0, 0),
//	SINGLELINE_STRING(128, 128, 128),
//	BACKQUOTE(128, 0, 128),
//	XML_COMMENT(128, 0, 0),
//	PROC_INSTR(128, 128, 128),
//	STRING(0, 128, 0),
//	TAG(0, 0, 128),
	;
	
	private RGB rgb;

	private ZdevColor(int red, int green, int blue) {
		rgb = new RGB(red, green, blue);
	}
	
	public RGB getRgb() {
		return rgb;
	}
}
