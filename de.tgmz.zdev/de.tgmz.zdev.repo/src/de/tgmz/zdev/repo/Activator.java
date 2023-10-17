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
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator implements BundleActivator {
    /** Logger. */
	private static final Logger LOG = LoggerFactory.getLogger(Activator.class);
	/** The folder for repository. */
	private static final String PROJECT_FOLDER = "zDev";

	private static BundleContext context;

	BundleContext getContext() {
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
    /**
     * Gets the utility project. The project is created if it does not exist already.
     * @return the utility project
     */
    public static IProject getZDevProject() {
        IWorkspace ws = ResourcesPlugin.getWorkspace();
        
        IProgressMonitor pm = new NullProgressMonitor();
        	
        IProject zDevProject = ws.getRoot().getProject(PROJECT_FOLDER);
            
        try {
        	IProjectDescription pd = ws.newProjectDescription(PROJECT_FOLDER);
        	
        	if (!zDevProject.exists()) {
        		zDevProject.create(pd, pm);
        	}
        	
        	if (!zDevProject.isOpen()) {
        		zDevProject.open(pm);
        	}
        } catch (CoreException e) {
           	// Happens if the physical folder is deleted.
        	LOG.warn("Core Exception, try again ... ", e);
                
        	try {
            	IProjectDescription pd = ws.newProjectDescription(PROJECT_FOLDER);
            	
            	zDevProject.delete(true, true, pm);
            	zDevProject.create(pd, pm);
            	zDevProject.open(pm);
        	} catch (CoreException e0) {
        		LOG.error("Core Exception, giving up.", e0);
        	}
        }
        
        return zDevProject;
    }
}
