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
import java.io.Reader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import javax.net.ssl.SSLContext;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.cics.core.comm.ConnectionException;
import com.ibm.cics.core.comm.CredentialsConfiguration;
import com.ibm.cics.core.comm.ExplorerSecurityHelper;
import com.ibm.cics.core.connections.ConnectionsPlugin;
import com.ibm.cics.core.connections.ICredentialsManager;
import com.ibm.cics.zos.comm.AbstractZOSConnection;
import com.ibm.cics.zos.comm.IZOSConnection;
import com.ibm.cics.zos.comm.IZOSConstants;
import com.ibm.cics.zos.comm.ZOSConnectionResponse;
import com.ibm.cics.zos.comm.ZOSFileNotFoundException;
import com.ibm.cics.zos.model.IJob;
import com.ibm.cics.zos.model.IJob.JobCompletion;

import zowe.client.sdk.core.ZosConnection;
import zowe.client.sdk.rest.Response;
import zowe.client.sdk.rest.exception.ZosmfRequestException;
import zowe.client.sdk.zosfiles.dsn.input.DownloadParams;
import zowe.client.sdk.zosfiles.dsn.input.ListParams;
import zowe.client.sdk.zosfiles.dsn.methods.DsnDelete;
import zowe.client.sdk.zosfiles.dsn.methods.DsnGet;
import zowe.client.sdk.zosfiles.dsn.methods.DsnList;
import zowe.client.sdk.zosfiles.dsn.methods.DsnWrite;
import zowe.client.sdk.zosfiles.dsn.response.Dataset;
import zowe.client.sdk.zosfiles.dsn.response.Member;
import zowe.client.sdk.zosfiles.dsn.types.AttributeType;
import zowe.client.sdk.zosfiles.uss.methods.UssGet;
import zowe.client.sdk.zosfiles.uss.methods.UssList;
import zowe.client.sdk.zosfiles.uss.response.UnixFile;
import zowe.client.sdk.zosjobs.input.GetJobParams;
import zowe.client.sdk.zosjobs.input.JobFile;
import zowe.client.sdk.zosjobs.methods.JobGet;
import zowe.client.sdk.zosjobs.response.Job;
import zowe.client.sdk.zosmfinfo.methods.ZosmfStatus;
import zowe.client.sdk.zosmfinfo.response.ZosmfInfoResponse;

public class ZoweConnection extends AbstractZOSConnection implements IZOSConnection {
	public static final String CATEGORY_ID = "com.ibm.cics.zos.comm.connection";

	private static final Logger LOG = LoggerFactory.getLogger(ZoweConnection.class);
	private static final String UNKNOWN = "UNKNOWN";

	private ICredentialsManager credentialsManager;

	private boolean connected;
	
	private ZosConnection connection;
	
	private Response response;
	
	private DsnGet dsnGet;
	private DsnWrite dsnWrite;
	private DsnDelete dsnDelete;
	private DsnList dsnList;
	
	private UssList ussList;
    private UssGet ussGet;

    private JobGet jobGet;

	private SSLContext sslContext;

	public ZoweConnection() {
		this.credentialsManager = ConnectionsPlugin.getDefault().getCredentialsManager();
	}
	
	@Override
	public void connect() throws ConnectionException {
		CredentialsConfiguration cc = credentialsManager.findCredentialsConfigurationByID(super.getConfiguration().getCredentialsID());
		
		this.connect(getConfiguration().getHost(), getConfiguration().getPort(), cc.getUserID(), new String(cc.getPasswordAsCharArray()));
	}
	
