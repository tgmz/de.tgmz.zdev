/*********************************************************************
* Copyright (c) 06.10.2023 Thomas Zierer
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package de.tgmz.zdev.editor;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.jface.text.source.projection.ProjectionAnnotation;
import org.eclipse.jface.text.source.projection.ProjectionAnnotationModel;
import org.eclipse.jface.text.source.projection.ProjectionSupport;
import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.cics.core.connections.ConnectionServiceListener;
import com.ibm.cics.core.connections.ConnectionsPlugin;
import com.ibm.cics.zos.model.DataEntry;
import com.ibm.cics.zos.model.IZOSObject;
import com.ibm.cics.zos.model.Member;
import com.ibm.cics.zos.ui.ZOSActivator;
import com.ibm.cics.zos.ui.editor.DataEntryEditorInput;
import com.ibm.cics.zos.ui.editor.MemberEditor;
import com.ibm.cics.zos.ui.editor.ZOSObjectEditorInput;

import de.tgmz.zdev.editor.assembler.AssemblerConfiguration;
import de.tgmz.zdev.editor.cobol.COBOLConfiguration;
import de.tgmz.zdev.editor.jcl.JCLConfiguration;
import de.tgmz.zdev.editor.pli.PLIConfiguration;
import de.tgmz.zdev.editor.pli.PLIDocumentProvider;
import de.tgmz.zdev.editor.rexx.RexxConfiguration;
import de.tgmz.zdev.editor.sql.SqlConfiguration;
import de.tgmz.zdev.history.HistoryException;
import de.tgmz.zdev.history.LocalHistory;
import de.tgmz.zdev.outline.view.ZdevContentOutlinePage;
import de.tgmz.zdev.preferences.Language;

/**
 * Base editor.
 */
public class ZdevEditor extends MemberEditor {
	public static final String ID = "de.tgmz.zdev.editor.dataentry.editor";
	
	private static final Logger LOG = LoggerFactory.getLogger(ZdevEditor.class);
	
	private class MyConnectionServiceListener extends ConnectionServiceListener {
		private MyConnectionServiceListener() {
		}

		@Override
		public void event(ConnectionServiceListener.ConnectionServiceEvent event) {
			if ("com.ibm.cics.zos.comm.connection".equals(event.getConnectionCategoryId())
				&& event instanceof ConnectionServiceListener.DisconnectingEvent) {
					ZdevEditor.this.close(false);
			}
		}
	}

    protected ZdevContentOutlinePage page;
	private ZdevColorManager colorManager;
	private Annotation[] oldAnnotations;
	private ProjectionAnnotationModel projectionAnnotationModel;
	
