/*********************************************************************
* Copyright (c) 10.10.2023 Thomas Zierer
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package de.tgmz.zdev.history;

import java.util.Calendar;

import org.eclipse.jface.preference.IPreferenceStore;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tgmz.zdev.preferences.ZdevPreferenceConstants;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator implements BundleActivator {
	// The plug-in ID
	public static final String PLUGIN_ID = "de.tgmz.zdev.history"; //$NON-NLS-1$

	private static final Logger LOG = LoggerFactory.getLogger(Activator.class);
	
	private static BundleContext context;

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
		
		clearHistory();
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(BundleContext bundleContext) {
		clearHistory();
		
		setContext(null);
	}
	
	private static void clearHistory() {
		// Provide some defaults in case the preference store is not active
		int months = 12;
		int versions = 100;
		
		de.tgmz.zdev.preferences.Activator activator = de.tgmz.zdev.preferences.Activator.getDefault();
		
		if (activator != null) {
			IPreferenceStore ps = activator.getPreferenceStore(); 
		
			months = ps.getInt(ZdevPreferenceConstants.HISTORY_MONTHS);
			versions = ps.getInt(ZdevPreferenceConstants.HISTORY_VERSIONS);
		}
		
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, -months);
			
		try {
			LocalHistory.getInstance().clear(cal.getTime(), versions);
		} catch (HistoryException e) {
			LOG.error("Error deleting history", e);
		}
		
	}
}
