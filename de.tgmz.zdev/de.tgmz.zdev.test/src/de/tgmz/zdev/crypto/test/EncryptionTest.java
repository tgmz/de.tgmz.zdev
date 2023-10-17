/*********************************************************************
* Copyright (c) 10.10.2023 Thomas Zierer
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package de.tgmz.zdev.crypto.test;

import static org.junit.Assert.assertEquals;

import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import de.tgmz.zdev.crypto.ZdevCrypto;

/**
 * Tests de- and encryption..
 */
@RunWith(value = Parameterized.class)
public class EncryptionTest {
	private String arg;

	public EncryptionTest(String arg) {
		super();
		this.arg = arg;
	}
	
	@Test
	public void roundtrip() throws GeneralSecurityException {
		String ciphertext = ZdevCrypto.getInstance().encrypt(arg);
		
	    assertEquals(arg, ZdevCrypto.getInstance().decrypt(ciphertext));
	}
	@Parameters
	public static Collection<Object[]> data() {
		Object[][] data = new Object[][] {{ "sa" }};
		return Arrays.asList(data);
	}
}
