/*********************************************************************
* Copyright (c) 06.10.2023 Thomas Zierer
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
import de.tgmz.zdev.preferences.ZdevPreferencePage;

public class LanguageTest {
	
	@Test
	public void testRegexLanguage() throws NoSuchFieldException, IllegalAccessException {
		for (Language l : Language.values()) {
			if (l != Language.DEFAULT) {
				Field f = ZdevPreferenceConstants.class.getField("REGEX_" + l.toString());
				
				String reg = (String) f.get(null);
				
				assertTrue(new ZdevPreferencePage().getPreferenceStore().getString(reg).length() > 0);

				String dsn = "BAT.DV." + l.toString().substring(0, Math.min(l.toString().length(), 8));
				
				assertEquals(l, Language.fromDatasetName(dsn));
			}
			
			assertEquals(Language.DEFAULT, Language.fromDatasetName("BAT.DV.NATURAL"));
		}
	}
	
	@Test
	public void testLanguageByExtension() {
		assertEquals(Language.PLI, Language.byExtension(".pli"));
		assertEquals(Language.JCL, Language.byExtension(".jcl"));
		assertEquals(Language.DEFAULT, Language.byExtension(".cbl"));
	}
}
