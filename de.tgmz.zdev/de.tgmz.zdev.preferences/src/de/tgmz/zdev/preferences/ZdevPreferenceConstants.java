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


/**
 * Constant definitions for plug-in preferences.
 */
public class ZdevPreferenceConstants {
    /** Constant field for den LogLevel. */
    public static final String LOG_LEVEL = "LOG_LEVEL";
    /** Constant field for job suffix. */
    public static final String JOB_SUFFIX = "JOB_SUFFIX";
    /** Constant field for job card. */
    public static final String JOB_CARD = "JOB_CARD";

    public static final String REGEX_PLI = "REGEX_PLI";
    public static final String REGEX_JCL = "REGEX_JCL";
    public static final String REGEX_COB = "REGEX_COB";
    public static final String REGEX_SQL = "REGEX_SQL";
    public static final String REGEX_ASM = "REGEX_ASM";
    public static final String REGEX_REX = "REGEX_REX";
    public static final String REGEX_C   = "REGEX_C";
    public static final String REGEX_CPP = "REGEX_CPP";
    
    public static final String OBJLIB = "OBJLIB";
    public static final String LOADLIB = "LOADLIB";
    public static final String LINK_OPTIONS = "LINK_OPTIONS";

    public static final String SYSLIB_PLI = "SYSLIB_PLI";
    public static final String SYSLIB_COB = "SYSLIB_COB";
    public static final String SYSLIB_ASM = "SYSLIB_ASM";
    public static final String SYSLIB_C   = "SYSLIB_C";
    public static final String SYSLIB_CPP = "SYSLIB_CPP";

    public static final String COMP_PLI = "COMP_PLI";
    public static final String COMP_COB = "COMP_COB";
    public static final String COMP_ASM = "COMP_ASM";
    public static final String COMP_C   = "COMP_C";
    public static final String COMP_CPP = "COMP_CPP";

    public static final String DB2_PLI = "DB2_PLI";
    public static final String DB2_COB = "DB2_COB";
    public static final String DB2_ASM = "DB2_ASM";
    public static final String DB2_C   = "DB2_C";
    public static final String DB2_CPP = "DB2_CPP";

    public static final String CICS_PLI = "CICS_PLI";
    public static final String CICS_COB = "CICS_COB";
    public static final String CICS_ASM = "CICS_ASM";
    public static final String CICS_C   = "CICS_C";
    public static final String CICS_CPP = "CICS_CPP";

    public static final String FILELOCK_AUTO = "FILELOCK_AUTO";

    public static final String CAPS_ON = "CAPS_ON";
    
    public static final String HISTORY_MONTHS = "HISTORY_MONTHS";
    public static final String HISTORY_VERSIONS = "HISTORY_VERSIONS";
    
    public static final String XSDOSRG_LIBPATH = "XSDOSRG_LIBPATH";
    
    private ZdevPreferenceConstants() {
    	// Prevent unintentional instanciation
    }
}
