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

import static org.junit.Assert.assertEquals;

import java.util.Iterator;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.junit.BeforeClass;
import org.junit.Test;

import de.tgmz.zdev.database.DbService;
import de.tgmz.zdev.domain.Item;
import de.tgmz.zdev.quickaccess.MemberSelectionDialog;
import jakarta.persistence.EntityManager;

public class QuickaccessTest {
	private static final String PGM = "HELLOW";
	private static final String PDS = "HLQ.PLI";
	
	@BeforeClass
	public static void setupOnce() {
		try (EntityManager em = DbService.getInstance().getEntityManagerFactory().createEntityManager()) {
			Iterator<?> it = em.createQuery("From Item", Item.class).getResultList().iterator();
			
			while (it.hasNext()) {
				Item i = (Item) it.next();
				
				em.remove(i);
			}
			
			Item i = new Item(PDS, PGM);
			
			em.persist(i);
		}
	}
	
	@Test
	public void testMemberSelectionDialog() {
		Shell shell = new Shell((Display) null);
		
		MemberSelectionDialog msd = new MemberSelectionDialog(shell, false);
		
		msd.create();
		msd.setBlockOnOpen(false);
		
		assertEquals(0, msd.open());
		
		msd.close();
		
		shell.dispose();
	}
}
