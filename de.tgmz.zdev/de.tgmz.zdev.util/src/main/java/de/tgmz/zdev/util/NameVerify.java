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
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

/**
 * Transforms PL/I-PDF into text.
 */
public class NameVerify {

	public static void main(String[] args) throws Exception {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Files.walkFileTree(Paths.get(".."), new SimpleFileVisitor<Path>() {
			@Override
			public java.nio.file.FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				if (file.getNameCount() > 3) {
					return FileVisitResult.SKIP_SUBTREE;
				}
				
				if (!"pom.xml".equals(file.getFileName().toString())) {
					return FileVisitResult.CONTINUE;
				}
				
				try {
					Document doc = db.parse(file.toFile());
					
					NodeList nl = doc.getElementsByTagName("packaging");
					
					if (!"eclipse-plugin".equals(nl.item(0).getTextContent())) {
						return FileVisitResult.CONTINUE;
					}
					
					String description0 = doc.getElementsByTagName("description").item(0).getTextContent();
				
					File prop = new File(file.getParent().toString(), "plugin.properties");
				
					if (!prop.exists()) {
						System.err.println("Keine Properties für " + file.toString());
						return FileVisitResult.CONTINUE;
					}
					
					Properties p = new Properties();
					p.load(new FileInputStream(prop));
					
					String description1 = p.getProperty("pluginName") + " Plugin";
					
					if (!description0.equals(description1)) {
						System.out.println(description0 + "\t" + description1);
					}
				} catch (Exception e) {
					e.printStackTrace(System.out);
				}
				
				return FileVisitResult.CONTINUE;
			};
		});
	}
}
