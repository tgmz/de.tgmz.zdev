/*********************************************************************
* Copyright (c) 09.10.2023 Thomas Zierer
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package de.tgmz.zdev.outline;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.eclipse.core.runtime.IAdaptable;

/**
 * Parser for PL/I- Outline-View.
 */

//CHECKSTYLE DISABLE LineLength
//CHECKSTYLE DISABLE EqualsAvoidNull
//CHECKSTYLE DISABLE CyclomaticComplexity
//CHECKSTYLE DISABLE NPathComplexity
//CHECKSTYLE DISABLE EmptyStatement
//CHECKSTYLE DISABLE ReturnCount
public class PliOutlineParser implements IOutlineParser {
	private List<MarkElement> topLevel;
	private String testString;
	private int startIndex;
	private int workIndex;
	private int newLineIndex;
	private long lineNo;
	private MarkElement currentME;
	private boolean execSqlActive;
	private int execSqlAt;
	private int endExecAt;
	private boolean commentActive;
	private int commentStartAt;
	private int commentEndAt;

	@Override
	public MarkElement[] parse(IAdaptable adaptable, String fileContents) {
		String fileString = fileContents;
		topLevel = new ArrayList<>();
		startIndex = 0;
		workIndex = startIndex;
		newLineIndex = fileString.indexOf('\n', startIndex);
		lineNo = 0L;
		currentME = null;
		MarkElement procElement = null;
		execSqlActive = false;
		commentActive = false;
		endExecAt = -1;
		commentEndAt = -1;
		
		while (newLineIndex != -1) {
			if (commentActive && commentEndAt != -1) {
				commentActive = false;
			}
				
			if (execSqlActive && endExecAt != -1) {
				execSqlActive = false;
			}
			
			lineNo++;
			
			if (lineNo > 1L) {
				startIndex = newLineIndex + 1;
				newLineIndex = fileString.indexOf('\n', startIndex);
			}
			
			if (newLineIndex > startIndex + 1) {
				if (newLineIndex - startIndex <= 72) {
					testString = fileString.substring(startIndex, newLineIndex);
				} else {
					testString = fileString.substring(startIndex, startIndex + 72);
				}
				
				execSqlAt = testString.toLowerCase(Locale.ROOT).indexOf("exec sql");
				endExecAt = testString.indexOf(';');
				
				if (execSqlAt != -1) {
					execSqlActive = true;
				}
				
				commentStartAt = testString.indexOf("/*");
				commentEndAt = testString.indexOf("*/");
				
				if (commentStartAt != -1) {
					commentActive = true;
				}
				
				int testStringIndex = 0;
				workIndex = testString.indexOf(':', testStringIndex);
				
				if (workIndex == -1) {
					//CHECKSTYLE DISABLE NeedBraces for 2 lines
					for (workIndex = 0; workIndex < testString.length() && Character.isWhitespace(testString.charAt(workIndex)); workIndex++)
						;

					if (isApplicabable("ON") || isApplicabable("REVERT") || isApplicabable("PROC") || isApplicabable("PROCEDURE")) {
						handleSubElement(adaptable, fileString, procElement);
					}
				} else {
					workIndex++;
					if (isColonPartOfLabel(workIndex, testString)) {
						//CHECKSTYLE DISABLE NeedBraces for 2 lines
						for (; workIndex < testString.length() && Character.isWhitespace(testString.charAt(workIndex)); workIndex++)
							;

						if (workIndex >= testString.length()) {
							int nextLineStartIndex = newLineIndex + 1;
							int nextLineNewLineIndex = fileString.indexOf('\n', nextLineStartIndex);
							if (nextLineNewLineIndex < 0) {
								testString = (new StringBuilder(String.valueOf(testString))).append(" ").append(fileString.substring(nextLineStartIndex)).toString();
							} else {
								testString = (new StringBuilder(String.valueOf(testString))).append(" ").append(fileString.substring(nextLineStartIndex, nextLineNewLineIndex)).toString();
							}
							
							//CHECKSTYLE DISABLE NeedBraces for 2 lines
							for (; workIndex < testString.length() && Character.isWhitespace(testString.charAt(workIndex)); workIndex++)
								;
						}
						int endIndex = newLineIndex - 1;
						if (endIndex - startIndex > 72) {
							endIndex = startIndex + 72;
						}
						
						String idHeader = fileString.substring(startIndex, endIndex);
						
						boolean containsProc = idHeader.contains("PROC");
						
						idHeader = removeSpecialChars(idHeader);
						if (procElement != null && !containsProc) {
							currentME = new MarkElement(currentME, procElement, idHeader, (int) lineNo, newLineIndex - workIndex);
						} else {
							currentME = new MarkElement(currentME, adaptable, idHeader, (int) lineNo, newLineIndex - workIndex);
							topLevel.add(currentME);
							if (containsProc) {
								procElement = currentME;
							}
						}
					}
				}
			}
		}
		if (currentME != null) {
			currentME.setNumberOfLines(((int) lineNo - currentME.getStart()) + 1);
		}
		
		return topLevel.toArray(new MarkElement[topLevel.size()]);
	}

