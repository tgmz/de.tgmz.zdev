/*********************************************************************
* Copyright (c) 10.10.2023 Thomas Zierer
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package de.tgmz.zdev.history.test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.nio.charset.Charset;
import java.util.Calendar;
import java.util.Date;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.tgmz.zdev.history.HistoryException;
import de.tgmz.zdev.history.LocalHistory;
import de.tgmz.zdev.history.model.IHistoryModel;


public class HistoryTest {
	private static final String MEMBER_NAME = "HLQ.PLI(MYMEMBER)";
	private static final byte[] CONTENT = "Some content".getBytes(Charset.defaultCharset());
	private static final IHistoryModel history = LocalHistory.getInstance();
	@Test
	public void testHistory() throws HistoryException {
		long key = history.save(CONTENT, MEMBER_NAME).getId();
		
		assertFalse("Versionlist is empty", history.getVersions(MEMBER_NAME).isEmpty());
		
		assertArrayEquals("Documents differ", CONTENT, history.retrieve(key));
	}
	@Test
	public void testNoHistory() throws HistoryException {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_YEAR, 1);
		
		assertArrayEquals("Documents differ", new byte[0], history.retrieve(cal.getTimeInMillis()));
	}
	@Test
	public void testHistoryClear() throws HistoryException, InterruptedException {
		history.save(CONTENT, MEMBER_NAME);
		
		// Enforce second version. DevOps pipeline sometimes overwrites because timestamps are identical.
		Thread.sleep(10);
		
		history.save(CONTENT, MEMBER_NAME);
		
		history.clear(new Date(System.currentTimeMillis() - 1000), 1);
		
		assertEquals("Versionlist is not 1", 1, history.getVersions(MEMBER_NAME).size());
	}
	@Before
	public void setup() throws HistoryException {
		history.clear(new Date(), 0);
	}
	@After
	public void teardown() throws HistoryException {
		history.clear(new Date(), 0);
	}
}
