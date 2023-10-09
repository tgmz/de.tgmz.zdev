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

import org.eclipse.core.runtime.IAdaptable;

/**
 * JCL parser for OutlineView.
 */

public class JclOutlineParser implements IOutlineParser {
	private MarkElement execElement;
	private MarkElement currentElement;

	@Override
	public MarkElement[] parse(IAdaptable adaptable, String fileContents) {
		List<MarkElement> topLevel;
		
		String[] lines = fileContents.split("\n");
		
		topLevel = new ArrayList<>();
		
		for (int i = 0; i < lines.length; i++) {
			if (lines[i].startsWith("//") && lines[i].length() > 2 && lines[i].charAt(2) != '*') {
				if (lines[i].contains(" EXEC ") || lines[i].contains(" JOB ")) {
					execElement = new MarkElement(execElement, adaptable, lines[i], i, lines[i].trim().length());
					
					topLevel.add(execElement);
				
					currentElement = execElement;
				} else {
					if (lines[i].contains(" DD ") || lines[i].contains(" INCLUDE ")) {
						currentElement = new MarkElement(currentElement, execElement, lines[i], i, lines[i].trim().length());
					}
				}
			}
		}
		
		return topLevel.toArray(new MarkElement[topLevel.size()]);
	}
}