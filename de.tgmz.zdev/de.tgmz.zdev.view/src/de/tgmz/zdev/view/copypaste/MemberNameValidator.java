/*********************************************************************
* Copyright (c) 09.10.2023 Thomas Zierer
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package de.tgmz.zdev.view.copypaste;

import java.util.regex.Pattern;

import org.eclipse.jface.dialogs.IInputValidator;

import de.tgmz.zdev.view.Activator;

/**
 * InputValidator for member name.
 */
public class MemberNameValidator implements IInputValidator {
	/** Regular expression:
	 * 1. position: National characters uppercase only
	 * 2-8. position: National character uppercase + digits
	 */
	public static final String REGEXP_MEMBER = "^[A-Z§\\$#]{1}[A-Z\\d§\\$#]{0,7}";
	@Override
	public String isValid(String newText) {
		return Pattern.matches(REGEXP_MEMBER, newText) ? null :	Activator.getDefault().getString("Paste.InvalidMemberName");
	}
}
