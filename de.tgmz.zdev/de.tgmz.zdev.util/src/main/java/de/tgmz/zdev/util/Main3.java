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

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

public class Main3 extends SimpleFileVisitor<Path>{
	private static Builtin b;
	
	public static void main(String[] args) throws IOException {
		new Main3().perform();
	}
	
	private void perform() throws IOException {
		Files.walkFileTree(Paths.get("doc"), this);
	}
	
	@Override
	public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
		if (b == null) {
			int i = file.getName(1).toString().indexOf('.');
			
			String s = file.getName(1).toString().substring(0, i);
			
			b = new Builtin(s);
		}
		
		
		
		
		return super.visitFile(file, attrs);
	}
}
