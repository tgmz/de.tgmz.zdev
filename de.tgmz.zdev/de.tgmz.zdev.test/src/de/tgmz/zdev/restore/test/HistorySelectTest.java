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

import java.nio.charset.Charset;
import java.util.Date;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.tgmz.zdev.history.HistoryException;
import de.tgmz.zdev.history.LocalHistory;
import de.tgmz.zdev.history.model.IHistoryModel;
import de.tgmz.zdev.restore.HistorySelectionDialog;

public class HistorySelectTest {
	private static final String MEMBER_NAME = "HLQ.PLI(MYMEMBER)";
	private static final byte[] CONTENT = "Some content".getBytes(Charset.defaultCharset());
	private static final IHistoryModel history = LocalHistory.getInstance();
	
	private Shell shell;
	
	private static long key;
	
	@Test
	public void testGetElementName() {
		HistorySelectionDialog d = new HistorySelectionDialog(shell, MEMBER_NAME);
		
		assertEquals(key, Long.parseLong(d.getElementName(key)));
		
		shell.dispose();
	}
	
	@Test
	public void testDialog() throws HistoryException {
		HistorySelectionDialog hsd = new HistorySelectionDialog(shell, MEMBER_NAME);
		
		hsd.create();
		hsd.setBlockOnOpen(false);
		hsd.setInitialPattern(MEMBER_NAME);
		hsd.setInitialElementSelections(LocalHistory.getInstance().getVersions(MEMBER_NAME));
		
		assertEquals(0, hsd.open());
		
		hsd.close();
	}
	
	@BeforeClass
	public static void setupOnce() throws HistoryException {
		history.clear(new Date(), 0);
		
		key = LocalHistory.getInstance().save(CONTENT, MEMBER_NAME);
	}
	@AfterClass
	public static void teardownOnce() throws HistoryException {
		history.clear(new Date(), 0);
	}
	@Before
	public void setup() {
		shell = new Shell((Display) null);
	}
}
