/*********************************************************************
* Copyright (c) 10.10.2023 Thomas Zierer
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package de.tgmz.zdev.filelock;

import org.eclipse.core.expressions.PropertyTester;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.cics.zos.model.Member;

/**
 * Tests if a dataset member is selected and file lock client is running.
 */
public class UnlockAvailableTester extends PropertyTester {
	private static final Logger LOG = LoggerFactory.getLogger(UnlockAvailableTester.class);
	
	@Override
	public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
		LOG.debug("Receiver [{}] Property [{} Args [{}]] Expected [{}]"
				, receiver
				, property
				, args 
				, expectedValue);
		
		return "isRunning".equals(property)
				&& (receiver != null) 
				&& (receiver instanceof Member);
	}
}
