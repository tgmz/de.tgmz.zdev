/*********************************************************************
* Copyright (c) 10.10.2023 Thomas Zierer
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package de.tgmz.zdev.editor.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;
import org.junit.Test;

import de.tgmz.zdev.editor.pli.PliNumberRule;


public class PliNumberRuleTest {
	private static final String PLI_NUMBER_TOKEN = "PliNumber";
	private static final PliNumberRule pnr = new PliNumberRule(new Token(PLI_NUMBER_TOKEN));
	
	private static class MockScanner implements ICharacterScanner {
		private String content;
		private int pos = 0;
		private int i = 0;

		public MockScanner(String content, int pos) {
			super();
			this.pos = pos;
			this.content = content;
		}
		@Override
		public void unread() {
			//Must implement, even if empty
		}
		@Override
		public char[][] getLegalLineDelimiters() {
			return new char[0][0];
		}
		@Override
		public int getColumn() {
			return pos + i;
		}
		@Override
		public int read() {
			if (i < content.length()) {
				return content.charAt(i++);
			}
			
			return '\r';
		}
	}
	
	@Test
	public void testNumber() {
		IToken evaluate = pnr.evaluate(new MockScanner("5", 10));
		assertEquals(PLI_NUMBER_TOKEN, evaluate.getData());
	}
	
	@Test
	public void testNumberWithUnderscore() {
		IToken evaluate = pnr.evaluate(new MockScanner("32_000", 5));
		assertEquals(PLI_NUMBER_TOKEN, evaluate.getData());
	}
	
	@Test
	public void testNoNumber() {
		IToken evaluate = pnr.evaluate(new MockScanner("x", 2));
		assertTrue(evaluate.isUndefined());
	}
	
	@Test
	public void testWrongColumn() {
		IToken evaluate = pnr.evaluate(new MockScanner("x", -1));
		assertTrue(evaluate.isUndefined());
	}
}
