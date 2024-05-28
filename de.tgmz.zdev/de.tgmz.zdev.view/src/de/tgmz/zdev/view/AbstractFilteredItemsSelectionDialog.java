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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.FilteredItemsSelectionDialog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractFilteredItemsSelectionDialog<T> extends FilteredItemsSelectionDialog {
	protected static final Logger LOG = LoggerFactory.getLogger(AbstractFilteredItemsSelectionDialog.class);

	private T[] elements;
	
	@SafeVarargs
	protected AbstractFilteredItemsSelectionDialog(Shell shell, boolean multi, T... elements) {
		super(shell, multi);
		
		LOG.info("Init new HistorySelectionDialog");

		this.elements = elements;

		setInitialPattern("?", FilteredItemsSelectionDialog.NONE);
	}

	@Override
	protected IDialogSettings getDialogSettings() {
		IDialogSettings settings = Activator.getDefault().getDialogSettings().getSection(getDialogSettingsId());
		if (settings == null) {
			settings = Activator.getDefault().getDialogSettings().addNewSection(getDialogSettingsId());
		}
		
		return settings;
	}

	protected abstract String getDialogSettingsId();

	@Override
	protected Control createExtendedContentArea(Composite parent) {
		return null;
	}

	@Override
	protected IStatus validateItem(Object item) {
		return Status.OK_STATUS;
	}

	@Override
	protected void fillContentProvider(AbstractContentProvider contentProvider, ItemsFilter itemsFilter,
			IProgressMonitor progressMonitor) throws CoreException {
		IProgressMonitor subMonitor = SubMonitor.convert(progressMonitor, 100);
    	
		subMonitor.beginTask("Searching", elements.length);
		
		for (T element : elements) {
			contentProvider.add(element, itemsFilter);
			subMonitor.worked(1);
		}
		
		subMonitor.done();
	}
	protected void setElements(T[] elements) {
		this.elements = elements;
	}
}
