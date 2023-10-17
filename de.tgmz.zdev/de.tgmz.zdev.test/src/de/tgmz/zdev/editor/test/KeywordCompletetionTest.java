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

import org.junit.Test;

public class KeywordCompletetionTest {
	@Test
	public void testPli() {
		assertEquals(de.tgmz.zdev.editor.pli.ReservedWord.values().length, de.tgmz.zdev.editor.pli.ReservedWord.getCompletionProposals().length);
	}
	@Test
	public void testCobol() {
		assertEquals(de.tgmz.zdev.editor.cobol.ReservedWords.values().length, de.tgmz.zdev.editor.cobol.ReservedWords.getCompletionProposals().length);
	}
	@Test
	public void testSql() {
		assertEquals(de.tgmz.zdev.editor.sql.ReservedWords.values().length, de.tgmz.zdev.editor.sql.ReservedWords.getCompletionProposals().length);
	}
	@Test
	public void testRexx() {
		assertEquals(de.tgmz.zdev.editor.rexx.ReservedWords.values().length, de.tgmz.zdev.editor.rexx.ReservedWords.getCompletionProposals().length);
	}
}
