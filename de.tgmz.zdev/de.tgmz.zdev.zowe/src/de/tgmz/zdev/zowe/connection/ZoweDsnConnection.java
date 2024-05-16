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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.cics.core.comm.ConnectionException;
import com.ibm.cics.zos.comm.IZOSConnection.DataSetArguments;
import com.ibm.cics.zos.comm.IZOSConstants;
import com.ibm.cics.zos.comm.ZOSConnectionResponse;
import com.ibm.cics.zos.comm.ZOSFileNotFoundException;
import com.ibm.cics.zos.comm.ZOSUnsupportedOperationException;

import zowe.client.sdk.core.ZosConnection;
import zowe.client.sdk.rest.Response;
import zowe.client.sdk.rest.exception.ZosmfRequestException;
import zowe.client.sdk.zosfiles.dsn.input.CopyParams;
import zowe.client.sdk.zosfiles.dsn.input.CreateParams;
import zowe.client.sdk.zosfiles.dsn.input.DownloadParams;
import zowe.client.sdk.zosfiles.dsn.input.ListParams;
import zowe.client.sdk.zosfiles.dsn.methods.DsnCopy;
import zowe.client.sdk.zosfiles.dsn.methods.DsnCreate;
import zowe.client.sdk.zosfiles.dsn.methods.DsnDelete;
import zowe.client.sdk.zosfiles.dsn.methods.DsnGet;
import zowe.client.sdk.zosfiles.dsn.methods.DsnList;
import zowe.client.sdk.zosfiles.dsn.methods.DsnWrite;
import zowe.client.sdk.zosfiles.dsn.response.Dataset;
import zowe.client.sdk.zosfiles.dsn.response.Member;
import zowe.client.sdk.zosfiles.dsn.types.AttributeType;

public class ZoweDsnConnection {
	private static final Logger LOG = LoggerFactory.getLogger(ZoweDsnConnection.class);
	
	private Response response;
	
	private DsnGet dsnGet;
	private DsnWrite dsnWrite;
	private DsnDelete dsnDelete;
	private DsnList dsnList;
	private DsnCreate dsnCreate;
	private DsnCopy dsnCopy;
	
	public ZoweDsnConnection(ZosConnection connection) {
		dsnWrite = new DsnWrite(connection);
		dsnDelete = new DsnDelete(connection);
		dsnGet = new DsnGet(connection);
		dsnList = new DsnList(connection);
		dsnCreate = new DsnCreate(connection);
		dsnCopy = new DsnCopy(connection);
	}

	public List<ZOSConnectionResponse> getDataSetMembers(String p0) throws ConnectionException {
		return p0.endsWith("*") ? getDataSets(p0) : getMembers(p0);
	}

	public ByteArrayOutputStream retrieveDataSetMember(String p0, String p1) throws ConnectionException {
		return retrieve(String.format("%s(%s)", p0, p1));
	}

	public void recallDataSetMember(String p0, String p1) throws ConnectionException {
		LOG.debug("recallDataSetMember {} {}", p0, p1);
		
		throw new ZOSUnsupportedOperationException("");
	}

	public ByteArrayOutputStream retrieveSequentialDataSet(String p0) throws ConnectionException {
		LOG.debug("retrieveSequentialDataSet {}", p0);
		return retrieve(p0);
	}

	public void saveDataSetMember(String p0, String p1, InputStream p2) throws ConnectionException {
		try (Reader r = new InputStreamReader(p2)) {
			dsnWrite.write(p0, p1, IOUtils.toString(r));
		} catch (ZosmfRequestException | IOException e) {
			throw new ConnectionException(e);
		}
	}

	public void deleteDataSet(String p0, String p1) throws ConnectionException {
		try {
			if (p0 == null) {
				response = dsnDelete.delete(p1);
			} else {
				response = dsnDelete.delete(p0, p1);
			}
			
			LOG.debug("dsnDelete {}", response);
		} catch (ZosmfRequestException e) {
			throw new ConnectionException(e);
		}
	}

