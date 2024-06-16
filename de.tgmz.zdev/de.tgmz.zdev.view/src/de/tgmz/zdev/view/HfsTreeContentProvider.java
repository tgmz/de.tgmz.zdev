/*********************************************************************
* Copyright (c) 30.05.2024 Thomas Zierer
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package de.tgmz.zdev.view;

import java.io.FileNotFoundException;
import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.cics.core.comm.ConnectionException;
import com.ibm.cics.zos.model.HFSEntry;
import com.ibm.cics.zos.model.HFSFolder;
import com.ibm.cics.zos.model.IZOSConnectable;
import com.ibm.cics.zos.model.PermissionDeniedException;

public class HfsTreeContentProvider implements ITreeContentProvider {
	private static final Logger LOG = LoggerFactory.getLogger(HfsTreeContentProvider.class);
	private static final HFSEntry[] EMPTY = new HFSEntry[0];
	
	private IZOSConnectable connectable;

	public HfsTreeContentProvider(IZOSConnectable connectable) {
		super();
		this.connectable = connectable;
	}

	@Override
	public Object[] getElements(Object inputElement) {
		LOG.debug("getElements {}", inputElement);
		
		if (inputElement instanceof HFSFolder) {
			try {
				List<HFSEntry> children = connectable.getChildren((HFSFolder) inputElement, true);
			
				return  children.toArray(new HFSEntry[children.size()]);
			} catch (FileNotFoundException | PermissionDeniedException | ConnectionException e) {
				LOG.error("Cannot get children of {}", inputElement);
			}
		}
		
		return EMPTY;
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		LOG.debug("getChildren {}", parentElement);
		
		return getElements(parentElement);
	}

	@Override
	public Object getParent(Object element) {
		LOG.debug("getParent {}", element);
		
		return ((HFSEntry) element).getParent();
	}

	@Override
	public boolean hasChildren(Object element) {
		LOG.debug("hasChildren {}", element);
		
		return element instanceof HFSFolder && getChildren(element).length > 0;
	}
}
