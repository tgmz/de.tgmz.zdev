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
import java.time.LocalDateTime;
import java.util.Arrays;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQuery;

/**
 * A historic item.
 */

@Entity
@NamedQuery(
		name="byFqdn",
		query="SELECT hi FROM HistoryItem hi WHERE hi.fqdn LIKE :fqdn"
)
public class HistoryItem implements Serializable {
	private static final long serialVersionUID = -5251258163182902698L;
	@Id
	private LocalDateTime id;
	private String fqdn;
    @Column(columnDefinition = "BLOB")
    private byte[] content;

    /**
     * Konstruktor.
     */
    public HistoryItem() {
        super();
        
        id = LocalDateTime.now();
    }

	public LocalDateTime getId() {
		return id;
	}

	public String getFqdn() {
		return fqdn;
	}

	public void setFqdn(String fqdn) {
		this.fqdn = fqdn;
	}

	public byte[] getContent() {
		return Arrays.copyOf(content, content.length);
	}

	public void setContent(byte[] content) {
		this.content = Arrays.copyOf(content, content.length);
	}
}

