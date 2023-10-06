/*********************************************************************
* Copyright (c) 06.10.2023 Thomas Zierer
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package de.tgmz.zdev.connection;

import org.eclipse.core.expressions.PropertyTester;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Tests if zipupload action is available.
 */
public class ConnectedTester extends PropertyTester {
	private static final Logger LOG = LoggerFactory.getLogger(ConnectedTester.class);

	@Override
	public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
		LOG.debug("Receiver [{}] Property [{} Args [{}]] Expected [{}]"
				, receiver
				, property
				, args 
				, expectedValue);
		
		return "isConnected".equals(property)
				&& (receiver != null) 
				&& (ZdevConnectable.getConnectable().isConnected());
	}

}
