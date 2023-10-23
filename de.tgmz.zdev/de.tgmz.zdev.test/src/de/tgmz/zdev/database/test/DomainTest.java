/*********************************************************************
* Copyright (c) 10.10.2023 Thomas Zierer
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package de.tgmz.zdev.database.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.util.Arrays;

import org.hibernate.Session;
import org.junit.BeforeClass;
import org.junit.Test;

import de.tgmz.zdev.database.DbService;
import de.tgmz.zdev.domain.Item;
import de.tgmz.zdev.domain.ItemComparator;

public class DomainTest {
	private static final String PGM = "HELLOW";
	private static final String PDS = "HLQ.PLI";
	
	@BeforeClass
	public static void setupOnce() {
		Session session = DbService.startTx();
		
		try {
			session.createMutationQuery("DELETE FROM Item").executeUpdate();
			
			Item i = new Item(PDS, PGM);
			
			session.persist(i);
		} finally {
			DbService.endTx(session);
		}
	}
	
	@Test
	public void testSimple() {
		Session session = DbService.startTx();
		
		try {
			Item i = getItem(session);
			
			assertEquals(PGM, i.getMember());
		} finally {
			DbService.endTx(session);
		}
	}
	@Test
	public void testCompare() {
		Item i0 = new Item(PDS, PGM);
		Item i1 = new Item('Z' + PDS.substring(1), PGM);

		Item[] items = new Item[] {i0, i1};
			
		Arrays.sort(items, new ItemComparator(ItemComparator.Sorting.FULLNAME, ItemComparator.Direction.DESCENDING));
			
		assertEquals(i1, items[0]);
	}

	@Test
	public void testEquals() {
		Item i0 = new Item(PDS, PGM);
		Item i1 = new Item(PDS, PGM);
		
		assertEquals(i0,  i1);
		
		i1.setDsn('Z' + PDS.substring(1));
		
		assertNotEquals(i0,  i1);
	}

	private Item getItem(Session session) {
		return session.createNamedQuery("byDsnAndMember", Item.class).setParameter("dsn", PDS).setParameter("member", PGM).uniqueResult();
	}
}

