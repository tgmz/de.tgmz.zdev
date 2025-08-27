/*********************************************************************
* Copyright (c) 10.10.2023 Thomas Zierer
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package de.tgmz.zdev.database;

import java.io.File;
import java.util.Collections;
import java.util.function.Consumer;

import org.eclipse.core.resources.ResourcesPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;

/**
 * The main contract of {@code DatabaseService} is the creation of Session instances
 * and transaction management. {@code DatabaseService} is a static class with no
 * state.
 * <p>
 * 
 * A typical usage should use the following idiom:
 * 
 * <pre>
 *	EntityManager em = DatabaseService.getInstance().getEntityManagerFactory().createEntityManager();
 *
 *	em.getTransaction().begin();
 *	//do some work
 *	em.getTransaction().commit();
 *
 *	em.close();
 * </pre>
 * 
 * For short transactions use e.g.
 * 
 * <pre>
 *	DatabaseService.getInstance().inTransaction((em) -> em.merge(o));
 * </pre>
 */
public final class DbService {
	private static final Logger LOG = LoggerFactory.getLogger(DbService.class);
	private static final DbService INSTANCE = new DbService();
	private EntityManagerFactory entityManagerFactory;

	/**
	 * Private constructor for security reasons
	 */
	private DbService() {
		long start = System.nanoTime();
		
		String jdbcUrl = "jdbc:h2:file:" 
				+ ResourcesPlugin.getWorkspace().getRoot().getLocation().toOSString() 
				+ File.separator 
				+ "Database" 
				+ File.separator 
				+ "Bender";
		
		entityManagerFactory = Persistence.createEntityManagerFactory("de.tgmz.zdev.database", Collections.singletonMap("jakarta.persistence.jdbc.url", jdbcUrl));
		
		LOG.info("Startuptime database service: {} ms", (System.nanoTime() - start) / 1000000.0);
	}

	public static DbService getInstance() {
		return INSTANCE;
	}

	public void inTransaction(Consumer<EntityManager> work) {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		EntityTransaction transaction = entityManager.getTransaction();
		try {
			transaction.begin();
			work.accept(entityManager);
			transaction.commit();
		} catch (Exception e) {
			if (transaction.isActive()) {
				transaction.rollback();
			}
			throw e;
		} finally {
			entityManager.close();
		}
	}

	public EntityManagerFactory getEntityManagerFactory() {
		return entityManagerFactory;
	}
}
