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
public class HistoryItemId implements Serializable {
	@Transient
	private static final long serialVersionUID = -1875520419999283029L;
    private String fqdn;
    private long version;

	public HistoryItemId() {
		super();
	}

	public HistoryItemId(String fqdn, long version) {
		super();
		this.fqdn = fqdn;
		this.version = version;
	}

	public String getFqdn() {
		return fqdn;
	}

	public void setFqdn(String fqdn) {
		this.fqdn = fqdn;
	}

	public long getVersion() {
		return version;
	}

	public void setVersion(long version) {
		this.version = version;
	}

	@Override
	public int hashCode() {
		return Objects.hash(fqdn, version);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		HistoryItemId other = (HistoryItemId) obj;
		return Objects.equals(fqdn, other.fqdn) && version == other.version;
	}
}