	private boolean isApplicabable(String s) {
		return testString.length() - workIndex >= s.length() && testString.substring(workIndex, workIndex + s.length()).equalsIgnoreCase(s);
	}

	private void handleSubElement(IAdaptable adaptable, String fileString, MarkElement procElement) {
		int endIndex = newLineIndex - 1;
		if (endIndex - startIndex > 72) {
			endIndex = startIndex + 72;
		}
		String idHeader = fileString.substring(startIndex, endIndex);
		idHeader = removeSpecialChars(idHeader);
		if (procElement != null) {
			currentME = new MarkElement(currentME, procElement, idHeader, (int) lineNo, newLineIndex - workIndex);
		} else {
			currentME = new MarkElement(currentME, adaptable, idHeader, (int) lineNo, newLineIndex - workIndex);
			topLevel.add(currentME);
		}
	}

	/**
	 * Removes carriage return, line feed and print control characters.
	 * @param line code line
	 * @return the processed line 
	 */
	private static String removeSpecialChars(String line) {
		String retString;
		
		retString = line.replace('\r', ' ').replace('\n', ' ').trim();
		
		// Remove print control characters
		char first = line.charAt(0);

		if (Character.isDigit(first) || first == '+' || first == '-') {
			retString = retString.substring(1);
		}

		return retString;
	}

	private boolean isColonPartOfLabel(int aWorkIndex, String aTestString) {
		if (isContained(aWorkIndex, aTestString, "/*",  "*/")) {
			return false;
		}
		
		if (isContained(aWorkIndex, aTestString, "'",  "'")) {
			return false;
		}
		
		if (isContained(aWorkIndex, aTestString, "\"",  "\"")) {
			return false;
		}
		
		if (isContained(aWorkIndex, aTestString, "(",  ")") && aTestString.indexOf("Y2L") == -1) {
			return false;
		}
		
		if (commentActive && (commentStartAt != -1 && aWorkIndex >= commentStartAt) && (commentEndAt != -1 && aWorkIndex <= commentEndAt)) {
			return false;
		}
		
		if (aWorkIndex < aTestString.length()) {
			char followingChar = aTestString.charAt(aWorkIndex);
			if (followingChar != ' ' && execSqlActive) {
				return execSqlAt != -1 && aWorkIndex <= execSqlAt;
			}
		}
		
		return true;
	}
	private boolean isContained(int aWorkIndex, String aTestString, String beginDelimiter, String endDelimiter) {
		int startComment = aTestString.indexOf(beginDelimiter);
		int endComment = aTestString.indexOf(endDelimiter, aWorkIndex);
		
		return startComment > 0 && startComment < aWorkIndex && endComment != -1;
	}
}