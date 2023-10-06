/*********************************************************************
* Copyright (c) 06.10.2023 Thomas Zierer
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package de.tgmz.zdev.logging.test;

import static org.junit.Assert.assertTrue;

import org.eclipse.jface.preference.IPreferenceStore;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tgmz.zdev.preferences.ZdevPreferenceConstants;

public class LoggerTest {
	private static final Logger LOG = LoggerFactory.getLogger(LoggerTest.class);
	private static String defaultLogLevel;
	@BeforeClass
	public static void setupOnce() {
		IPreferenceStore preferenceStore = de.tgmz.zdev.preferences.Activator.getDefault().getPreferenceStore();
		
		defaultLogLevel = preferenceStore.getString(ZdevPreferenceConstants.LOG_LEVEL);
		
		preferenceStore.setValue(ZdevPreferenceConstants.LOG_LEVEL, "ALL");
	}
	@Test
	public void test() {
		LOG.error("Test error");
		LOG.warn("Test warning");
		LOG.info("Test info");
		LOG.debug("Test debug");
		
		assertTrue(LOG.isErrorEnabled());
		assertTrue(LOG.isWarnEnabled());
		assertTrue(LOG.isInfoEnabled());
		assertTrue(LOG.isDebugEnabled());
	}
	@AfterClass
	public static void teardownOnce() {
		de.tgmz.zdev.preferences.Activator.getDefault().getPreferenceStore().setValue(ZdevPreferenceConstants.LOG_LEVEL, defaultLogLevel);
	}
}
