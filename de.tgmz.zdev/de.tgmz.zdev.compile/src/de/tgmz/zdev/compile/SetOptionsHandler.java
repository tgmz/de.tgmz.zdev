/*********************************************************************
* Copyright (c) 10.10.2023 Thomas Zierer
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package de.tgmz.zdev.compile;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.cics.zos.model.Member;

import de.tgmz.zdev.database.DbService;
import de.tgmz.zdev.domain.Item;
import jakarta.persistence.EntityManager;

public class SetOptionsHandler extends AbstractHandler {
	private static final Logger LOG = LoggerFactory.getLogger(SetOptionsHandler.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.
	 * ExecutionEvent)
	 */
	@Override
	public Object execute(ExecutionEvent event) {
		LOG.debug("Event {}", event);
		
		Member m = (Member) ((IStructuredSelection) HandlerUtil.getCurrentSelection(event)).getFirstElement();
		
		try (EntityManager em = DbService.getInstance().getEntityManagerFactory().createEntityManager()) {
			em.getTransaction().begin();
			
			Item item = em.createNamedQuery("byDsnAndMember", Item.class).setParameter("dsn", m.getParentPath()).setParameter("member", m.getName()).getSingleResultOrNull();

			if (item == null) {
				item = new Item(m.getParentPath(), m.getName());
			}
			
			ItemOptionsDialog d = new ItemOptionsDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), item);
			
			if ((item = d.open()) != null) {
				em.merge(item);
			}

			em.getTransaction().commit();
		}
		
		return null;
	}
}
