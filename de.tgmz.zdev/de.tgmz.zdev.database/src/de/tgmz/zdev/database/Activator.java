/*********************************************************************
* Copyright (c) 10.10.2023 Thomas Zierer
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package de.tgmz.zdev.database;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.core.resources.ResourcesPlugin;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator implements BundleActivator {
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
	public void start(BundleContext bundleContext) throws ParserConfigurationException, SAXException, IOException {
		setContext(bundleContext);
		
		URL hibernateConfirurationgUrl = this.getClass().getClassLoader().getResource("hibernate.cfg.xml");
		
		String jdbcUrl = "jdbc:h2:file:" 
					+ ResourcesPlugin.getWorkspace().getRoot().getLocation().toOSString() 
					+ File.separator 
					+ "Database" 
					+ File.separator 
					+ "Bender";
		
		LOG.info("Using {} for Bender Database", jdbcUrl);
		
		DbService.setSessionFactory(hibernateConfirurationgUrl, jdbcUrl); 
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(BundleContext bundleContext) {
		setContext(null);
		
		DbService.unload();
	}
}
