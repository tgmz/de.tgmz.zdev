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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import de.tgmz.zdev.editor.pli.PreProcInstructionDetector;


public class PreProcInstructionDetectorTest {
	@Test
	public void testComment() {
		PreProcInstructionDetector detector = new PreProcInstructionDetector();
		
		assertTrue(detector.isWordStart('%'));
		assertTrue(detector.isWordPart('A'));
		
		assertFalse(detector.isWordStart('x'));
		assertFalse(detector.isWordPart('?'));
	}
}
