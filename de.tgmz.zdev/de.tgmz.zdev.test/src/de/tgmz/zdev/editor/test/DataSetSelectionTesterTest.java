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

import org.eclipse.jface.text.ITextSelection;
import org.junit.Test;
import org.mockito.Mockito;

import de.tgmz.zdev.editor.open.DataSetSelectionTester;


public class DataSetSelectionTesterTest {
	private static final DataSetSelectionTester TESTER = new DataSetSelectionTester();
	private ITextSelection mock = Mockito.mock(ITextSelection.class);
	private static final String PROPERTY = "isDataSet";
	
	@Test
	public void testSequential() {
		Mockito.when(mock.getText()).thenReturn("HLQ.ZDEV.LISTLIB");

		assertTrue(TESTER.test(mock, PROPERTY, null, null));
	}
	@Test
	public void testPartitioned() {
		Mockito.when(mock.getText()).thenReturn("HLQ.ZDEV.PROCLIB(COMPPL1)");

		assertTrue(TESTER.test(mock, PROPERTY, null, null));
	}
	@Test
	public void testTooShort() {
		Mockito.when(mock.getText()).thenReturn("HLQ");

		assertFalse(TESTER.test(mock, PROPERTY, null, null));
	}
	@Test
	public void testInvalidMember() {
		Mockito.when(mock.getText()).thenReturn("HLQ.ZDEV.PROCLIB(ABCDEFGHIJ)");

		assertFalse(TESTER.test(mock, PROPERTY, null, null));
	}
}
