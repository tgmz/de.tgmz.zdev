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
    /** Konstante für den LogLevel. */
    public static final String INFO_ONLY_ON_SUBMIT = "infoOnlyOnSubmit";
    /** Konstante für den LogLevel. */
    public static final String INFO_TIME = "infoTime";
    /** Konstante für den LogLevel. */
    public static final String LOG_LEVEL = "logLevel";
    /** Konstante für das Job-Präfix. */
    public static final String JOB_SUFFIX = "jobSuffix";
    /** Konstante für die Job-Karte. */
    public static final String JOB_CARD = "jobCard";

    public static final String REGEX_PLI = "regexPli";
    public static final String REGEX_JCL = "regexJcl";
    public static final String REGEX_COBOL = "regexCob";
    public static final String REGEX_SQL = "regexSql";
    public static final String REGEX_ASSEMBLER = "regexAsm";
    public static final String REGEX_REXX = "regexRexx";
    
    public static final String CAPS_ON = "capsOn";
    
    public static final String HISTORY_MONTHS = "historyMonths";
    public static final String HISTORY_VERSIONS = "historyVersions";
    
    private ZdevPreferenceConstants() {
    	// Prevent unintentional instanciation
    }
}
