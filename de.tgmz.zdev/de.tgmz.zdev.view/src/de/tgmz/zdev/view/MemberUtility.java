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

import java.io.FileNotFoundException;

import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.PlatformUI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.cics.core.comm.ConnectionException;
import com.ibm.cics.zos.model.DataEntry;
import com.ibm.cics.zos.model.Member;
import com.ibm.cics.zos.model.PartitionedDataSet;
import com.ibm.cics.zos.model.PermissionDeniedException;

import de.tgmz.zdev.connection.ZdevConnectable;
import de.tgmz.zdev.view.copypaste.MemberNameValidator;

/**
 * Utility for transferring  members.
 */
public class MemberUtility {
	private static final Logger LOG = LoggerFactory.getLogger(MemberUtility.class);
	private static final MemberUtility INSTANCE = new MemberUtility();
	private static final String TITLE = Activator.getDefault().getString("Paste.Title");
	
	private MemberUtility() {
	}
	
	public static final MemberUtility getInstance() {
		return INSTANCE;
	}
	/**
	 * Computes the member to save. This is either an new member, an existing one, if the user presses &quot;Overwrite&quot; 
	 * or <code>null</code> on &quot;Cancel&quot;.
	 * @param target PartitionedDataSet
	 * @param oldMemberName old name
	 * @return the new Member
	 */
	public Member getNewMember(PartitionedDataSet target, String oldMemberName) {
		Member result = null;
		String newMemberName = oldMemberName;
		
		boolean finish = false;
		
		// Check if the name is valid.
		if (new MemberNameValidator().isValid(newMemberName) != null) {
			InputDialog id = new MemberNameInputDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell()
					, TITLE 
					, Activator.getDefault().getString("Paste.InvalidMemberName") 
					, newMemberName);
			
			if (id.open() == Window.CANCEL) {
				return null;
			}
			
			newMemberName = id.getValue();
		}	

		// We check until a FileNotFoundException occurs or the user presses "Overwrite"
		do {
			try {
				result = ZdevConnectable.getConnectable().getDataSetMember(target, newMemberName); // throws FileNotFoundException

				// If no FileNotFoundException occurred the member exists already and we must ask the user to overwrite.
				// The InputValidator ensures that the name is valid.
				InputDialog id = new MemberNameInputDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell()
						, TITLE 
						, Activator.getDefault().getString("Paste.Overwrite2", newMemberName) 
						, newMemberName);
				
				if (id.open() == Window.CANCEL) {
					return null;
				}
				
				if (newMemberName.equals(id.getValue())) {
					// OK clicked and name is unchanged: Overwrite
					finish = true;
				} else {
					// OK clicked and name has changed: Check again
					newMemberName = id.getValue();
				}
			} catch (FileNotFoundException e) {
				LOG.debug("File not found", e);

				finish = true;

				result = (Member) DataEntry.newFrom(target.getFullPath() + "(" + newMemberName + ")", ZdevConnectable.getConnectable());
			} catch (PermissionDeniedException e ){
				LOG.error("Cannot create member {}", result, e);

				MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell()
						, TITLE 
						, Activator.getDefault().getString("Paste.NotPermitted", target.getName()));
        
				finish = true;	// We can stop here, it no longer makes sense
			} catch (ConnectionException e ){
				LOG.error("Cannot create member {}", result, e);

				MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell()
						, TITLE 
						, Activator.getDefault().getString("Paste.Error", result, e.getMessage()));
        
				finish = true;	// We can stop here, it no longer makes sense
			}
		} while (!finish);
				
		return result;
	}
}
