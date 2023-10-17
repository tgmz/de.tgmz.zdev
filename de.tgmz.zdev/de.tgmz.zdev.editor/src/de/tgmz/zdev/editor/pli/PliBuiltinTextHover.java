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

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DefaultTextHover;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.source.ISourceViewer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple hover returning the description of a PL/I builtin function.
 */
public class PliBuiltinTextHover extends DefaultTextHover {
	private static final Logger LOG = LoggerFactory.getLogger(PliBuiltinTextHover.class);

	public PliBuiltinTextHover(ISourceViewer sourceViewer) {
		super(sourceViewer);
	}

	@Override
	public String getHoverInfo(ITextViewer textViewer, IRegion hoverRegion) {
		String s = ""; 
		try {
			s = textViewer.getDocument().get(hoverRegion.getOffset(), hoverRegion.getLength());
		} catch (BadLocationException e) {
			LOG.error("Bad location", e);
		}
		
		for (PLIBuiltin bi : PLIBuiltin.values()) {
			if (s.equalsIgnoreCase(bi.name())) {
				return PLIBuiltin.getDecsription(bi);
			}
		}
		
		return null;
	}
}
