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

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tgmz.zdev.domain.Item;

/**
 * Provider for label and text of an item.
 */
class MemberLabelProvider extends LabelProvider {
	private static final Logger LOG = LoggerFactory.getLogger(MemberLabelProvider.class);
	private Image fileImage;
	
	public MemberLabelProvider() {
		super();
		
		IWorkbench workbench;
		
		try {
			workbench = PlatformUI.getWorkbench();
			
			fileImage = workbench.getSharedImages().getImage(ISharedImages.IMG_OBJ_FILE);
		} catch (IllegalStateException e) {
			// Happens during testing
			LOG.debug("Exception accessing workbench", e);
		}
	}

	@Override
	public Image getImage(Object element) {
		if (element instanceof Item) {
			return fileImage;
		}
		
		return null;
	}

	@Override
	public String getText(Object element) {
		if (element instanceof Item item) {
			return item.getFullName();
		}

		return element == null ? "" : element.toString();
	}
}