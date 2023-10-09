/*********************************************************************
* Copyright (c) 09.10.2023 Thomas Zierer
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package de.tgmz.zdev.view.copypaste;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility for transferring members.
 */
public class TransferUtility {
	private static final Logger LOG = LoggerFactory.getLogger(TransferUtility.class);
	private static final TransferUtility INSTANCE = new TransferUtility();
	private List<Transfer> transfers;
	
	private TransferUtility() {
		transfers = new LinkedList<>();
	}
	
	public static final TransferUtility getInstance() {
		return INSTANCE;
	}
	
	/**
	 * Erases the local cache.
	 */
	public void reset() {
		LOG.debug("Deleting {} contents", transfers.size());
		
		transfers = new LinkedList<>();
	}
	
	public void put(String name, byte[]... content) {
		for (byte[] bs : content) {
			if (bs != null) {
				transfers.add(new Transfer(name, Arrays.copyOf(bs, bs.length)));
			}
		}
	}

	public List<Transfer> getTransfers() {
		return transfers;
	}
}
