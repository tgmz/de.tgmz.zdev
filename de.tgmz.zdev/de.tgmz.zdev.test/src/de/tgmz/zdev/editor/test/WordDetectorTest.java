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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.eclipse.jface.text.rules.IWordDetector;
import org.junit.Test;

import de.tgmz.zdev.editor.assembler.AssemblerWordDetector;
import de.tgmz.zdev.editor.cobol.COBOLWordDetector;
import de.tgmz.zdev.editor.cpp.CppWordDetector;
import de.tgmz.zdev.editor.pli.PLIWordDetector;
import de.tgmz.zdev.editor.rexx.RexxWordDetector;
import de.tgmz.zdev.editor.sql.SqlWordDetector;


public class WordDetectorTest {
	@Test
	public void testPli() {
		IWordDetector wd = new PLIWordDetector();
		assertTrue(wd.isWordPart('_'));
		assertTrue(wd.isWordPart('a'));
		assertTrue(wd.isWordPart('1'));
		assertTrue(wd.isWordStart('§'));
		assertFalse(wd.isWordPart('-'));
		assertFalse(wd.isWordStart('1'));
	}
	@Test
	public void testCobol() {
		IWordDetector wd = new COBOLWordDetector();
		assertTrue(wd.isWordPart('-'));
		assertTrue(wd.isWordStart('§'));
		assertTrue(wd.isWordPart('a'));
		assertTrue(wd.isWordPart('1'));
		assertFalse(wd.isWordPart('_'));
		assertFalse(wd.isWordStart('1'));
	}
	@Test
	public void testAssembler() {
		IWordDetector wd = new AssemblerWordDetector();
		assertTrue(wd.isWordPart('#'));
		assertTrue(wd.isWordStart('§'));
		assertTrue(wd.isWordPart('a'));
		assertTrue(wd.isWordPart('1'));
		assertFalse(wd.isWordPart('-'));
		assertFalse(wd.isWordStart('1'));
	}
	@Test
	public void testSql() {
		IWordDetector wd = new SqlWordDetector();
		assertTrue(wd.isWordPart('-'));
		assertTrue(wd.isWordStart('#'));
		assertTrue(wd.isWordPart('a'));
		assertTrue(wd.isWordPart('1'));
		assertFalse(wd.isWordPart('&'));
		assertFalse(wd.isWordStart('1'));
	}
	@Test
	public void testRexx() {
		IWordDetector wd = new RexxWordDetector();
		assertTrue(wd.isWordPart('$'));
		assertTrue(wd.isWordStart('§'));
		assertTrue(wd.isWordPart('a'));
		assertTrue(wd.isWordPart('1'));
		assertFalse(wd.isWordPart('-'));
		assertFalse(wd.isWordStart('1'));
	}
	@Test
	public void testC() {
		IWordDetector wd = new CppWordDetector();
		assertTrue(wd.isWordPart('$'));
		assertTrue(wd.isWordStart('§'));
		assertTrue(wd.isWordPart('a'));
		assertFalse(wd.isWordPart('-'));
		assertFalse(wd.isWordStart('1'));
	}
}
