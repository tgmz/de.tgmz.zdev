/*********************************************************************
* Copyright (c) 06.10.2023 Thomas Zierer
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package de.tgmz.zdev.editor;

import javax.annotation.Generated;

/**
 * ZdevCompletionProposal.
 */
public class ZdevCompletionProposal implements Comparable<ZdevCompletionProposal> {
	private String pattern;
	private String replacementString; 
	private String additionalProposalInfo;
	
	public ZdevCompletionProposal(String key, String replacementString, String additionalProposalInfo) {
		this.pattern = key;
		this.replacementString = replacementString;
		this.additionalProposalInfo = additionalProposalInfo;
	}
	
	public String getReplacementString() {
		return replacementString;
	}
	public String getPattern() {
		return pattern;
	}
	public String getAdditionalProposalInfo() {
		return additionalProposalInfo;
	}
	@Override
	public int compareTo(ZdevCompletionProposal o) {
		return this.replacementString.compareTo(o.replacementString);
	}

	@Override
    //BEGIN-GENERATED
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((replacementString == null) ? 0 : replacementString.hashCode());
		return result;
	}

	@Override
	@Generated(value = { "Eclipse" })
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ZdevCompletionProposal other = (ZdevCompletionProposal) obj;
		if (replacementString == null) {
			if (other.replacementString != null)
				return false;
		} else if (!replacementString.equals(other.replacementString))
			return false;
		return true;
	}	
    //BEGIN-GENERATED
}
