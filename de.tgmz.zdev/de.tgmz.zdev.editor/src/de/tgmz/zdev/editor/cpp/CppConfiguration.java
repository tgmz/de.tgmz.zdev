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
 * Configures editor for C.
 */
public class CppConfiguration extends ZdevSourceViewerConfiguration {
	private CppScanner scanner;

	public CppConfiguration(ZdevColorManager colorManager) {
		super(colorManager);
	}

	protected CppScanner getScanner() {
		if (scanner == null) {
			scanner = new CppScanner(getColorManager());
			scanner.setDefaultReturnToken(
				new Token(
					new TextAttribute(getColorManager().getSwtColor(ZdevColor.DEFAULT))));
		}
		
		return scanner;
	}

	@Override
	public IContentAssistant getContentAssistant(ISourceViewer sourceViewer) {
		ContentAssistant ca = new ContentAssistant();
		IContentAssistProcessor cap = new ReservedWordCompletionProcessor(new CppWordDetector(), ReservedWords.getCompletionProposals());
		ca.setContentAssistProcessor(cap, IDocument.DEFAULT_CONTENT_TYPE);
		ca.setInformationControlCreator(getInformationControlCreator(sourceViewer));
		return ca;
	}
}