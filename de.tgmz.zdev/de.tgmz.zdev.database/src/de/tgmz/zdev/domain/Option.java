/*********************************************************************
* Copyright (c) 29.11.2023 Thomas Zierer
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
import jakarta.persistence.Transient;

@Entity
public class Option implements Serializable {
	@Transient
	private static final long serialVersionUID = -4970348044191374725L;
	@Id
	@GeneratedValue
	private long id;	
	private boolean comp;
	private String compOption;
	private boolean db2;
	private String db2Option;
	private boolean cics;
	private String cicsOption;
	private boolean bind;
	private String bindOption;

	public boolean isComp() {
		return comp;
	}
	public void setComp(boolean comp) {
		this.comp = comp;
	}
	public String getCompOption() {
		return compOption;
	}
	public void setCompOption(String compOption) {
		this.compOption = compOption;
	}
	public boolean isDb2() {
		return db2;
	}
	public void setDb2(boolean db2) {
		this.db2 = db2;
	}
	public String getDb2Option() {
		return db2Option;
	}
	public void setDb2Option(String db2Option) {
		this.db2Option = db2Option;
	}
	public boolean isCics() {
		return cics;
	}
	public void setCics(boolean cics) {
		this.cics = cics;
	}
	public String getCicsOption() {
		return cicsOption;
	}
	public void setCicsOption(String cicsOption) {
		this.cicsOption = cicsOption;
	}
	public boolean isBind() {
		return bind;
	}
	public void setBind(boolean bind) {
		this.bind = bind;
	}
	public String getBindOption() {
		return bindOption;
	}
	public void setBindOption(String bindOption) {
		this.bindOption = bindOption;
	}
}
