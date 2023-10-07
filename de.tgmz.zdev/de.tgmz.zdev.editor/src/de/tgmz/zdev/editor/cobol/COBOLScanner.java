/*********************************************************************
* Copyright (c) 06.10.2023 Thomas Zierer
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package de.tgmz.zdev.editor.cobol;

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
 * COBOL-Scanner.
 */
public class COBOLScanner extends RuleBasedScanner {
	@SuppressWarnings("unused")
	private static final Logger LOG = LoggerFactory.getLogger(COBOLScanner.class);

	public COBOLScanner(ZdevColorManager manager) {
		List<IRule> rules = new ArrayList<>();
		
		final IToken defaultToken = new Token(new TextAttribute(manager.getSwtColor(ZdevColor.DEFAULT)));
		IToken keywordToken = new Token(new TextAttribute(manager.getSwtColor(ZdevColor.KEYWORD)));
		IToken numericToken = new Token(new TextAttribute(manager.getSwtColor(ZdevColor.NUMERIC)));
		IToken literalToken = new Token(new TextAttribute(manager.getSwtColor(ZdevColor.LITERAL)));
		final IToken commentToken = new Token(new TextAttribute(manager.getSwtColor(ZdevColor.COMMENT)));
		
		rules.add(new MultiLineRule("\"","\"", literalToken, (char) 0, false));
		rules.add(new MultiLineRule("\'","\'", literalToken, (char) 0, false));
		
		rules.add(new WhitespaceRule(new DefaultWhitespaceDetector()));
		
		PatternRule pr = new PatternRule("*", null, commentToken, (char) 0, true);
		pr.setColumnConstraint(6);
		
		rules.add(pr);
		
		WordRule keywords = new WordRule(new COBOLWordDetector(), defaultToken, true);
	
		for (ReservedWords w : ReservedWords.values()) {
			keywords.addWord(w.toString(), keywordToken);
		}
		
		rules.add(keywords);
		
		rules.add(new NumberRule(numericToken));
		
		setRules(rules.toArray(new IRule[rules.size()]));
	}
}
