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

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import de.tgmz.zdev.editor.Activator;
import de.tgmz.zdev.editor.ZdevReconcilingStrategy;

public class ReconcilingStrategyTest {
	@Test
	public void testAnnotation() throws IOException {
		ZdevReconcilingStrategy rs = new ZdevReconcilingStrategy();
		
		Activator.getDefault().setPattern("PROC");
		
		try(InputStream is = this.getClass().getClassLoader().getResourceAsStream("testresources/HELLOW.pli")) {
			rs.setDocument(new TestDocument(IOUtils.toString(is, StandardCharsets.UTF_8.name())));
		}
		
		rs.reconcile(null);
		
		assertEquals(2, rs.getfPositions().size());
	}
}
