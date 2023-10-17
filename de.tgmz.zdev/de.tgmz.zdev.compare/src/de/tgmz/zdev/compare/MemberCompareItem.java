/*********************************************************************
* Copyright (c) 10.10.2023 Thomas Zierer
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package de.tgmz.zdev.compare;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.eclipse.compare.IEditableContent;
import org.eclipse.compare.IStreamContentAccessor;
import org.eclipse.compare.ITypedElement;
import org.eclipse.swt.graphics.Image;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.cics.core.comm.ConnectionException;
import com.ibm.cics.zos.comm.IZOSConstants.FileType;
import com.ibm.cics.zos.model.Member;

import de.tgmz.zdev.connection.ZdevConnectable;

/**
 * CompareItem based on a member.
 */
public class MemberCompareItem implements IStreamContentAccessor, ITypedElement, IEditableContent {
	private static final Logger LOG = LoggerFactory.getLogger(MemberCompareItem.class);
	
	private Member member;
	
	public MemberCompareItem(Member member) {
		this.member = member;
	}

	@Override
	public InputStream getContents() {
		try (ByteArrayOutputStream bos = ZdevConnectable.getConnectable().getContents(member, FileType.ASCII)) {
			return new ByteArrayInputStream(bos.toByteArray());
		} catch (ConnectionException | IOException e) {
			LOG.error("Cannot get contens of {}, reason:", member.toDisplayName(), e);
			
			return null;
		}
	}

	public Member getMember() {
		return member;
	}

	@Override
	public void setContent(byte[] newContent) {
		try (ByteArrayInputStream bais = new ByteArrayInputStream(newContent)){
			ZdevConnectable.getConnectable().save(member, bais);
		} catch (IOException e) {
			LOG.error("Cannot set contens of {}, reason:", member.toDisplayName(), e);
		}
	}

	@Override
	public boolean isEditable() {
		return false;
	}

	@Override
	public ITypedElement replace(ITypedElement dest, ITypedElement src) {
		return null;
	}

	@Override
	public String getName() {
		return member.toDisplayName();
	}

	@Override
	public Image getImage() {
		return null;
	}

	@Override
	public String getType() {
		return ITypedElement.TEXT_TYPE;
	}
}
