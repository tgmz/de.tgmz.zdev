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
import org.mockito.Mockito;

import com.ibm.cics.zos.model.Member;

import de.tgmz.zdev.history.HistoryException;
import de.tgmz.zdev.history.LocalHistory;
import de.tgmz.zdev.history.model.IHistoryModel;
import de.tgmz.zdev.restore.HistorySelectionDialog;

public class HistorySelectTest {
	private static final byte[] CONTENT = "Some content".getBytes(Charset.defaultCharset());
	private static final IHistoryModel history = LocalHistory.getInstance();
	private static Member member;
	
	private Shell shell;
	
	@Test
	public void testDialog() {
		HistorySelectionDialog hsd = new HistorySelectionDialog(shell, member);
		
		hsd.create();
		hsd.setBlockOnOpen(false);
		
		assertEquals(0, hsd.open());
		
		hsd.close();
	}
	
	@BeforeClass
	public static void setupOnce() throws HistoryException {
		history.clear(new Date(), 0);
		
		member = Mockito.mock(Member.class);
		Mockito.when(member.toDisplayName()).thenReturn("HLQ.PLI(MYMEMBER)");
		
		LocalHistory.getInstance().save(CONTENT, member.toDisplayName());
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
