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
import java.security.SecureRandom;
import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import org.apache.commons.io.IOUtils;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

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
import com.ibm.cics.zos.model.UpdateFailedException;

import de.tgmz.zdev.connection.ZdevConnectable;
import de.tgmz.zdev.operation.JCLOperationRunnable;
import de.tgmz.zdev.preferences.ZdevPreferenceConstants;
import de.tgmz.zdev.transfer.upload.HfsUploadRunner;
import de.tgmz.zdev.view.DatasetSelectionDialog;
import de.tgmz.zdev.view.MemberNameInputDialog;

/**
 * Creates an optimized schema representation from a set of schema files.
 */
public class XsdosrgHandler extends AbstractHandler {
	private static final Logger LOG = LoggerFactory.getLogger(XsdosrgHandler.class);
	private static final SecureRandom SR = new SecureRandom();
	protected static final String MSG_DIALOG_TITLE = "Xsdosrg.Title";

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ISelection selection = HandlerUtil.getCurrentSelection(event);

		IFile schema = (IFile) ((IStructuredSelection) selection).getFirstElement();
		
		try {
			LocalXsdValidator.getInstance().validate(schema);
		} catch (SAXException e) {
			if (!MessageDialog.openQuestion(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
					Activator.getDefault().getString(MSG_DIALOG_TITLE),
					Activator.getDefault().getString("Xsdosrg.validatelocal", e.getMessage()))) {

				return null;
			}
		}

		HFSFolder workingFolder = createWorkingFolder();
		
		Member m = getOsrMember(schema);
		
		if (m == null) {
			return null;
		}
		
		boolean success = generateOsr(schema, workingFolder, m.toDisplayName());
		
		cleanupWorkingFolder(workingFolder);
		
		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		String title = Activator.getDefault().getString(MSG_DIALOG_TITLE);
		
		if (success) {
			MessageDialog.openInformation(shell, title, Activator.getDefault().getString("Xsdosrg.success", m.toDisplayName()));
		} else {
			MessageDialog.openError(shell, title, Activator.getDefault().getString("Xsdosrg.failure"));
		}
		
		return null;
	}

	private boolean generateOsr(IFile schema, HFSFolder workingFolder, String member) {
		List<IResource> schemas = getSchemas(schema);
		
		if (!runInUI(new HfsUploadRunner(workingFolder, schemas, FileType.BINARY))) {
				
			return false;
		}

		try {
			String script = generate("xsdosrg.sh"
					, getFullyQualyfiedDataset(member)
					, schema.getName()
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
	
	private String generate(String template, String... args) throws IOException {
		try (InputStream is = this.getClass().getClassLoader().getResourceAsStream("templates/" + template)) {
			String s0 = IOUtils.toString(is, StandardCharsets.UTF_8.name());

			MessageFormat mf = new MessageFormat(s0);
		
			return mf.format(args);
		}
	}
	
	private boolean runInUI(IRunnableWithProgress runner) {
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
	
	/**
	 * Get a list of all xsd files in parent folder
	 * @param schema
	 * @return
	 */
	private List<IResource> getSchemas(IFile schema) {
		IContainer parent = schema.getParent();

		List<IResource> schemas = new LinkedList<>();

		try {
			for (IResource member : parent.members()) {
				if (member instanceof IFile && "xsd".equals(member.getFileExtension())) {
					schemas.add(member);
				}
			}
		} catch (CoreException e) {
			LOG.error("Cannot get list of schemas", e);
		}

		return schemas;
	}
	
	private void cleanupWorkingFolder(HFSFolder tmp) {
		runInUI(new CleanupRunner(tmp));
	}
	
	private static HFSFolder createWorkingFolder() throws ExecutionException {
		for (int i = 0; i < 10; ++i) {
			HFSFolder tmp = new HFSFolder(ZdevConnectable.getConnectable(), "/tmp/osrg" + SR.nextInt(100000));
			
			if (!ZdevConnectable.getConnectable().exists(tmp)) {
				try {
					ZdevConnectable.getConnectable().create(tmp);
					
					return tmp;
				} catch (UpdateFailedException e) {
					LOG.warn("Creation of tmp folder failed, retrying ...", e);
				}
			} else {
				LOG.warn("Tmp folder {} exists, retrying ...", tmp);
			}
		}
		
		throw new ExecutionException("Creation of working folder failed, see z/Dev console log for details");
	}
	
	private static String getFullyQualyfiedDataset(String s) {
		return "//'" + s + "'";
	}
	
	private Member getOsrMember(IFile schema) {
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
					schema.getName().toUpperCase(Locale.ROOT));

			if (id.open() == Window.CANCEL) {
				return null;
			}

			return new Member(dsd.getTarget().getPath(), id.getValue(), ZdevConnectable.getConnectable());
		} else {
			return (Member) dsd.getTarget();
		}
	}
}
