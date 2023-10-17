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

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;

/**
 * Rule to identify assembler comments.
 */

public class AssemblerCommentRule implements IRule {
	/** The token to be returned when this rule is successful */
	private final IToken fToken;

	public AssemblerCommentRule(IToken fToken) {
		super();
		this.fToken = fToken;
	}

	@Override
	public IToken evaluate(ICharacterScanner scanner) {
		int c = (char) scanner.read();
		
		if ((c == '*' && scanner.getColumn() == 1)	// ASSEMBLER default comment
		 || (scanner.getColumn() >= 40)) {			// A blank after column 40 indicates the rest of the  
													// line is a comment
			do {
				c = scanner.read();
			} while (c != '\r' && c != '\n' && c != -1);	// -1 or LOOP otherwise!
			
			scanner.unread();
			return fToken;
		}

		scanner.unread();
		return Token.UNDEFINED;
	}

}

