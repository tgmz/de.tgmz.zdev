/*********************************************************************
* Copyright (c) 03.12.2023 Thomas Zierer
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package de.tgmz.zdev.history;

import java.text.DateFormat;
import java.text.MessageFormat;
import java.util.Date;

public class HistoryIdentifyer {
	protected static final ThreadLocal<DateFormat> DF = ThreadLocal.withInitial(() -> DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG));
	
	private String fqdn;
	private long id;
	private long size;
	
	public HistoryIdentifyer(String fqdn, long id, long size) {
		super();
		this.fqdn = fqdn;
		this.id = id;
		this.size = size;
	}
	public long getId() {
		return id;
	}
	public long getSize() {
		return size;
	}
	public String getFqdn() {
		return fqdn;
	}
	@Override
	public String toString() {
		return MessageFormat.format("{0} - {1} ({2} bytes)", fqdn, DF.get().format(new Date(id)), size);
	}
}
