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

import org.eclipse.jface.text.source.Annotation;

/**
 * An annotation representing a compiler message.
 */
public class CompilerMessageAnnotation extends Annotation {
	private int lineNumber;
	public CompilerMessageAnnotation(int lineNumber) {
		// CompilerMessageAnnotationen are always non-persistent as the Editor-Objekt does not implement IFile.
		super(false);
		this.lineNumber = lineNumber;
	}
	public int getLineNumber() {
		return lineNumber;
	}
}
