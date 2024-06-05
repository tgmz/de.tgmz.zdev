/*********************************************************************
* Copyright (c) 10.10.2023 Thomas Zierer
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package de.tgmz.zdev.editor.pli;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.NumberRule;
import org.eclipse.jface.text.rules.Token;

/**
 * Scans for numbers in PL/I source.
 */
public class PliNumberRule extends NumberRule {
	public PliNumberRule(IToken token) {
		super(token);
	}

	@Override
	public IToken evaluate(ICharacterScanner scanner) {
		int c = scanner.read();
		if (isPliNumeric((char) c)
			&& (fColumn == UNDEFINED || (fColumn == scanner.getColumn() - 1))) {
			do {
				c = scanner.read();
			} while (isPliNumeric((char) c));
			scanner.unread();
			return fToken;
		}

		scanner.unread();
		return Token.UNDEFINED;
	}
	/**
	 * Accept "_" in numbers.
	 */
	private static boolean isPliNumeric(char c) {
		return c == '_' || Character.isDigit(c);
	}
}
