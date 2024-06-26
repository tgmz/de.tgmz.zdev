/*********************************************************************
* Copyright (c) 09.10.2023 Thomas Zierer
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package de.tgmz.zdev.outline;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.ui.model.IWorkbenchAdapter;

/**
 * Node in Outline-View.
 */
public class MarkElement implements IWorkbenchAdapter, IAdaptable {
	private String headingName;
	private IAdaptable parent;
	private int offset;
	private int numberOfLines;
	private int length;
	private List<IAdaptable> children;
	private IAdaptable previous;
	private IAdaptable next;
	private boolean deleted;

	public MarkElement(IAdaptable prev, IAdaptable parent, String heading, int offset, int length) {
		this.parent = parent;
		
		if (parent instanceof MarkElement) {
			((MarkElement) parent).addChild(this);
		}
		
		if (heading == null) {
			this.headingName = "";
			this.length = 0;
		} else {
			this.headingName = heading.trim();
			this.length = this.headingName.length();
		}
		
		this.offset = offset;
		this.length = length;

		this.previous = null;
		this.next = null;
		
		if (prev instanceof MarkElement) {
			this.previous = prev;
			
			MarkElement p = (MarkElement) prev;
			
			p.setNext(this);
			p.setNumberOfLines(offset - p.getStart());
		}
		
		this.deleted = false;
	}

	private void addChild(IAdaptable child) {
		if (children == null) {
			children = new LinkedList<>();
		}
		
		children.add(child);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getAdapter(Class<T> adapter) {
		if (adapter == IWorkbenchAdapter.class)
			return (T) this;

		return null;
	}


	@Override
	public Object[] getChildren(Object o) {
		if (children != null) {
			return children.toArray();
		}
		
		return new Object[0];
	}

	public List<IAdaptable> getChildren() {
		return children;
	}

	@Override
	public ImageDescriptor getImageDescriptor(Object object) {
		// Use default ">"
		return null;
	}

	@Override
	public String getLabel(Object o) {
		return headingName;
	}

	public int getLength() {
		return length;
	}

	public int getNumberOfLines() {
		return numberOfLines;
	}

	public IAdaptable getParent() {
		return parent;
	}

	@Override
	public Object getParent(Object o) {
		return getParent();
	}

	public int getStart() {
		return offset;
	}

	public void setNumberOfLines(int newNumberOfLines) {
		numberOfLines = newNumberOfLines;
	}

	@Override
	public String toString() {
		return "MarkElement(" + headingName + ")";
	}

	public IAdaptable getNext() {
		return next;
	}

	public void setNext(IAdaptable newNext) {
		next = newNext;
	}

	public void setPrevious(IAdaptable newPrevious) {
		previous = newPrevious;
		
		if (previous instanceof MarkElement) {
			MarkElement p = (MarkElement) parent;
			
			p.setNext(this);
			
			int nLines = offset - p.getStart();
			
			if (nLines > 0) {
				p.setNumberOfLines(nLines);
			}
		}
	}

	public void setLabel(String label) {
		headingName = label;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public RGB getBackground() {
		return null;
	}
}
