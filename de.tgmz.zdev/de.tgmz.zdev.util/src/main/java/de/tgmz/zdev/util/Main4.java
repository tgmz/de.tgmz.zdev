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
import java.io.FileWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.io.IOUtils;

public class Main4 {
	@XmlRootElement
	private static class Rule {
		public String key;
		public String name;
		public String internalKey;
		public String description;
		public String severity;
		public String cardinality;
		public String status;
		public String tag;
		public String remediationFunction;
		public String remediationFunctionBaseEffort;
	}
	public static void main(String[] args) throws Exception {
		new Main4().perform();
	}
	
	private void perform() throws Exception {
		PrintWriter pw = new PrintWriter(new FileWriter("plicomp-rules.xml"));
		
		pw.println("<plicomp-rules>");
		
		JAXBContext jaxbContext = JAXBContext.newInstance(Rule.class);
		Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

		// output pretty printed
		jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		jaxbMarshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);

		FileInputStream f = new FileInputStream(new File("pli.txt"));
		
		List<String> l = IOUtils.readLines(f, Charset.defaultCharset());
		
		f.close();
		
		StringBuilder sb = new StringBuilder();
		
		for (String s0 : l) {
			if (!(s0.startsWith("© Copyright IBM Corp.")	// Copyright
					|| s0.contains(" • ")				// Überschrift
					|| s0.contains("Enterprise PL/I for z/OS Messages and Codes"))) {	// Footer
				sb.append(s0);
				sb.append(' ');
			}
		}
		
		String s = sb.toString();
		
		Pattern p = Pattern.compile("IBM\\d{4}I\\s[IWESU]");
		
		Matcher m = p.matcher(s);
		
		m.find();
		int sta = m.start();
		
		while (m.find()) {
			int end = m.start();
			String msg = s.substring(sta, end);
			
			String key = msg.substring(0, 8);
			String sev = msg.substring(9, 10);
			
			Rule r = new Rule();
			r.cardinality = "SINGLE";
			r.internalKey = key;
			r.key = key;
			r.remediationFunction = "CONSTANT_ISSUE";
			r.remediationFunctionBaseEffort = "1h";
			r.status = "READY";
			r.tag = "zdev";
			
			switch (sev) {
			case "I": r.severity = "MINOR"; break;
			case "W": r.severity = "MAJOR"; break;
			case "E": r.severity = "CRITICAL"; break;
			default: r.severity = "BLOCKER"; break;
			}

			int desc = s.indexOf("Explanation: ", sta);
			r.description = s.substring(desc + "Explanation: ".length(), end);
			
			r.name = s.substring(sta + 11, desc);
			r.name = r.name.substring(0, Math.min(r.name.length(), 200)); // VARCHAR(200) in DB
			
			jaxbMarshaller.marshal(r, pw);
			
			pw.println();
			
			sta = end;
		}
		
		pw.println("</plicomp-rules>");
		
		pw.close();
	}

}
