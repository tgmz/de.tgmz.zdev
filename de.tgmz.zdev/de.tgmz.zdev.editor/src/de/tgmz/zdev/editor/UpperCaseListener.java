/*********************************************************************
* Copyright (c) 06.10.2023 Thomas Zierer
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package de.tgmz.zdev.editor;

import java.util.Locale;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Turns every character to uppercase. Useful e.g. when editing JCL.
 */
public final class UpperCaseListener implements VerifyListener, KeyListener {
	private static final Logger LOG = LoggerFactory.getLogger(UpperCaseListener.class);
	
	private boolean upperCase;
	
	public UpperCaseListener() {
		upperCase = de.tgmz.zdev.preferences.Activator.getDefault().getPreferenceStore()
						.getBoolean(de.tgmz.zdev.preferences.ZdevPreferenceConstants.CAPS_ON);
		
		LOG.debug("Created new upperCase with initial {}", upperCase);
	}
	@Override
	public void keyPressed(KeyEvent e) {
		// F6 = Toggle
		if (e.keyCode == SWT.F6) {
			upperCase = !upperCase;

			LOG.debug("Toggled upperCase to {}", upperCase);
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
    	// Must implement
	}
	
	@Override
	public void verifyText(VerifyEvent e) {
		if (upperCase) {
			e.text = e.text.toUpperCase(Locale.ROOT);
		}
	}
}