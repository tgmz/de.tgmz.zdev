/*********************************************************************
* Copyright (c) 10.10.2023 Thomas Zierer
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package de.tgmz.zdev.view;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.contexts.IContextService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.cics.zos.model.DataSet;
import com.ibm.cics.zos.model.Member;
import com.ibm.cics.zos.ui.actions.OpenDataEntryAction;
import com.ibm.cics.zos.ui.views.DataSetsExplorer;

import de.tgmz.zdev.editor.OpenZdevEntryAction;

/**
 * z/Dev adaption of IBMs DataSetsExplorer.
 */
public class ZdevDataSetsExplorer extends DataSetsExplorer {
	private static final Logger LOG = LoggerFactory.getLogger(ZdevDataSetsExplorer.class);

	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);
		
		// Quite dirty but DefaultSelection is defined by code so this is the only way to overwrite it. 
		try {
			Field fTree = DataSetsExplorer.class.getDeclaredField("tree");
			fTree.setAccessible(true);
			
			final Tree theTree = (Tree) fTree.get(this);
			
			Listener[] listeners = theTree.getListeners(SWT.DefaultSelection);
			
			for (Listener listener : listeners) {
				if (listener != null) {
					// We remove the DefaultSelection...
					LOG.debug("Remove listener of type {}", listener);
					
					theTree.removeListener(SWT.DefaultSelection, listener);
				}
			}
			
			// ... and overwrite it with our own DefaultSelection namely OpenZdevEntryAction
			theTree.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
					if (theTree.getSelectionCount() > 0) {
						TreeItem selectedTreeItem = theTree.getSelection()[0];
						if (selectedTreeItem.getData() instanceof Member) {
							OpenDataEntryAction openAction = new OpenZdevEntryAction();	// THIS IS IT!!!!!
							openAction.selectionChanged(null, new StructuredSelection(selectedTreeItem.getData()));
							openAction.setActivePart(null, ZdevDataSetsExplorer.this);
							openAction.run(null);
						} else if (selectedTreeItem.getData() instanceof DataSet dataSet) {
							if ("PS".equals(dataSet.getOrganization())) {
								OpenDataEntryAction openAction = new OpenDataEntryAction();
								openAction.setActivePart(null, ZdevDataSetsExplorer.this);
								openAction.selectionChanged(null, new StructuredSelection(selectedTreeItem.getData()));
								openAction.run(null);
							}
						}
					}
				}
			});
			
			fTree.setAccessible(false);
		} catch (ReflectiveOperationException | SecurityException | IllegalArgumentException e) {
    		LOG.error("Cannot get tree mediator", e);
		}
		
		IContextService contextService = getSite().getService(IContextService.class);
		contextService.activateContext("de.tgmz.zdev.keybindings.contexts.rename");
	}
	/**
	 * Forces refresh.
	 */
	public void forceRefresh() {
		// Quite dirty: 
		// view.setTextAndRefresh(String s) is useless: We cannot access the filter of DataSetsExplorerView.
		try {
			Method refresh = DataSetsExplorer.class.getDeclaredMethod("refresh");
			refresh.setAccessible(true);
			refresh.invoke(this);
			refresh.setAccessible(false);
		} catch (ReflectiveOperationException | SecurityException | IllegalArgumentException e) {
    		LOG.error("Cannot refresh", e);
		}
	}
	
	public String getFilter() {
		try {
			Field fText = DataSetsExplorer.class.getDeclaredField("text");
			fText.setAccessible(true);
			
			Text text = (Text) fText.get(this);
			
			fText.setAccessible(false);
			
			return text.getText();
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
    		LOG.error("Cannot get filter", e);
		}
		
		return null;
	}
}
