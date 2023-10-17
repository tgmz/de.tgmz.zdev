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
			String xml = IOUtils.readInputStreamAsString(is, true, Charset.defaultCharset().name());

			is.close();
			
			xml = xml.replace("<ÜDOCTYPE", "<!DOCTYPE");

			try (Reader isr = new StringReader(xml)) {
				Document doc = db.parse(new InputSource(isr));

				PACKAGE plicomp = (PACKAGE) unmarshaller.unmarshal(doc);
			
				plicomp.getFILEREFERENCETABLE().getFILE().get(0).setFILENAME(mainSrcDatasetName);
			
				return plicomp;
			}
		} catch (IOException | SAXException | JAXBException e) {
			throw new PlicompException("Error parsing plicomp", e);
		}
	}
}
