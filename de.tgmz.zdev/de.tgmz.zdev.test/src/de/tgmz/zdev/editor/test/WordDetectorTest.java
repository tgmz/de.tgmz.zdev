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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.eclipse.jface.text.rules.IWordDetector;
import org.junit.Test;

import de.tgmz.zdev.editor.assembler.AssemblerWordDetector;
import de.tgmz.zdev.editor.cobol.COBOLWordDetector;
import de.tgmz.zdev.editor.pli.PLIWordDetector;
import de.tgmz.zdev.editor.rexx.RexxWordDetector;
import de.tgmz.zdev.editor.sql.SqlWordDetector;


public class WordDetectorTest {
	@Test
	public void testPli() {
		IWordDetector pwd = new PLIWordDetector();
		assertTrue(pwd.isWordPart('_'));
		assertTrue(pwd.isWordStart('§'));
		assertFalse(pwd.isWordPart('-'));
		assertFalse(pwd.isWordStart('1'));
	}
	@Test
	public void testCobol() {
		IWordDetector cwd = new COBOLWordDetector();
		assertTrue(cwd.isWordPart('-'));
		assertTrue(cwd.isWordStart('§'));
		assertFalse(cwd.isWordPart('_'));
		assertFalse(cwd.isWordStart('1'));
	}
	@Test
	public void testAssembler() {
		IWordDetector awd = new AssemblerWordDetector();
		assertTrue(awd.isWordPart('#'));
		assertTrue(awd.isWordStart('§'));
		assertFalse(awd.isWordPart('-'));
		assertFalse(awd.isWordStart('1'));
	}
	@Test
	public void testSql() {
		IWordDetector swd = new SqlWordDetector();
		assertTrue(swd.isWordPart('-'));
		assertTrue(swd.isWordStart('#'));
		assertFalse(swd.isWordPart('&'));
		assertFalse(swd.isWordStart('1'));
	}
	@Test
	public void testRexx() {
		IWordDetector cwd = new RexxWordDetector();
		assertTrue(cwd.isWordPart('$'));
		assertTrue(cwd.isWordStart('§'));
		assertFalse(cwd.isWordPart('-'));
		assertFalse(cwd.isWordStart('1'));
	}
}
