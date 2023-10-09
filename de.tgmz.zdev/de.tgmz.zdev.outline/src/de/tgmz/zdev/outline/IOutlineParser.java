/*********************************************************************
* Copyright (c) 09.10.2023 Thomas Zierer
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package de.tgmz.zdev.outline;

import org.eclipse.core.runtime.IAdaptable;

/**
 * General OutlineParser.
 */
public interface IOutlineParser {
	MarkElement[] parse(IAdaptable adaptable, String fileContents);
}
