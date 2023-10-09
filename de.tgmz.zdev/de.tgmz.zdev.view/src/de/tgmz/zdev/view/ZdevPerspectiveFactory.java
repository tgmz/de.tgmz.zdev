/*********************************************************************
* Copyright (c) 09.10.2023 Thomas Zierer
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package de.tgmz.zdev.view;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.cics.zos.ui.views.JobView;

/**
 * z/Dev default perspektive.
 */
public class ZdevPerspectiveFactory implements IPerspectiveFactory {
	public static final String ID = "de.tgmz.zdev.zos.ui.perspective";
	private static final Logger LOG = LoggerFactory.getLogger(ZdevPerspectiveFactory.class);

	@Override
	public void createInitialLayout(IPageLayout layout) {
		LOG.debug("Layout {}", layout);
		
		layout.setEditorAreaVisible(true);

		String editorArea = layout.getEditorArea();

		IFolderLayout leftFolder = layout.createFolder("mvs", IPageLayout.LEFT, 0.33F, editorArea);
		leftFolder.addView("de.tgmz.zdev.view.ZdevDataSetsExplorer");
		leftFolder.addView("com.ibm.cics.zos.ui.views.hfsfiles");

		IFolderLayout jobsFolder = layout.createFolder("jobs", IPageLayout.BOTTOM, 0.66F, "mvs");
		jobsFolder.addView("com.ibm.cics.zos.ui.views.jobs");

		IFolderLayout bottomFolder = layout.createFolder("bottom", IPageLayout.BOTTOM, 0.66F, editorArea);
		bottomFolder.addView(JobView.ID);
		bottomFolder.addView("org.eclipse.ui.views.PropertySheet");
		bottomFolder.addView("org.eclipse.ui.console.ConsoleView");
		bottomFolder.addView("de.tgmz.zdev.markers.customMarkers");
		bottomFolder.addView("com.ibm.cics.explorer.connections_view");
		
		IFolderLayout rightFolder = layout.createFolder("right", IPageLayout.RIGHT, 0.75F, editorArea);
		rightFolder.addView("org.eclipse.ui.views.ContentOutline");
		
		layout.addNewWizardShortcut("com.ibm.cics.zos.ui.wizards.dataset");
		layout.addNewWizardShortcut("com.ibm.cics.zos.ui.wizards.datasetmember");
	}
}