	public ZdevEditor() {
		super();
		LOG.debug("Init");
	}
	
	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);
		
		UpperCaseListener uc = new UpperCaseListener();
		
		// Use same instance of UpperCaseListener as VerifyListener.verifyText() and interact.
		getSourceViewer().getTextWidget().addVerifyListener(uc);
		getSourceViewer().getTextWidget().addKeyListener(uc);
		
        ProjectionViewer viewer =(ProjectionViewer)getSourceViewer();
        
        ProjectionSupport projectionSupport = new ProjectionSupport(viewer,getAnnotationAccess(),getSharedColors());
		projectionSupport.install();
		
		//turn projection mode on
		viewer.doOperation(ProjectionViewer.TOGGLE);
		
		projectionAnnotationModel = viewer.getProjectionAnnotationModel();
	}
	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		ConnectionsPlugin.getDefault().getConnectionService().addConnectionServiceListener(new MyConnectionServiceListener());
		
		colorManager = new ZdevColorManager();
		
		IDocumentProvider myDocumentProvider;
		
		// getMember() yields null before super.init() is called
		ZOSObjectEditorInput editorinput = (ZOSObjectEditorInput) input;
		Member m = (Member) editorinput.getZOSObject();
		
		switch (Language.fromDatasetName(m.getParentPath())) {
		case JCL:
			setSourceViewerConfiguration(new JCLConfiguration(com.ibm.cics.zos.ui.editor.jcl.ColorManager.getDefault()));
			myDocumentProvider = ZOSActivator.getDefault().getMemberDocumentProvider();
			break;
		case ASSEMBLER:
			setSourceViewerConfiguration(new AssemblerConfiguration(colorManager));
			myDocumentProvider = ZOSActivator.getDefault().getMemberDocumentProvider();
			break;
		case COBOL:
			setSourceViewerConfiguration(new COBOLConfiguration(colorManager));
			myDocumentProvider = ZOSActivator.getDefault().getMemberDocumentProvider();
			break;
		case PLI:
			PLIConfiguration pliConfiguration = new PLIConfiguration(colorManager);
			pliConfiguration.setEditor(this);
			
			setSourceViewerConfiguration(pliConfiguration);
			myDocumentProvider = new PLIDocumentProvider();
			break;
		case SQL:
			setSourceViewerConfiguration(new SqlConfiguration(colorManager));
			myDocumentProvider = ZOSActivator.getDefault().getMemberDocumentProvider();
			break;
		case REXX:
			setSourceViewerConfiguration(new RexxConfiguration(colorManager));
			myDocumentProvider = ZOSActivator.getDefault().getMemberDocumentProvider();
			break;
		default:
			myDocumentProvider = ZOSActivator.getDefault().getMemberDocumentProvider();
			break;
		}
		
		setDocumentProvider(myDocumentProvider);
		
		super.init(site, input);
	}
	
    @SuppressWarnings("unchecked")
	@Override
	public <T> T getAdapter(Class<T> adapter) {
        if (adapter == IContentOutlinePage.class) {
            page = new ZdevContentOutlinePage(this, Language.fromDatasetName(getMember().getParentPath()));
            return (T) page;
        }
        
        return super.getAdapter(adapter);
    }

	@Override
	public void dispose() {
		LOG.debug("Exit");
		
		if (colorManager != null) {
			colorManager.dispose();
		}
		
		super.dispose();
	}

	@Override
	public void doSave(IProgressMonitor progressMonitor) {
		String content = getDocumentProvider().getDocument(getEditorInput()).get();
		
		if (content != null && content.trim().length() > 0) {
			try {
				LocalHistory.getInstance().save(content.getBytes(Charset.defaultCharset()), getMember().toDisplayName());
			} catch (HistoryException e) {
				LOG.warn("Exception caching {}", getMember(), e);
			}
		}
	
		
		super.doSave(progressMonitor);
	}
	private Member getMember() {
		return (Member) ((ZOSObjectEditorInput) getEditorInput()).getZOSObject();
	}
	
	public void updateFoldingStructure(List<Position> positions) {
		Annotation[] annotations = new Annotation[positions.size()];

		// this will hold the new annotations along
		// with their corresponding positions
		HashMap<ProjectionAnnotation, Position> newAnnotations = new HashMap<>();

		for (int i = 0; i < positions.size(); i++) {
			ProjectionAnnotation annotation = new ProjectionAnnotation();

			newAnnotations.put(annotation, positions.get(i));

			annotations[i] = annotation;
		}

		projectionAnnotationModel.modifyAnnotations(oldAnnotations, newAnnotations, null);

		oldAnnotations = annotations;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.texteditor.AbstractTextEditor#createSourceViewer(org.eclipse.
	 * swt.widgets.Composite, org.eclipse.jface.text.source.IVerticalRuler, int)
	 */
	@Override
	protected ISourceViewer createSourceViewer(Composite parent, IVerticalRuler ruler, int styles) {
		ISourceViewer viewer = new ProjectionViewer(parent, ruler, getOverviewRuler(), isOverviewRulerVisible(), styles);

		// ensure decoration support has been created and configured.
		getSourceViewerDecorationSupport(viewer);

		return viewer;
	}
	/**
	 * Utilitymethod to find and open a an editor for a datasetmeber.
	 * @param de Dataentry
	 * @return the Editor or null
	 * @throws PartInitException on problems
	 */
	public static final ITextEditor findEditor(DataEntry de, boolean forceOpen) throws PartInitException {
		ITextEditor result = null;
		
		IWorkbench w = PlatformUI.getWorkbench();
		IWorkbenchWindow[] wws = w.getWorkbenchWindows();
		
		for (IWorkbenchWindow ww : wws) {
			IWorkbenchPage[] wps = ww.getPages();
			
			for (IWorkbenchPage wp : wps) {
				IEditorReference[] ers = wp.getEditorReferences();
				
				for (IEditorReference er : ers) {
					IEditorInput ei = er.getEditorInput();
					
					if (ei instanceof DataEntryEditorInput mei) {
						IZOSObject i = mei.getZOSObject();
						
						if (i.getPath().equals(de.getPath())) {
							result = (ITextEditor) wp.findEditor(mei);
							
							break;
						}
					}
				}
				
				if (forceOpen 
					&& result == null 
					&& de.getZOSConnectable() != null 
					&& de.getZOSConnectable().isConnected()) {
					IEditorInput ei = new DataEntryEditorInput(de);
					result = (ITextEditor) wp.openEditor(ei, ZdevEditor.ID);
				}
			}
		}
		
		return result;
	}
    public ProjectionAnnotationModel getProjectionAnnotationModel() {
		return projectionAnnotationModel;
	}
}
