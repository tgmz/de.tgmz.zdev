/*********************************************************************
* Copyright (c) 12.04.2024 Thomas Zierer
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package de.tgmz.zdev.zowe.connection;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.cics.core.comm.ConnectionException;
import com.ibm.cics.zos.comm.IZOSConstants;
import com.ibm.cics.zos.comm.IZOSConstants.FileType;
import com.ibm.cics.zos.comm.ZOSConnectionResponse;

import zowe.client.sdk.core.ZosConnection;
import zowe.client.sdk.rest.Response;
import zowe.client.sdk.rest.exception.ZosmfRequestException;
import zowe.client.sdk.zosfiles.uss.input.ChangeModeParams;
import zowe.client.sdk.zosfiles.uss.input.CreateParams;
import zowe.client.sdk.zosfiles.uss.input.GetParams;
import zowe.client.sdk.zosfiles.uss.input.ListParams;
import zowe.client.sdk.zosfiles.uss.methods.UssChangeMode;
import zowe.client.sdk.zosfiles.uss.methods.UssCreate;
import zowe.client.sdk.zosfiles.uss.methods.UssDelete;
import zowe.client.sdk.zosfiles.uss.methods.UssGet;
import zowe.client.sdk.zosfiles.uss.methods.UssList;
import zowe.client.sdk.zosfiles.uss.methods.UssWrite;
import zowe.client.sdk.zosfiles.uss.response.UnixFile;
import zowe.client.sdk.zosfiles.uss.types.CreateType;

public class ZoweUssConnection {
	private static final Logger LOG = LoggerFactory.getLogger(ZoweUssConnection.class);

	private Response response;
	
	private UssList ussList;
    private UssGet ussGet;
    private UssDelete ussDelete;
    private UssCreate ussCreate;
    private UssWrite ussWrite;
    private UssChangeMode ussChangeMode;

	public ZoweUssConnection(ZosConnection connection) {
		ussList = new UssList(connection);
		ussGet = new UssGet(connection);
		ussDelete = new UssDelete(connection);
		ussCreate = new UssCreate(connection);
		ussWrite = new UssWrite(connection);
		ussChangeMode = new UssChangeMode(connection);
	}
	
	public List<ZOSConnectionResponse> getHFSChildren(String p0, boolean p1) throws ConnectionException {
		LOG.debug("getHFSChildren {}, {}", p0, p1);
		
		// Trailing slash yields "incorrect path"
		String path = p0.endsWith("/") ? p0.substring(0, p0.length() - 1) : p0;

        List<UnixFile> items;
        
        try {
            ListParams params = new ListParams.Builder().path(path).build();
            items = ussList.getFiles(params);
        } catch (ZosmfRequestException e) {
            throw new ConnectionException(e);
        }

        List<ZOSConnectionResponse> result = new ArrayList<>(items.size());
        
        for (UnixFile item : items) {
			ZOSConnectionResponse cr = new ZOSConnectionResponse();
			
			String mode = item.getMode().orElse("-rw-r-----");
			
			cr.addAttribute(IZOSConstants.HFS_PARENT_PATH, p0);
			cr.addAttribute(IZOSConstants.NAME, item.getName().orElse(ZoweConnection.UNKNOWN));
			cr.addAttribute(IZOSConstants.HFS_SIZE, item.getSize().orElse(0L));
			cr.addAttribute(IZOSConstants.HFS_DIRECTORY, mode.startsWith("d"));
			cr.addAttribute(IZOSConstants.HFS_USER, item.getUser().orElse(ZoweConnection.UNKNOWN));
			cr.addAttribute(IZOSConstants.HFS_GROUP, item.getGroup().orElse(ZoweConnection.UNKNOWN));
			cr.addAttribute(IZOSConstants.HFS_PERMISSIONS, mode);
			cr.addAttribute(IZOSConstants.HFS_LAST_USED_DATE, item.getMtime().orElse(ZoweConnection.UNKNOWN));

			result.add(cr);
        }

		return result;
	}

	public boolean existsHFS(String p0) throws ConnectionException {
		LOG.debug("existsHFS {}", p0);
		
        GetParams params = new GetParams.Builder().insensitive(false).search(p0).build();
        try {
			response = ussGet.getCommon(p0, params);
			
			LOG.debug("ussGet {}", response);
		} catch (ZosmfRequestException e) {
			OptionalInt oStatusCode = e.getResponse().getStatusCode();
			if (oStatusCode.isPresent() && oStatusCode.getAsInt() == 404) {
				return false;
			} else {
				throw new ConnectionException(e);
			}
		}
        
        return true;
	}

	public boolean existsHFSFile(String p0, String p1) throws ConnectionException {
		LOG.debug("existsHFSFile {} {}", p0, p1);
		return existsHFS(String.format("%s/%s", p0, p1));
	}

	public void createFolderHFS(String p0) throws ConnectionException {
		LOG.debug("createFolderHFS {}", p0);
		
        CreateParams params = new CreateParams(CreateType.DIR, "rwxr-xr-x");
        try {
			response = ussCreate.create(p0, params);
			
			LOG.debug("ussCreate {}", response);
		} catch (ZosmfRequestException e) {
			throw new ConnectionException(e);
		}
	}

	public void deletePathHFS(String p0) throws ConnectionException {
		try {
			response = ussDelete.delete(p0, true);
			
			LOG.debug("ussDelete {}", response);
		} catch (ZosmfRequestException e) {
			throw new ConnectionException(e);
		}
	}

	public void saveFileHFS(String p0, InputStream p1, FileType p2) throws ConnectionException {
		LOG.debug("saveFileHFS {} {} {}", p0, p1, p2);
		
		try {
			ussWrite.writeBinary(p0, IOUtils.toByteArray(p1));
		} catch (ZosmfRequestException | IOException e) {
			throw new ConnectionException(e);
		}
	}

	public void saveFileHFS(String p0, InputStream p1, String p2) throws ConnectionException {
		LOG.debug("saveFileHFS {} {} {}", p0, p1, p2);
		
		try (InputStreamReader r = new InputStreamReader(p1)) {
			ussWrite.writeBinary(p0, IOUtils.toByteArray(r, Charset.forName(p2)));
		} catch (ZosmfRequestException | IOException e) {
			throw new ConnectionException(e);
		}
	}

	public ByteArrayOutputStream getFileHFS(String p0, FileType p1) throws ConnectionException {
		LOG.debug("getFileHFS {}, {}", p0, p1);
		
		byte[] content;
		
		try {
			content = ussGet.getBinary(p0);
		} catch (ZosmfRequestException e) {
			throw new ConnectionException(e);
		}
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream(content.length);
		
		try (InputStream is = new ByteArrayInputStream(content)) {
			IOUtils.copy(is, baos);
		} catch (IOException e) {
			throw new ConnectionException(e);
		}
		
		return baos;
	}

	public void changePermissions(String p0, String p1) throws ConnectionException {
		LOG.debug("changePermissions {} {}", p0, p1);
		
		try {
			ussChangeMode.change(p0, new ChangeModeParams.Builder().mode(p1).build());
		} catch (ZosmfRequestException e) {
			throw new ConnectionException(e);
		}
	}
}