	public void connect(String host, int port, String user, String pass) throws ConnectionException {
		connection = new ZosConnection(host, String.valueOf(port), user, pass);
		
		initSSLConfiguration();
		
		dsnWrite = new DsnWrite(connection);
		dsnDelete = new DsnDelete(connection);
		dsnGet = new DsnGet(connection);
		dsnList = new DsnList(connection);
		
		ussList = new UssList(connection);
		ussGet = new UssGet(connection);

		jobGet = new JobGet(connection);
		
        ZosmfStatus zosmfStatus = new ZosmfStatus(connection);

        try {
        	ZosmfInfoResponse zosmfInfoResponse = zosmfStatus.get();
        	
        	String realHost = zosmfInfoResponse.getZosmfHostName().orElse(UNKNOWN);
        	String osVersion =  zosmfInfoResponse.getZosVersion().orElse(UNKNOWN);
        	
        	connected = true;
        	
        	LOG.info("Connected to {} running on z/OS version {}", realHost, osVersion);
        } catch (ZosmfRequestException e) {
        	connected = false;
        	
        	throw new ConnectionException(e);
        }
	}

	@Override
	public void disconnect() throws ConnectionException {
		LOG.debug("disconnect");
		
		connected = false;
	}

	@Override
	public String getHost() {
		LOG.debug("getHost");
		return connection.getHost();
	}

	@Override
	public String getName() {
		LOG.debug("getName");
		return CATEGORY_ID;
	}

	@Override
	public int getPort() {
		LOG.debug("getPort");
		return Integer.parseInt(connection.getZosmfPort());
	}

	@Override
	public String getUserID() {
		LOG.debug("getUserID");
		return connection.getUser();
	}

	@Override
	public boolean isConnected() {
		return connected;
	}

	@Override
	public boolean isSecure() {
		return sslContext != null;
	}

