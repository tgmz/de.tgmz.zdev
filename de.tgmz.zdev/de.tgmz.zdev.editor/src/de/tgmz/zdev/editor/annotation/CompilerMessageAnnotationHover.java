/*********************************************************************
* Copyright (c) 10.10.2023 Thomas Zierer
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package de.tgmz.zdev.editor.annotation;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.IAnnotationHover;
import org.eclipse.jface.text.source.ISourceViewer;

/**
 * Hover for {@link CompilerMessageAnnotation} 
 *
 */
public class CompilerMessageAnnotationHover implements IAnnotationHover {

	@Override
	public String getHoverInfo(ISourceViewer sourceViewer, int lineNumber) {
		Object o = getHoverInfoInternal(sourceViewer, lineNumber);

		if (o != null) {
			return o.toString();
		}

 		return null;
	}

	private Object getHoverInfoInternal(final ISourceViewer sourceViewer, final int lineNumber) {
		final Set<String> messages = new HashSet<>();
		List<Annotation> annotations = getAnnotations(sourceViewer, lineNumber);
		for (Annotation annotation : annotations) {
			if (annotation.getText() != null) {
				messages.add(annotation.getText().trim());
			}
		}
		if (!messages.isEmpty()) {
			return formatInfo(messages);
		}
		
		return null;
	}

	private List<Annotation> getAnnotations(ISourceViewer sourceViewer, int lineNumber) {
		Iterator<?> annotationIterator = sourceViewer.getAnnotationModel().getAnnotationIterator();
		List<Annotation> result = new LinkedList<>();
		
		while (annotationIterator.hasNext()) {
			Object o = annotationIterator.next();
			
			if (o instanceof CompilerMessageAnnotation cma && cma.getLineNumber() == lineNumber) {
				result.add(cma);
			}
			
		}
		
		return result;
	}
 	private String formatInfo(final Collection<String> messages) {
 		final StringBuilder buffer = new StringBuilder();

		final Iterator<String> e = messages.iterator();
		
 		while (e.hasNext()) {
 			buffer.append(e.next());
 			buffer.append("\n");
 		}

 		buffer.deleteCharAt(buffer.length() - 1);

 		return buffer.toString();
 	}
}
