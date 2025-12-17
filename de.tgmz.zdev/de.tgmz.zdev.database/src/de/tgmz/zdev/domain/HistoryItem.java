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

import de.tgmz.zdev.domain.id.HistoryItemId;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.NamedQuery;

/**
 * A historic item.
 */

@Entity
@NamedQuery(
		name="byFqdn",
		query="SELECT hi FROM HistoryItem hi WHERE hi.id.fqdn LIKE :fqdn"
)
public class HistoryItem implements Serializable {
	private static final long serialVersionUID = -5251258163182902698L;
	@EmbeddedId
	private HistoryItemId id;
    @Column(columnDefinition = "BLOB")
    private byte[] content;

    /**
     * Konstruktor.
     */
    public HistoryItem() {
        super();
        
        id = new HistoryItemId();
    }

	public HistoryItemId getId() {
		return id;
	}

	public String getFqdn() {
		return id.getFqdn();
	}

	public void setFqdn(String fqdn) {
		this.id.setFqdn(fqdn);
	}

	public long getVersion() {
		return id.getVersion();
	}

	public void setVersion(long version) {
		this.id.setVersion(version);
	}

	public byte[] getContent() {
		return Arrays.copyOf(content, content.length);
	}

	public void setContent(byte[] content) {
		this.content = Arrays.copyOf(content, content.length);
	}
}

