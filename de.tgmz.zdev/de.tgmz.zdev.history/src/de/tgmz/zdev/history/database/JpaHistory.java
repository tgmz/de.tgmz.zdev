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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tgmz.zdev.database.DbService;
import de.tgmz.zdev.domain.HistoryItem;
import de.tgmz.zdev.history.HistoryDisplayItem;
import de.tgmz.zdev.history.HistoryException;
import de.tgmz.zdev.history.model.IHistoryModel;
import jakarta.persistence.EntityManager;

/**
 * History based on a H2 database with hibernate.
 */
public class JpaHistory implements IHistoryModel {
	private static final Logger LOG = LoggerFactory.getLogger(JpaHistory.class);
	
	@Override
	public HistoryDisplayItem save(String fqdn, byte[] content) throws HistoryException {
		HistoryItem hi = new HistoryItem();
		hi.setContent(content);
		hi.setFqdn(fqdn);

		DbService.getInstance().inTransaction(em -> em.persist(hi));
		
		return new HistoryDisplayItem(hi.getFqdn(), hi.getId(), hi.getContent().length);
	}

	@Override
	public byte[] retrieve(LocalDateTime key) throws HistoryException {
		try (EntityManager em = DbService.getInstance().getEntityManagerFactory().createEntityManager()) {
       		HistoryItem c = em.find(HistoryItem.class, key);
       		
       		return c != null ? c.getContent() : new byte[0];
		}
	}

	@Override
	public List<HistoryDisplayItem> getVersions(String fqdn) throws HistoryException {
		List<HistoryDisplayItem> result = new ArrayList<>();
		
		try (EntityManager em = DbService.getInstance().getEntityManagerFactory().createEntityManager()) {
			em.createNamedQuery("byFqdn", HistoryItem.class)
				.setParameter("fqdn", fqdn)
				.getResultStream()
				.forEach(hi -> result.add(new HistoryDisplayItem(hi.getFqdn(), hi.getId(), hi.getContent().length)));
		}
		
   		return result;
	}
	
	@Override
	public void clear(LocalDateTime offset, int maxVersions) {
		// Map a member to its number of history entries
		Map<String, Integer> m = new TreeMap<>();
		
		try (EntityManager em = DbService.getInstance().getEntityManagerFactory().createEntityManager()) {
			em.getTransaction().begin();
			
       		Iterator<HistoryItem> itemCursor = em.createQuery("SELECT hi FROM HistoryItem hi ORDER BY hi.id DESC", HistoryItem.class).getResultList().iterator();
       		
       		int x = 0;
       		int y = 0;
       		
       		while (itemCursor.hasNext()) {
       			++y;
       			
       		    HistoryItem hi = itemCursor.next();
           	
       		    int numVersions = m.merge(hi.getFqdn(), 1, (i,j) -> i + j);
    				
    			if (hi.getId().isBefore(offset) || numVersions > maxVersions) {
    				++x;

    				em.remove(hi);
    				
    				if (x % 100 == 0) {
    					em.getTransaction().commit();
    					em.getTransaction().begin();
    				}
    			}
			}
       		
			em.getTransaction().commit();
       		
       		LOG.info("Removed {} out of {} items from history", x, y);
		}
	}
}
