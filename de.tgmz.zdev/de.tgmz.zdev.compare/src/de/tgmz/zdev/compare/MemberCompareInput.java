/*********************************************************************
* Copyright (c) 10.10.2023 Thomas Zierer
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package de.tgmz.zdev.compare;

import org.eclipse.compare.CompareConfiguration;
import org.eclipse.compare.CompareEditorInput;
import org.eclipse.compare.structuremergeviewer.DiffNode;
import org.eclipse.compare.structuremergeviewer.ICompareInput;
import org.eclipse.core.runtime.IProgressMonitor;

import com.ibm.cics.zos.model.Member;

/**
 * Input for two members to be compared.
 */
public class MemberCompareInput extends CompareEditorInput {
	private Member left;
	private Member right;
	
	public MemberCompareInput(CompareConfiguration cc, Member left, Member right) {
		super(cc);
		
		this.left = left;
		this.right = right;
	}

	@Override
	protected ICompareInput prepareInput(IProgressMonitor pm) {
		return new DiffNode(new MemberCompareItem(left), new MemberCompareItem(right));
	}
}
