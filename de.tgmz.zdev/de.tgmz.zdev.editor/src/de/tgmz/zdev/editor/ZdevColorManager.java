/*********************************************************************
* Copyright (c) 10.10.2023 Thomas Zierer
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package de.tgmz.zdev.editor;

import java.util.EnumMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

/**
 * Color manager.
 */
public class ZdevColorManager {

	protected Map<ZdevColor, Color> fColorTable = new EnumMap<>(ZdevColor.class);

	public void dispose() {
		Iterator<Color> e = fColorTable.values().iterator();
		while (e.hasNext()) {
			 e.next().dispose();
		}
	}
	public Color getSwtColor(ZdevColor aColor) {
		return fColorTable.computeIfAbsent(aColor, key -> new Color(Display.getCurrent(), key.getRgb()));
	}
}
