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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.rules.FastPartitioner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.cics.zos.ui.editor.DataEntryDocumentProvider;

/**
 * Document provider for PL/I programms.
 */
public class PLIDocumentProvider extends DataEntryDocumentProvider {
	private static final Logger LOG = LoggerFactory.getLogger(PLIDocumentProvider.class);

	@Override
	public IDocument createDocument(Object element) throws CoreException {
		LOG.debug("Element: {}", element);
		
		IDocument document = super.createDocument(element);
		if (document != null) {
			IDocumentPartitioner partitioner =
				new FastPartitioner(
					new PLIPartitionScanner(),
					new String[] {
						PLIPartitionScanner.PLI_COMMENT});
			partitioner.connect(document);
			document.setDocumentPartitioner(partitioner);
		}
		return document;
	}
}