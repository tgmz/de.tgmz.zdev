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

import org.eclipse.compare.CompareConfiguration;
import org.junit.Test;
import org.junit.Test.None;
import org.mockito.Mockito;

import com.ibm.cics.zos.model.Member;

import de.tgmz.zdev.restore.compare.CompareInput;

public class CompareInputTest {
	@Test(expected = None.class)
	public void testNewCompareInput() {
		new CompareInput(new CompareConfiguration(), Mockito.mock(Member.class), new byte[0]);
	}
}
