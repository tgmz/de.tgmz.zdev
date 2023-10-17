/*********************************************************************
* Copyright (c) 10.10.2023 Thomas Zierer
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package de.tgmz.zdev.view.copypaste;

import java.io.ByteArrayInputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.ui.PlatformUI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.cics.zos.model.Member;
import com.ibm.cics.zos.model.PartitionedDataSet;
import com.ibm.cics.zos.model.UpdateFailedException;

import de.tgmz.zdev.connection.ZdevConnectable;
import de.tgmz.zdev.view.Activator;
import de.tgmz.zdev.view.MemberUtility;

/**
 * Pastes the content of TransferUtility into the target dataset.
 */
public class PasteRunner implements IRunnableWithProgress {
	private static final Logger LOG = LoggerFactory.getLogger(PasteRunner.class);
	private PartitionedDataSet target;
	
	public PasteRunner(PartitionedDataSet target) {
		super();
		this.target = target;
	}
	@Override
	public void run(IProgressMonitor pm) throws InvocationTargetException, InterruptedException {
    	IProgressMonitor subMonitor = SubMonitor.convert(pm, 100);
    	
		List<Transfer> ts = TransferUtility.getInstance().getTransfers();
		
		subMonitor.beginTask("Paste", ts.size());
		
		for (Transfer t : ts) {
			Member m = MemberUtility.getInstance().getNewMember(target, t.getName());

			if (m != null) {
				try {
					ZdevConnectable.getConnectable().save(m, new ByteArrayInputStream(t.getContent()));
				} catch (UpdateFailedException e) {
					LOG.error("Cannot save member {}", m.getName(), e);
			
					MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell()
							, Activator.getDefault().getString("Paste.Title") 
							, Activator.getDefault().getString("Paste.Error", m.getName(), e.getMessage()));
            
					continue;
				}
			}
			
			subMonitor.worked(1);
		}
		
		subMonitor.done();
	}
}
