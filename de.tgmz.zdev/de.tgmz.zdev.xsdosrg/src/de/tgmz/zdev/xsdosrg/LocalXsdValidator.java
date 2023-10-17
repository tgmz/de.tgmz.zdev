/*********************************************************************
* Copyright (c) 10.10.2023 Thomas Zierer
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package de.tgmz.zdev.xsdosrg;

import javax.xml.XMLConstants;
import javax.xml.validation.SchemaFactory;

import org.eclipse.core.resources.IFile;
import org.xml.sax.SAXException;

/**
 * Perform a local validation of a xsd file prior to uploading it.
 */
public class LocalXsdValidator {
	private static final LocalXsdValidator INSTANCE = new LocalXsdValidator();
	private SchemaFactory schemaFactory;

	private LocalXsdValidator() {
		schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
	}

	public static LocalXsdValidator getInstance() {
		return INSTANCE;
	}

	public void validate(IFile src) throws SAXException {
		schemaFactory.newSchema(src.getLocation().toFile());
	}
}
