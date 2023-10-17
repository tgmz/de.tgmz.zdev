/*********************************************************************
* Copyright (c) 10.10.2023 Thomas Zierer
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package de.tgmz.zdev.editor.sql;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.EndOfLineRule;
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
 * SQL-Scanner.
 */
public class SqlScanner extends RuleBasedScanner {
	@SuppressWarnings("unused")
	private static final Logger LOG = LoggerFactory.getLogger(SqlScanner.class);

	public SqlScanner(ZdevColorManager manager) {
		List<IRule> rules = new ArrayList<>();

		rules.add(new MultiLineRule("\'","\'", new Token(new TextAttribute(manager.getSwtColor(ZdevColor.LITERAL))), (char) 0, false));
		rules.add(new WhitespaceRule(new DefaultWhitespaceDetector()));
		rules.add(new EndOfLineRule("--", new Token(new TextAttribute(manager.getSwtColor(ZdevColor.COMMENT)))));
		rules.add(new NumberRule(new Token(new TextAttribute(manager.getSwtColor(ZdevColor.NUMERIC)))));

		WordRule keywords = new WordRule(new SqlWordDetector(), new Token(new TextAttribute(manager.getSwtColor(ZdevColor.DEFAULT))), true);

		IToken keywordToken = new Token(new TextAttribute(manager.getSwtColor(ZdevColor.KEYWORD)));
		
		for (ReservedWords rw : ReservedWords.values()) {
			keywords.addWord(rw.toString(), keywordToken);
		}

		rules.add(keywords);

		setRules(rules.toArray(new IRule[rules.size()]));
	}
}
