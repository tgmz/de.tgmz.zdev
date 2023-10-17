/*********************************************************************
* Copyright (c) 10.10.2023 Thomas Zierer
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package de.tgmz.zdev.editor.assembler;

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.Token;

import de.tgmz.zdev.editor.ZdevColor;
import de.tgmz.zdev.editor.ZdevColorManager;
import de.tgmz.zdev.editor.ZdevSourceViewerConfiguration;

/**
 * Configures the editor for Assembler.
 */
public class AssemblerConfiguration extends ZdevSourceViewerConfiguration {
	private AssemblerScanner scanner;

	public AssemblerConfiguration(ZdevColorManager colorManager) {
		super(colorManager);
	}

	@Override
	protected AssemblerScanner getScanner() {
		if (scanner == null) {
			scanner = new AssemblerScanner(getColorManager());
			scanner.setDefaultReturnToken(
				new Token(
					new TextAttribute(getColorManager().getSwtColor(ZdevColor.DEFAULT))));
		}
		
		return scanner;
	}
}