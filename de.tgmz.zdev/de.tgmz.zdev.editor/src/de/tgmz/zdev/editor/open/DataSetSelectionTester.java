/*********************************************************************
* Copyright (c) 10.10.2023 Thomas Zierer
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package de.tgmz.zdev.editor.open;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.jface.text.ITextSelection;

import com.ibm.cics.common.util.StringUtil;

/**
 * Tests if selection may be the name on a dataset.
 */
//CHECKSTYLE DISABLE ReturnCount
public class DataSetSelectionTester extends PropertyTester {
	@Override
	public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
		if (receiver instanceof ITextSelection textSelection) {
			if ("isDataSet".equals(property)) {
				String text = textSelection.getText();
				
				if (StringUtil.isEmpty(text)) {
					return false;
				}
				
				if (text.indexOf('(') == -1 && text.length() > 44) {
					return false;
				}
				
				if (text.indexOf('(') > -1 && text.length() > 54) {
					return false;
				}

				String[] names = text.split("[\\.\\(\\)]");
				
				if (names.length > 1) {
					for (String s : names) {
						if (!s.matches("^[\\w#@\\$\\-]{1,8}")) {
							return false;
						}
					}
				} else {
					return false;
				}
				
				return true;
			}
		}
		
		return false;
	}
}
