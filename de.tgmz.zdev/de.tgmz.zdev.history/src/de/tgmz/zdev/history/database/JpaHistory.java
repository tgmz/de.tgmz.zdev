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

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tgmz.zdev.database.DbService;
import de.tgmz.zdev.domain.HistoryItem;
import de.tgmz.zdev.history.HistoryException;
import de.tgmz.zdev.history.HistoryIdentifyer;
import de.tgmz.zdev.history.model.IHistoryModel;
import jakarta.persistence.EntityManager;

/**
 * History based on a H2 database with hibernate.
 */
public class JpaHistory implements IHistoryModel {
	private static final Logger LOG = LoggerFactory.getLogger(JpaHistory.class);
	
	@Override
	public HistoryIdentifyer save(String fqdn, byte[] content) throws HistoryException {
		try (EntityManager em = DbService.getInstance().getEntityManagerFactory().createEntityManager()) {
			HistoryItem c = new HistoryItem();
   		
			c.setContent(content);
			c.setFqdn(fqdn);
			c.setVersion(Instant.now().toEpochMilli());

			em.persist(c);
		
			return new HistoryIdentifyer(fqdn, c.getVersion(), content.length);
		}
	}

	@Override
	public byte[] retrieve(HistoryIdentifyer key) throws HistoryException {
		try (EntityManager em = DbService.getInstance().getEntityManagerFactory().createEntityManager()) {
       		HistoryItem c = em.createNamedQuery("byVersion", HistoryItem.class).setParameter("version",  key.getId()).getSingleResult();
       		
       		return c != null ? c.getContent() : new byte[0];
		}
	}

	@Override
	public List<HistoryIdentifyer> getVersions(String fqdn) throws HistoryException {
		try (EntityManager em = DbService.getInstance().getEntityManagerFactory().createEntityManager()) {
			List<HistoryIdentifyer> result = new ArrayList<>();
	
			List<HistoryItem> zwerg = em.createNamedQuery("byFqdn", HistoryItem.class).setParameter("fqdn", fqdn).getResultList();
       		
       		for (HistoryItem c : zwerg) {
				result.add(new HistoryIdentifyer(c.getFqdn(), c.getVersion(), c.getContent().length));
			}
			
       		return result;
		}
	}
	
	@Override
	public void clear(Date timeout, int maxVersions) throws HistoryException {
		// Map a member to its number of history entries
		Map<String, Integer> m = new TreeMap<>();
		
		try (EntityManager em = DbService.getInstance().getEntityManagerFactory().createEntityManager()) {
       		Iterator<HistoryItem> itemCursor = em.createQuery("from HistoryItem order by version desc", HistoryItem.class).getResultList().iterator();
       		
       		int count = 0;
       		int x = 0;
       		int y = 0;
       		
       		while (itemCursor.hasNext()) {
       			++y;
       			
       		    HistoryItem c = itemCursor.next();
           	
				Integer i = m.get(c.getFqdn());
				
				if (i != null) {
					++i;
				} else {
					i = 1;
				}
				
				m.put(c.getFqdn(), i);
    				
    			if (c.getVersion() < timeout.getTime() || i > maxVersions) {
    				++x;
    				
    				em.remove(c);
    			}

    			if (++count % 100 == 0) {
    				em.flush();
    				em.clear();
    			}
			}
       		
       		LOG.info("Removed {} out of {} items from history", x, y);
		}
	}
}
