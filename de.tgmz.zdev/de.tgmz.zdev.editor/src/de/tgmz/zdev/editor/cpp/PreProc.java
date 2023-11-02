/*********************************************************************
* Copyright (c) 10.10.2023 Thomas Zierer
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package de.tgmz.zdev.editor.cpp;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import de.tgmz.zdev.editor.ZdevCompletionProposal;

/**
 * C preprocessor functions.
 */
public enum PreProc {
	IF, PRAGMA, INCLUDE, ENDIF, ERROR, DEFINE, LINE;
	
	@Override
	public String toString() {
		return "#" + super.toString().toLowerCase(Locale.getDefault());
	}

	public static ZdevCompletionProposal[] getCompletionProposals() {
		List<ZdevCompletionProposal> l = new LinkedList<>();
		
		for (PreProc pp : values()) {
			l.add(new ZdevCompletionProposal(pp.toString(),  pp.toString(),  null));
		}
		
		return l.toArray(new ZdevCompletionProposal[l.size()]);
	}
}
