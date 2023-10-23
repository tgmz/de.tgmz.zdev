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

import java.net.URL;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tgmz.zdev.domain.HistoryItem;
import de.tgmz.zdev.domain.Item;

/**
 * The main contract of {@code DbService} is the creation of Session instances
 * and transaction management. {@code DbService} is a static class with no
 * state.
 * <p>
 * 
 * A typical usage should use the following idiom:
 * 
 * <pre>
 *    Session session = DbService.startTx();
 *    //do some work
 *    ...
 *    DbService.endTx(session);
 * </pre>
 * 
 */
public final class DbService {
	private static final Logger LOG = LoggerFactory.getLogger(DbService.class);
	/**
	 * @see org.hibernate.SessionFactory
	 */
	private static ThreadLocal<SessionFactory> factory;
	/**
	 * Private constructor for security reasons
	 */
	private DbService() {
	}

	/**
	 * Create a new SessionFactory, using the properties and mappings 
	 * in this configuration file with user name and password and Connection-Url (Jdbc-String).
	 * 
	 * @param hibernateCfg Configuration file
	 */
	public static synchronized void setSessionFactory(URL hibernateCfg, String url) {
		if(hibernateCfg == null) {
			throw new IllegalArgumentException("hibernate.cfg.xml cannot be null!!");
		}

		try {
			Configuration cfg = new Configuration().configure(hibernateCfg);

			cfg.addAnnotatedClass(Item.class);
			cfg.addAnnotatedClass(HistoryItem.class);
			
			if (url != null) {
				cfg.setProperty("hibernate.connection.url", url);
			}

			long start = System.nanoTime();
			
			StandardServiceRegistry ssr = new StandardServiceRegistryBuilder().applySettings(cfg.getProperties()).build();
			
			factory = ThreadLocal.withInitial(() -> cfg.buildSessionFactory(ssr));
			
			long end = System.nanoTime();
			LOG.info("Starttime SessionFactory: {} ms", (end - start) / 1000000.0);
			//CHECKSTYLE DISABLE IllegalCatch for 1 lines
		} catch (Exception ex) {
			throw new ExceptionInInitializerError(ex);
		}
	}

	/**
	 * Begin a unit of work and return the associated <tt>Session</tt> object.
	 * 
	 * @return a Session instance
	 * @see org.hibernate.Session
	 */
	public static Session startTx() {
		Session sess = getSession();
		sess.beginTransaction();
		return sess;
	}

	/**
	 * Flush the associated <tt>Session</tt> and end the unit of work
	 * 
	 * </p>
	 * This method will commit the underlying transaction if and only if the
	 * underlying transaction was initiated by this Session.
	 * 
	 * @param session
	 *            Session to commit.
	 * 
	 */
	public static void endTx(Session session) {
		try {
			session.getTransaction().commit();
		//CHECKSTYLE DISABLE IllegalCatch FOR 1 LINES
 		} catch (Exception e) {
			if (session.getTransaction() != null) {
				session.getTransaction().rollback();
			}
			throw e;
		}
	}

	/**
	 * Get a session Object from the central SessionFactory
	 * 
	 * @return The session.
	 * @see org.hibernate.SessionFactory
	 * @see org.hibernate.Session
	 */
	public static Session getSession() {
		SessionFactory tmpFactory = factory.get();

		if (tmpFactory != null) {
			return factory.get().getCurrentSession();
		} 
		throw new HibernateException("SessionFactory not initialized");
	}
	/**
	 * Call this once the database is no longer needed.
	 */
	public static void unload() {
		factory.remove();
	}
}
