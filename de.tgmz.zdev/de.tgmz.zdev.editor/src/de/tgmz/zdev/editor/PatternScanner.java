/*********************************************************************
* Copyright (c) 10.10.2023 Thomas Zierer
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package de.tgmz.zdev.editor;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Position;

/**
 * Splits a document in partitions based on patterns.
 */
public class PatternScanner {
	private static final PatternScanner INSTANCE = new PatternScanner();

	public static PatternScanner getInstance() {
		return INSTANCE;
	}
	
	public List<Position> calculatePositions(IDocument document) {
		Pattern pattern = Activator.getDefault().getPattern();
		
		if (pattern == null) {
			return Collections.emptyList();
		}
		
		List<Position> result = new LinkedList<>();
		
		Matcher m = pattern.matcher(document.get());
		
		Position p = new Position(0);
		
		while (m.find()) {
			p.length = m.start() - p.offset;
				
			result.add(p);
				
			p = new Position(m.end());
		}
			
		p.length = document.getLength() - p.offset;
			
		result.add(p);
			
		return result;
	}
}
