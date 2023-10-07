/*********************************************************************
* Copyright (c) 06.10.2023 Thomas Zierer
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package de.tgmz.zdev.editor;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;
import org.eclipse.jface.text.rules.IWordDetector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ContentAssistProcessor for key words.
 */
public class ReservedWordCompletionProcessor implements IContentAssistProcessor {
	private static final Logger LOG = LoggerFactory.getLogger(ReservedWordCompletionProcessor.class);
	private static final IContextInformation[] NO_CONTEXTS = {};
	private static final char[] PROPOSAL_ACTIVATION_CHARS = { 's', 'f', 'p', 'n', 'm', };
	@SuppressWarnings("unused")
	private static final ICompletionProposal[] NO_COMPLETIONS = {};
	private IWordDetector wordDetector;
	private ZdevCompletionProposal[] proposals;

	public ReservedWordCompletionProcessor(IWordDetector wordDetector, ZdevCompletionProposal[] proposals) {
		super();
		this.wordDetector = wordDetector;
		this.proposals = proposals;
	}

	@Override
	public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer, int offset) {
		IDocument document = viewer.getDocument();
		List<ICompletionProposal> result = new ArrayList<>();
		String prefix = lastWord(document, offset);

		for (ZdevCompletionProposal cp : proposals) {
			if (cp.getPattern().toLowerCase(Locale.ROOT).startsWith(prefix.toLowerCase(Locale.ROOT))) {
				result.add(new CompletionProposal(cp.getReplacementString()
												, offset - prefix.length() // replacementOffset
												, prefix.length() // replacementLength
												, cp.getReplacementString().length() // cursorPosition
												, null // image
												, cp.getReplacementString() // displayString
												, null // contextInformation
												, cp.getAdditionalProposalInfo()));
			}
		}

		return result.toArray(new ICompletionProposal[result.size()]);
	}

	private String lastWord(IDocument doc, int offset) {
		try {
			for (int n = offset - 1; n >= 0; n--) {
				char c = doc.getChar(n);
				if (!wordDetector.isWordPart(c)) {
					return doc.get(n + 1, offset - n - 1);
				}
			}
		} catch (BadLocationException e) {
			LOG.error("Bad location", e);
		}
		return "";
	}

	@Override
	public IContextInformation[] computeContextInformation(ITextViewer viewer, int offset) {
		return NO_CONTEXTS;
	}

	@Override
	public char[] getCompletionProposalAutoActivationCharacters() {
		return PROPOSAL_ACTIVATION_CHARS;
	}

	@Override
	public char[] getContextInformationAutoActivationCharacters() {
		return new char[0];
	}

	@Override
	public String getErrorMessage() {
		return Activator.getDefault().getString("ContentAssist.error");
	}

	@Override
	public IContextInformationValidator getContextInformationValidator() {
		return null;
	}
}
