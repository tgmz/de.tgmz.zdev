/*********************************************************************
* Copyright (c) 06.10.2023 Thomas Zierer
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package de.tgmz.zdev.preferences;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class ZdevPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {
	@Override
	protected Control createContents(Composite parent) {
		return new Composite(parent, SWT.NULL);
	}

	/**
	 * @see IWorkbenchPreferencePage
	 */
	@Override
	public void init(IWorkbench workbench) {
		// Must implement this, even if it is empty
	}
}
