/*********************************************************************
* Copyright (c) 10.10.2023 Thomas Zierer
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package de.tgmz.zdev.editor.cpp;

import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.text.rules.IWordDetector;

/**
 * Identifies words in C.
 */
public class CppWordDetector implements IWordDetector {
	/** Special characters */
	private static final List<Character> SC;

	static {
		SC = Arrays.asList('$','§', '#', '_');
	}
	
	@Override
	public boolean isWordStart(char c) {
		return Character.isLetter(c) || SC.contains(c);
	}

	@Override
	public boolean isWordPart(char c) {
		return isWordStart(c) || Character.isDigit(c);
	}
}

