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

import java.util.Locale;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.PlatformUI;

import com.ibm.cics.zos.model.Member;
import com.ibm.cics.zos.model.PartitionedDataSet;

import de.tgmz.zdev.connection.ZdevConnectable;
import de.tgmz.zdev.view.DatasetSelectionDialog;
import de.tgmz.zdev.view.MemberNameInputDialog;

/**
 * Creates an optimized schema representation from a set of schema files.
 */
public class XsdosrgHandler extends AbstractXsdosrgHandler {
	@Override
	protected Member getOsrMember(IFile schema) {
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

	@Override
	protected void runFinish(Member m) {
		MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
				Activator.getDefault().getString(MSG_DIALOG_TITLE),
				Activator.getDefault().getString("Xsdosrg.success", m.toDisplayName()));
	}
}
