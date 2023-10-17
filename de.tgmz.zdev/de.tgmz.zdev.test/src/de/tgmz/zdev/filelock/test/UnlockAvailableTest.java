/*********************************************************************
* Copyright (c) 10.10.2023 Thomas Zierer
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package de.tgmz.zdev.filelock.test;

import static org.junit.Assert.assertFalse;

import org.junit.Test;

import com.ibm.cics.zos.model.Member;

import de.tgmz.zdev.filelock.UnlockAvailableTester;

public class UnlockAvailableTest {
	@Test
	public void test() {
		UnlockAvailableTester unlockAvailableTester = new UnlockAvailableTester();
		
		assertFalse(unlockAvailableTester.test((Member) null, "isNotRunning", null, null));
	}
}
