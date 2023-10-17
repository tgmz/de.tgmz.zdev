/*********************************************************************
* Copyright (c) 10.10.2023 Thomas Zierer
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package de.tgmz.zdev.plicomp.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import de.tgmz.zdev.plicomp.PlicompUtil;
import de.tgmz.zdev.xinfo.generated.FILE;
import de.tgmz.zdev.xinfo.generated.FILEREFERENCETABLE;
import de.tgmz.zdev.xinfo.generated.ObjectFactory;
import de.tgmz.zdev.xinfo.generated.PACKAGE;


public class PlicomUtilTest {
	private static final String FILENAME = "HLQ.ZDEV.MACLIB";
	@Test
	public void test() {
		ObjectFactory of = new ObjectFactory();
		
		PACKAGE p = of.createPACKAGE();
		FILEREFERENCETABLE frt = of.createFILEREFERENCETABLE();
		FILE f = of.createFILE();
		
		f.setFILENAME(FILENAME);
		f.setFILENUMBER("1");
		
		frt.getFILE().add(f);
		
		p.setFILEREFERENCETABLE(frt);
		
		assertEquals(FILENAME, PlicompUtil.getFileNameFromFileNumber("1", p));
		
		assertNull(PlicompUtil.getFileNameFromFileNumber("2", p));
	}
}
