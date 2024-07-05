/*********************************************************************
* Copyright (c) 25.06.2024 Thomas Zierer
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package de.tgmz.zdev.preferences;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;

public class InitializeSelectionListener implements SelectionListener {
	private String s;
	
	public InitializeSelectionListener(String s) {
		super();
		this.s = s;
	}
	@Override
	public void widgetSelected(SelectionEvent e) {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();

		store.setDefault(ZdevPreferenceConstants.USER, s);

		new ZdevPreferenceInitializer().initializeDefaultPreferences();
	}

	@Override
	public void widgetDefaultSelected(SelectionEvent e) {
		// Must implement even if empty
	}
}
