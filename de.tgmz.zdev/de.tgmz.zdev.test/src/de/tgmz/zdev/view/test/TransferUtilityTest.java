/*********************************************************************
* Copyright (c) 10.10.2023 Thomas Zierer
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package de.tgmz.zdev.view.test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import de.tgmz.zdev.view.copypaste.TransferUtility;

/**
 * Test class for TransferUtility.
 */
public class TransferUtilityTest {
	private static final String PGM = "HELLOW";
	private static TransferUtility tu;

	@BeforeClass
	public static void setupOnce() {
		tu = TransferUtility.getInstance();
	}
	
	@After
	public void tearDown() {
		tu.reset();
	}

	@Test
	public void transfer() throws IOException {
		byte[] b0 = IOUtils.toByteArray(this.getClass().getClassLoader().getResourceAsStream("testresources/" + PGM + ".pli"));
		
		assertTrue(b0.length > 0);
		
		tu.put(PGM, b0);
		
		byte[] b1 = tu.getTransfers().get(0).getContent();
		
		assertArrayEquals(b0, b1);
		assertEquals(PGM, tu.getTransfers().get(0).getName());
	}
}
