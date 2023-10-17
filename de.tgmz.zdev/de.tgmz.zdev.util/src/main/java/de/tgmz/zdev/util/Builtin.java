/*********************************************************************
* Copyright (c) 10.10.2023 Thomas Zierer
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package de.tgmz.zdev.util;

public class Builtin {
	private String name;
	private String shortDescription;
	private String longDescription;
	private String signature;
	
	public Builtin(String name) {
		super();
		this.name = name;
	}
	public String getShortDescription() {
		return shortDescription;
	}
	public void addShortDescription(String aShortDescription) {
		this.shortDescription = this.shortDescription == null ? aShortDescription : this.shortDescription + "\\r\\n" + aShortDescription;
	}
	public String getLongDescription() {
		return longDescription;
	}
	public void addLongDescription(String aLongDescription) {
		this.longDescription = this.longDescription == null ? aLongDescription : this.longDescription + "\\r\\n" + aLongDescription;
	}
	public String getSignature() {
		return signature;
	}
	public void addSignature(String aSignature) {
		// Signature must be a single line
		this.signature = this.signature == null ? aSignature : this.signature + aSignature;
	}
	public void setSignature(String signature) {
		this.signature = signature;
	}
	public String getName() {
		return name;
	}
	@Override
	public String toString() {
		return "Builtin [name=" + name + ", shortDescription=" + shortDescription + ", longDescription="
				+ longDescription + ", signature=" + signature + "]";
	}
}
