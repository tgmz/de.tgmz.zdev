/*********************************************************************
* Copyright (c) 06.10.2023 Thomas Zierer
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package de.tgmz.zdev.editor.pli;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.PatternRule;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WhitespaceRule;
import org.eclipse.jface.text.rules.WordPatternRule;
import org.eclipse.jface.text.rules.WordRule;

import de.tgmz.zdev.editor.DefaultWhitespaceDetector;
import de.tgmz.zdev.editor.ZdevColor;
import de.tgmz.zdev.editor.ZdevColorManager;

/**
 * PL/I-Scanner.
 */
public class PLIScanner extends RuleBasedScanner {

	public PLIScanner(ZdevColorManager manager) {
		List<IRule> rules = new ArrayList<>();
		
		IToken defaultToken = new Token(new TextAttribute(manager.getSwtColor(ZdevColor.DEFAULT)));
		IToken keywordToken = new Token(new TextAttribute(manager.getSwtColor(ZdevColor.KEYWORD)));
		IToken numericToken = new Token(new TextAttribute(manager.getSwtColor(ZdevColor.NUMERIC)));
		IToken literalToken = new Token(new TextAttribute(manager.getSwtColor(ZdevColor.LITERAL)));
		IToken commentToken = new Token(new TextAttribute(manager.getSwtColor(ZdevColor.COMMENT)));
		IToken builtinToken = new Token(new TextAttribute(manager.getSwtColor(ZdevColor.BUILTIN)));
		IToken preprocToken = new Token(new TextAttribute(manager.getSwtColor(ZdevColor.PREPROC), null, TextAttribute.UNDERLINE));
		
		rules.add(new PliNumberRule(numericToken));
		
		rules.add(new MultiLineRule("\"", "\"", literalToken, (char) 0, false));
		rules.add(new MultiLineRule("\'", "\'", literalToken, (char) 0, false));
		
		rules.add(new MultiLineRule("/*", "*/", commentToken, (char) 0, false));
		rules.add(new PatternRule("//", null, commentToken, (char) 0, true));
		
		rules.add(new WhitespaceRule(new DefaultWhitespaceDetector()));
		
		WordRule keywords = new WordRule(new PLIWordDetector(), defaultToken, true);

		for (ReservedWord kw : ReservedWord.values()) {
			keywords.addWord(kw.toString(), keywordToken);
		}
		
		for (PLIBuiltin bi : PLIBuiltin.values()) {
			keywords.addWord(bi.toString(), builtinToken);
		}
		
		rules.add(keywords);
		
		WordPatternRule preProc = new WordPatternRule(new PreProcInstructionDetector(), "%", "", preprocToken, (char) 0);
		
		rules.add(preProc);
		
		setRules(rules.toArray(new IRule[rules.size()]));
	}
}
