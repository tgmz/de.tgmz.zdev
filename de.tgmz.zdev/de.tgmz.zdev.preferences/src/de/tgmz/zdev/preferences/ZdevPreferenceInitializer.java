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

import java.util.Locale;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

/**
 * Class used to initialize default preference values.
 */
public class ZdevPreferenceInitializer extends AbstractPreferenceInitializer {

	/** {@inheritDoc} */
	@Override
	public void initializeDefaultPreferences() {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		
		String userId = store.getString(ZdevPreferenceConstants.USER);
		String userIdUpper = userId.toUpperCase(Locale.ROOT);
		
		store.setDefault(ZdevPreferenceConstants.JOB_SUFFIX, false);
		store.setDefault(ZdevPreferenceConstants.LOG_LEVEL, "INFO");
		store.setDefault(ZdevPreferenceConstants.FILELOCK_AUTO, false);
		store.setDefault(ZdevPreferenceConstants.JOB_CARD, "//" 
							+  userIdUpper
							+ "B JOB '<Insert accounting information>',"
							+ "'" + userIdUpper + "'"
							+ ",CLASS=Z,MSGCLASS=T," 
							+ System.lineSeparator()
							+ "//     MSGLEVEL=(1,1),REGION=0M");
		store.setDefault(ZdevPreferenceConstants.REGEX_PLI, "^.*\\.(PLI|PL1|INC|INCLUDE)$");
		store.setDefault(ZdevPreferenceConstants.REGEX_JCL, "^.*\\.(JCL|CNTL)$");
		store.setDefault(ZdevPreferenceConstants.REGEX_COB, "^.*\\.(COBOL|COB|CPY|CBL)$");
		store.setDefault(ZdevPreferenceConstants.REGEX_ASM, "^.*\\.(ASSEMBLE|ASM)$");
		store.setDefault(ZdevPreferenceConstants.REGEX_C, "^.*\\.(C)$");
		store.setDefault(ZdevPreferenceConstants.REGEX_CPP, "^.*\\.(CPP)$");
		store.setDefault(ZdevPreferenceConstants.REGEX_SQL, "^.*\\.(SQL|QMF)$");
		store.setDefault(ZdevPreferenceConstants.REGEX_REX, "^.*\\.(REXX|TSOPROC)$");
		store.setDefault(ZdevPreferenceConstants.CAPS_ON, false);
		
		store.setDefault(ZdevPreferenceConstants.HISTORY_MONTHS, 3);
		store.setDefault(ZdevPreferenceConstants.HISTORY_VERSIONS, 10);
		
		store.setDefault(ZdevPreferenceConstants.OBJLIB, userIdUpper + ".ZDEV.OBJ");
		store.setDefault(ZdevPreferenceConstants.LOADLIB, userIdUpper + ".ZDEV.LOAD");
		store.setDefault(ZdevPreferenceConstants.LINK_OPTIONS, "MAP,LIST");
		
		store.setDefault(ZdevPreferenceConstants.SYSLIB_PLI, userIdUpper + ".ZDEV.INCLUDE");
		store.setDefault(ZdevPreferenceConstants.SYSLIB_COB, userIdUpper + ".ZDEV.COPY");
		store.setDefault(ZdevPreferenceConstants.SYSLIB_ASM, "SYS1.MACLIB;" + userIdUpper + ".ZDEV.MAC");
		store.setDefault(ZdevPreferenceConstants.SYSLIB_C, userIdUpper + ".ZDEV.H");
		store.setDefault(ZdevPreferenceConstants.SYSLIB_CPP, userIdUpper + ".ZDEV.H");
		
		store.setDefault(ZdevPreferenceConstants.DB2_PLI, "PP(SQL)");
		store.setDefault(ZdevPreferenceConstants.DB2_COB, "SQL");
		store.setDefault(ZdevPreferenceConstants.DB2_C, "SQL");
		store.setDefault(ZdevPreferenceConstants.DB2_CPP, "SQL");
		
		store.setDefault(ZdevPreferenceConstants.CICS_PLI, "PP(CICS)");
		store.setDefault(ZdevPreferenceConstants.CICS_COB, "CICS");
		store.setDefault(ZdevPreferenceConstants.CICS_C, "CICS");
		store.setDefault(ZdevPreferenceConstants.CICS_CPP, "CICS");
		
		store.setDefault(ZdevPreferenceConstants.XSDOSRG_LIBPATH, "/usr/lib:/usr/lpp/java/J8.0/lib/s390/classic/:/usr/lib/java_runtime");
		
		store.setDefault(ZdevPreferenceConstants.USS_HOME, "/z/" + userId);
	}
}
