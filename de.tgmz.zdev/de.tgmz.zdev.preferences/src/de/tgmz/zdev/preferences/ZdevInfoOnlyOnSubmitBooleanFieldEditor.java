/*********************************************************************
* Copyright (c) 10.10.2023 Thomas Zierer
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package de.tgmz.zdev.preferences;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

/**
 * Class for editing the behavior after a job submit.
 */

public class ZdevInfoOnlyOnSubmitBooleanFieldEditor extends BooleanFieldEditor {

	/**
	 * CTR.
	 * @param name Name
	 * @param labelText Label
	 * @param style Style
	 * @param parent Parent element
	 */
	public ZdevInfoOnlyOnSubmitBooleanFieldEditor(final String name, final String labelText,
			final int style, final Composite parent) {
		super(name, labelText, style, parent);
	}
	/** {@inheritDoc} */
	@Override
	protected Button getChangeControl(final Composite parent) {
		// Must override this, so ZdevPreferencePage sees it.
		return super.getChangeControl(parent);
	}
}
