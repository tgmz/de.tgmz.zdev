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

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import de.tgmz.zdev.editor.pli.PLIBuiltin;

public class KeywordTest {
	@Test
	public void testPliBif() {
		assertTrue(PLIBuiltin.getCompletionProposals().length > 0);
	}
	@Test
	public void testPliReservedWord() {
		assertTrue(de.tgmz.zdev.editor.pli.ReservedWord.getCompletionProposals().length > 0);
	}
	@Test
	public void testCobolReservedWord() {
		assertTrue(de.tgmz.zdev.editor.cobol.ReservedWords.getCompletionProposals().length > 0);
	}
	@Test
	public void testSqlReservedWord() {
		assertTrue(de.tgmz.zdev.editor.sql.ReservedWords.getCompletionProposals().length > 0);
	}
	@Test
	public void testRexxReservedWord() {
		assertTrue(de.tgmz.zdev.editor.rexx.ReservedWords.getCompletionProposals().length > 0);
	}
}
