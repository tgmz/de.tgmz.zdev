/*********************************************************************
* Copyright (c) 10.10.2023 Thomas Zierer
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package de.tgmz.zdev.util;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;

import org.apache.commons.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;

/**
 * Transforms PL/I-PDF into text.
 */
public class Main {

	public static void main(String[] args) throws IOException {
		PDDocument pdd = PDDocument.load("SendFile1.pdf");
		
		PDFTextStripper pdfts = new PDFTextStripper();
		
		String text = pdfts.getText(pdd);
		
		IOUtils.copy(new StringReader(text), new FileOutputStream("pli.txt"));
	}
}
