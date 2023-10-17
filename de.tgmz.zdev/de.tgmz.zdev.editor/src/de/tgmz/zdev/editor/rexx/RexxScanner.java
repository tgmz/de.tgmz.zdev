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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.NumberRule;
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
 * REXX-Scanner.
 */
public class RexxScanner extends RuleBasedScanner {
	@SuppressWarnings("unused")
	private static final Logger LOG = LoggerFactory.getLogger(RexxScanner.class);

	public RexxScanner(ZdevColorManager manager) {
		List<IRule> rules = new ArrayList<>();

		IToken literalToken = new Token(new TextAttribute(manager.getSwtColor(ZdevColor.LITERAL)));
		IToken commentToken = new Token(new TextAttribute(manager.getSwtColor(ZdevColor.COMMENT)));
		IToken builtinToken = new Token(new TextAttribute(manager.getSwtColor(ZdevColor.BUILTIN)));

		rules.add(new MultiLineRule("\"", "\"", literalToken, (char) 0, false));
		rules.add(new MultiLineRule("\'", "\'", literalToken, (char) 0, false));

		rules.add(new MultiLineRule("/*", "*/", commentToken, (char) 0, false));
		
		rules.add(new WhitespaceRule(new DefaultWhitespaceDetector()));

		rules.add(new NumberRule(new Token(new TextAttribute(manager.getSwtColor(ZdevColor.NUMERIC)))));

		WordRule keywords = new WordRule(new RexxWordDetector(), new Token(new TextAttribute(manager.getSwtColor(ZdevColor.DEFAULT))), true);

		IToken keywordToken = new Token(new TextAttribute(manager.getSwtColor(ZdevColor.KEYWORD)));
		
		for (ReservedWords rw : ReservedWords.values()) {
			keywords.addWord(rw.toString(), keywordToken);
		}

		for (RexxBuiltin rb : RexxBuiltin.values()) {
			keywords.addWord(rb.toString(), builtinToken);
		}
		
		rules.add(keywords);

		setRules(rules.toArray(new IRule[rules.size()]));
	}
}
