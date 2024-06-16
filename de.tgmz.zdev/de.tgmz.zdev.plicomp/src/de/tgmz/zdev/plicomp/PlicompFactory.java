/*********************************************************************
* Copyright (c) 10.10.2023 Thomas Zierer
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package de.tgmz.zdev.plicomp;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.regex.Pattern;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.ibm.cics.common.util.IOUtils;

import de.tgmz.zdev.connection.ZdevConnectable;
import de.tgmz.zdev.xinfo.generated.FILE;
import de.tgmz.zdev.xinfo.generated.MESSAGE;
import de.tgmz.zdev.xinfo.generated.ObjectFactory;
import de.tgmz.zdev.xinfo.generated.PACKAGE;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;

/**
 * Creates a secure parser for plicomp.xml.
 */
public class PlicompFactory {
	private static final Logger LOG = LoggerFactory.getLogger(PlicompFactory.class);
	private static PlicompFactory instance;
	private DocumentBuilder db;
	private Unmarshaller unmarshaller;

	private PlicompFactory() throws ParserConfigurationException, JAXBException {
		//CHECKSTYLE DISABLE LineLength FOR 1 LINE
		//cf https://www.owasp.org/index.php?title=XML_External_Entity_(XXE)_Prevention_Cheat_Sheet&setlang=en#JAXP_DocumentBuilderFactory.2C_SAXParserFactory_and_DOM4J
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		
		dbf.setValidating(false);
		dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true); // cf. findsecbugs XXE_DOCUMENT
			
		dbf.setFeature("http://xml.org/sax/features/external-general-entities", false);
		dbf.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
		dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
		dbf.setFeature("http://apache.org/xml/features/validation/schema", false);
		dbf.setFeature("http://xml.org/sax/features/validation", false);
		dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
		dbf.setFeature("http://apache.org/xml/features/allow-java-encodings", true);

		dbf.setXIncludeAware(false);
		dbf.setExpandEntityReferences(false);
		
		db = dbf.newDocumentBuilder();

		JAXBContext jaxbContext = JAXBContext.newInstance(PACKAGE.class);
		unmarshaller = jaxbContext.createUnmarshaller();
	}

	public static PlicompFactory getInstance() throws PlicompConfigurationException {
		if (instance == null) {
			LOG.info("Create new Plicomp factory");
			
			try {
				instance = new PlicompFactory();
			} catch (ParserConfigurationException | JAXBException e) {
				throw new PlicompConfigurationException("Error creating plicomp factory", e);
			}
		}

		return instance;
	}

	/**
	 * Creates a PACKAGE object from a InputStram
	 * @param is InputStream
	 * @param mainSrcDatasetName the dataset name of the main program if the compile input derives from a shop specific preprocessor.
	 * @return PACKAGE
	 * @throws PlicompException on errors on parsing
	 */
	public PACKAGE getPlicomp(InputStream is, String mainSrcDatasetName) throws PlicompException {
		// Two problems:
		// 1. Prologue contains encoding="IBM-1141" but "IBM01141" would be correct.
		// Therefore we cannot parse InputStream directly. But we convert it into a InputSource 
		// with correct encoding and XERCES ignores the encoding in prologue.
		// 2. For some reason the PL/I compiler write "<ÜDOCTYPE" instead of "<!DOCTYPE". Don't know why.

		try {
			String sbcsLocal = ZdevConnectable.getConnectable().getConnection().getConfiguration().getExtendedAttribute("SBCS_LOCAL");
			
			String xml = IOUtils.readInputStreamAsString(is, true, sbcsLocal != null ? sbcsLocal : Charset.defaultCharset().name());

			is.close();
			
			return getPackage(xml, mainSrcDatasetName);
		} catch (IOException | JAXBException e) {
			throw new PlicompException("Error parsing plicomp", e);
		}
	}
	private PACKAGE getPackage(String xml, String mainSrcDatasetName) throws IOException, JAXBException {
		String myXml = xml.replace("<ÜDOCTYPE", "<!DOCTYPE");

		try (Reader isr = new StringReader(myXml)) {
			Document doc = db.parse(new InputSource(isr));

			PACKAGE plicomp = (PACKAGE) unmarshaller.unmarshal(doc);
		
			plicomp.getFILEREFERENCETABLE().getFILE().get(0).setFILENAME(mainSrcDatasetName);
		
			return plicomp;
		} catch (SAXException e) {
			// C/C++ produce the feedback not as XML but as text
			return getPackageFromSysevent(xml);
		}
		
	}
	private PACKAGE getPackageFromSysevent(String sysevent) {
		ObjectFactory of = new ObjectFactory();
		
		PACKAGE ccomp = of.createPACKAGE();
		ccomp.setFILEREFERENCETABLE(of.createFILEREFERENCETABLE());

		Pattern.compile("\\R").splitAsStream(sysevent).forEach(line -> {
			// See https://www-40.ibm.com/servers/resourcelink/svc00100.nsf/pages/zOSV2R3sc147307/$file/cbcux01_v2r3.pdf
			// page 648 for the detailed format of the SYSEVENT file
			if (line.startsWith("ERROR")) {
				// The ERROR field looks like this:
				// ERROR 0 1 0 0 3 3 0 0 CCNnnnn E 12 26 Undeclared identifier add.
				//       | | | | | | | | |       | |  |  |
				//       A B C D E F G H I       J K  L  M
				String[] s = line.split("\\s", 14);
				
				MESSAGE m = of.createMESSAGE();

				m.setMSGFILE(s[2]);						// B: Increments starting with 1 for the primary file
				m.setMSGLINE(s[5]);						// E: The source line number for which the message was issued.
				m.setMSGNUMBER(s[9] + " " + s[10]);		// I: String Containing the message identifier
														// J: Message severity character (I/W/E/S/U)
				m.setMSGTEXT(s[13]);					// M: String containing message text
				
				ccomp.getMESSAGE().add(m);
			}
			if (line.startsWith("FILEID")) {
				// The FILEID field looks like this:
				// FILEID 0 1 0 10 ./simple.c
				//        | | | |  |
				//        A B C D  E
				String[] s = line.split("\\s", 6);
				
				FILE f = of.createFILE();
				f.setFILENUMBER(s[2]);					// B: Increments starting with 1 for the primary file
				f.setINCLUDEDONLINE(s[3]);				// C: The line number of the #include directive. For the primary source file this value is 0
				f.setFILENAME(s[5])	;					// E: String containing file/dataset name.
				
				ccomp.getFILEREFERENCETABLE().getFILE().add(f);
			}
		});
		
		ccomp.getFILEREFERENCETABLE().setFILECOUNT(String.valueOf(ccomp.getFILEREFERENCETABLE().getFILE().size()));
		
		return ccomp;
		
	}
}
