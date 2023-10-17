/*********************************************************************
* Copyright (c) 10.10.2023 Thomas Zierer
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package de.tgmz.zdev.repo;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.QualifiedName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility for saving and reading simple properties.
 */
public final class Repository {
	private static final Logger LOG = LoggerFactory.getLogger(Repository.class);
    private static final String DEFAULT_NAMESPACE = "http://www.tgmz.de/zdev";
    private static final String LAST_DOWNLOAD_LOCATION = "lastDownloadLocation";

    /**
     * Must not instantiate.
     */
    private Repository() {
    }

    /**
     * Saves a property.
     * @param key property key
     * @param value property value
     */
    private static void setProperty(final String key, final String value) {
        try {
            IProject project = Activator.getZDevProject();

            if (project != null) {
                if (value == null) {
                    removeProperty(key);
                } else {
                    project.setPersistentProperty(new QualifiedName(DEFAULT_NAMESPACE, key), value);
                }
            }
        } catch (CoreException e) {
			LOG.error("Cannot set property {}", key);
        }
    }
    /**
     * Removes a property.
     * @param key property key
     */
    private static void removeProperty(final String key) {
        try {
            IProject project = Activator.getZDevProject();

            if (project != null) {
                project.setPersistentProperty(new QualifiedName(DEFAULT_NAMESPACE, key), null);
            }
        } catch (CoreException e) {
			LOG.error("Cannot remove property {}", key);
        }
    }
    /**
     * Reads a property.
     * @param key property key
     * @return property value
     */
    private static String getProperty(final String key) {
        String value = "";
        try {
            IProject project = Activator.getZDevProject();

            value = project.getPersistentProperty(new QualifiedName(DEFAULT_NAMESPACE, key));
            
            if (value == null) {
                value = "";
            }
        } catch (CoreException | IllegalStateException e) {
			LOG.error("Cannot get property {}", key);
			
			value = "";
        }

        return value;
    }
    
    public static String getLastDownloadLocation() {
        return getProperty( LAST_DOWNLOAD_LOCATION);
    }
    public static void storeLastDownloadLocation(String container) {
        setProperty(LAST_DOWNLOAD_LOCATION, container);
    }
}
