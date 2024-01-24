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

import java.util.Date;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import de.tgmz.zdev.history.HistoryException;
import de.tgmz.zdev.history.LocalHistory;
import de.tgmz.zdev.history.model.IHistoryModel;

public class HistoryTest {
	private static final String NAME0 = "one";
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
		long z = history.save(new byte[1], NAME0).getId();
		
		assertEquals(z, history.getVersions(NAME0).get(0).getId());
		assertEquals(1, history.getVersions(NAME0).get(0).getSize());
	}
}
 
