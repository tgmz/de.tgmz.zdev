/*********************************************************************
* Copyright (c) 10.10.2023 Thomas Zierer
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package de.tgmz.zdev.editor.jcl;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;

import com.ibm.cics.zos.ui.editor.jcl.ColorManager;
import com.ibm.cics.zos.ui.editor.jcl.JCLPartitionScanner;

/**
 * Integrates IBMs JCL editor into z/Dev.
 * @see com.ibm.cics.zos.ui.editor.jcl.JCLConfiguration
 */
public class JCLConfiguration extends SourceViewerConfiguration {
	private JCLPartitionScanner scanner;
	private final ColorManager colorManager;

	public JCLConfiguration(ColorManager colorManager) {
		this.colorManager = colorManager;
	}
	@Override
	public String[] getConfiguredContentTypes(ISourceViewer sourceViewer) {
		return new String[] {
			IDocument.DEFAULT_CONTENT_TYPE};
	}

	protected JCLPartitionScanner getScanner() {
		if (scanner == null) {
			scanner = new JCLPartitionScanner(colorManager);
		}
		return scanner;
	}

	@Override
	public IPresentationReconciler getPresentationReconciler(ISourceViewer sourceViewer) {
		PresentationReconciler reconciler = new PresentationReconciler();

		DefaultDamagerRepairer dr = new DefaultDamagerRepairer(getScanner());
		reconciler.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
		reconciler.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);

		return reconciler;
	}

}