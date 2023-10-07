/*********************************************************************
* Copyright (c) 06.10.2023 Thomas Zierer
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package de.tgmz.zdev.editor;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.cics.common.util.StringUtil;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {
	// The plug-in ID
	public static final String PLUGIN_ID = "de.tgmz.zdev.editor"; //$NON-NLS-1$

	private static final Logger LOG = LoggerFactory.getLogger(Activator.class);
	// The shared instance
	private static Activator plugin;
	
	private Pattern pattern;
	
    private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(Activator.PLUGIN_ID);

	/**
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
        	
            return '!' + key + '!' + Arrays.toString(objects);
        }
    }
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	//CHECKSTYLE DISABLE IllegalThrows
    // start() und stop() müssen super... aufrufen und beide deklarieren ... throws Excpetion.
	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		setDefault(this);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(BundleContext context) throws Exception {
		setDefault(null);
		super.stop(context);
	}
	
	public Pattern getPattern() {
		return pattern;
	}
	
	public void setPattern(String s) {
		if (StringUtil.hasContent(s)) {
			this.pattern = Pattern.compile(s.replaceAll("[\\<\\(\\[\\{\\\\\\^\\-\\=\\$\\!\\|\\]\\}\\)\\?\\*\\+\\.\\>]", "\\\\$0"));
		}
	}
}
