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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;

import org.apache.commons.io.IOUtils;

import de.tgmz.zdev.editor.pli.PLIBuiltin;

/**
 * Extracts the documentation for builtin functions.
 */
public class Main2 {

	public static void main(String[] args) throws IOException {
		new File("doc").mkdir();
		
		PLIBuiltin[] values = PLIBuiltin.values();
		
		PrintStream os = new PrintStream(new FileOutputStream("src/main/resources/builtin.properties"), true, "ISO-8859-1");
		
		List<?> readLines = IOUtils.readLines(new FileInputStream("pli.txt"), "UTF-8");
		
		for (int i = 0; i < values.length; i++) {
			Builtin b = new Builtin(values[i].toString());
			
			int j0 = readLines.indexOf(values[i].toString());
			int j1 = readLines.indexOf(values[i+1].toString());

			if (j0 < 0) {
				System.err.println(b.getName());
			}
			
			for (int j2 = j0 + 1; j2 < j1; ++j2) {
				String line = (String) readLines.get(j2);
				
				if (mustOmit(b.getName(), line)) {
					continue;
				}
				
				if (line.startsWith("\u0001")) {
					b.addSignature(line.replace("\u0001", "").replace("\u0003", "").trim());

					while (!line.endsWith("\u0003")) {
						line = (String) readLines.get(++j2);
						
						if (mustOmit(b.getName(), line)) {
							continue;
						}
						
						b.addSignature(line.replace("\u0001", "").replace("\u0003", "").trim());
					}

					
					String z = b.getSignature();
					
					if (z.startsWith(",")) {
						z = z.substring(1);
					}
					
					int l = z.length();
					
					if (l > 0 && l % 2 == 0) {
						if (z.substring(0, l/2).equals(z.substring(l/2))) {
							z = z.substring(0, l/2); 
						}
					}
					
					b.setSignature(z);

					
					continue;
				}
				
				if (b.getSignature() == null) {
					b.addShortDescription(line);
				} else {
					b.addLongDescription(line);
				}
			}
			
			if (b.getShortDescription() != null || b.getSignature() != null || b.getLongDescription() != null) {
				os.println(b.getName() + " = " + b.getShortDescription() + "$" + b.getSignature() + "$" + b.getLongDescription());
//				PrintStream ps = new PrintStream("doc/" +  b.getName() + ".short.txt"); ps.print(b.getShortDescription()); ps.close();
//				ps = new PrintStream("doc/" + b.getName() + ".signature.txt"); ps.print(b.getSignature()); ps.close();
//				ps = new PrintStream("doc/" + b.getName() + ".long.txt"); ps.print(b.getLongDescription()); ps.close();
			}
		}
		
		os.close();
	}
	
	private static boolean mustOmit(String s, String line) {
		if (s.equals(line)) {
			return true;
		}
		
		return line.startsWith("Chapter")
				|| line.contains("Enterprise PL/I for z/OS: Language Reference");
	}
}
