/*********************************************************************
* Copyright (c) 10.10.2023 Thomas Zierer
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package de.tgmz.zdev.editor.pli;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.reconciler.IReconciler;
import org.eclipse.jface.text.reconciler.MonoReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.source.ISourceViewer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tgmz.zdev.editor.NonRuleBasedDamagerRepairer;
import de.tgmz.zdev.editor.ReservedWordCompletionProcessor;
import de.tgmz.zdev.editor.ZdevColor;
import de.tgmz.zdev.editor.ZdevColorManager;
import de.tgmz.zdev.editor.ZdevCompletionProposal;
import de.tgmz.zdev.editor.ZdevEditor;
import de.tgmz.zdev.editor.ZdevReconcilingStrategy;
import de.tgmz.zdev.editor.ZdevSourceViewerConfiguration;

/**
 * Configures the editor for PL/I.
 */
public class PLIConfiguration extends ZdevSourceViewerConfiguration {
	private static final Logger LOG = LoggerFactory.getLogger(PLIConfiguration.class);
	private PLIScanner scanner;
	private ZdevEditor editor;

	public PLIConfiguration(ZdevColorManager colorManager) {
		super(colorManager);
	}
	@Override
	public String[] getConfiguredContentTypes(ISourceViewer sourceViewer) {
		return new String[] {
			IDocument.DEFAULT_CONTENT_TYPE,
			PLIPartitionScanner.PLI_COMMENT};
	}

	protected PLIScanner getScanner() {
		if (scanner == null) {
			scanner = new PLIScanner(getColorManager());
			scanner.setDefaultReturnToken(
				new Token(
					new TextAttribute(getColorManager().getSwtColor(ZdevColor.DEFAULT))));
		}
		return scanner;
	}

	@Override
	public IPresentationReconciler getPresentationReconciler(ISourceViewer sourceViewer) {
		PresentationReconciler reconciler = new PresentationReconciler();

		DefaultDamagerRepairer dr = new DefaultDamagerRepairer(getScanner());
		reconciler.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
		reconciler.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);
		
		NonRuleBasedDamagerRepairer ndr = new NonRuleBasedDamagerRepairer(new TextAttribute(getColorManager().getSwtColor(ZdevColor.COMMENT)));
		reconciler.setDamager(ndr, PLIPartitionScanner.PLI_COMMENT);
		reconciler.setRepairer(ndr, PLIPartitionScanner.PLI_COMMENT);

		return reconciler;
	}
	
	@Override
	public ITextHover getTextHover(ISourceViewer sourceViewer, String contentType) {
		return new PliBuiltinTextHover(sourceViewer);
	}
	
	@Override
	public IContentAssistant getContentAssistant(ISourceViewer sourceViewer) {
		ContentAssistant ca = new ContentAssistant();
		
		Set<ZdevCompletionProposal> rw = new HashSet<>(Arrays.asList(ReservedWord.getCompletionProposals()));
		rw.addAll(Arrays.asList(PLIBuiltin.getCompletionProposals()));
		
		ZdevCompletionProposal[] s = rw.toArray(new ZdevCompletionProposal[rw.size()]);
		
		Arrays.sort(s);
		
		IContentAssistProcessor cap = new ReservedWordCompletionProcessor(new PLIWordDetector(), s);
		ca.setContentAssistProcessor(cap, IDocument.DEFAULT_CONTENT_TYPE);
		ca.setInformationControlCreator(getInformationControlCreator(sourceViewer));
		return ca;
	}
	
	@Override
	public IReconciler getReconciler(ISourceViewer sourceViewer) {
		if (editor != null) {
			ZdevReconcilingStrategy strategy = new ZdevReconcilingStrategy();
			strategy.setEditor(editor);
        
			return new MonoReconciler(strategy, false);
		} else {
			LOG.error("Cannot provide reconciler as the editor is null");
		}
		
		return null;
    }
	public ZdevEditor getEditor() {
		return editor;
	}
	public void setEditor(ZdevEditor editor) {
		this.editor = editor;
	}
}