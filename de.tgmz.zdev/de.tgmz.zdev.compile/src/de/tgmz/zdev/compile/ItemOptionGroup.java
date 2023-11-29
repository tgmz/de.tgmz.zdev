/*********************************************************************
* Copyright (c) 08.11.2023 Thomas Zierer
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package de.tgmz.zdev.compile;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ItemOptionGroup {
	private static final Logger LOG = LoggerFactory.getLogger(ItemOptionGroup.class);
    private Button chkOption;
    private Text txtOption;

    public ItemOptionGroup(final Composite parent, String chkTxt, String labTxt) {
		LOG.debug("Create option group");
		
        Group group = new Group(parent, SWT.NONE);
        
		GridLayoutFactory.swtDefaults().margins(0, 0).numColumns(3).applyTo(group);
		GridDataFactory.fillDefaults().grab(false, false).applyTo(group);
        
        chkOption = new Button(group, SWT.CHECK);
        chkOption.setText(chkTxt);
        chkOption.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, true));
        ((GridData) chkOption.getLayoutData()).widthHint = 50;
        
        Label theLabel = new Label(group, SWT.NONE);
        theLabel.setText(labTxt);
        theLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, true));
        
        txtOption = new Text(group, SWT.BORDER);
        GridData gridData = new GridData(SWT.RIGHT, SWT.CENTER, true, true);
        gridData.widthHint = 200;
        txtOption.setLayoutData(gridData);
	}
	public Button getChkOption() {
		return chkOption;
	}
	public Text getTxtOption() {
		return txtOption;
	}
}
