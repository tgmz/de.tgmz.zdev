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

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tgmz.zdev.domain.Item;

public class ItemOptionsDialog {
	private static final Logger LOG = LoggerFactory.getLogger(ItemOptionsDialog.class);
    private static final int LAYOUT_MARGIN_WIDTH = 30; 
    private static final int LAYOUT_MARGIN_HEIGTH = 12;
    private static final int LAYOUT_VERTICAL_SPACING = 8;
    private static final int LAYOUT_COLUMNS_NORMAL = 1;
	private Shell parent;
    private Button chkLock;
    private ItemOptionGroup compGroup;
    private ItemOptionGroup db2Group;
    private ItemOptionGroup cicsGroup;
    private ItemOptionGroup bindGroup;
    private Button btnOk;
    private Button btnCancel;
    private Image image;
    private Item item;

    public ItemOptionsDialog(final Shell parent, Item item) {
        this.parent = new Shell(parent);
        this.item = item;

        parent.setImage(image);
        
        createControl();
        
        addListeners();      

    }
	private void createControl() {
		parent.setText("Item options");
		
        chkLock = new Button(parent, SWT.CHECK);
        chkLock.setText("Lock options");
        chkLock.setSelection(item.isLock());
        
        compGroup = new ItemOptionGroup(parent, "Compile", "Override compile options");
        
        if (item.getOption().isComp()) {
        	compGroup.getChkOption().setSelection(true);
        	compGroup.getTxtOption().setText(item.getOption().getCompOption());
        } else {
        	compGroup.getChkOption().setSelection(false);
        	compGroup.getTxtOption().setEnabled(false);
        }
        
        db2Group = new ItemOptionGroup(parent, "DB/2", "Override DB/2 options");
        
        if (item.getOption().isDb2()) {
        	db2Group.getChkOption().setSelection(true);
        	db2Group.getTxtOption().setText(item.getOption().getDb2Option());
        } else {
        	db2Group.getChkOption().setSelection(false);
        	db2Group.getTxtOption().setEnabled(false);
        }
        
        cicsGroup = new ItemOptionGroup(parent, "CICS", "Override CICS options");
        
        if (item.getOption().isCics()) {
        	cicsGroup.getChkOption().setSelection(true);
        	cicsGroup.getTxtOption().setText(item.getOption().getCicsOption());
        } else {
        	cicsGroup.getChkOption().setSelection(false);
        	cicsGroup.getTxtOption().setEnabled(false);
        }
        
        bindGroup = new ItemOptionGroup(parent, "Bind", "Override binder options");
        
        if (item.getOption().isBind()) {
        	bindGroup.getChkOption().setSelection(true);
        	bindGroup.getTxtOption().setText(item.getOption().getBindOption());
        } else {
        	bindGroup.getChkOption().setSelection(false);
        	bindGroup.getTxtOption().setEnabled(false);
        }
        
        Group group = new Group(parent, 0);
        GridLayout layout = new GridLayout();
        layout.numColumns = 3;
        group.setLayout(layout);
        GridData gridData = new GridData(768);
        gridData.horizontalSpan = 2;
        group.setLayoutData(gridData);
        
        btnOk = new Button(group, SWT.PUSH);
        btnOk.setText("OK");
        btnOk.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, true));
        ((GridData) btnOk.getLayoutData()).widthHint = 80;
        
        btnCancel = new Button(group, SWT.PUSH);
        btnCancel.setText("Cancel");
        btnCancel.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, true));
        ((GridData) btnCancel.getLayoutData()).widthHint = 80;
        
        parent.setLayout(getLayout(LAYOUT_COLUMNS_NORMAL));
	}
	private void addListeners() {
        btnOk.addSelectionListener(new SelectionAdapter() {

            @Override
			public void widgetSelected(final SelectionEvent e) {
                item.setLock(chkLock.getSelection());
                item.getOption().setComp(compGroup.getChkOption().getSelection());
                item.getOption().setCompOption(compGroup.getChkOption().getSelection() ? compGroup.getTxtOption().getText() : null);
                item.getOption().setDb2(db2Group.getChkOption().getSelection());
                item.getOption().setDb2Option(db2Group.getChkOption().getSelection() ? db2Group.getTxtOption().getText() : null);
                item.getOption().setCics(cicsGroup.getChkOption().getSelection());
                item.getOption().setCicsOption(cicsGroup.getChkOption().getSelection() ? cicsGroup.getTxtOption().getText() : null);
                item.getOption().setBind(bindGroup.getChkOption().getSelection());
                item.getOption().setBindOption(bindGroup.getChkOption().getSelection() ? bindGroup.getTxtOption().getText() : null);
                
                parent.dispose();
            }

        });
		btnCancel.addSelectionListener(new SelectionAdapter() {
            @Override
			public void widgetSelected(final SelectionEvent e) {
            	item = null;
                parent.dispose();
            }

        });

		compGroup.getChkOption().addSelectionListener(new CompileSelectionAdapter(item, compGroup.getChkOption(), compGroup.getTxtOption()));      
        
		db2Group.getChkOption().addSelectionListener(new Db2SelectionAdapter(item, db2Group.getChkOption(), db2Group.getTxtOption()));      
        
		cicsGroup.getChkOption().addSelectionListener(new CicsSelectionAdapter(item, cicsGroup.getChkOption(), cicsGroup.getTxtOption()));
        
		bindGroup.getChkOption().addSelectionListener(new BindSelectionAdapter(item, bindGroup.getChkOption(), bindGroup.getTxtOption()));
	}
    public Item open() {
		LOG.debug("open()");
    	
    	LOG.info( "Init");
    	
        parent.pack();
        parent.open();
        while (!parent.isDisposed()) {
            if (!parent.getDisplay().readAndDispatch()) {
                parent.getDisplay().sleep();
            }
        }

		LOG.debug("stop");
		
		return item;
    }
    private static GridLayout getLayout(final int numColumns) {
        GridLayout l = new GridLayout();
        l.marginWidth = LAYOUT_MARGIN_WIDTH;
        l.marginHeight = LAYOUT_MARGIN_HEIGTH;
//        l.horizontalSpacing = Defaultwert
        l.verticalSpacing = LAYOUT_VERTICAL_SPACING;
        l.numColumns = numColumns;

        return l;
    }
}
