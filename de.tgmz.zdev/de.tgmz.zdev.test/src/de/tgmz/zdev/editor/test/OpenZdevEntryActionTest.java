package de.tgmz.zdev.editor.test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;

import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.Test.None;
import org.mockito.Mockito;

import com.ibm.cics.core.comm.ConnectionException;
import com.ibm.cics.zos.comm.IZOSConstants.FileType;
import com.ibm.cics.zos.model.IZOSConnectable;
import com.ibm.cics.zos.model.Member;
import com.ibm.cics.zos.model.PermissionDeniedException;

import de.tgmz.zdev.connection.ZdevConnectable;
import de.tgmz.zdev.editor.OpenZdevEntryAction;

public class OpenZdevEntryActionTest {
	private static IZOSConnectable mock = Mockito.mock(IZOSConnectable.class);
	private static IZOSConnectable origin;

	@BeforeClass
	public static void setupOnce() {
		origin = ZdevConnectable.getConnectable();
		
		ZdevConnectable.setConnectable(mock);
	}

	@AfterClass
	public static void teardownOnce() {
		ZdevConnectable.setConnectable(origin);
	}
	
	@Test(expected = None.class)
	public void testOpenEditorIWorkbenchPage() throws PartInitException, FileNotFoundException, PermissionDeniedException, ConnectionException {
		OpenZdevEntryAction ozea = new OpenZdevEntryAction();
		
		Member m = Mockito.mock(Member.class);
		Mockito.when(m.getParentPath()).thenReturn("HLQ.PLI");
		Mockito.when(m.getName()).thenReturn("HELLOW");
		Mockito.when(m.toDisplayName()).thenReturn("HLQ.PLI(HELLOW)");

		Mockito.when(mock.getContents(eq(m), any(FileType.class))).thenReturn(new ByteArrayOutputStream(1));
		
		ozea.setZOSLocation(m);
		
		ozea.openEditor(Mockito.mock(IWorkbenchPage.class));
	}

}
