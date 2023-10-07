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

import org.eclipse.jface.text.AbstractDocument;
import org.eclipse.jface.text.DefaultLineTracker;

/**
 * Simple implementation of a IDocument for tests.
 */
public class TestDocument extends AbstractDocument {
	public TestDocument(String content) {
		super();
		setTextStore(new StringTextStore(content));
		setLineTracker(new DefaultLineTracker());
	}
}
