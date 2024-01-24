/*********************************************************************
* Copyright (c) 10.10.2023 Thomas Zierer
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package de.tgmz.zdev.history.model;

import java.util.Date;
import java.util.List;

import de.tgmz.zdev.history.HistoryException;
import de.tgmz.zdev.history.HistoryIdentifyer;

/**
 * The history model
 */
public interface IHistoryModel {
	/**
	 * Saves the content of an item in the history and returns an identifier
	 * @param content the content
	 * @param fqdn fully qualified dataset name
	 * @return identifier in history
	 * @throws HistoryException if something happens
	 */
	HistoryIdentifyer save(byte[] content, String fqdn) throws HistoryException;
	/**
	 * Retrieves the contents of an item from the history
	 * @param key the identifier
	 * @return the contents
	 * @throws HistoryException if something happens
	 */
	byte[] retrieve(long key) throws HistoryException;
	/**
	 * Returns the list of identifiers of an item in the history
	 * @param fqdn fully qualified dataset name
	 * @return the list of identifiers
	 * @throws HistoryException if something happens
	 */
	List<HistoryIdentifyer> getVersions(String fqdn) throws HistoryException;
	/**
	 * Clears the history
	 * @param offset Delete all entries older than offset
	 * @param maxVersions Maximum number of history entries to retain
	 * @throws HistoryException if something happens
	 */
	void clear(Date offset, int maxVersions) throws HistoryException;
}
