/*********************************************************************
* Copyright (c) 10.10.2023 Thomas Zierer
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package de.tgmz.zdev.syntaxcheck.markers;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.views.markers.MarkerSupportView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Remote errors view.
 */
public class RemoteErrorMarkersView extends MarkerSupportView {
	private static final Logger LOG = LoggerFactory.getLogger(RemoteErrorMarkersView.class);

	public RemoteErrorMarkersView() {
		super("de.tgmz.zdev.markers.remoteErrorMarkerGenerator");
	}
	
	@Override
	public void createPartControl(final Composite parent) {
		// Quite ugly but the only way to register the mouse adapter
	    super.createPartControl(parent);
	    
	    LOG.info("Registring RemoteErrorMouseAdapter");

	    for (final Control control : parent.getChildren()) {
	        if (!(control instanceof Tree)) {
	            continue;
	        }

	        Tree tree = (Tree) control;

	        final Listener[] listeners = tree.getListeners(SWT.DefaultSelection);
	        if (listeners != null) {
	            for (final Listener listener : listeners) {
	                tree.removeListener(SWT.DefaultSelection, listener);
	            }
	        }
	        
	        tree.addMouseListener(new RemoteErrorMouseAdapter());
	        
		    LOG.info("RemoteErrorMouseAdapter succesfully registered");
	    }
	}
}

