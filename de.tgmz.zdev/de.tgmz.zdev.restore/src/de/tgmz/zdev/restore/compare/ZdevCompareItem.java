/*********************************************************************
* Copyright (c) 10.10.2023 Thomas Zierer
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package de.tgmz.zdev.restore.compare;

import java.io.InputStream;
import java.util.Arrays;

import org.eclipse.compare.IEditableContent;
import org.eclipse.compare.IStreamContentAccessor;
import org.eclipse.compare.ITypedElement;
import org.eclipse.swt.graphics.Image;

/**
 * Common services for CompareItems.
 */
public abstract class ZdevCompareItem implements IStreamContentAccessor, ITypedElement, IEditableContent {
	protected String name;
	protected byte[] contents;

	protected ZdevCompareItem(String name) {
		this.name = name;
	}

	@Override
	public abstract InputStream getContents();

	@Override
	public Image getImage() {
		return null;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getType() {
		return ITypedElement.TEXT_TYPE;
	}

	@Override
	public boolean isEditable() {
		return false;
	}

	@Override
	public void setContent(byte[] newContent) {
		if (newContent == null) {
			this.contents = new byte[0];
		} else {
			this.contents = Arrays.copyOf(newContent, newContent.length);
		}
	}

	@Override
	public ITypedElement replace(ITypedElement dest, ITypedElement src) {
		return null;
	}
}
