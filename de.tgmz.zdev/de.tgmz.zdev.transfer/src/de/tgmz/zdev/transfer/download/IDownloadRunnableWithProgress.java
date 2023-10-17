/*********************************************************************
* Copyright (c) 10.10.2023 Thomas Zierer
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package de.tgmz.zdev.transfer.download;

import org.eclipse.core.resources.IFolder;
import org.eclipse.jface.operation.IRunnableWithProgress;

/**
 * Downloads z contents to local folder.
 */
public interface IDownloadRunnableWithProgress extends IRunnableWithProgress {
	IFolder getDestination();
}
