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

import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.tgmz.zdev.history.HistoryDisplayItem;
import de.tgmz.zdev.history.HistoryException;
import de.tgmz.zdev.history.LocalHistory;
import de.tgmz.zdev.history.model.IHistoryModel;

public class HistoryTest {
	private static final String MEMBER_NAME = "HLQ.PLI(MYMEMBER)";
	private static final byte[] CONTENT = "Some content".getBytes(Charset.defaultCharset());
	private static final IHistoryModel history = LocalHistory.getInstance();
	@Test
	public void testHistory() throws HistoryException {
		HistoryDisplayItem hdi = history.save(MEMBER_NAME, CONTENT);
		
		assertArrayEquals(CONTENT, history.retrieve(hdi.getId()));
	}
	@Test
	public void testHistoryList() throws HistoryException {
		history.save(MEMBER_NAME, CONTENT);
	
		List<HistoryDisplayItem> versions = history.getVersions(MEMBER_NAME);
		
		assertEquals(1, versions.size());
		
		HistoryDisplayItem hdi = versions.get(0);
		
		assertArrayEquals(CONTENT, history.retrieve(hdi.getId()));
	}
	@Test
	public void testNoHistory() throws HistoryException {
		LocalDateTime offset = LocalDateTime.now().plusDays(1);
		
		assertArrayEquals(new byte[0], history.retrieve(offset));
	}
	@Test
	public void testHistoryClear() throws HistoryException, InterruptedException {
		history.save(MEMBER_NAME, CONTENT);
		
		// Enforce second version. DevOps pipeline sometimes overwrites because timestamps are identical.
		Thread.sleep(10);
		
		history.save(MEMBER_NAME, CONTENT);
		
		history.clear(LocalDateTime.now().minusSeconds(1), 1);
		
		assertEquals(1, history.getVersions(MEMBER_NAME).size());
	}
	@Before
	public void setup() throws HistoryException {
		history.clear(LocalDateTime.now(), 0);
	}
	@After
	public void teardown() throws HistoryException {
		history.clear(LocalDateTime.now(), 0);
	}
}
