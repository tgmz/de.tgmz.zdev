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

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import de.tgmz.zdev.editor.ZdevColorManager;
import de.tgmz.zdev.editor.assembler.AssemblerScanner;
import de.tgmz.zdev.editor.cobol.COBOLScanner;
import de.tgmz.zdev.editor.pli.PLIScanner;
import de.tgmz.zdev.editor.rexx.RexxScanner;
import de.tgmz.zdev.editor.sql.SqlScanner;


public class ScannerTest {
	private static ZdevColorManager zdevColorManager =  new ZdevColorManager();
	@Test
	public void testPli() {
		assertNotNull(new PLIScanner(zdevColorManager));
	}
	@Test
	public void testCobol() {
		assertNotNull(new COBOLScanner(zdevColorManager));
	}
	@Test
	public void testSql() {
		assertNotNull(new SqlScanner(zdevColorManager));
	}
	@Test
	public void testAssembler() {
		assertNotNull(new AssemblerScanner(zdevColorManager));
	}
	@Test
	public void testRexx() {
		assertNotNull(new RexxScanner(zdevColorManager));
	}
}
