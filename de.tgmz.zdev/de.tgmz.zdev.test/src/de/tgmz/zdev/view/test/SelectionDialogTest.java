/*********************************************************************
* Copyright (c) 10.10.2023 Thomas Zierer
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package de.tgmz.zdev.view.test;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;

import java.io.IOException;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import com.ibm.cics.core.comm.ConnectionException;
import com.ibm.cics.zos.model.DataPath;
import com.ibm.cics.zos.model.HFSFile;
import com.ibm.cics.zos.model.HFSFolder;
import com.ibm.cics.zos.model.IZOSConnectable;
import com.ibm.cics.zos.model.PartitionedDataSet;
import com.ibm.cics.zos.model.PermissionDeniedException;

import de.tgmz.zdev.connection.ZdevConnectable;
import de.tgmz.zdev.view.ContainerSelectionDialogWithTransfermode;
import de.tgmz.zdev.view.DatasetSelectionDialog;
import de.tgmz.zdev.view.HFSSelectionDialog;

/**
 * Test class for SelectionDialogs.
 */
public class SelectionDialogTest {
	private static Shell shell;
	private static IZOSConnectable origin;
	private static IZOSConnectable connectable;
	
	@BeforeClass
	public static void setupOnce() {
		origin = ZdevConnectable.getConnectable();
		
		connectable = Mockito.mock(IZOSConnectable.class);
		
		ZdevConnectable.setConnectable(connectable);
		
		shell= new Shell((Display) null);
	}

	@AfterClass
	public static void teardownOnce() {
		ZdevConnectable.setConnectable(origin);
	}
	
	@Test
<<<<<<< Upstream, based on develop
	public void testHfsSelectionDialog() {
		HFSSelectionDialog hsd = new HFSSelectionDialog(shell);
=======
	public void testDatasetSelectionDialog() throws PermissionDeniedException, ConnectionException {
		Mockito.when(connectable.getDataSetEntries(any(DataPath.class))).thenReturn(List.of(Mockito.mock(PartitionedDataSet.class)));
>>>>>>> e96cb40 Add transfer mode
		
<<<<<<< Upstream, based on develop
		assertNull(hsd.getTarget());
=======
		testDialog(new DatasetSelectionDialog(shell));
	}

	@Test
	public void testHfsSelectionDialog() throws IOException, ConnectionException {
		Mockito.when(connectable.getChildren(any(HFSFolder.class), anyBoolean())).thenReturn(List.of(Mockito.mock(HFSFile.class)));
		
		testDialog(new HFSSelectionDialog(shell, ZdevConnectable.getConnectable(), "/"));
>>>>>>> e96cb40 Add transfer mode
	}
	
	@Test
	public void testContainerSelectionDialog() {
		testDialog(new ContainerSelectionDialogWithTransfermode(shell, null, ""));
	}
	
	private void testDialog(Dialog d) {
		d.create();
		d.setBlockOnOpen(false);
		
		assertEquals(0, d.open());
		
		d.close();
	}
}