	@Override
	public ZOSConnectionResponse getJob(String p0) throws ConnectionException {
		LOG.debug("getJob {}", p0);
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ByteArrayOutputStream getJobStepSpool(String p0) throws ConnectionException {
		LOG.debug("getJobStepSpool {}", p0);
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ZOSConnectionResponse> getJobSteps(String p0) throws ConnectionException {
		LOG.debug("getJobSteps {}", p0);

		List<JobFile> spoolFilesByJob;
		
		try {
			Job byId = jobGet.getById(p0);
			 spoolFilesByJob = jobGet.getSpoolFilesByJob(byId);
		} catch (ZosmfRequestException e) {
        	throw new ConnectionException(e);
		}

		List<ZOSConnectionResponse> result = new ArrayList<>(spoolFilesByJob.size());

		for (JobFile jf : spoolFilesByJob) {
			ZOSConnectionResponse cr = new ZOSConnectionResponse();
			
			cr.addAttribute(IZOSConstants.JOB_STEPNAME, jf.getDdName().orElse(UNKNOWN));
			cr.addAttribute(IZOSConstants.JOB_ID, jf.getJobId().orElse(p0));
			cr.addAttribute(IZOSConstants.JOB_DDNAME, jf.getDdName().orElse(p0));
			cr.addAttribute(IZOSConstants.JOB_SPOOL_FILES_AVAILABLE, true);

			result.add(cr);
		}
	
		return result;
	}

	@Override
	public List<ZOSConnectionResponse> getJobs(String p0, JobStatus p1, String p2) throws ConnectionException {
		LOG.debug("getJobs {}, {}, {}", p0, p1, p2);

    	List<ZOSConnectionResponse> result = new LinkedList<>();
    	List<Job> jobs;
    	
        GetJobParams params = new GetJobParams.Builder(p2).prefix(p0).build();

        try {
        	jobs = jobGet.getCommon(params);
        } catch (ZosmfRequestException e) {
        	throw new ConnectionException(e);
        }
        	
       	for (Job job : jobs) {
       		ZOSConnectionResponse cr = new ZOSConnectionResponse();

       		cr.addAttribute(IZOSConstants.NAME, job.getJobName().orElse(UNKNOWN));
       		cr.addAttribute(IZOSConstants.JOB_ID, job.getJobId().orElse(UNKNOWN));
       		cr.addAttribute(IZOSConstants.JOB_USER, job.getOwner().orElse(UNKNOWN));
       		cr.addAttribute(IZOSConstants.JOB_CLASS, job.getClasss().orElse(UNKNOWN));
       		cr.addAttribute(IZOSConstants.JOB_ERROR_CODE, job.getRetCode().orElse(UNKNOWN));
       		cr.addAttribute(IZOSConstants.JOB_HAS_SPOOL_FILES, true);
       		cr.addAttribute(IZOSConstants.JOB_STATUS, job.getStatus().orElse(UNKNOWN));
       		cr.addAttribute(IZOSConstants.JOB_SPOOL_FILES_AVAILABLE, true);
       		
       		Optional<String> oStatus = job.getStatus();
       		
       		if (oStatus.isPresent()) {
       			String status = oStatus.get();
       			JobCompletion jc;
       			try {
       				jc = IJob.JobCompletion.valueOf(oStatus.get());
       			} catch (IllegalArgumentException e) {
       				jc = "OUTPUT".equals(status) ? JobCompletion.NORMAL : JobCompletion.NA;
       			}
       			
           		cr.addAttribute(IZOSConstants.JOB_COMPLETION, jc);
       		}

       		result.add(cr);
       	}
		
		return result;
	}

	@Override
	public List<ZOSConnectionResponse> getDataSetMembers(String p0) throws ConnectionException {
		return p0.endsWith("*") ? getDataSets(p0) : getMembers(p0);
	}

	@Override
	public ByteArrayOutputStream retrieveDataSetMember(String p0, String p1) throws ConnectionException {
		LOG.debug("retrieveDataSetMember {}, {}", p0, p1);
		return retrieve(String.format("%s(%s)", p0, p1));
	}

	@Override
	public void recallDataSetMember(String p0, String p1) throws ConnectionException {
		LOG.debug("recallDataSetMember {}, {}", p0, p1);
		// TODO Auto-generated method stub
		
	}

	@Override
	public ByteArrayOutputStream retrieveSequentialDataSet(String p0) throws ConnectionException {
		LOG.debug("retrieveSequentialDataSet {}", p0);
		return retrieve(p0);
	}

	@Override
	public ByteArrayOutputStream submitDataSetMember(String p0, String p1) throws ConnectionException {
		LOG.debug("submitDataSetMember {}, {}", p0, p1);
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void saveDataSetMember(String p0, String p1, InputStream p2) throws ConnectionException {
		LOG.debug("saveDataSetMember {}, {}, {}", p0, p1, p2);
		try (Reader r = new InputStreamReader(p2)) {
			dsnWrite.write(p0, p1, IOUtils.toString(r));
		} catch (ZosmfRequestException | IOException e) {
			throw new ConnectionException(e);
		}
	}

	@Override
	public void deleteDataSet(String p0, String p1) throws ConnectionException {
		LOG.debug("deleteDataSet {}, {}", p0, p1);

		try {
			if (p0 == null) {
				response = dsnDelete.delete(p1);
			} else {
				response = dsnDelete.delete(p0, p1);
			}
			
			LOG.debug("Delete {}", response);
		} catch (ZosmfRequestException e) {
			throw new ConnectionException(e);
		}
	}

	@Override
	public void createDataSet(String p0, DataSetArguments p1) throws ConnectionException {
		LOG.debug("createDataSet {}, {}", p0, p1);
		// TODO Auto-generated method stub
		
	}

	@Override
	public ZOSConnectionResponse getDataSet(String p0) throws ConnectionException {
		LOG.debug("getDataSet {}", p0);
		
		Optional<ZOSConnectionResponse> first = getDataSets(p0).stream().filter(s -> p0.equals(s.getAttribute("FILE_NAME"))).findFirst();

		if (first.isPresent()) {
			return first.get();
		} else {
			throw new ZOSFileNotFoundException(p0, null);
		}
	}

	@Override
	public ZOSConnectionResponse getDataSetMember(String p0, String p1)	throws ConnectionException {
		LOG.debug("getDataSetMember {}, {}", p0, p1);

		Optional<ZOSConnectionResponse> first = getDataSetMembers(p0).stream().filter(s -> p1.equals(s.getAttribute("NAME"))).findFirst();
		
		if (first.isPresent()) {
			return first.get();
		} else {
			throw new ZOSFileNotFoundException(p0, p1);
		}
	}

	@Override
	public ZOSConnectionResponse createDataSetMember(String p0, String p1) throws ConnectionException {
		LOG.debug("createDataSetMember {}, {}", p0, p1);
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void createDataSet(String p0, String p1, InputStream p2) throws ConnectionException {
		LOG.debug("createDataSet {}, {}, {}", p0, p1, p2);
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<ZOSConnectionResponse> getHFSChildren(String p0, boolean p1) throws ConnectionException {
		LOG.debug("getHFSChildren {}, {}", p0, p1);
		
		// Trailing slash yields "incorrect path"
		String path = p0.endsWith("/") ? p0.substring(0, p0.length() - 1) : p0;

        List<UnixFile> items;
        
        try {
            zowe.client.sdk.zosfiles.uss.input.ListParams params = new zowe.client.sdk.zosfiles.uss.input.ListParams.Builder().path(path).build();
            items = ussList.getFiles(params);
        } catch (ZosmfRequestException e) {
            throw new ConnectionException(e);
        }

        List<ZOSConnectionResponse> result = new ArrayList<>(items.size());
        
        for (UnixFile item : items) {
			ZOSConnectionResponse cr = new ZOSConnectionResponse();
			
			String mode = item.getMode().orElse("-rw-r-----");
			
			cr.addAttribute(IZOSConstants.HFS_PARENT_PATH, p0);
			cr.addAttribute(IZOSConstants.NAME, item.getName().orElse(UNKNOWN));
			cr.addAttribute(IZOSConstants.HFS_SIZE, item.getSize().orElse(0L));
			cr.addAttribute(IZOSConstants.HFS_DIRECTORY, mode.startsWith("d"));
			cr.addAttribute(IZOSConstants.HFS_USER, item.getUser().orElse(UNKNOWN));
			cr.addAttribute(IZOSConstants.HFS_GROUP, item.getGroup().orElse(UNKNOWN));
			cr.addAttribute(IZOSConstants.HFS_PERMISSIONS, mode);
			cr.addAttribute(IZOSConstants.HFS_LAST_USED_DATE, item.getMtime().orElse(UNKNOWN));

			result.add(cr);
        }

		return result;
	}

	@Override
	public boolean existsHFS(String p0) throws ConnectionException {
		LOG.debug("existsHFS {}", p0);
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean existsHFSFile(String p0, String p1) throws ConnectionException {
		LOG.debug("existsHFSFile {}, {}", p0, p1);
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void createFolderHFS(String p0) throws ConnectionException {
		LOG.debug("createFolderHFS {}", p0);
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deletePathHFS(String p0) throws ConnectionException {
		LOG.debug("deletePathHFS {}", p0);
		// TODO Auto-generated method stub
		
	}

	@Override
	public void saveFileHFS(String p0, InputStream p1, FileType p2) throws ConnectionException {
		LOG.debug("changePermissions {}, {}, {}", p0, p1, p2);
		// TODO Auto-saveFileHFS method stub
		
	}

	@Override
	public void saveFileHFS(String p0, InputStream p1, String p2) throws ConnectionException {
		LOG.debug("saveFileHFS {}, {}, {}", p0, p1, p2);
		// TODO Auto-generated method stub
		
	}

	@Override
	public ByteArrayOutputStream getFileHFS(String p0, FileType p1) throws ConnectionException {
		LOG.debug("getFileHFS {}, {}", p0, p1);
		
		byte[] content;
		
		try {
			content = p1 == FileType.BINARY ?  ussGet.getBinary(p0) : ussGet.getText(p0).getBytes();
		} catch (ZosmfRequestException e) {
			throw new ConnectionException(e);
		}
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream(content.length);
		
		try (InputStream is = new ByteArrayInputStream(content)) {
			IOUtils.copy(is, baos);
		} catch (IOException e) {
			LOG.error("Error converting content", e);
		}
		
		return baos;
	}

	@Override
	public ByteArrayOutputStream getJobSpool(String p0) throws ConnectionException {
		LOG.debug("getJobSpool {}", p0);
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ZOSConnectionResponse submitJob(InputStream p0) throws ConnectionException {
		LOG.debug("submitJob {}", p0);
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteJob(String p0) throws ConnectionException {
		LOG.debug("deleteJob {}", p0);
		// TODO Auto-generated method stub
		
	}

	@Override
	public void cancelJob(String p0) throws ConnectionException {
		LOG.debug("cancelJob {}", p0);
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean canPerform(String p0, String p1) {
		LOG.debug("canPerform {}, {}", p0, p1);
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void perform(String p0, String p1) throws ConnectionException {
		LOG.debug("perform {}, {}", p0, p1);
		// TODO Auto-generated method stub
		
	}

	@Override
	public void changePermissions(String p0, String p1) throws ConnectionException {
		LOG.debug("changePermissions {}, {}", p0, p1);
		// TODO Auto-generated method stub
		
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
	
	private void initSSLConfiguration() {
		try {
			Object[] helper = ExplorerSecurityHelper.getSSLContext(getConfiguration().getName(), getConfiguration().getHost());
			sslContext = (SSLContext) helper[0];
		} catch (IOException e) {
			LOG.info("Cannot get SSL context");
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
			cr.addAttribute(IZOSConstants.FILE_NAME, item.getDsname().orElse(UNKNOWN));
			cr.addAttribute(IZOSConstants.FILE_BLOCK_SIZE, item.getBlksz().orElse(UNKNOWN));
			cr.addAttribute(IZOSConstants.FILE_EXT, item.getExtx().orElse(UNKNOWN));
			cr.addAttribute(IZOSConstants.FILE_RECORD_LENGTH, item.getLrectl().orElse(UNKNOWN));
			cr.addAttribute(IZOSConstants.FILE_REFERRED_DATE, item.getRdate().orElse(UNKNOWN));
			cr.addAttribute(IZOSConstants.FILE_RECORD_FORMAT, item.getRecfm().orElse(UNKNOWN));
			cr.addAttribute(IZOSConstants.FILE_ALLOCATED, item.getUsed().orElse(UNKNOWN));
			cr.addAttribute(IZOSConstants.FILE_VOLUME, item.getVol().orElse(UNKNOWN));
			cr.addAttribute(IZOSConstants.FILE_CREATION_DATE, item.getCdate().orElse(UNKNOWN));
			cr.addAttribute(IZOSConstants.FILE_SIZE, item.getSizex().orElse(UNKNOWN));
			
			if (item.getMigr().isPresent() && "YES".equals(item.getMigr().get())) {
				cr.addAttribute(IZOSConstants.FILE_UNAVAILABLE, IZOSConstants.Unavailable.Migrated);
			}

			if (item.getDsorg().isPresent()) {
				String dsorg = item.getDsorg().get();
				
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
			cr.addAttribute(IZOSConstants.NAME, item.getMember().orElse(UNKNOWN));
			cr.addAttribute(IZOSConstants.FILE_CREATION_DATE, item.getC4date().orElse(UNKNOWN));
			cr.addAttribute(IZOSConstants.FILE_CHANGED_DATE, item.getM4date().orElse(UNKNOWN));
			cr.addAttribute(IZOSConstants.FILE_MOD, item.getVers().orElse(0L));
			
			result.add(cr);
		}
		
		return result;
	}
}
