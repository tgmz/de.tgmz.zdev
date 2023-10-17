/*********************************************************************
* Copyright (c) 10.10.2023 Thomas Zierer
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package de.tgmz.zdev.editor.local.editors;

import org.apache.commons.io.FilenameUtils;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tgmz.zdev.editor.ZdevColorManager;
import de.tgmz.zdev.editor.assembler.AssemblerConfiguration;
import de.tgmz.zdev.editor.cobol.COBOLConfiguration;
import de.tgmz.zdev.editor.pli.PLIConfiguration;
import de.tgmz.zdev.editor.sql.SqlConfiguration;
import de.tgmz.zdev.outline.view.ZdevContentOutlinePage;
import de.tgmz.zdev.preferences.Language;

/**
 * Editor for local sources.
 */
public class LocalZdevEditor extends TextEditor {
	private static final Logger LOG = LoggerFactory.getLogger(LocalZdevEditor.class);

    protected ZdevContentOutlinePage page;
	private ZdevColorManager colorManager;
	private String ext;

	public LocalZdevEditor() {
		super();
	}
	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		LOG.info("Initialize Editor on site {} for input {}", site.getRegisteredName(), input.getName());
		
		colorManager = new ZdevColorManager();
		
		ext = FilenameUtils.getExtension(input.getName());
		
		switch (ext) {
		case "asm", ".asm":
			setSourceViewerConfiguration(new AssemblerConfiguration(colorManager));
			break;
		case "cbl", ".cbl":
			setSourceViewerConfiguration(new COBOLConfiguration(colorManager));
			break;
		case "pli", ".pli":
			setSourceViewerConfiguration(new PLIConfiguration(colorManager));
			
			setDocumentProvider(new LocalPLIDocumentProvider());
			break;
		case "sql", ".sql":
			setSourceViewerConfiguration(new SqlConfiguration(colorManager));
			break;
		default:
			break;
		}
		
		super.init(site, input);
	}
    @SuppressWarnings("unchecked")
	@Override
	public <T> T getAdapter(Class<T> adapter) {
        if (adapter == IContentOutlinePage.class) {
            page = new ZdevContentOutlinePage(this, Language.byExtension(ext));
            return (T) page;
        }
        
        return super.getAdapter(adapter);
    }

	@Override
	public void dispose() {
		colorManager.dispose();
		super.dispose();
	}

}
