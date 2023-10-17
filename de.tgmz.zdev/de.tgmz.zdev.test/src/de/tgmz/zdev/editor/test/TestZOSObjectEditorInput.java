/*********************************************************************
* Copyright (c) 10.10.2023 Thomas Zierer
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package de.tgmz.zdev.editor.test;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;

import com.ibm.cics.core.comm.ConnectionException;
import com.ibm.cics.zos.model.GetFailedException;
import com.ibm.cics.zos.model.IZOSObject;
import com.ibm.cics.zos.model.PermissionDeniedException;
import com.ibm.cics.zos.ui.editor.ZOSObjectEditorInput;

/**
 * Simple implementation of a ZOSObjectEditorInput for tests.
 */
public class TestZOSObjectEditorInput extends ZOSObjectEditorInput {
	private String content;

	public TestZOSObjectEditorInput(IZOSObject aMember) {
		super(aMember);
	}

	@Override
	protected ByteArrayOutputStream doGet() throws FileNotFoundException, PermissionDeniedException, GetFailedException, ConnectionException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			bos.write(content.getBytes(Charset.forName("Cp1252")));
			bos.close();
		} catch (IOException e) {
			throw new GetFailedException(e, "");
		}
		
		return bos;
	}
	@Override
	public String getContents() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}

}
