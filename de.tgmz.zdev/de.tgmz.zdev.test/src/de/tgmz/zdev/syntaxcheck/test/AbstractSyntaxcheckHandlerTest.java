/*********************************************************************
* Copyright (c) 10.10.2023 Thomas Zierer
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package de.tgmz.zdev.syntaxcheck.test;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;
import org.junit.Test;
import org.junit.Test.None;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import de.tgmz.zdev.domain.Item;
import de.tgmz.zdev.editor.annotation.CompilerMessageAnnotation;
import de.tgmz.zdev.syntaxcheck.AbstractSyntaxcheckHandler;
import de.tgmz.zdev.xinfo.generated.MESSAGE;

public class AbstractSyntaxcheckHandlerTest extends AbstractSyntaxcheckHandler {
	@Test(expected = None.class)
	public void testDeleteMarkers() throws ExecutionException {
		deleteMarkers("");
	}
	@Test
	public void testCreateJcl() throws IOException {
		final String pgm = "PGM000";
		assertNotNull(createJcl(new Item("HLQ.PLI", pgm), ""));
		assertNotNull(createJcl(new Item("HLQ.COBOL", pgm), ""));
		assertNotNull(createJcl(new Item("HLQ.ASSEMBLE", pgm), ""));
	}
	@Test(expected = None.class)
	public void testDeleteAnnotations() {
		ITextEditor editor = Mockito.mock(ITextEditor.class);
		IDocumentProvider documentProvider = Mockito.mock(IDocumentProvider.class);
		IEditorInput editorInput = Mockito.mock(IEditorInput.class);
		IAnnotationModel annotationModel = Mockito.mock(IAnnotationModel.class);
		
		List<Annotation> annotationList = Collections.singletonList(new CompilerMessageAnnotation(1));
		
		Mockito.when(annotationModel.getAnnotationIterator()).thenReturn(annotationList.iterator());

		Mockito.when(documentProvider.getAnnotationModel(ArgumentMatchers.any(IEditorInput.class))).thenReturn(annotationModel);
		
		Mockito.when(editor.getDocumentProvider()).thenReturn(documentProvider);
		Mockito.when(editor.getEditorInput()).thenReturn(editorInput);
		
		deleteAnnotations(editor);
	}
	@Test(expected = None.class)
	public void testCreateMarkerAndAnnotation() throws CoreException {
		TextEditor editor = new TextEditor();
		
		MESSAGE msg = new MESSAGE();
		msg.setMSGFILE("");
		msg.setMSGLINE("");	//Forces NumberFormatException intentionally
		msg.setMSGTEXT("");
		msg.setMSGNUMBER("IBM3567I E");
		
		createMarkerAndAnnotation(editor, msg, "");
		
		msg.setMSGNUMBER("IBM3567I W");
		
		createMarkerAndAnnotation(editor, msg, "");
		
		msg.setMSGNUMBER("IBM3567I I");
		
		createMarkerAndAnnotation(editor, msg, "");
		
		msg.setMSGNUMBER("IBM3567I S");
		
		createMarkerAndAnnotation(editor, msg, "");
	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		return null;
	}
}
