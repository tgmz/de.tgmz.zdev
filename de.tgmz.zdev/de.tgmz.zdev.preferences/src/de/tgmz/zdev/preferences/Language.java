/*********************************************************************
* Copyright (c) 06.10.2023 Thomas Zierer
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
	PLI(".pli"), COBOL(".cbl"),	ASSEMBLER(".asm"), JCL(".jcl"), SQL(".sql"), REXX(".rexx"), DEFAULT("");
	
	private String extension;
	
	private Language(String extension) {
		this.extension = extension;
	}
    //CHECKSTYLE DISABLE ReturnCount
	public static Language fromDatasetName(String dsName) {
		Activator activator = Activator.getDefault();
		
		if (activator != null) {
			IPreferenceStore ps = activator.getPreferenceStore(); 
		
			if (dsName.matches(ps.getString(ZdevPreferenceConstants.REGEX_PLI))) {
				return PLI;
			}
		
			if (dsName.matches(ps.getString(ZdevPreferenceConstants.REGEX_JCL))) {
				return JCL;
			}
		
			if (dsName.matches(ps.getString(ZdevPreferenceConstants.REGEX_COBOL))) {
				return COBOL;
			}
		
			if (dsName.matches(ps.getString(ZdevPreferenceConstants.REGEX_ASSEMBLER))) {
				return ASSEMBLER;
			}
		
			if (dsName.matches(ps.getString(ZdevPreferenceConstants.REGEX_SQL))) {
				return SQL;
			}
			
			if (dsName.matches(ps.getString(ZdevPreferenceConstants.REGEX_REXX))) {
				return REXX;
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
