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

import java.util.Locale;
import java.util.Optional;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.ibm.cics.core.comm.CredentialsConfiguration;
import com.ibm.cics.core.connections.ConnectionsPlugin;

public class ZdevPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {
	@Override
	protected Control createContents(Composite parent) {
		Group group = new Group(parent, SWT.NONE);
		
		GridLayoutFactory.swtDefaults().margins(0, 0).numColumns(3).applyTo(group);
		GridDataFactory.fillDefaults().grab(false, false).applyTo(group);

		Button button = new Button(group, SWT.PUSH);
		button.setText(Activator.getDefault().getString("ZdevPreferencePage.Button"));
		button.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, true));
		((GridData) button.getLayoutData()).widthHint = button.getText().length() * 10;

		Label label = new Label(group, SWT.NONE);
		label.setText(Activator.getDefault().getString("ZdevPreferencePage.Label"));
		label.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, true));

		Text text = new Text(group, SWT.BORDER);
		GridData gridData = new GridData(SWT.RIGHT, SWT.CENTER, true, true);
		gridData.widthHint = 200;
		text.setLayoutData(gridData);
		
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		String userId = store.getString(ZdevPreferenceConstants.USER);
		
		if (userId == null || "".equals(userId)) {
			Optional<CredentialsConfiguration> any = ConnectionsPlugin.getDefault().getCredentialsManager().getAllCredentials().stream().filter(c -> !c.getUserID().isEmpty()).findAny();
			
			if (any.isPresent()) {
				userId = any.get().getUserID();
			} else {
				userId =System.getProperty("user.name").trim().toUpperCase(Locale.ROOT);
			}
		}

		text.setText(userId);
		
		button.addSelectionListener(new InitializeSelectionListener(text.getText()));
		
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
