/*********************************************************************
* Copyright (c) 10.10.2023 Thomas Zierer
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package de.tgmz.zdev.restore.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

import java.io.IOException;

import org.eclipse.compare.ITypedElement;
import org.junit.Test;

import de.tgmz.zdev.restore.compare.HistoryCompareItem;

public class HistoryCompareItemTest {
	private static final String NAME = "one";
	private static final byte CONTENT = 'c';
	@Test
	public void testHistoryCompareItem() throws IOException {
		HistoryCompareItem cci = new HistoryCompareItem(NAME, new byte[] {CONTENT});
		
		assertEquals(NAME, cci.getName());
		assertEquals(CONTENT, (byte) cci.getContents().read());
		
		assertEquals(ITypedElement.TEXT_TYPE, cci.getType());
		
		assertNull(cci.getImage());
		
		assertNull(cci.replace(null,  null));
		
		assertFalse(cci.isEditable());
	}
	@Test
	public void testHistoryCompareEmptyItem() throws IOException {
		HistoryCompareItem cci = new HistoryCompareItem(NAME, null);
		
		assertEquals(-1, cci.getContents().read());
	}
}
 
