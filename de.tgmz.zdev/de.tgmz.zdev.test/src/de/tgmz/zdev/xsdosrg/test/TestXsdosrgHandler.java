/*********************************************************************
* Copyright (c) 10.10.2023 Thomas Zierer
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package de.tgmz.zdev.xsdosrg.test;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

import com.ibm.cics.zos.model.HFSFile;
import com.ibm.cics.zos.model.Member;

import de.tgmz.zdev.xsdosrg.AbstractXsdosrgHandler;

/**
 * Creates an optimized schema representation from a set of schema files.
 */
public class TestXsdosrgHandler extends AbstractXsdosrgHandler {
	private Member m;
	
	public TestXsdosrgHandler(Member m) {
		super();
		this.m = m;
	}
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ISelection selection = HandlerUtil.getCurrentSelection(event);

		HFSFile schema = (HFSFile) ((IStructuredSelection) selection).getFirstElement();
		
		generateOsr(schema.getName(), schema.getParent(), m);
		
		return null;
	}
}
