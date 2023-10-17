/*********************************************************************
* Copyright (c) 10.10.2023 Thomas Zierer
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/

package de.tgmz.zdev.quickaccess;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.dialogs.FilteredItemsSelectionDialog;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tgmz.zdev.database.DbService;
import de.tgmz.zdev.domain.Item;
import de.tgmz.zdev.domain.ItemComparator;

/**
 * Class for selecting an item from a filtered list. 
 */
public class MemberSelectionDialog extends FilteredItemsSelectionDialog {
	private static final Logger LOG = LoggerFactory.getLogger(MemberSelectionDialog.class);
	private static List<Item> items = new ArrayList<>();
	private static final String DIALOG_SETTINGS = "MemberSelectionDialog";
	
	/**
	 * Dummy-Historie. We need one so we construct an empty one.
	 */
	private class ResourceSelectionHistory extends SelectionHistory {
		@Override
		protected Object restoreItemFromMemento(IMemento element) {
			return null;
		}

		@Override
		protected void storeItemToMemento(Object item, IMemento element) {
	    	// Must implement
		}
	}
	
	/**
	 * Filter. Case-insensitiv.
	 */
	private class MemberFilter extends ItemsFilter {
		@Override
		public boolean matchItem(Object o) {
			if (o instanceof Item item) {
				return matches(item.getFullName());
			}
			
			return false;
		}

		@Override
		public boolean isConsistentItem(Object o) {
			return o instanceof Item;
		}

		@Override
		protected boolean matches(String text) {
			return super.matches(text.toUpperCase(Locale.ROOT));
		}
	}
	
	public MemberSelectionDialog(Shell shell, boolean multi) {
		super(shell, multi);
		
		Activator act = Activator.getDefault();
		
		setTitle(act != null ? act.getString("Quickaccess.Title") : "");
		setSelectionHistory(new ResourceSelectionHistory());
		
		Session session = DbService.startTx();
		try {
			setItems(session);
		} finally {
			DbService.endTx(session);
		}
		
		setListLabelProvider(new MemberLabelProvider());
	}

	@SuppressWarnings("unchecked")
	private static void setItems(Session session) {
		try {
			items = session.createCriteria(Item.class).list();
		} catch (HibernateException e) {
			LOG.error("Cannot execute named query", e);
		}
	}

	@Override
	protected Control createExtendedContentArea(Composite parent) {
		// We don't need this
		return null;
	}

	@Override
	protected IDialogSettings getDialogSettings() {
		IDialogSettings settings = Activator.getDefault().getDialogSettings().getSection(DIALOG_SETTINGS);
		if (settings == null) {
			settings = Activator.getDefault().getDialogSettings().addNewSection(DIALOG_SETTINGS);
		}
		
		return settings;
	}

	@Override
	protected IStatus validateItem(Object o) {
		return Status.OK_STATUS;
	}
	
	@Override
	protected ItemsFilter createFilter() {
		return new MemberFilter();
	}

	@Override
	protected Comparator<Item> getItemsComparator() {
		return new ItemComparator(ItemComparator.Sorting.FULLNAME, ItemComparator.Direction.ASCENDING);
	}

	@Override
	protected void fillContentProvider(AbstractContentProvider contentProvider
									, ItemsFilter itemsFilter
									, IProgressMonitor progressMonitor) throws CoreException {

		IProgressMonitor subMonitor = SubMonitor.convert(progressMonitor, 100);
    	
		subMonitor.beginTask("Searching", items.size());
		
		for (Item item : items) {
			contentProvider.add(item, itemsFilter);
			subMonitor.worked(1);
		}
		subMonitor.done();
	}

	@Override
	public String getElementName(Object o) {
		if (o instanceof Item item) {
			return item.getFullName();
		}
		
		return o.toString();
	}
}
