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
import java.util.Objects;

import de.tgmz.zdev.domain.id.ItemId;
import jakarta.persistence.CascadeType;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Transient;

/**
 * A member stored in the database.
 * Use this to store additional attributes of a source e.g. compile options.
 */

@Entity
public class Item implements Serializable {
	@Transient
	private static final long serialVersionUID = -5230886354906404806L;
	@EmbeddedId
	private ItemId id;	
	private boolean lock;
	@OneToOne(fetch=FetchType.EAGER, cascade=CascadeType.ALL, orphanRemoval=true)
	private Option option;
	
	public Item() {
		this(null, null);
	}
	/**
	 * Constructor.
	 * @param aDsn Dataset name
	 * @param aMember Member name
	 */
	public Item(final String aDsn, final String aMember) {
		super();
		
		id = new ItemId(aDsn, aMember);
		
		option = new Option();
	}
	/**
	 * @return Returns the dataset name.
	 */
	public String getDsn() {
		return id.getDsn();
	}
	/**
	 * @param aDsn The dataset name to set.
	 */
	public void setDsn(final String aDsn) {
		this.id.setDsn(aDsn);
	}
	
	/**
	 * @param aMember The member name to set.
	 */
	public void setMember(final String aMember) {
		this.id.setMember(aMember);
	}

	/**
	 * @return Returns the member name.
	 */
	public String getMember() {
		return id.getMember();
	}
	public boolean isLock() {
		return lock;
	}
	public void setLock(boolean lock) {
		this.lock = lock;
	}
	/**
	 * @return returns the full name.
	 */
	public String getFullName() {
		return String.format("%s(%s)", id.getDsn(), id.getMember());
	}
	
	public Option getOption() {
		return option;
	}
	public void setOption(Option option) {
		this.option = option;
	}
	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Item other = (Item) obj;
		return Objects.equals(id, other.id);
	}
}
