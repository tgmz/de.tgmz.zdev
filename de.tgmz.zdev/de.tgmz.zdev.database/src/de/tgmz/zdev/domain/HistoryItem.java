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
import java.util.Arrays;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQuery;

/**
 * A historic item.
 */

@Entity
@NamedQuery(
		name="byDsn",
		query="FROM HistoryItem h WHERE h.dsn = :dsn"
)
@NamedQuery(
		name="byVersionAndDsn",
		query="FROM HistoryItem h WHERE h.version = :version AND h.dsn = :dsn"
)
public class HistoryItem implements Serializable {
	private static final long serialVersionUID = -5251258163182902698L;
	@Id
	@GeneratedValue
	private long id;
    private String dsn;
    private long version;
    @Column(columnDefinition = "BLOB")
    private byte[] content;

    /**
     * Konstruktor.
     */
    public HistoryItem() {
        super();
    }

	public String getDsn() {
		return dsn;
	}

	public void setDsn(String dsn) {
		this.dsn = dsn;
	}

	public long getVersion() {
		return version;
	}

	public void setVersion(long version) {
		this.version = version;
	}

	public byte[] getContent() {
		return Arrays.copyOf(content, content.length);
	}

	public void setContent(byte[] content) {
		this.content = Arrays.copyOf(content, content.length);
	}
}

