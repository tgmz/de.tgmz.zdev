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

import java.security.SecureRandom;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.xml.sax.SAXException;

import com.ibm.cics.zos.comm.IZOSConstants.FileType;
import com.ibm.cics.zos.model.HFSFolder;
import com.ibm.cics.zos.model.Member;
import com.ibm.cics.zos.model.UpdateFailedException;

import de.tgmz.zdev.connection.ZdevConnectable;
import de.tgmz.zdev.transfer.upload.HfsUploadRunner;

/**
 * Creates an optimized schema representation from a set of local schema files.
 */
public class XsdosrgHandler extends AbstractXsdosrgHandler {
	private static final SecureRandom SR = new SecureRandom();

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
		
		Member m = getOsrMember(schema.getName());
		
		if (m == null) {
			return null;
		}
		
		List<IResource> schemas = getSchemas(schema);
		
		if (!runInUI(new HfsUploadRunner(workingFolder, schemas, FileType.BINARY))) {
				
			return false;
		}
		
		boolean success = generateOsr(schema, workingFolder, m);
		
		cleanupWorkingFolder(workingFolder);
		
		runFinish(m, success);
		
		return null;
	}

	private boolean generateOsr(IFile schema, HFSFolder workingFolder, Member member) {
		List<IResource> schemas = getSchemas(schema);
		
		if (!runInUI(new HfsUploadRunner(workingFolder, schemas, FileType.BINARY))) {
				
			return false;
		}
		
		return super.generateOsr(schema.getName(), workingFolder, member);
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
}
