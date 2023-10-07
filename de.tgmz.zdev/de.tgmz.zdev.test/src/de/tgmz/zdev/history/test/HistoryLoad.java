/*********************************************************************
* Copyright (c) 06.10.2023 Thomas Zierer
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package de.tgmz.zdev.history.test;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Date;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import de.tgmz.zdev.history.HistoryException;
import de.tgmz.zdev.history.LocalHistory;
import de.tgmz.zdev.history.model.IHistoryModel;
import net.bytebuddy.utility.RandomString;


/**
 * Useful for generating masses of history entries.
 */
public class HistoryLoad {
	private static final IHistoryModel history = LocalHistory.getInstance();
	private static final int ENTRIES = 200;
	private static final int VERSIONS = 100;
	@Test
	public void testHistory() throws HistoryException {
		SecureRandom r = new SecureRandom();
		
		for (int i = 0; i < ENTRIES; i++) {
			String s0 = "" + r.nextLong();
			
			for (int j = 0; j < VERSIONS; j++) {
				String s1 = RandomString.make(32000);
				
				history.save(s1.getBytes(StandardCharsets.UTF_8), s0);
			}
		}
	}
	@BeforeClass
	public static void setupOnce() throws HistoryException {
		history.clear(new Date(), 0);
	}
	@AfterClass
	public static void teardownOnce() throws HistoryException {
		history.clear(new Date(), 0);
	}
}
