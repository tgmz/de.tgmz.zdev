/*********************************************************************
* Copyright (c) 10.10.2023 Thomas Zierer
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package de.tgmz.zdev.xsdosrg;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;

import org.apache.commons.io.IOUtils;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.cics.core.comm.ConnectionException;
import com.ibm.cics.zos.comm.IZOSConstants.FileType;
import com.ibm.cics.zos.model.HFSFile;
import com.ibm.cics.zos.model.HFSFolder;
import com.ibm.cics.zos.model.IJob;
import com.ibm.cics.zos.model.IJob.JobCompletion;
import com.ibm.cics.zos.model.Job;
import com.ibm.cics.zos.model.JobStep;
import com.ibm.cics.zos.model.Member;
import com.ibm.cics.zos.model.PartitionedDataSet;

import de.tgmz.zdev.connection.ZdevConnectable;
import de.tgmz.zdev.operation.JCLOperationRunnable;
import de.tgmz.zdev.preferences.ZdevPreferenceConstants;
import de.tgmz.zdev.view.DatasetSelectionDialog;
import de.tgmz.zdev.view.MemberNameInputDialog;

/**
 * Creates an optimized schema representation from a set of schema files.
 */
public abstract class AbstractXsdosrgHandler extends AbstractHandler {
	protected static final Logger LOG = LoggerFactory.getLogger(AbstractXsdosrgHandler.class);
	protected static final String MSG_DIALOG_TITLE = "Xsdosrg.Title";

	protected boolean generateOsr(String schema, HFSFolder workingFolder, Member member) {
		try {
			String script = generate("xsdosrg.sh"
					, getFullyQualyfiedDataset(member.toDisplayName())
					, schema
					, de.tgmz.zdev.preferences.Activator.getDefault().getPreferenceStore().getString(ZdevPreferenceConstants.XSDOSRG_LIBPATH));
			
			LOG.debug("Script generated: {}", script);
			
			InputStream scriptIs = IOUtils.toInputStream(script, StandardCharsets.UTF_8.name());
					
			HFSFile hfsScript = new HFSFile(ZdevConnectable.getConnectable(), workingFolder.getPath() + "/tmp.sh");
			ZdevConnectable.getConnectable().createAndRefresh(hfsScript, scriptIs, FileType.ASCII);
					
			String jcl = generate("xsdosrg.jcl", 
					de.tgmz.zdev.preferences.Activator.getDefault().getPreferenceStore().getString(ZdevPreferenceConstants.JOB_CARD),
					workingFolder.toDisplayName(),
					hfsScript.getName());

			LOG.debug("JCL generated: {}", jcl);
			
			JCLOperationRunnable xsdosrgRunner = new JCLOperationRunnable(jcl);
					
			runInUI(xsdosrgRunner);
					
			IJob job = ZdevConnectable.getConnectable().getJob(xsdosrgRunner.getJobDetails().getId());
				
			if (job.getCompletion() != JobCompletion.NORMAL) {
				MessageDialog.openWarning(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
						Activator.getDefault().getString(MSG_DIALOG_TITLE),
						Activator.getDefault().getString("Xsdosrg.runjclerror", job.getId()));
			} else {
				JobStep jobStep = ZdevConnectable.getConnectable().getSteps((Job) job).get(3);

				//CHECKSTYLE DISABLE IllegalInstantiation for 1 lines
				String s = new String(ZdevConnectable.getConnectable().getSpool(jobStep).toByteArray(), StandardCharsets.ISO_8859_1);

				if (s.contains("FATAL")) {
					LOG.error(s);

					MessageDialog.openWarning(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
							Activator.getDefault().getString(MSG_DIALOG_TITLE),
							Activator.getDefault().getString("Xsdosrg.runjclerror", job.getId()));

					return false;
				} else {
					LOG.info(s);

					ZdevConnectable.getConnectable().delete(job);
				}
			}
		} catch (IOException | ConnectionException e) {
			LOG.error("Cannot upload and/or run xsdosrg script", e);
					
			MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
					Activator.getDefault().getString(MSG_DIALOG_TITLE),
					Activator.getDefault().getString("Xsdosrg.uploadscript"));
		}

		return true;
	}
	
	protected void runFinish(Member m, boolean success) {
		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		String title = Activator.getDefault().getString(MSG_DIALOG_TITLE);
		
		if (success) {
			MessageDialog.openInformation(shell, title, Activator.getDefault().getString("Xsdosrg.success", m.toDisplayName()));
		} else {
			MessageDialog.openError(shell, title, Activator.getDefault().getString("Xsdosrg.failure"));
		}
	}
	
	private String generate(String template, String... args) throws IOException {
		try (InputStream is = this.getClass().getClassLoader().getResourceAsStream("templates/" + template)) {
			String s0 = IOUtils.toString(is, StandardCharsets.UTF_8.name());

			MessageFormat mf = new MessageFormat(s0);
		
			return mf.format(args);
		}
	}
	
	protected boolean runInUI(IRunnableWithProgress runner) {
		try {
			PlatformUI
           		.getWorkbench()
           		.getProgressService()
           		.runInUI(PlatformUI.getWorkbench().getProgressService(), runner, ResourcesPlugin.getWorkspace().getRoot());
		} catch (InvocationTargetException e) {
			LOG.error("Error running in UI", e);
			
			MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
					Activator.getDefault().getString(MSG_DIALOG_TITLE),
					Activator.getDefault().getString("Xsdosrg.operationfailed"));
			
			return false;
		} catch (InterruptedException e) {
			LOG.error("Thread got interrupted", e);

			Thread.currentThread().interrupt();
		}
		
		return true;
	}
	
	private static String getFullyQualyfiedDataset(String s) {
		return "//'" + s + "'";
	}
	
	protected Member getOsrMember(String schema) {
		DatasetSelectionDialog dsd = new DatasetSelectionDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell()
				, DatasetSelectionDialog.AllowedTypes.PDS
				, DatasetSelectionDialog.AllowedTypes.MEMBER);

		if (!dsd.open()) {
			return null;
		}
		
		if (dsd.getTarget() instanceof PartitionedDataSet) {
			InputDialog id = new MemberNameInputDialog(
					PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), 
					Activator.getDefault().getString(MSG_DIALOG_TITLE),
					Activator.getDefault().getString("Xsdosrg.membername"),
					schema);

			if (id.open() == Window.CANCEL) {
				return null;
			}

			return new Member(dsd.getTarget().getPath(), id.getValue(), ZdevConnectable.getConnectable());
		} else {
			return (Member) dsd.getTarget();
		}
	}
}
