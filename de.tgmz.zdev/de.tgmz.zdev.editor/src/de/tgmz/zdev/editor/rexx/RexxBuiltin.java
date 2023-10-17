/*********************************************************************
* Copyright (c) 10.10.2023 Thomas Zierer
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
import java.util.ResourceBundle;

import de.tgmz.zdev.editor.ZdevCompletionProposal;

/**
 * REXX builtins.
 */
public enum RexxBuiltin {
	ABBREV,
	ABS,
	ADDRESS,
	ARG,
	BITAND,
	BITOR,
	BITXOR,
	B2X,
	CENTER,
	CENTRE,
	COMPARE,
	CONDITION,
	COPIES,
	C2D,
	C2X,
	DATATYPE,
	DATE,
	DBCS,
	DELSTR,
	DELWORD,
	DIGITS,
	D2C,
	D2X,
	ERRORTEXT,
	EXTERNALS,
	FIND,
	FORM,
	FORMAT,
	FUZZ,
	GETMSG,
	INDEX,
	INSERT,
	JUSTIFY,
	LASTPOS,
	LENGTH,
	LINESIZE,
	LISTDSI,
	MAX,
	MIN,
	MSG,
	MVSVAR,
	OUTTRAP,
	OVERLAY,
	POS,
	PROMPT,
	QUEUED,
	RANDOM,
	REVERSE,
	RIGHT,
	SETLANG,
	SIGN,
	SOURCELINE,
	SPACE,
	STORAGE,
	STRIP,
	SUBSTR,
	SUBWORD,
	SYMBOL,
	SYSCPUS,
	SYSDSN,
	SYSVAR,
	TIME,
	TRACE,
	TRANSLATE,
	TRUNC,
	USERID,
	VALUE,
	VERIFY,
	WORD,
	WORDINDEX,
	WORDLENGTH,
	WORDPOS,
	WORDS,
	XRANGE,
	X2B,
	X2C,
	X2D,
	//TSO/E external functions
	//GETMSG,	LISTDSI,	MSG,	MVSVAR,	OUTTRAP,	PROMPT,	SETLANG,	STORAGE,	SYSCPUS,	SYSDSN,	SYSVAR,
	//Using the SYSPLANG and SYSSLANG arguments
	TRAPMSG
	;

	private static final ResourceBundle BUNLDE = ResourceBundle.getBundle("de/tgmz/zdev/builtin");
	
	public static ZdevCompletionProposal[] getCompletionProposals() {
		List<ZdevCompletionProposal> l = new LinkedList<>();
		
		for (RexxBuiltin rw : values()) {
			// Format: key = shortDescription$signature$longDescription
			// de.tgmz.zdev.editor.test.KeywordTest ensures that every BIF has a completion proposal
			String[] s0 = BUNLDE.getString(rw.toString()).split("\\$");
				
			l.add(new ZdevCompletionProposal(rw.toString(), s0[1], s0[0] + "\r\n" + "\r\n" + s0[2]));
		}
		
		return l.toArray(new ZdevCompletionProposal[l.size()]);
	}
}
