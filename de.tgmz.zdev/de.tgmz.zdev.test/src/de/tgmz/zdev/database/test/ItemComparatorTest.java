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

import java.util.Arrays;

import org.junit.BeforeClass;
import org.junit.Test;

import de.tgmz.zdev.domain.Item;
import de.tgmz.zdev.domain.ItemComparator;
import de.tgmz.zdev.domain.ItemComparator.Direction;
import de.tgmz.zdev.domain.ItemComparator.Sorting;

public class ItemComparatorTest {
	private static final String PGM = "HELLOW";
	private static final String PDS = "HLQ.PLI";
	private static ItemComparator ic;
	private static Item i0;
	private static Item i1;
	
	@BeforeClass
	public static void setupOnce() {
		ic = new ItemComparator(Sorting.NONE, Direction.ASCENDING);
		
		i0 = new Item(PDS, PGM);
		i1 = new Item(PDS, PGM);
	}
	
	@Test
	public void testCompare() {
		i0.setDsn('A' + PDS.substring(1));
		i1.setDsn('Z' + PDS.substring(1));
		executeSortTest(Sorting.FULLNAME, new Item[] {i0, i1});
	}
	private void executeSortTest(Sorting sorting, Item[] items) {
		ic.setSorting(sorting);
		ic.setDirection(Direction.ASCENDING);
			
		Arrays.sort(items, ic);
			
		assertEquals(i1, items[1]);
		
		ic.setDirection(Direction.DESCENDING);
			
		Arrays.sort(items, ic);
			
		assertEquals(i1, items[0]);
	}
}

