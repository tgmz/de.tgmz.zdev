/*********************************************************************
* Copyright (c) 06.10.2023 Thomas Zierer
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package de.tgmz.zdev.editor.sql;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.source.ISourceViewer;

import de.tgmz.zdev.editor.ReservedWordCompletionProcessor;
import de.tgmz.zdev.editor.ZdevColor;
import de.tgmz.zdev.editor.ZdevColorManager;
import de.tgmz.zdev.editor.ZdevSourceViewerConfiguration;

/**
 * SQL editor configuration.
 */
public class SqlConfiguration extends ZdevSourceViewerConfiguration {
	private SqlScanner scanner;

	public SqlConfiguration(ZdevColorManager colorManager) {
		super(colorManager);
	}

	@Override
	protected SqlScanner getScanner() {
		if (scanner == null) {
			scanner = new SqlScanner(getColorManager());
			scanner.setDefaultReturnToken(
				new Token(
					new TextAttribute(getColorManager().getSwtColor(ZdevColor.DEFAULT))));
		}
		
		return scanner;
	}
	
	@Override
	public IContentAssistant getContentAssistant(ISourceViewer sourceViewer) {
		ContentAssistant ca = new ContentAssistant();
		IContentAssistProcessor cap = new ReservedWordCompletionProcessor(new SqlWordDetector(), ReservedWords.getCompletionProposals());
		ca.setContentAssistProcessor(cap, IDocument.DEFAULT_CONTENT_TYPE);
		ca.setInformationControlCreator(getInformationControlCreator(sourceViewer));
		return ca;
	}
}