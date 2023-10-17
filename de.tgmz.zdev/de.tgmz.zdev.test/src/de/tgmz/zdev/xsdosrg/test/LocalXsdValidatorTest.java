/*********************************************************************
* Copyright (c) 10.10.2023 Thomas Zierer
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package de.tgmz.zdev.xsdosrg.test;

import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.junit.Test;
import org.junit.Test.None;
import org.mockito.Mockito;
import org.xml.sax.SAXException;

import de.tgmz.zdev.xsdosrg.LocalXsdValidator;

public class LocalXsdValidatorTest {
	@Test(expected = None.class)
	public void testLocalXsdValidator() throws SAXException {
		IPath mockPath = Mockito.mock(IPath.class);
		Mockito.when(mockPath.toFile()).thenReturn(new File("src/testresources/osr/BBkICFBlkCdtTrf.xsd"));

		IFile mockFile = Mockito.mock(IFile.class);
		Mockito.when(mockFile.getLocation()).thenReturn(mockPath);
		
		LocalXsdValidator.getInstance().validate(mockFile);
	}
}