	public void createDataSet(String p0, DataSetArguments p1) throws ConnectionException {
		// Convert alcunit com.ibm.cics.zos.model.DataSet$SpaceUnits
		String alcunit;
		
		switch (p1.spaceUnits) {
		case "CYLINDERS":
			alcunit = "CYL"; break;
		case "TRACKS":
			alcunit = "TRK"; break;
		case "BLOCKS":
		default:
			alcunit = null; break;
		}
		
        CreateParams createParams = new CreateParams.Builder()
                .dsorg(p1.datasetType.startsWith("PDS") ? "PO" : p1.datasetType)
                .dsntype("PDSE".equals(p1.datasetType) ? "LIBRARY" : null)
                .alcunit(alcunit)
                .primary((int) p1.primaryAllocation)
                .secondary((int) p1.secondaryAllocation)
                .dirblk((int) p1.directoryBlocks)
                .recfm(p1.recordFormat)
                .blksize((int) p1.blockSize)
                .lrecl((int) p1.recordLength)
                .build();

		try {
			response = dsnCreate.create(p0, createParams);
			
			LOG.debug("dsnCreate {}", response);
		} catch (ZosmfRequestException e) {
			throw new ConnectionException(e);
		}
		
	}

	public ZOSConnectionResponse getDataSet(String p0) throws ConnectionException {
		Optional<ZOSConnectionResponse> first = getDataSets(p0).stream().filter(s -> p0.equals(s.getAttribute("FILE_NAME"))).findFirst();

		if (first.isPresent()) {
			return first.get();
		} else {
			throw new ZOSFileNotFoundException(p0, null);
		}
	}

	public ZOSConnectionResponse getDataSetMember(String p0, String p1)	throws ConnectionException {
		Optional<ZOSConnectionResponse> first = getDataSetMembers(p0).stream().filter(s -> p1.equals(s.getAttribute("NAME"))).findFirst();
		
		if (first.isPresent()) {
			return first.get();
		} else {
			throw new ZOSFileNotFoundException(p0, p1);
		}
	}

	public ZOSConnectionResponse createDataSetMember(String p0, String p1) throws ConnectionException {
		LOG.debug("createDataSetMember {} {}", p0, p1);
		
		try {
			response = dsnWrite.write(p0, p1, "");
		} catch (ZosmfRequestException e) {
            throw new ConnectionException(e);
		}
			
		LOG.debug("dsnWrite {}", response);
			
		ZOSConnectionResponse cr = new ZOSConnectionResponse();
		
		cr.addAttribute(IZOSConstants.NAME, p1);
		
		return cr;
	}

	public void createDataSet(String p0, String p1, InputStream p2) throws ConnectionException {
		LOG.debug("createDataSet {} {} {}", p0, p1, p2);

        CopyParams copyParams = new CopyParams.Builder().fromDataSet(p1).toDataSet(p0).build();
        
        try {
        	response = dsnCopy.copyCommon(copyParams);
        	
			LOG.debug("dsnCopy {}", response);
        } catch (ZosmfRequestException e) {
            throw new ConnectionException(e);
        }
        
		try (Reader r = new InputStreamReader(p2)) {
			dsnWrite.write(p0, IOUtils.toString(r));
		} catch (ZosmfRequestException | IOException e) {
			throw new ConnectionException(e);
		}
	}

	private ByteArrayOutputStream retrieve(String p0) throws ConnectionException {
        DownloadParams params = new DownloadParams.Builder().build();

        try (InputStream is = dsnGet.get(p0, params);
        		ByteArrayOutputStream os = new ByteArrayOutputStream()) {
        	IOUtils.copy(is, os);
        	
        	return os;
        } catch (IOException | ZosmfRequestException e) {
        	throw new ConnectionException(String.format("Cannot retrieve %s", p0), e);
		}
	}
	
