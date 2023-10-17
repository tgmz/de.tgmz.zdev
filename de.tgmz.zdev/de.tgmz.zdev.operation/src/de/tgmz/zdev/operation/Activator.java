/*********************************************************************
* Copyright (c) 10.10.2023 Thomas Zierer
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package de.tgmz.zdev.operation;

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator implements BundleActivator {

	// The plug-in ID
	public static final String PLUGIN_ID = "de.tgmz.zdev.operation"; //$NON-NLS-1$

	private static BundleContext context;

	private static final Logger LOG = LoggerFactory.getLogger(Activator.class);
	
    private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(Activator.PLUGIN_ID);
    
	static BundleContext getContext() {
		return context;
	}

	private static void setContext(BundleContext context) {
		Activator.context = context;
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	@Override
	public void start(BundleContext bundleContext) {
		setContext(bundleContext);
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(BundleContext bundleContext) {
		setContext(null);
	}

    public static String getString(final String key, Object... objects) {
        try {
        	MessageFormat mf = new MessageFormat(RESOURCE_BUNDLE.getString(key));

        	return mf.format(objects);
        } catch (@SuppressWarnings("unused") MissingResourceException e) {
        	LOG.warn("Resource {} missing", key);
        	
            return '!' + key + '!';
        }
    }
}
