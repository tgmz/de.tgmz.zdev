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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collection;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.mockito.Mockito;

import com.ibm.cics.zos.ui.editor.jcl.ColorManager;

import de.tgmz.zdev.editor.ZdevColorManager;
import de.tgmz.zdev.editor.assembler.AssemblerConfiguration;
import de.tgmz.zdev.editor.cobol.COBOLConfiguration;
import de.tgmz.zdev.editor.cpp.CppConfiguration;
import de.tgmz.zdev.editor.jcl.JCLConfiguration;
import de.tgmz.zdev.editor.pli.PLIConfiguration;
import de.tgmz.zdev.editor.rexx.RexxConfiguration;
import de.tgmz.zdev.editor.sql.SqlConfiguration;

@RunWith(value = Parameterized.class)
public class ConfigurationTest {
	private static ZdevColorManager cm =  new ZdevColorManager();
	private static ISourceViewer sv =  Mockito.mock(ISourceViewer.class);
	
	private Class<SourceViewerConfiguration> svcc;
	private int configuredContentTypes;
	private boolean hasContentAssist;
	
	public ConfigurationTest(Class<SourceViewerConfiguration> svcc, int configuredContentTypes, boolean hasContentAssist) {
		super();
		this.svcc = svcc;
		this.configuredContentTypes = configuredContentTypes;
		this.hasContentAssist = hasContentAssist;
	}

	@Test
	public void testConfiguration() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		SourceViewerConfiguration svc;
		
		try {
			// Default
			svc = svcc.getConstructor(ZdevColorManager.class).newInstance(cm);
		} catch (NoSuchMethodException e) {
			// For JCLConfiguration
			svc = svcc.getConstructor(ColorManager.class).newInstance(Mockito.mock(ColorManager.class));
		}
		
		assertEquals(configuredContentTypes, svc.getConfiguredContentTypes(sv).length);
		assertNotNull(svc.getPresentationReconciler(sv));
		
		if (hasContentAssist) {
			testContentAssist(svc);
		}
	}
	
	private void testContentAssist(SourceViewerConfiguration svc) {
		IContentAssistant ca = svc.getContentAssistant(sv);
		IContentAssistProcessor cap = ca.getContentAssistProcessor(IDocument.DEFAULT_CONTENT_TYPE);
		ICompletionProposal[] cp = cap.computeCompletionProposals(sv, 0);

		assertTrue(cp.length > 0);
	}
	
	@Parameters(name = "{index}: Parser [{0}]")
	public static Collection<Object[]> data() {
		Object[][] data = new Object[][] {
				{ PLIConfiguration.class, 2, true},
				{ COBOLConfiguration.class, 1, true},
				{ SqlConfiguration.class, 1, true},
				{ AssemblerConfiguration.class, 1, false},
				{ JCLConfiguration.class, 1, false},
				{ RexxConfiguration.class, 1, true},
				{ CppConfiguration.class, 1, true},
				};
		return Arrays.asList(data);
	}
}
