/*********************************************************************
* Copyright (c) 10.10.2023 Thomas Zierer
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package de.tgmz.zdev.filelock;

import java.io.InputStream;
import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "de.tgmz.zdev.filelock";

	// The shared instance
	private static Activator plugin;
	
	private static final Logger LOG = LoggerFactory.getLogger(Activator.class);
	
    private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(Activator.PLUGIN_ID);
    
    private Image yDevIcon;

    /**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}
	private static void setDefault(Activator aPlugin) {
		Activator.plugin = aPlugin;
	}
    public String getString(final String key, Object... objects) {
        try {
        	MessageFormat mf = new MessageFormat(RESOURCE_BUNDLE.getString(key));

        	return mf.format(objects);
        } catch (@SuppressWarnings("unused") MissingResourceException e) {
        	LOG.warn("Resource {} missing", key);
        	
            return '!' + key + '!';
        }
    }
	//CHECKSTYLE DISABLE IllegalThrows
    // start() and stop() must call super... and both declare ... throws Exception.
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		setDefault(this);
		
        try (InputStream is = this.getClass().getClassLoader().getResourceAsStream("logo-16x16.png")) {
        	if (is == null) {
        		LOG.warn("Cannot load icon");
        	
        		yDevIcon = new Image(null, new Rectangle(0, 0, 10, 10));
        	} else {
        		yDevIcon = new Image(null, is);
        	}        	
        }
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(BundleContext context) throws Exception {
		if (FileLockClient.getInstance().isRunning()) {
			FileLockClient.getInstance().stop();
		}
		
		setDefault(null);
		super.stop(context);
	}
	//CHECKSTYLE ENABLE IllegalThrows

	public Image getYDevIcon() {
		return yDevIcon;
	}

	private static void showStatus(boolean error, String s) {
		IActionBars bars = null;
		IWorkbench workbench = PlatformUI.getWorkbench();
		IWorkbenchWindow workbenchWindow = workbench.getActiveWorkbenchWindow();
		
		if (workbenchWindow != null) {
			IWorkbenchPage activePage = workbenchWindow.getActivePage();
			
			if (activePage != null) {
				IWorkbenchPart activePart = activePage.getActivePart();

				if (activePart instanceof IViewPart ivp) {
					bars = ivp.getViewSite().getActionBars();
				} else {
					if (activePart instanceof IEditorPart iep) {
						bars = iep.getEditorSite().getActionBars();
					}
				}

				if (bars != null) {
					if (error) {
						bars.getStatusLineManager().setErrorMessage(Activator.getDefault().getYDevIcon(), s);
					} else {
						bars.getStatusLineManager().setMessage(Activator.getDefault().getYDevIcon(), s);
					}
				}
			}
		}
	}

	public void showInfo(String s) {
		showStatus(false, s);
	}

	public void showError(String s) {
		showStatus(true, s);
	}

}
