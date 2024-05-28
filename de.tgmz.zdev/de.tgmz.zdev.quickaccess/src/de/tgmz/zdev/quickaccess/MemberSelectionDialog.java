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

import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import org.eclipse.swt.widgets.Shell;
import org.hibernate.HibernateException;
import org.hibernate.Session;

import de.tgmz.zdev.database.DbService;
import de.tgmz.zdev.domain.Item;
import de.tgmz.zdev.view.AbstractFilteredItemsSelectionDialog;

/**
 * Class for selecting an item from a filtered list. 
 */
public class MemberSelectionDialog extends AbstractFilteredItemsSelectionDialog<Item> {
	private static final String DIALOG_SETTINGS = "MemberSelectionDialog";
	
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

		Session session = DbService.startTx();
		try {
			List<Item> items = session.createQuery("from Item", Item.class).list();
			
			setElements(items.toArray(new Item[items.size()]));
		} catch (HibernateException e) {
			LOG.error("Cannot execute named query", e);
		} finally {
			DbService.endTx(session);
		}
		
		
		Activator act = Activator.getDefault();
		
		setTitle(act != null ? act.getString("Quickaccess.Title") : "");
		
		setListLabelProvider(new MemberLabelProvider());
	}

	@Override
	protected ItemsFilter createFilter() {
		return new MemberFilter();
	}

	@Override
	protected Comparator<Item> getItemsComparator() {
		return (o1, o2) -> o1.getFullName().compareTo(o2.getFullName());
	}

	@Override
	public String getElementName(Object o) {
		if (o instanceof Item item) {
			return item.getFullName();
		}
		
		return o.toString();
	}

	@Override
	protected String getDialogSettingsId() {
		return DIALOG_SETTINGS;
	}
}
