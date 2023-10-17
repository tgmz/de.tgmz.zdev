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

import java.util.List;

import org.eclipse.compare.CompareConfiguration;
import org.eclipse.compare.CompareUI;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.cics.zos.model.Member;

/**
 * Handler for the Command "compare"
 */
public class CompareHandler extends AbstractHandler {
	private static final Logger LOG = LoggerFactory.getLogger(CompareHandler.class);

	/* (non-Javadoc)
	 * @see org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.ExecutionEvent)
	 */
	@Override
	public Object execute(ExecutionEvent event) {
		if (HandlerUtil.getCurrentSelection(event) instanceof IStructuredSelection iss) {
			if (iss.size() == 2) {
				List<?> list = iss.toList();
				
				Object o0 = list.get(0);
				Object o1 = list.get(1);
				
				if (o0 instanceof Member left && o1 instanceof Member right) {
					CompareConfiguration cc = new CompareConfiguration();
					cc.setLeftEditable(false);
					cc.setRightEditable(false);
				
					cc.setLeftLabel(left.toDisplayName());
					cc.setRightLabel(right.toDisplayName());
				
					CompareUI.openCompareDialog(new MemberCompareInput(cc, left, right));
				} else {
					// Should not happen. cf "visibleWhen section in plugin.xml
					LOG.warn("Selection is not a member: {} {}", o0.getClass(), o1.getClass());
				}
			} else {
				// dito
				LOG.warn("Selection contains {} elements", iss.size());
			}
		}
		
		return null;
	}
}
