/*********************************************************************
* Copyright (c) 10.10.2023 Thomas Zierer
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package de.tgmz.zdev.editor.assembler;

import org.eclipse.jface.text.rules.IWordDetector;

/**
 * Words according to assembler standards.
 */
public class AssemblerWordDetector implements IWordDetector {

	@Override
	public boolean isWordStart(char c) {
		return Character.isLetter(c) || c == '$' || c == '§' || c == '#';
	}

	@Override
	public boolean isWordPart(char c) {
		return isWordStart(c) || Character.isDigit(c);
	}
}

