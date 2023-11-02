/*********************************************************************
* Copyright (c) 10.10.2023 Thomas Zierer
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package de.tgmz.zdev.preferences;

import org.apache.commons.io.FilenameUtils;
import org.eclipse.jface.preference.IPreferenceStore;


/**
 * Languages with specific editor.
 */
public enum Language {
	PLI(".pli", ZdevPreferenceConstants.REGEX_PLI)
	, COBOL(".cbl", ZdevPreferenceConstants.REGEX_COBOL)
	, ASSEMBLER(".asm", ZdevPreferenceConstants.REGEX_ASSEMBLER)
	, JCL(".jcl", ZdevPreferenceConstants.REGEX_JCL)
	, SQL(".sql", ZdevPreferenceConstants.REGEX_SQL)
	, REXX(".rexx", ZdevPreferenceConstants.REGEX_REXX)
	, CPP(".cpp", ZdevPreferenceConstants.REGEX_CPP)
	, C(".c", ZdevPreferenceConstants.REGEX_C)
	, DEFAULT("", "");
	
	private String extension;
	private String preferenceField;
	
	private Language(String extension, String preferenceField) {
		this.extension = extension;
		this.preferenceField = preferenceField;
	}
    //CHECKSTYLE DISABLE ReturnCount
	public static Language fromDatasetName(String dsName) {
		Activator activator = Activator.getDefault();
		
		if (activator != null) {
			IPreferenceStore ps = activator.getPreferenceStore(); 
			
			for (Language l : Language.values()) {
				if (dsName.matches(ps.getString(l.preferenceField))) {
					return l;
				}
			}
		
			return DEFAULT;
		}
		
		return PLI;
	}
	
	public static Language byExtension(String fileName) {
		switch (FilenameUtils.getExtension(fileName)) {
		case "pli":
			return PLI;
		case "jcl":
			return JCL;
		default:
			return DEFAULT;
		}
	}
	
	public String getExtension() {
		return extension;
	}
}
