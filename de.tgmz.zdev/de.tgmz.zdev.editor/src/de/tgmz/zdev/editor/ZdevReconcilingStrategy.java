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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.reconciler.DirtyRegion;
import org.eclipse.jface.text.reconciler.IReconcilingStrategy;
import org.eclipse.jface.text.reconciler.IReconcilingStrategyExtension;
import org.eclipse.swt.widgets.Display;

/**
 * Reconciler for sources.
 */
public class ZdevReconcilingStrategy implements IReconcilingStrategy, IReconcilingStrategyExtension {

	private ZdevEditor editor;

	private IDocument fDocument;

	/** holds the calculated positions */
	private final List<Position> fPositions = new ArrayList<>();

	/**
	 * @return Returns the editor.
	 */
	public ZdevEditor getEditor() {
		return editor;
	}

	public void setEditor(ZdevEditor editor) {
		this.editor = editor;
	}

	@Override
	public void setDocument(IDocument document) {
		this.fDocument = document;

	}

	@Override
	public void reconcile(DirtyRegion dirtyRegion, IRegion subRegion) {
		initialReconcile();
	}

	@Override
	public void reconcile(IRegion partition) {
		initialReconcile();
	}

	@Override
	public void setProgressMonitor(IProgressMonitor monitor) {
		// intentionally empty
	}

	@Override
	public void initialReconcile() {
		calculatePositions();
	}

	public void calculatePositions() {
		fPositions.clear();

		fPositions.addAll(PatternScanner.getInstance().calculatePositions(fDocument));
		
		Collections.sort(fPositions, (o1, o2) -> o1.offset - o2.offset);

		Display.getDefault().asyncExec(() -> editor.updateFoldingStructure(fPositions));
	}

	public List<Position> getfPositions() {
		return fPositions;
	}
}
