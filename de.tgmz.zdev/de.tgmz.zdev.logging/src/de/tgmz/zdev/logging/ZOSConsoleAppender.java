/*********************************************************************
* Copyright (c) 10.10.2023 Thomas Zierer
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package de.tgmz.zdev.logging;

import java.io.IOException;
import java.io.Serializable;
import java.net.URL;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;

import de.tgmz.zdev.preferences.Activator;
import de.tgmz.zdev.preferences.ZdevPreferenceConstants;

/**
 * Logs to a dedicated console. 
 */
// note: class name need not match the @Plugin name.
@Plugin(name = "ZOSConsoleAppender", category = "Core", elementType = "appender", printObject = true)
public final class ZOSConsoleAppender extends AbstractAppender {
	private static final String CONSOLE_ID = "z/Dev";

	protected ZOSConsoleAppender(String name, Filter filter, Layout<? extends Serializable> layout, 
			boolean ignoreExceptions, Property[] properties) {
		super(name, filter, layout, ignoreExceptions, properties);
	}

	// Your custom appender needs to declare a factory method
	// annotated with `@PluginFactory`. Log4j will parse the configuration
	// and call this factory method to construct an appender instance with
	// the configured attributes.
	@PluginFactory
	public static ZOSConsoleAppender createAppender(@PluginAttribute("name") String name,
			@PluginElement("Layout") Layout<? extends Serializable> layout,
			@PluginElement("Filter") final Filter filter) {
		if (name == null) {
			LOGGER.error("No name provided for ZOSConsoleAppender");
			return null;
		}
		
		return new ZOSConsoleAppender(name
				, filter
				, layout != null ?  layout : PatternLayout.createDefaultLayout()
				, true
				, Property.EMPTY_ARRAY);
	}
	
	@Override
	public void append(LogEvent event) {
		Level level;
		
		Activator preferenceStoreActivator = de.tgmz.zdev.preferences.Activator.getDefault();

		if (preferenceStoreActivator == null) {
			level = Level.ERROR;
		} else {
			level = Level.toLevel(preferenceStoreActivator.getPreferenceStore().getString(ZdevPreferenceConstants.LOG_LEVEL));
		}

		if (event.getLevel().isMoreSpecificThan(level)) {
			MessageConsole mc = findZDevConsole();

			try (MessageConsoleStream ms = mc.newMessageStream()) {
				ms.print(this.getLayout().toSerializable(event).toString());
			} catch (@SuppressWarnings("unused") IOException e) {
				// Ignore this silently
			}
		}
	}

	private MessageConsole findZDevConsole() {
		IConsoleManager conMan = ConsolePlugin.getDefault().getConsoleManager();

		for (IConsole ic : conMan.getConsoles()) {
			if (CONSOLE_ID.equals(ic.getName())) {
				return (MessageConsole) ic;
			}
		}

		// no console found, so create a new one
		URL resource = this.getClass().getClassLoader().getResource("icons/logo-16-16.gif");

		ImageDescriptor id = resource != null ? ImageDescriptor.createFromURL(resource) : null;

		MessageConsole myConsole = new MessageConsole(CONSOLE_ID, id);
		conMan.addConsoles(new IConsole[] { myConsole });

		return myConsole;
	}
}