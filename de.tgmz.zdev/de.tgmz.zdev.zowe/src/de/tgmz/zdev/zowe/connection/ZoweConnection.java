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
import java.util.List;

import javax.net.ssl.SSLContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.cics.core.comm.ConnectionException;
import com.ibm.cics.core.comm.CredentialsConfiguration;
import com.ibm.cics.core.comm.ExplorerSecurityHelper;
import com.ibm.cics.core.connections.ConnectionsPlugin;
import com.ibm.cics.zos.comm.AbstractZOSConnection;
import com.ibm.cics.zos.comm.IZOSConnection;
import com.ibm.cics.zos.comm.ZOSConnectionResponse;

import zowe.client.sdk.core.ZosConnection;
import zowe.client.sdk.rest.exception.ZosmfRequestException;
import zowe.client.sdk.zosmfinfo.methods.ZosmfStatus;
import zowe.client.sdk.zosmfinfo.response.ZosmfInfoResponse;

public class ZoweConnection extends AbstractZOSConnection implements IZOSConnection {
	private static final Logger LOG = LoggerFactory.getLogger(ZoweConnection.class);

	public static final String CATEGORY_ID = "com.ibm.cics.zos.comm.connection";

	public static final String UNKNOWN = "UNKNOWN";

	private boolean connected;
	
	private ZosConnection connection;
	
	private ZoweUssConnection ussConnection;
	private ZoweJobConnection jobConnection;
	private ZoweDsnConnection dsnConnection;
	
	private SSLContext sslContext;

	@Override
	public void connect() throws ConnectionException {
		CredentialsConfiguration cc = ConnectionsPlugin.getDefault().getCredentialsManager().findCredentialsConfigurationByID(super.getConfiguration().getCredentialsID());
		
		this.connect(getConfiguration().getHost(), getConfiguration().getPort(), cc.getUserID(), new String(cc.getPasswordAsCharArray()));
	}
	
