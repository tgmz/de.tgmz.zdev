/*********************************************************************
* Copyright (c) 10.10.2023 Thomas Zierer
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package de.tgmz.zdev.editor.assembler;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.NumberRule;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WhitespaceRule;
import org.eclipse.jface.text.rules.WordRule;

import de.tgmz.zdev.editor.ZdevColorManager;
import de.tgmz.zdev.editor.ZdevColor;
import de.tgmz.zdev.editor.DefaultWhitespaceDetector;

/**
 * Assembler-Scanner.
 */
public class AssemblerScanner extends RuleBasedScanner {

	public AssemblerScanner(ZdevColorManager manager) {
		List<IRule> rules = new ArrayList<>();
		
		IToken defaultToken = new Token(new TextAttribute(manager.getSwtColor(ZdevColor.DEFAULT)));
		IToken keywordToken = new Token(new TextAttribute(manager.getSwtColor(ZdevColor.KEYWORD)));
		IToken numericToken = new Token(new TextAttribute(manager.getSwtColor(ZdevColor.NUMERIC)));
		IToken literalToken = new Token(new TextAttribute(manager.getSwtColor(ZdevColor.LITERAL)));
		IToken commentToken = new Token(new TextAttribute(manager.getSwtColor(ZdevColor.COMMENT)));
		
		rules.add(new AssemblerCommentRule(commentToken));
		
		rules.add(new WhitespaceRule(new DefaultWhitespaceDetector()));
		
		WordRule keywords = new WordRule(new AssemblerWordDetector(), defaultToken, true);
		keywords.addWord("AINSERT", keywordToken);
		keywords.addWord("CNOP", keywordToken);
		keywords.addWord("COPY", keywordToken);
		keywords.addWord("END", keywordToken);
		keywords.addWord("EXITCTL", keywordToken);
		keywords.addWord("ICTL", keywordToken);
		keywords.addWord("ISEQ", keywordToken);
		keywords.addWord("LTORG", keywordToken);
		keywords.addWord("ORG", keywordToken);
		keywords.addWord("POP", keywordToken);
		keywords.addWord("PUNCH", keywordToken);
		keywords.addWord("PUSH", keywordToken);
		keywords.addWord("REPRO", keywordToken);
		keywords.addWord("CEJECT", keywordToken);
		keywords.addWord("EJECT", keywordToken);
		keywords.addWord("PRINT", keywordToken);
		keywords.addWord("SPACE", keywordToken);
		keywords.addWord("TITLE", keywordToken);
		keywords.addWord("OPSYN", keywordToken);
		keywords.addWord("ALIAS", keywordToken);
		keywords.addWord("AMODE", keywordToken);
		keywords.addWord("CATTR", keywordToken);
		keywords.addWord("COM", keywordToken);
		keywords.addWord("CSECT", keywordToken);
		keywords.addWord("CXD", keywordToken);
		keywords.addWord("DSECT", keywordToken);
		keywords.addWord("DXD", keywordToken);
		keywords.addWord("ENTRY", keywordToken);
		keywords.addWord("EXTRN", keywordToken);
		keywords.addWord("LOCTR", keywordToken);
		keywords.addWord("RMODE", keywordToken);
		keywords.addWord("RSECT", keywordToken);
		keywords.addWord("START", keywordToken);
		keywords.addWord("WXTRN", keywordToken);
		keywords.addWord("XATTR", keywordToken);
		keywords.addWord("DROP", keywordToken);
		keywords.addWord("USING", keywordToken);
		keywords.addWord("CCW", keywordToken);
		keywords.addWord("CCW0", keywordToken);
		keywords.addWord("CCW1", keywordToken);
		keywords.addWord("DC", keywordToken);
		keywords.addWord("DS", keywordToken);
		keywords.addWord("EQU", keywordToken);
		keywords.addWord("ADATA", keywordToken);
		keywords.addWord("*PROCESS", keywordToken);
		keywords.addWord("ACONTROL", keywordToken);
		
		rules.add(keywords);
		
		rules.add(new SingleLineRule("\"","\"", literalToken, (char) 0, true));
		rules.add(new SingleLineRule("\'","\'", literalToken, (char) 0, true));
		
		rules.add(new NumberRule(numericToken));
		
		setRules(rules.toArray(new IRule[rules.size()]));
	}
}

