/*********************************************************************
* Copyright (c) 06.10.2023 Thomas Zierer
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package de.tgmz.zdev.editor.rexx;

import java.util.LinkedList;
import java.util.List;

import de.tgmz.zdev.editor.ZdevCompletionProposal;

/**
 * REXX key words. enum to identify doubles quickly.
 */
public enum ReservedWords {
	//CHECKSTYLE DISABLE LineLength
	ADDRESS,
	ARG,
	CALL,
	DO,
	WHILE,
	UNTIL,
	DROP,
	EXIT,
	IF,
	INTERPRET,
	ITERATE,
	LEAVE,
	NOP,
	NUMERIC,
	OPTIONS,
	PARSE,
	PROCEDURE,
	PULL,
	PUSH,
	QUEUE,
	RETURN,
	SAY,
	SELECT,
	SIGNAL,
	TRACE,
	UPPER;
	//CHECKSTYLE ENABLE LineLength
	
	public static ZdevCompletionProposal[] getCompletionProposals() {
		List<ZdevCompletionProposal> l = new LinkedList<>();
		
		for (ReservedWords rw : values()) {
			l.add(new ZdevCompletionProposal(rw.toString(), rw.toString(),  null));
		}
		
		return l.toArray(new ZdevCompletionProposal[l.size()]);
	}
}
