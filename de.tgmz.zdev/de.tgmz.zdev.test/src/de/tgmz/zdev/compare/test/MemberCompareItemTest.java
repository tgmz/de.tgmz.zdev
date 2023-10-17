/*********************************************************************
* Copyright (c) 10.10.2023 Thomas Zierer
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package de.tgmz.zdev.compare.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

import org.eclipse.compare.ITypedElement;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import com.ibm.cics.zos.model.Member;

import de.tgmz.zdev.compare.MemberCompareItem;

/**
 * MemberCompareItemTest.
 */
public class MemberCompareItemTest {
	private static final String NAME = "HLQ.ZDEV.PLI(HELLOW)";
	private static Member member;
	private static MemberCompareItem mci;

	@BeforeClass
	public static void setUpBeforeClass() {
		member = Mockito.mock(Member.class);
		
		Mockito.when(member.toDisplayName()).thenReturn(NAME);
		 
		mci = new MemberCompareItem(member);
	}

	@Test
	public void testGetMember() {
		assertEquals(member,  mci.getMember());
	}

	@Test
	public void testIsEditable() {
		assertFalse(mci.isEditable());
	}

	@Test
	public void testReplace() {
		assertNull(mci.replace(null, null));
	}

	@Test
	public void testGetName() {
		assertEquals(NAME, mci.getName());
	}

	@Test
	public void testGetImage() {
		assertNull(mci.getImage());
	}

	@Test
	public void testGetType() {
		assertEquals(ITypedElement.TEXT_TYPE, mci.getType());
	}

}
