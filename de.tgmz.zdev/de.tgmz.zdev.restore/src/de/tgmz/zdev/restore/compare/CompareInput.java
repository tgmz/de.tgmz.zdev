/*********************************************************************
* Copyright (c) 10.10.2023 Thomas Zierer
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package de.tgmz.zdev.restore.compare;

import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.eclipse.compare.CompareConfiguration;
import org.eclipse.compare.CompareEditorInput;
import org.eclipse.compare.structuremergeviewer.DiffNode;
import org.eclipse.compare.structuremergeviewer.ICompareInput;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.PlatformUI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.cics.zos.model.Member;
import com.ibm.cics.zos.model.UpdateFailedException;

import de.tgmz.zdev.connection.ZdevConnectable;
import de.tgmz.zdev.history.HistoryException;
import de.tgmz.zdev.history.LocalHistory;
import de.tgmz.zdev.restore.Activator;

/**
 * A compare operation between an editor content and a history version which can present its results in a special editor.
 */
public class CompareInput extends CompareEditorInput {
	private static final Logger LOG = LoggerFactory.getLogger(CompareInput.class);
	
	private Member member;
	private byte[] history;
	
	public CompareInput(CompareConfiguration cc, Member member, byte[] history) {
		super(cc);
		
		this.member = member;
		this.history = history;
		
		setDirty(true);
	}

	@Override
	protected ICompareInput prepareInput(IProgressMonitor pm) {
		return new DiffNode(new MemberCompareItem(member), new HistoryCompareItem(member.getName(), history));
	}
	@Override
	public void saveChanges(IProgressMonitor monitor) throws CoreException {
		ICompareInput compareResult = (ICompareInput) getCompareResult();
		
		HistoryCompareItem right = (HistoryCompareItem) compareResult.getRight();
		MemberCompareItem left = (MemberCompareItem) compareResult.getLeft();
		
		try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
			IOUtils.copy(left.getContents(), os);
			
			LocalHistory.getInstance().save(os.toByteArray(), left.getName());
		} catch (HistoryException | IOException e) {
			LOG.warn("Cannot write history for {}", member.toDisplayName(), e);
		}
		
		try {
			ZdevConnectable.getConnectable().save(left.getMember(), right.getContents());
		} catch (UpdateFailedException e) {
			LOG.warn("Cannot save {}", member.toDisplayName(), e);
			
			MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell()
					, Activator.getDefault().getString("Restore.Title") 
					, Activator.getDefault().getString("Restore.Error", (member).getName(), e.getMessage()));
		}
		
		setDirty(false);
	}
}
