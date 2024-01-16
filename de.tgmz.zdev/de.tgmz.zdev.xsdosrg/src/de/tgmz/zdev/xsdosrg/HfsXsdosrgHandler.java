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

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

import com.ibm.cics.zos.model.HFSFile;
import com.ibm.cics.zos.model.Member;

/**
 * Creates an optimized schema representation from a set of HFS schema files.
 */
public class HfsXsdosrgHandler extends AbstractXsdosrgHandler {
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ISelection selection = HandlerUtil.getCurrentSelection(event);

		HFSFile schema = (HFSFile) ((IStructuredSelection) selection).getFirstElement();
		
		Member m = getOsrMember(schema.getName());
		
		if (m == null) {
			return null;
		}
		
		boolean success = generateOsr(schema.getName(), schema.getParent(), m);
		
		runFinish(m, success);
		
		return null;
	}
}
