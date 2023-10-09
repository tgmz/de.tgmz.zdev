/*********************************************************************
* Copyright (c) 09.10.2023 Thomas Zierer
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package de.tgmz.zdev.view.test;

import static org.junit.Assert.assertNull;

import org.junit.Test;

import de.tgmz.zdev.view.copypaste.MemberNameValidator;

/**
 * Testclass for MemberNameValidator.
 */
public class MemberNameValidatorTest {
	@Test
	public void checkMember() {
		assertNull(new MemberNameValidator().isValid("QMID013"));
	}
}
