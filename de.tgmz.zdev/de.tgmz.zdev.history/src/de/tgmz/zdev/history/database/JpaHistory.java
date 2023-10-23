/*********************************************************************
* Copyright (c) 10.10.2023 Thomas Zierer
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package de.tgmz.zdev.history.database;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.hibernate.HibernateException;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tgmz.zdev.database.DbService;
import de.tgmz.zdev.domain.HistoryItem;
import de.tgmz.zdev.history.HistoryException;
import de.tgmz.zdev.history.model.IHistoryModel;

/**
 * History based on a H2 database with hibernate.
 */
public class JpaHistory implements IHistoryModel {
	private static final Logger LOG = LoggerFactory.getLogger(JpaHistory.class);
	
	@Override
	public long save(byte[] content, String itemName) throws HistoryException {
   		HistoryItem c = new HistoryItem();
   		
   		c.setContent(content);
   		c.setDsn(itemName);
   		c.setVersion((System.currentTimeMillis() / 1000) * 1000);

       	Session session = DbService.startTx();
       	
       	try {
       		session.persist(c);
		} catch (HibernateException e) {
			throw new HistoryException(e);
		} finally {
			DbService.endTx(session);
		}
		
		return c.getVersion();
	}

	@Override
	public byte[] retrieve(long key, String itemName) throws HistoryException {
       	Session session = DbService.startTx();
       	
       	try {
       		HistoryItem c = session.createNamedQuery("byVersionAndDsn", HistoryItem.class).setParameter("version",  key).setParameter("dsn", itemName).uniqueResult();
       		
       		return c != null ? c.getContent() : new byte[0];
		} catch (HibernateException e) {
			throw new HistoryException(e);
		} finally {
			DbService.endTx(session);
		}
	}

	@Override
	public List<Long> getVersions(String itemName) throws HistoryException {
		List<Long> result = new LinkedList<>();
	
       	Session session = DbService.startTx();
       	
       	try {
			List<HistoryItem> zwerg = session.createNamedQuery("byDsn", HistoryItem.class).setParameter("dsn", itemName).list();
       		
       		for (HistoryItem c : zwerg) {
				result.add(c.getVersion());
			}
		} catch (HibernateException e) {
			throw new HistoryException(e);
		} finally {
			DbService.endTx(session);
		}
			
		return result;
	}
	
	@Override
	public void clear(Date timeout, int maxVersions) throws HistoryException {
		// Map a member to its number of history entries
		Map<String, Integer> m = new TreeMap<>();
		
       	Session session = DbService.startTx();
       	
       	try {
       		ScrollableResults<HistoryItem> itemCursor = session.createQuery("from HistoryItem order by version desc", HistoryItem.class).scroll();
       		
       		int count = 0;
       		int x = 0;
       		int y = 0;
       		
       		while (itemCursor.next()) {
       			++y;
       			
       		    HistoryItem c = (HistoryItem) itemCursor.get();
           	
				Integer i = m.get(c.getDsn());
				
				if (i != null) {
					++i;
				} else {
					i = 1;
				}
				
				m.put(c.getDsn(), i);
    				
    			if (c.getVersion() < timeout.getTime() || i > maxVersions) {
    				++x;
    				
    				session.remove(c);
    			}

    			if (++count % 100 == 0) {
    				session.flush();
    				session.clear();
    			}
			}
       		
       		LOG.info("Removed {} out of {} items from history", x, y);
		} catch (HibernateException e) {
			throw new HistoryException(e);
		} finally {
			DbService.endTx(session);
		}
	}
}
