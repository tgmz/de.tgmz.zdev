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
	
	public List<ZOSConnectionResponse> getHFSChildren(String aPath, boolean includeHiddenFiles) throws ConnectionException {
		LOG.debug("getHFSChildren {}, {}", aPath, includeHiddenFiles);
		
		// Trailing slash yields "incorrect path"
		String path = aPath.endsWith("/") ? aPath.substring(0, aPath.length() - 1) : aPath;

        List<UnixFile> items;
        
        try {
            ListParams params = new ListParams.Builder().path(path).depth(1).build();
            items = ussList.getFiles(params);
        } catch (ZosmfRequestException e) {
            throw new ConnectionException(e);
        }

        List<ZOSConnectionResponse> result = new ArrayList<>(items.size());
        
        for (UnixFile item : items) {
			ZOSConnectionResponse cr = new ZOSConnectionResponse();
			
			String mode = item.getMode().orElse("-rw-r-----");
			
			cr.addAttribute(IZOSConstants.HFS_PARENT_PATH, aPath);
			cr.addAttributeDontTrim(IZOSConstants.NAME, item.getName().orElse(ZoweConnection.UNKNOWN));
			cr.addAttribute(IZOSConstants.HFS_SIZE, item.getSize().orElse(0L));
			cr.addAttribute(IZOSConstants.HFS_DIRECTORY, mode.startsWith("d"));
			cr.addAttribute(IZOSConstants.HFS_USER, item.getUser().orElse(ZoweConnection.UNKNOWN));
			cr.addAttribute(IZOSConstants.HFS_GROUP, item.getGroup().orElse(ZoweConnection.UNKNOWN));
			cr.addAttribute(IZOSConstants.HFS_PERMISSIONS, mode.substring(1));
			cr.addAttribute(IZOSConstants.HFS_LAST_USED_DATE, item.getMtime().orElse(ZoweConnection.UNKNOWN));

			result.add(cr);
        }

		return result;
	}

	public boolean existsHFS(String aPath) throws ConnectionException {
		LOG.debug("existsHFS {}", aPath);
		
        GetParams params = new GetParams.Builder().insensitive(false).search(aPath).build();
        try {
			response = ussGet.getCommon(aPath, params);
			
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

	public boolean existsHFSFile(String aPath, String aName) throws ConnectionException {
		LOG.debug("existsHFSFile {} {}", aPath, aName);
		return existsHFS(String.format("%s/%s", aPath, aName));
	}

	public void createFolderHFS(String aPath) throws ConnectionException {
		LOG.debug("createFolderHFS {}", aPath);
		
        CreateParams params = new CreateParams(CreateType.DIR, "rwxr-xr-x");
        try {
			response = ussCreate.create(aPath, params);
			
			LOG.debug("ussCreate {}", response);
		} catch (ZosmfRequestException e) {
			throw new ConnectionException(e);
		}
	}

	public void deletePathHFS(String aPath) throws ConnectionException {
		try {
			response = ussDelete.delete(aPath, true);
			
			LOG.debug("ussDelete {}", response);
		} catch (ZosmfRequestException e) {
			throw new ConnectionException(e);
		}
	}

	public void saveFileHFS(String aPath, InputStream fileContents, IZOSConstants.FileType aFileType) throws ConnectionException {
		LOG.debug("saveFileHFS {} {} {}", aPath, fileContents, aFileType);
		
		try {
			ussWrite.writeBinary(aPath, IOUtils.toByteArray(fileContents));
		} catch (ZosmfRequestException | IOException e) {
			throw new ConnectionException(e);
		}
	}

	public void saveFileHFS(String filePath, InputStream fileContents, String charset) throws ConnectionException {
		LOG.debug("saveFileHFS {} {} {}", filePath, fileContents, charset);
		
		try (InputStreamReader r = new InputStreamReader(fileContents)) {
			ussWrite.writeBinary(filePath, IOUtils.toByteArray(r, Charset.forName(charset)));
		} catch (ZosmfRequestException | IOException e) {
			throw new ConnectionException(e);
		}
	}

	public ByteArrayOutputStream getFileHFS(String aPath, FileType p1) throws ConnectionException {
		LOG.debug("getFileHFS {}, {}", aPath, p1);
		
		byte[] content;
		
		try {
			content = ussGet.getBinary(aPath);
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

	public void changePermissions(String aPath, String octal) throws ConnectionException {
		LOG.debug("changePermissions {} {}", aPath, octal);
		
		try {
			ussChangeMode.change(aPath, new ChangeModeParams.Builder().mode(octal).build());
		} catch (ZosmfRequestException e) {
			throw new ConnectionException(e);
		}
	}
}
