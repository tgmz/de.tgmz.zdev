/*********************************************************************
* Copyright (c) 10.10.2023 Thomas Zierer
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package de.tgmz.zdev.editor.pli;

import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.RuleBasedPartitionScanner;
import org.eclipse.jface.text.rules.Token;

/**
 * Scans for comments.
 */
public class PLIPartitionScanner extends RuleBasedPartitionScanner {
	public static final String PLI_COMMENT = "__pli_comment"; //$NON-NLS-1$

	public PLIPartitionScanner() {
		setPredicateRules(new IPredicateRule[] {
					new MultiLineRule("/*", "*/", new Token(PLIPartitionScanner.PLI_COMMENT)),
				}
		);
	}
}
