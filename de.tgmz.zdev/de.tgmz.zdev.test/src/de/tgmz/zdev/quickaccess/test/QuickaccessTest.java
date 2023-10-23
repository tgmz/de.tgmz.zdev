/*********************************************************************
* Copyright (c) 10.10.2023 Thomas Zierer
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package de.tgmz.zdev.quickaccess.test;

import static org.junit.Assert.assertTrue;

import java.util.Iterator;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.hibernate.Session;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.tgmz.zdev.database.DbService;
import de.tgmz.zdev.domain.Item;
import de.tgmz.zdev.quickaccess.MemberSelectionDialog;

public class QuickaccessTest {
	private static final String PGM = "HELLOW";
	private static final String PDS = "HLQ.PLI";
	private Shell shell;
	
	@BeforeClass
	public static void setupOnce() {
		Session session = DbService.startTx();
		
		try {
			Iterator<?> it = session.createQuery("From Item", Item.class).list().iterator();
			
			while (it.hasNext()) {
				Item i = (Item) it.next();
				
				session.remove(i);
			}
			
			Item i = new Item(PDS, PGM);
			
			session.persist(i);
		} finally {
			DbService.endTx(session);
		}
	}
	@Before
	public void setup() {
		shell = new Shell((Display) null);
	}
	
	@Test
	public void testMemberSelectionDialog() {
		Item item = null;
		
		Session session = DbService.startTx();
		
		try {
			item = session.createNamedQuery("byDsnAndMember", Item.class).setParameter("dsn", PDS).setParameter("member", PGM).uniqueResult();
		} finally {
			DbService.endTx(session);
		}
			
		MemberSelectionDialog d = new MemberSelectionDialog(shell, false);
		
		assertTrue(d.getElementName(item).length() > 0);
		
		shell.dispose();
	}
}