	public void connect(String host, int port, String user, String pass) throws ConnectionException {
		connection = new ZosConnection(host, String.valueOf(port), user, pass);

		initSSLConfiguration();
		
		ussConnection = new ZoweUssConnection(connection);
		jobConnection = new ZoweJobConnection(connection);
		dsnConnection = new ZoweDsnConnection(connection);
		
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
	public void disconnect() {
		connection = null;
		connected = false;
	}

	@Override
	public String getHost() {
		return connection.getHost();
	}

	@Override
	public String getName() {
		return CATEGORY_ID;
	}

	@Override
	public int getPort() {
		return Integer.parseInt(connection.getZosmfPort());
	}

	@Override
	public String getUserID() {
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
		return jobConnection.getJob(p0);
	}

	@Override
	public ByteArrayOutputStream getJobStepSpool(String p0) throws ConnectionException {
		return jobConnection.getJobStepSpool(p0);
	}

	@Override
	public List<ZOSConnectionResponse> getJobSteps(String p0) throws ConnectionException {
		return jobConnection.getJobSteps(p0);
	}

	@Override
	public List<ZOSConnectionResponse> getJobs(String p0, JobStatus p1, String p2) throws ConnectionException {
		return jobConnection.getJobs(p0, p1, p2);
	}

	@Override
	public List<ZOSConnectionResponse> getDataSetMembers(String p0) throws ConnectionException {
		return dsnConnection.getDataSetMembers(p0);
	}

	@Override
	public ByteArrayOutputStream retrieveDataSetMember(String p0, String p1) throws ConnectionException {
		return dsnConnection.retrieveDataSetMember(p0, p1);
	}

	@Override
	public void recallDataSetMember(String p0, String p1) throws ConnectionException {
		dsnConnection.recallDataSetMember(p0, p1);
	}

	@Override
	public ByteArrayOutputStream retrieveSequentialDataSet(String p0) throws ConnectionException {
		return dsnConnection.retrieveSequentialDataSet(p0);
	}

	@Override
	public ByteArrayOutputStream submitDataSetMember(String p0, String p1) throws ConnectionException {
		return jobConnection.submitDataSetMember(p0, p1);
	}

	@Override
	public void saveDataSetMember(String p0, String p1, InputStream p2) throws ConnectionException {
		dsnConnection.saveDataSetMember(p0, p1, p2);
	}

	@Override
	public void deleteDataSet(String p0, String p1) throws ConnectionException {
		dsnConnection.deleteDataSet(p0, p1);
	}

	@Override
	public void createDataSet(String p0, DataSetArguments p1) throws ConnectionException {
		dsnConnection.createDataSet(p0, p1);
	}

	@Override
	public ZOSConnectionResponse getDataSet(String p0) throws ConnectionException {
		return dsnConnection.getDataSet(p0);
	}

	@Override
	public ZOSConnectionResponse getDataSetMember(String p0, String p1)	throws ConnectionException {
		return dsnConnection.getDataSetMember(p0, p1);
	}

	@Override
	public ZOSConnectionResponse createDataSetMember(String p0, String p1) throws ConnectionException {
		return dsnConnection.createDataSetMember(p0, p1);
	}

	@Override
	public void createDataSet(String p0, String p1, InputStream p2) throws ConnectionException {
		dsnConnection.createDataSet(p0, p1, p2);
	}

	@Override
	public List<ZOSConnectionResponse> getHFSChildren(String p0, boolean p1) throws ConnectionException {
		return ussConnection.getHFSChildren(p0, p1);
	}

	@Override
	public boolean existsHFS(String p0) throws ConnectionException {
		return ussConnection.existsHFS(p0);
	}

	@Override
	public boolean existsHFSFile(String p0, String p1) throws ConnectionException {
		return ussConnection.existsHFSFile(p0, p1);
	}

	@Override
	public void createFolderHFS(String p0) throws ConnectionException {
		ussConnection.createFolderHFS(p0);
	}

	@Override
	public void deletePathHFS(String p0) throws ConnectionException {
		ussConnection.deletePathHFS(p0);
	}

	@Override
	public void saveFileHFS(String p0, InputStream p1, FileType p2) throws ConnectionException {
		ussConnection.saveFileHFS(p0, p1, p2);
	}

	@Override
	public void saveFileHFS(String p0, InputStream p1, String p2) throws ConnectionException {
		ussConnection.saveFileHFS(p0, p1, p2);
	}

	@Override
	public ByteArrayOutputStream getFileHFS(String p0, FileType p1) throws ConnectionException {
		return ussConnection.getFileHFS(p0, p1);
	}

	@Override
	public ByteArrayOutputStream getJobSpool(String p0) throws ConnectionException {
		return jobConnection.getJobSpool(p0);
	}

	@Override
	public ZOSConnectionResponse submitJob(InputStream p0) throws ConnectionException {
		return jobConnection.submitJob(p0);
	}

	@Override
	public void deleteJob(String p0) throws ConnectionException {
		jobConnection.deleteJob(p0);
	}

	@Override
	public void cancelJob(String p0) throws ConnectionException {
		jobConnection.cancelJob(p0);
	}

	@Override
	public boolean canPerform(String p0, String p1) {
		return true;
	}

	@Override
	public void perform(String p0, String p1) throws ConnectionException {
		LOG.debug("perform {} {}", p0, p1);
		super.perform(p0, p1);
	}

	@Override
	public void changePermissions(String p0, String p1) throws ConnectionException {
		ussConnection.changePermissions(p0, p1);
	}
	
	private void initSSLConfiguration() {
		try {
			Object[] helper = ExplorerSecurityHelper.getSSLContext(getConfiguration().getName(), getConfiguration().getHost());
			sslContext = (SSLContext) helper[0];
		} catch (IOException e) {
			LOG.info("Cannot get SSL context", e);
		}
	}
}
