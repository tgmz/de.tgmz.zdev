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
import static org.junit.Assert.assertNull;

import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.AnnotationModel;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.ISourceViewer;
import org.junit.Test;
import org.mockito.Mockito;

import de.tgmz.zdev.editor.annotation.CompilerMessageAnnotation;
import de.tgmz.zdev.editor.annotation.CompilerMessageAnnotationHover;

public class CompilerMessageAnnotationTest {
	private static final String MESSAGE = "IBM1091I W FIXED BIN precision less than storage allows";
	
	@Test
	public void test() {
		CompilerMessageAnnotation cma = new CompilerMessageAnnotation(1);
		cma.setText(MESSAGE);
				
		IAnnotationModel am = new AnnotationModel();
		am.addAnnotation(cma, new Position(0));
		
		ISourceViewer sv = Mockito.mock(ISourceViewer.class);
		Mockito.when(sv.getAnnotationModel()).thenReturn(am);
		
		CompilerMessageAnnotationHover cmah = new CompilerMessageAnnotationHover();
		
		assertEquals(MESSAGE, cmah.getHoverInfo(sv, 1));
		assertNull(cmah.getHoverInfo(sv, 2));
	}
}
