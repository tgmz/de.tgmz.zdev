/*********************************************************************
* Copyright (c) 09.10.2023 Thomas Zierer
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package de.tgmz.zdev.view;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract class for selecting a {@link com.ibm.cics.zos.model.DataEntry}.
 */

public abstract class AbstractSelectionDialog implements SelectionListener {
	protected static final Logger LOG = LoggerFactory.getLogger(AbstractSelectionDialog.class);
    /** Default Font. */
    protected static final String FONT = "ARIAL";
    /** Parentshell. */
    protected Shell shell;
    /** OK (Submit)-Button. */
    protected Button btnOk;
    protected boolean rc = false;
    
    protected AbstractSelectionDialog(final Shell parentShell) {
        shell = new Shell(parentShell);

    	LOG.info("Init");
    	
        createControl();
    }
    
	/**
	 * Creates the dialog.
	 */
	protected void createControl() {
		shell.setText(Activator.getDefault().getString("DatasetSelectionDialog.Title"));
        Label label = createLabel(shell, Activator.getDefault().getString("DatasetSelectionDialog.Description"), null);
        label.setFont(new Font(shell.getDisplay(), FONT, 11, SWT.BOLD));
        GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        gridData.horizontalSpan = 4;
        label.setLayoutData(gridData);
        
        insertControls(gridData);
        
        Group grpButtons = new Group(shell, 0);
        GridLayout buttonLayout = new GridLayout();
        buttonLayout.numColumns = 2;
        grpButtons.setLayout(buttonLayout);
        gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        gridData.horizontalSpan = 4;
        grpButtons.setLayoutData(gridData);
        btnOk = createButton(grpButtons, Activator.getDefault().getString("DatasetSelectionDialog.OK"));
        
        btnOk.setEnabled(false);
        
        Button btnCancel = createButton(grpButtons, Activator.getDefault().getString("DatasetSelectionDialog.Cancel"));
        
		btnCancel.addSelectionListener(new SelectionAdapter() {
            @Override
			public void widgetSelected(final SelectionEvent e) {
                rc = false;
                shell.dispose();
            }
        });

		btnOk.addSelectionListener(new SelectionAdapter() {
            @Override
			public void widgetSelected(final SelectionEvent e) {
                rc = true;
                shell.dispose();
            }
        });
		
        GridLayout l = new GridLayout();
        l.marginWidth = 30;
        l.marginHeight = 12;
//        l.horizontalSpacing = Default
        l.verticalSpacing = 8;
        l.numColumns = 4;
		
        shell.setLayout(l);
	}
	

	protected abstract void insertControls(GridData gridData);

	public boolean open() {
		LOG.debug("open()");
    	
    	LOG.info( "Init");
    	
        rc = false;
        shell.pack();
        shell.open();
        while (!shell.isDisposed()) {
            if (!shell.getDisplay().readAndDispatch()) {
                shell.getDisplay().sleep();
            }
        }

		LOG.debug("stop {}", rc);
        
        return rc;
    }

    /**
     * Adds a button.
     * @param shell Parentshell
     * @param text Text
     * @return den Button
     */
    public static Button createButton(final Composite shell, final String text) {
        Button theButton = new Button(shell, SWT.PUSH);
        theButton.setText(text);
        theButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING));
        ((GridData) theButton.getLayoutData()).widthHint = 80;
        return theButton;
    }

    /**
     * Adds a label.
     * @param shell Parentshell
     * @param text Test
     * @param toolTipText Tooltip
     * @return the Label
     */
    public static Label createLabel(final Composite shell, final String text, final String toolTipText) {
        Label theLabel = new Label(shell, SWT.NONE);
        theLabel.setText(text);
        theLabel.setToolTipText(toolTipText);
        theLabel.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_CENTER));
        return theLabel;
    }

	@Override
	public void widgetDefaultSelected(SelectionEvent arg0) {
		// Must implement even if empty
	}
}
