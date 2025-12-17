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
import java.text.DateFormat;
import java.text.MessageFormat;
import java.util.Date;
import java.util.Objects;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Transient;

@Embeddable
public class HistoryItemId implements Serializable {
	@Transient
	private static final long serialVersionUID = -1875520419999283029L;
	@Transient
	private static final ThreadLocal<DateFormat> DF = ThreadLocal.withInitial(() -> DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG));

    private String fqdn;
    private long version;
    @Transient
    private long size;

	public HistoryItemId() {
		super();
	}

	public HistoryItemId(String fqdn, long version) {
		this(fqdn, version, 0L);
	}

	public HistoryItemId(String fqdn, long version, long size) {
		super();
		this.fqdn = fqdn;
		this.version = version;
		this.size = size;
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

	public long getSize() {
		return size;
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
	
	@Override
	public String toString() {
		return MessageFormat.format("{0} - {1} ({2} bytes)", fqdn, DF.get().format(new Date(version)), size);
	}
}
