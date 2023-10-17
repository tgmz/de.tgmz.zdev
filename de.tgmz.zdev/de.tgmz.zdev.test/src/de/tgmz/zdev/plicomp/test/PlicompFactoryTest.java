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

import java.io.InputStream;

import org.junit.BeforeClass;
import org.junit.Test;

import de.tgmz.zdev.plicomp.PlicompConfigurationException;
import de.tgmz.zdev.plicomp.PlicompException;
import de.tgmz.zdev.plicomp.PlicompFactory;


public class PlicompFactoryTest {
	private static PlicompFactory pf;

	@BeforeClass
	public static void setupOnce() throws PlicompConfigurationException {
		pf = PlicompFactory.getInstance();
	}
	
	@Test
	public void test() throws PlicompException {
		InputStream is = this.getClass().getClassLoader().getResourceAsStream("testresources/HELLOW.xml");
		
		assertEquals(1, pf.getPlicomp(is, "").getMESSAGE().size()); // Der Datasetname interessiert nicht
	}
	@Test(expected=PlicompException.class)
	public void fail() throws PlicompException {
		InputStream is = this.getClass().getClassLoader().getResourceAsStream("testresources/empty.xml");
		
		pf.getPlicomp(is, "");
	}
}