	private List<ZOSConnectionResponse> getDataSets(String pattern) throws ConnectionException {
		List<Dataset> items;
		
		try {
			items = dsnList.getDatasets(pattern, new ListParams.Builder().attribute(AttributeType.BASE).build());
		} catch (ZosmfRequestException e) {
			throw new ConnectionException(e);
		}
			
		List<ZOSConnectionResponse> result = new ArrayList<>(items.size());
			
		for (Dataset item : items) {
			ZOSConnectionResponse cr = new ZOSConnectionResponse();
			cr.addAttribute(IZOSConstants.FILE_NAME, item.getDsname().orElse(ZoweConnection.UNKNOWN));
			cr.addAttribute(IZOSConstants.FILE_BLOCK_SIZE, item.getBlksz().orElse(ZoweConnection.UNKNOWN));
			cr.addAttribute(IZOSConstants.FILE_EXT, item.getExtx().orElse(ZoweConnection.UNKNOWN));
			cr.addAttribute(IZOSConstants.FILE_RECORD_LENGTH, item.getLrectl().orElse(ZoweConnection.UNKNOWN));
			cr.addAttribute(IZOSConstants.FILE_REFERRED_DATE, item.getRdate().orElse(ZoweConnection.UNKNOWN));
			cr.addAttribute(IZOSConstants.FILE_RECORD_FORMAT, item.getRecfm().orElse(ZoweConnection.UNKNOWN));
			cr.addAttribute(IZOSConstants.FILE_ALLOCATED, item.getUsed().orElse(ZoweConnection.UNKNOWN));
			cr.addAttribute(IZOSConstants.FILE_VOLUME, item.getVol().orElse(ZoweConnection.UNKNOWN));
			cr.addAttribute(IZOSConstants.FILE_CREATION_DATE, item.getCdate().orElse(ZoweConnection.UNKNOWN));
			cr.addAttribute(IZOSConstants.FILE_SIZE, item.getSizex().orElse(ZoweConnection.UNKNOWN));
			
			if ("YES".equals(item.getMigr().orElse("NO"))) {
				cr.addAttribute(IZOSConstants.FILE_UNAVAILABLE, IZOSConstants.Unavailable.Migrated);
			}

			String dsorg = item.getDsorg().orElse(ZoweConnection.UNKNOWN);
				
			if ("VS".equals(dsorg)) {
				cr.addAttribute(IZOSConstants.FILE_DSORG, "VSAM");
					
				if (cr.getAttribute(IZOSConstants.FILE_NAME).endsWith(".DATA")) {
					cr.addAttribute(IZOSConstants.FILE_VSAM_DATA, true);
				}
					
				if (cr.getAttribute(IZOSConstants.FILE_NAME).endsWith(".INDEX")) {
					cr.addAttribute(IZOSConstants.FILE_VSAM_INDEX, true);
				}
			} else {
				cr.addAttribute(IZOSConstants.FILE_DSORG, dsorg);
			}
			
			result.add(cr);
		}
		
		return result;
	}
	private List<ZOSConnectionResponse> getMembers(String dsn) throws ConnectionException {
		List<Member> items;
		
		try {
			items = dsnList.getMembers(dsn, new ListParams.Builder().attribute(AttributeType.MEMBER).build());
		} catch (ZosmfRequestException e) {
			throw new ConnectionException(e);
		}
			
		List<ZOSConnectionResponse> result = new ArrayList<>(items.size());
			
		for (Member item : items) {
			ZOSConnectionResponse cr = new ZOSConnectionResponse();
			
			cr.addAttribute(IZOSConstants.FILE_PARENTPATH, dsn);
			cr.addAttribute(IZOSConstants.NAME, item.getMember().orElse(ZoweConnection.UNKNOWN));
			cr.addAttribute(IZOSConstants.FILE_CREATION_DATE, item.getC4date().orElse(ZoweConnection.UNKNOWN));
			cr.addAttribute(IZOSConstants.FILE_CHANGED_DATE, item.getM4date().orElse(ZoweConnection.UNKNOWN));
			cr.addAttribute(IZOSConstants.FILE_MOD, item.getVers().orElse(0L));
			
			result.add(cr);
		}
		
		return result;
	}
}
