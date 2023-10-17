/*********************************************************************
* Copyright (c) 10.10.2023 Thomas Zierer
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package de.tgmz.zdev.restore.compare;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.cics.core.comm.ConnectionException;
import com.ibm.cics.zos.comm.IZOSConstants.FileType;
import com.ibm.cics.zos.model.Member;
import com.ibm.cics.zos.model.PermissionDeniedException;

import de.tgmz.zdev.connection.ZdevConnectable;

/**
 * CompareItem based on a member.
 */
public class MemberCompareItem extends ZdevCompareItem {
	private static final Logger LOG = LoggerFactory.getLogger(MemberCompareItem.class);
	
	private Member member;

	public MemberCompareItem(Member member) {
		super(member.toDisplayName());
		this.member = member;
	}

	@Override
	public InputStream getContents() {
		ByteArrayOutputStream bos;
		try {
			bos = ZdevConnectable.getConnectable().getContents(member, FileType.ASCII);
		} catch (FileNotFoundException | PermissionDeniedException | ConnectionException e) {
			LOG.error("Cannot get contens of {}, reason:", member.toDisplayName(), e);
			
			return null;
		}
		
		this.contents = bos.toByteArray();
		
		return new ByteArrayInputStream(this.contents);
	}

	public Member getMember() {
		return member;
	}
}
