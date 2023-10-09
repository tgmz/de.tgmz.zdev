/*********************************************************************
* Copyright (c) 06.10.2023 Thomas Zierer
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package de.tgmz.zdev.history;

import de.tgmz.zdev.history.database.JpaHistory;
import de.tgmz.zdev.history.model.IHistoryModel;

/**
 * Local Item
 */
public class LocalHistory {
	private static final IHistoryModel INSTANCE = new JpaHistory();

	private LocalHistory() {
	}

	public static synchronized IHistoryModel getInstance() {
		return INSTANCE;
	}
}
