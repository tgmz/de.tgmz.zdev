/*********************************************************************
* Copyright (c) 10.10.2023 Thomas Zierer
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package de.tgmz.zdev.editor.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import de.tgmz.zdev.editor.ZdevColorManager;
import de.tgmz.zdev.editor.pli.PLIBuiltin;
import de.tgmz.zdev.editor.pli.PLIConfiguration;
import de.tgmz.zdev.editor.pli.PliBuiltinTextHover;

/**
 * Test case for {@link PliBuiltinTextHover}. 
 */
public class PliHoverTest {
	private static final PLIBuiltin BI = PLIBuiltin.ABS;
	private static final IDocument doc = new TestDocument("x = " + BI + "(y);");
	private static ISourceViewer sv =  Mockito.mock(ISourceViewer.class);
	private static SourceViewerConfiguration svc = new PLIConfiguration(new ZdevColorManager());
	private static ITextHover textHover;

	@BeforeClass
	public static void setupOnce() {
		Mockito.when(sv.getDocument()).thenReturn(doc);
		
		textHover = svc.getTextHover(sv, IDocument.DEFAULT_CONTENT_TYPE);
	}
	
	@SuppressWarnings("deprecation")
	@Test
	public void testHover() {
		int idx = doc.get().indexOf(BI.name());
		
		IRegion hoverRegion = textHover.getHoverRegion(sv, idx);
		
		assertEquals(idx, hoverRegion.getOffset());
		assertEquals(BI.name().length(), hoverRegion.getLength());
		
		assertEquals(PLIBuiltin.getDecsription(BI), textHover.getHoverInfo(sv, hoverRegion));
	}
	@SuppressWarnings("deprecation")
	@Test
	public void testEmptyHover() {
		IRegion hoverRegion = textHover.getHoverRegion(sv, 0);
		
		assertNull(textHover.getHoverInfo(sv, hoverRegion));
	}
}
