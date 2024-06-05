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

import java.util.Locale;
import java.util.UUID;

import org.eclipse.jface.text.ITextSelection;
import org.junit.Test;
import org.mockito.Mockito;

import de.tgmz.zdev.editor.open.DataSetSelectionTester;


public class DataSetSelectionTesterTest {
	private static final DataSetSelectionTester TESTER = new DataSetSelectionTester();
	private ITextSelection textSelection = Mockito.mock(ITextSelection.class);
	private static final String PROPERTY = "isDataSet";
	
	@Test
	public void testSequential() {
		Mockito.when(textSelection.getText()).thenReturn("HLQ.ZDEV.LISTLIB");

		assertTrue(TESTER.test(textSelection, PROPERTY, null, null));
	}
	@Test
	public void testPartitioned() {
		Mockito.when(textSelection.getText()).thenReturn("HLQ.ZDEV.PROCLIB(COMPPL1)");

		assertTrue(TESTER.test(textSelection, PROPERTY, null, null));
	}
	@Test
	public void testTooShort() {
		Mockito.when(textSelection.getText()).thenReturn("HLQ");

		assertFalse(TESTER.test(textSelection, PROPERTY, null, null));
	}
	@Test
	public void testTooLong() {
		String s = UUID.randomUUID().toString().replace("-", ".").toUpperCase(Locale.getDefault());
		
		s += s;
		
		Mockito.when(textSelection.getText()).thenReturn(s);
		assertFalse(TESTER.test(textSelection, PROPERTY, null, null));
		
		Mockito.when(textSelection.getText()).thenReturn(String.format("%s(%s)", s, s));
		assertFalse(TESTER.test(textSelection, PROPERTY, null, null));
	}
	@Test
	public void testInvalidMember() {
		Mockito.when(textSelection.getText()).thenReturn("HLQ.ZDEV.PROCLIB(ABCDEFGHIJ)");

		assertFalse(TESTER.test(textSelection, PROPERTY, null, null));
	}
	@Test
	public void testEmpty() {
		assertFalse(TESTER.test(textSelection, PROPERTY, null, null));
	}
	@Test
	public void testWrongType() {
		assertFalse(TESTER.test(new Object(), PROPERTY, null, null));
		assertFalse(TESTER.test(textSelection, "foo", null, null));
	}
}
