/*********************************************************************
* Copyright (c) 03.12.2023 Thomas Zierer
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package de.tgmz.zdev.restore.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.text.ParseException;
import java.util.Date;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import de.tgmz.zdev.history.HistoryException;
import de.tgmz.zdev.history.LocalHistory;
import de.tgmz.zdev.history.model.IHistoryModel;
import de.tgmz.zdev.restore.compare.HistoryItemComparator;

public class HistoryTest {
	private static final String NAME0 = "one";
	private static final String NAME1 = "two";
	private static final IHistoryModel history = LocalHistory.getInstance();
	
	@BeforeClass
	public static void setupOnce() throws HistoryException {
		history.clear(new Date(), 0);
	}
	@AfterClass
	public static void teardownOnce() throws HistoryException {
		history.clear(new Date(), 0);
	}
	@Test
	public void testHistory() throws HistoryException {
		long z = LocalHistory.getInstance().save(new byte[1], NAME0);
		
		assertEquals(z, LocalHistory.getInstance().getVersions(NAME0).get(0).getId());
		assertEquals(1, LocalHistory.getInstance().getVersions(NAME0).get(0).getSize());
	}
	@Test
	public void testHistoryCompare() throws HistoryException, ParseException {
		long z0 = LocalHistory.getInstance().save(new byte[1], NAME0);
		long z1 = LocalHistory.getInstance().save(new byte[1], NAME1);
		
		String s0 = HistoryItemComparator.fromTime(z0);
		
		assertEquals(z0, HistoryItemComparator.fromDisplay(s0));
		
		String s1 = HistoryItemComparator.fromTime(z1);
		
		HistoryItemComparator hic = new HistoryItemComparator();
		
		assertTrue(hic.compare(s0, s1) < 0);
		
		assertEquals(0, hic.compare("", ""));
	}
}
 
