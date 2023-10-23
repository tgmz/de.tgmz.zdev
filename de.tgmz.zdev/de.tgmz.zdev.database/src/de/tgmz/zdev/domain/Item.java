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

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Transient;

/**
 * A member stored in the database.
 * Use this to store additional attributes of a source e.g. compile options.
 */

@Entity
@NamedQueries({
	@NamedQuery(
			name="byDsnAndMember",
			query="FROM Item i WHERE i.dsn = :dsn AND i.member = :member"
	)}
)
public class Item implements Serializable {
	@Transient
	private static final long serialVersionUID = -5230886354906404806L;
	@Id
	@GeneratedValue
	private long id;	
	/** The dataset name. Persistent. */
	private String dsn;
	/** The member name. Persistent. */
	private String member;
	
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
		this.dsn = aDsn;
		this.member = aMember;
	}
	/**
	 * @return returns the full name.
	 */
	public String getFullName() {
		return dsn.trim() + '(' + member.trim() + ')';
	}
	/**
	 * @return Returns the dataset name.
	 */
	public String getDsn() {
		return dsn;
	}
	/**
	 * @param aDsn The dataset name to set.
	 */
	public void setDsn(final String aDsn) {
		this.dsn = aDsn;
	}
	/**
	 * @return Returns the member name.
	 */
	public String getMember() {
		return member;
	}
	/**
	 * @param aMember The member name to set.
	 */
	public void setMember(final String aMember) {
		this.member = aMember;
	}
	/** {@inheritDoc} */
    @Override
    //BEGIN-GENERATED
	public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        
        if (!(obj instanceof Item)) {
            return false;
        }
        
        if (this == obj) {
            return true;
        }
        
        Item i1 = (Item) obj;
        
        return dsn.equals(i1.getDsn())
                && member.equals(i1.getMember());
    }
	/** {@inheritDoc} */
    @Override
	public int hashCode() {
        return dsn.hashCode() + member.hashCode();
    }
    //END-GENERATED
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	@Override
	public String toString() {
		return "Item [id=" + id + ", dsn=" + dsn + ", member=" + member + "]";
	}
}
