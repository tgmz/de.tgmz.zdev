/*********************************************************************
* Copyright (c) 06.10.2023 Thomas Zierer
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package de.tgmz.zdev.editor.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.junit.Test;
import org.mockito.Mockito;

import com.ibm.cics.zos.ui.editor.jcl.ColorManager;

import de.tgmz.zdev.editor.ZdevColorManager;
import de.tgmz.zdev.editor.assembler.AssemblerConfiguration;
import de.tgmz.zdev.editor.cobol.COBOLConfiguration;
import de.tgmz.zdev.editor.jcl.JCLConfiguration;
import de.tgmz.zdev.editor.pli.PLIConfiguration;
import de.tgmz.zdev.editor.rexx.RexxConfiguration;
import de.tgmz.zdev.editor.sql.SqlConfiguration;


public class ConfigurationTest {
	private static ZdevColorManager cm =  new ZdevColorManager();
	private static ISourceViewer sv =  Mockito.mock(ISourceViewer.class);
	@Test
	public void testPliConfigurations() {
		SourceViewerConfiguration svc = new PLIConfiguration(cm);
		
		assertEquals(2, svc.getConfiguredContentTypes(sv).length);
		assertNotNull(svc.getPresentationReconciler(sv));
		testContentAssist(svc);
	}
	
	@Test
	public void testCobolConfigurations() {
		SourceViewerConfiguration svc = new COBOLConfiguration(cm);
		
		assertEquals(1, svc.getConfiguredContentTypes(sv).length);
		assertNotNull(svc.getPresentationReconciler(sv));
		assertNull(svc.getTextHover(sv, IDocument.DEFAULT_CONTENT_TYPE));
		testContentAssist(svc);
	}
	
	@Test
	public void testSqlConfigurations() {
		SourceViewerConfiguration svc = new SqlConfiguration(cm);
		
		assertEquals(1, svc.getConfiguredContentTypes(sv).length);
		assertNotNull(svc.getPresentationReconciler(sv));
		assertNull(svc.getTextHover(sv, IDocument.DEFAULT_CONTENT_TYPE));
		testContentAssist(svc);
	}
	
	@Test
	public void testAssemblerConfigurations() {
		SourceViewerConfiguration svc = new AssemblerConfiguration(cm);
		
		assertEquals(1, svc.getConfiguredContentTypes(sv).length);
		assertNotNull(svc.getPresentationReconciler(sv));
		assertNull(svc.getTextHover(sv, IDocument.DEFAULT_CONTENT_TYPE));
	}
	
	@Test
	public void testJclConfigurations() {
		SourceViewerConfiguration svc = new JCLConfiguration(Mockito.mock(ColorManager.class));
		
		assertEquals(1, svc.getConfiguredContentTypes(sv).length);
		assertNotNull(svc.getPresentationReconciler(sv));
		assertNull(svc.getTextHover(sv, IDocument.DEFAULT_CONTENT_TYPE));
	}
	
	@Test
	public void testRexxConfigurations() {
		SourceViewerConfiguration svc = new RexxConfiguration(cm);
		
		assertEquals(1, svc.getConfiguredContentTypes(sv).length);
		assertNotNull(svc.getPresentationReconciler(sv));
		assertNull(svc.getTextHover(sv, IDocument.DEFAULT_CONTENT_TYPE));
		testContentAssist(svc);
	}
	
	private void testContentAssist(SourceViewerConfiguration svc) {
		IContentAssistant ca = svc.getContentAssistant(sv);
		IContentAssistProcessor cap = ca.getContentAssistProcessor(IDocument.DEFAULT_CONTENT_TYPE);
		ICompletionProposal[] cp = cap.computeCompletionProposals(sv, 0);

		assertTrue(cp.length > 0);
	}
}
