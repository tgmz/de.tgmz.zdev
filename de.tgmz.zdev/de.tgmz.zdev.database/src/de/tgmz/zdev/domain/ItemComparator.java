/*********************************************************************
* Copyright (c) 10.10.2023 Thomas Zierer
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package de.tgmz.zdev.domain;

import java.io.Serializable;
import java.util.Comparator;

/**
 * A comparison mechanism for sorting Items.
 */

public class ItemComparator implements Comparator<Item>, Serializable {
	private static final long serialVersionUID = 1996744398062390726L;
	
	/**
	 * The column used for sorting.
	 */
	public enum Sorting {
		NONE, FULLNAME, DSN, MEMBER 
	}
	
	private Sorting sorting;
	
	/**
	 * The sorting direction.
	 */
	public enum Direction {
		ASCENDING, DESCENDING 
	}
	
	private Direction direction;
	
	/**
	 * Constructor.
	 * 
	 * @param sorting the column to use for sorting
	 * @param direction the sorting direction
	 */
	public ItemComparator(Sorting sorting, Direction direction) {
		super();
		this.sorting = sorting;
		this.direction = direction;
	}

	/** {@inheritDoc} */
	@Override
	public int compare(final Item i1, final Item i2) {
		int result;
		
		int sign = direction == Direction.ASCENDING ? 1 : -1;

		switch (sorting) {
		case FULLNAME:
			result = sign * i1.getFullName().compareTo(i2.getFullName());
			break;
		case DSN:
			result = sign * i1.getDsn().compareTo(i2.getDsn());
			break;
		case MEMBER:
		default:
			result = sign * i1.getMember().compareTo(i2.getMember());
		}
		
		return result;
	}

	public void setSorting(Sorting sorting) {
		this.sorting = sorting;
	}

	public void setDirection(Direction direction) {
		this.direction = direction;
	}
}
