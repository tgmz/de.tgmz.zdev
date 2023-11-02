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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.NumberRule;
import org.eclipse.jface.text.rules.PatternRule;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WhitespaceRule;
import org.eclipse.jface.text.rules.WordRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tgmz.zdev.editor.DefaultWhitespaceDetector;
import de.tgmz.zdev.editor.ZdevColor;
import de.tgmz.zdev.editor.ZdevColorManager;

/**
 * C-Scanner.
 */
public class CppScanner extends RuleBasedScanner {
	@SuppressWarnings("unused")
	private static final Logger LOG = LoggerFactory.getLogger(CppScanner.class);

	public CppScanner(ZdevColorManager manager) {
		List<IRule> rules = new ArrayList<>();

		IToken defaultToken = new Token(new TextAttribute(manager.getSwtColor(ZdevColor.DEFAULT)));
		IToken keywordToken = new Token(new TextAttribute(manager.getSwtColor(ZdevColor.KEYWORD)));
		IToken numericToken = new Token(new TextAttribute(manager.getSwtColor(ZdevColor.NUMERIC)));
		IToken literalToken = new Token(new TextAttribute(manager.getSwtColor(ZdevColor.LITERAL)));
		IToken commentToken = new Token(new TextAttribute(manager.getSwtColor(ZdevColor.COMMENT)));
		IToken preprocToken = new Token(new TextAttribute(manager.getSwtColor(ZdevColor.PREPROC), null, TextAttribute.UNDERLINE));
		
		rules.add(new MultiLineRule("\"", "\"", literalToken, (char) 0, false));
		rules.add(new MultiLineRule("\'", "\'", literalToken, (char) 0, false));
		
		rules.add(new MultiLineRule("/*", "*/", commentToken, (char) 0, false));
		rules.add(new PatternRule("//", null, commentToken, (char) 0, true));
		
		rules.add(new WhitespaceRule(new DefaultWhitespaceDetector()));
		
		rules.add(new NumberRule(numericToken));
		
		WordRule reservedWords = new WordRule(new CppWordDetector(), defaultToken, true);

		for (ReservedWords rw : ReservedWords.values()) {
			reservedWords.addWord(rw.toString(), keywordToken);
		}
		
		for (PreProc pp : PreProc.values()) {
			reservedWords.addWord(pp.toString(), preprocToken);
		}
		
		rules.add(reservedWords);
		
		setRules(rules.toArray(new IRule[rules.size()]));
	}
}
