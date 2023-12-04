/*********************************************************************
* Copyright (c) 10.10.2023 Thomas Zierer
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package de.tgmz.zdev.restore.compare;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Comparator;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HistoryItemComparator implements Comparator<String> {
	private static final Logger LOG = LoggerFactory.getLogger(HistoryItemComparator.class);
	protected static final ThreadLocal<DateFormat> DF = ThreadLocal.withInitial(() -> DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG));

	@Override
	public int compare(String o1, String o2) {
		try {
			return fromDisplay(o2) - fromDisplay(o1) > 0 ? 1 : -1; // fromKey(o2) - fromKey(o1) yields overflow!
		} catch (ParseException e) {
			LOG.error("Cannot compare {} and {}, reason:" , o1, o2, e);
			
			return 0;
		}
	}
	public static long fromDisplay(String display) throws ParseException {
		return DF.get().parse(display).getTime();
	}
	
	public static String fromTime(long time) {
		return DF.get().format(new Date(time));
	}
}
