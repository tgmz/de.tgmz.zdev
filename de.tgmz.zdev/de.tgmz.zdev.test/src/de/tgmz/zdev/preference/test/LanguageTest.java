/*********************************************************************
* Copyright (c) 10.10.2023 Thomas Zierer
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package de.tgmz.zdev.preference.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;

import org.junit.Test;

import de.tgmz.zdev.preferences.Language;
import de.tgmz.zdev.preferences.ZdevPreferenceConstants;
import de.tgmz.zdev.preferences.EditorPreferencePage;

public class LanguageTest {
	
	@Test
	public void testRegex() throws IllegalArgumentException, IllegalAccessException {
		for (Field f : ZdevPreferenceConstants.class.getDeclaredFields()) {
			if (f.getName().startsWith("REGEX_")) {
				String reg = (String) f.get(null);
				
				assertTrue(new EditorPreferencePage().getPreferenceStore().getString(reg).length() > 0);
			}
		}
	}
	
	@Test
	public void testLanguage() {
		for (Language l : Language.values()) {
			if (l != Language.DEFAULT) {
				String dsn = "HLQ." + l.toString().substring(0, Math.min(l.toString().length(), 8));
				
				assertEquals(l, Language.fromDatasetName(dsn));
			}
		}
		
		assertEquals(Language.DEFAULT, Language.fromDatasetName("HLQ.NATURAL"));
	}
	
	@Test
	public void testLanguageByExtension() {
		assertEquals(Language.PLI, Language.byExtension(".pli"));
		assertEquals(Language.JCL, Language.byExtension(".jcl"));
		assertEquals(Language.DEFAULT, Language.byExtension(".cbl"));
	}
}
