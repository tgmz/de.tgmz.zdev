/*********************************************************************
* Copyright (c) 10.10.2023 Thomas Zierer
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package de.tgmz.zdev.ui;

import java.util.List;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.cics.zos.model.Member;
import com.ibm.cics.zos.ui.DeleteDataEntryCommandHandler;

import de.tgmz.zdev.database.DbService;
import de.tgmz.zdev.domain.Item;

/**
 * Deletes a member and the corresponding item from the database.
 */
public class ZdevDeleteDataEntryCommandHandler extends DeleteDataEntryCommandHandler {
	private static final Logger LOG = LoggerFactory.getLogger(ZdevDeleteDataEntryCommandHandler.class);
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		// First delete the member and, if no exception occurs, the item.
		Object result = super.execute(event);
		
		ISelection selection = HandlerUtil.getCurrentSelection(event);

        Session session = DbService.startTx();
       	
        try {
        	if (selection instanceof IStructuredSelection iss && !iss.isEmpty()) {
            	List<?> dataEntries = iss.toList();
			
            	for (Object o : dataEntries) {
            		if (o instanceof Member) {
                    	Item zwerg0 = (Item) session.createCriteria(Item.class)
            					.add(Restrictions.eq("dsn", ((Member) o).getParentPath()))
            					.add(Restrictions.eq("member", ((Member) o).getName())).uniqueResult();

                    	if (zwerg0 != null) {
                    		session.delete(zwerg0);
                    	}
            		}
				}
        	}
   		} catch (HibernateException e) {
   			LOG.error("Database error", e);
   		} finally {
   			DbService.endTx(session);
		}
        
        return result;
	}
}
