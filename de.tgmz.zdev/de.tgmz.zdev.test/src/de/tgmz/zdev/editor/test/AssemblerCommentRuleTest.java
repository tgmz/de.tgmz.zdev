/*********************************************************************
* Copyright (c) 06.10.2023 Thomas Zierer
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package de.tgmz.zdev.editor.test;

import static org.junit.Assert.assertEquals;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;
import org.junit.Test;

import de.tgmz.zdev.editor.assembler.AssemblerCommentRule;


public class AssemblerCommentRuleTest {
	@Test
	public void testComment() {
		AssemblerCommentRule acr = new AssemblerCommentRule(new Token("AssemblerComment"));
		
		IToken evaluate = acr.evaluate(new ICharacterScanner() {
			private boolean firstCall = true;
			@Override
			public void unread() {
		    	// Must implement
			}
			
			@Override
			public int read() {
				if (firstCall) {
					firstCall = false;
					return '*';
				}
				
				return '\r';
			}
			
			@Override
			public char[][] getLegalLineDelimiters() {
				return new char[0][0];
			}
			
			@Override
			public int getColumn() {
				return 1;
			}
		});
		
		assertEquals("AssemblerComment", evaluate.getData());
	}
}
