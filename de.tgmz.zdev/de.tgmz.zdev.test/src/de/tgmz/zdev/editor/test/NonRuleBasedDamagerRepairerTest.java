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

import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.TextPresentation;
import org.junit.Test;
import org.mockito.Mockito;

import de.tgmz.zdev.editor.NonRuleBasedDamagerRepairer;

/**
 * NonRuleBasedDamagerRepairerTest. 
 */
public class NonRuleBasedDamagerRepairerTest {
	@Test
	public void test() {
		TextAttribute textAttribute = Mockito.mock(TextAttribute.class);
		TextPresentation textPresentation = Mockito.mock(TextPresentation.class);
		ITypedRegion typedRegion = Mockito.mock(ITypedRegion.class);
		
		NonRuleBasedDamagerRepairer nrbdr = new NonRuleBasedDamagerRepairer(textAttribute);
		nrbdr.setDocument(new TestDocument(""));
		
		nrbdr.createPresentation(textPresentation, typedRegion);
	}
}
