/*********************************************************************
* Copyright (c) 10.10.2023 Thomas Zierer
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package de.tgmz.zdev.domain.id;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Transient;

@Embeddable
public class ItemId implements Serializable {
	@Transient
	private static final long serialVersionUID = -1875520419999283029L;
	private String dsn;
	private String member;

	public ItemId() {
		super();
	}

	public ItemId(String dsn, String member) {
		this();
		this.dsn = dsn;
		this.member = member;
	}
	
	public String getDsn() {
		return dsn;
	}

	public String getMember() {
		return member;
	}

	public void setDsn(String dsn) {
		this.dsn = dsn;
	}

	public void setMember(String member) {
		this.member = member;
	}

	@Override
	public int hashCode() {
		return Objects.hash(dsn, member);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ItemId other = (ItemId) obj;
		return Objects.equals(dsn, other.dsn) && Objects.equals(member, other.member);
	}
}
