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

import org.eclipse.jface.text.ITextStore;

/**
 * Simple implementation of a ITextStore.
 */
public class StringTextStore implements ITextStore {
	private String content;

	public StringTextStore(String content) {
		super();
		this.content = content;
	}

	@Override
	public char get(int offset) {
		return content.charAt(offset);
	}

	@Override
	public int getLength() {
		return content.length();
	}

	@Override
	public String get(int offset, int length) {
		return content.substring(offset, offset + length);
	}

	@Override
	public void set(String text) {
		content = text;
	}

	@Override
	public void replace(int offset, int length, String text) {
		content = content.substring(0, offset) + text + content.substring(offset + length + 1);
	}
}
