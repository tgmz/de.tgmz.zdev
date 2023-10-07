/*********************************************************************
* Copyright (c) 06.10.2023 Thomas Zierer
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
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tgmz.zdev.database.DbService;
import de.tgmz.zdev.domain.History;
import de.tgmz.zdev.history.HistoryException;
import de.tgmz.zdev.history.model.IHistoryModel;

/**
 * History based on a H2 database with hibernate.
 */
public class JpaHistory implements IHistoryModel {
	private static final Logger LOG = LoggerFactory.getLogger(JpaHistory.class);
	
	@Override
	public long save(byte[] content, String itemName) throws HistoryException {
   		History c = new History();
   		
   		c.setContent(content);
   		c.setDsn(itemName);
   		c.setVersion((System.currentTimeMillis() / 1000) * 1000);

       	Session session = DbService.startTx();
       	
       	try {
       		session.saveOrUpdate(c);
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
       		History c = (History) session.createCriteria(History.class)
					.add(Restrictions.eq("version", key))
					.add(Restrictions.eq("dsn", itemName)).uniqueResult();
       		
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
       		@SuppressWarnings("unchecked")
			List<History> zwerg = session.createCriteria(History.class)
					.add(Restrictions.eq("dsn", itemName)).list();
       		
       		for (History c : zwerg) {
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
       		ScrollableResults itemCursor = session.createQuery("from History order by version desc").scroll();
       		
       		int count = 0;
       		int x = 0;
       		int y = 0;
       		
       		while (itemCursor.next()) {
       			++y;
       			
       		    History c = (History) itemCursor.get(0);
           	
				Integer i = m.get(c.getDsn());
				
				if (i != null) {
					++i;
				} else {
					i = 1;
				}
				
				m.put(c.getDsn(), i);
    				
    			if (c.getVersion() < timeout.getTime() || i > maxVersions) {
    				++x;
    				
    				session.delete(c);
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
