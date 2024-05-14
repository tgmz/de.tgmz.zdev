/*********************************************************************
* Copyright (c) 12.04.2024 Thomas Zierer
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package de.tgmz.zdev.zowe.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import javax.net.ssl.HttpsURLConnection;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.NullInputStream;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.Test.None;
import org.mockserver.configuration.Configuration;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.logging.MockServerLogger;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.mockserver.socket.PortFactory;
import org.mockserver.socket.tls.KeyStoreFactory;

import com.ibm.cics.core.comm.ConnectionException;
import com.ibm.cics.zos.comm.IZOSConnection.DataSetArguments;
import com.ibm.cics.zos.comm.IZOSConstants;
import com.ibm.cics.zos.comm.IZOSConstants.FileType;
import com.ibm.cics.zos.comm.IZOSConstants.JobStatus;
import com.ibm.cics.zos.comm.ZOSFileNotFoundException;
import com.ibm.cics.zos.comm.ZOSUnsupportedOperationException;

import de.tgmz.zdev.zowe.connection.ZoweConnection;

public class ZoweConnectionTest {
	private static final String LOG_LEVEL_KEY = "org.slf4j.simpleLogger.defaultLogLevel";
	private static final String PO_NAME = "HLQ.FOO";
	private static final String MEMBER_NAME = "BAR";
	private static final String JOB_NAME = "FOOBAR";
	private static final String USER = "foo";
	private static final String PASS = "bar";
	private static final String HFS_PATH = String.format("/%s/%s/", USER, PASS);	// doesn't matter
	private static final String HTTP_DELETE = "DELETE";
	private static final String HTTP_GET = "GET";
	private static final String HTTP_POST = "POST";
	private static final String HTTP_PUT = "PUT";
	
	private enum ZosmfPaths {
		DATASETS("/zosmf/restfiles/ds"), FILES("/zosmf/restfiles/fs"), JOBS("/zosmf/restjobs/jobs");

		private String path;
		
		private ZosmfPaths(String path) {
			this.path = path;
		}

		public String getPath() {
			return path;
		}
	}
	
	private static String logLevel;
	private static ClientAndServer server;
	private static ZoweConnection connection;
	
	@BeforeClass
	public static void setupOnce() throws  ConnectionException {
		logLevel = System.getProperty(LOG_LEVEL_KEY, "INFO");
		System.setProperty(LOG_LEVEL_KEY, "INFO");	// Set to "DEBUG" to force noisy logging
		
		HttpsURLConnection.setDefaultSSLSocketFactory(new KeyStoreFactory(Configuration.configuration(), new MockServerLogger()).sslContext().getSocketFactory());
		server = ClientAndServer.startClientAndServer(PortFactory.findFreePort());

		// Mock successful login
		server.when(HttpRequest.request().withMethod(HTTP_GET).withPath("/zosmf/info")).respond(HttpResponse.response().withStatusCode(200));
		
		connection = new ZoweConnection();
		connection.connect(server.remoteAddress().getHostName(), server.getPort(), USER, PASS);
	}
	
	@AfterClass
	public static void teardownOnce() {
		System.setProperty(LOG_LEVEL_KEY, logLevel);
		connection.disconnect();
	}

	@Before
	public void setup() {
		server.reset();
	}
	
	@Test
	public void testCommon() {
		assertTrue(connection.canPerform(IZOSConstants.ACTION_SUPPORT_DATA_SETS, null));
		assertNotNull(connection.getConfiguration());
		assertEquals(server.remoteAddress().getHostName(), connection.getHost());
		assertEquals(server.getPort().intValue(), connection.getPort());
		assertTrue(connection.isConnected());
		assertTrue(connection.isSecure());
		assertEquals(ZoweConnection.CATEGORY_ID, connection.getName());
		assertEquals(USER, connection.getUserID());
		
		assertThrows(ZOSUnsupportedOperationException.class, () -> connection.perform(IZOSConstants.ACTION_SUPPORT_DATA_SETS, null));
	}
	@Test(expected = None.class)
	public void testDelete() throws IOException, ConnectionException {
		server.when(HttpRequest.request().withMethod(HTTP_DELETE).withPath(getUri(ZosmfPaths.DATASETS, "/.*"))).respond(HttpResponse.response().withStatusCode(204));
		server.when(HttpRequest.request().withMethod(HTTP_DELETE).withPath(getUri(ZosmfPaths.FILES, ".*"))).respond(HttpResponse.response().withStatusCode(204));
		connection.deleteDataSet(PO_NAME, MEMBER_NAME);
		connection.deleteDataSet(null, PO_NAME);
		connection.deletePathHFS(HFS_PATH);
		
		try (InputStream is0 = ZoweConnectionTest.class.getClassLoader().getResourceAsStream("testresources/jcl0.json")) {
			server.when(HttpRequest.request().withMethod(HTTP_GET).withPath(getUri(ZosmfPaths.JOBS, ""))).respond(HttpResponse.response(IOUtils.toString(is0, StandardCharsets.UTF_8)));
		}
		server.when(HttpRequest.request().withMethod(HTTP_DELETE).withPath(getUri(ZosmfPaths.JOBS, "/.*"))).respond(HttpResponse.response().withStatusCode(204));
		connection.deleteJob(JOB_NAME);
	}
	@Test
	public void testCreate() throws ConnectionException {
		server.when(HttpRequest.request().withMethod(HTTP_POST).withPath(getUri(ZosmfPaths.DATASETS, "/.*"))).respond(HttpResponse.response().withStatusCode(201));
		
		DataSetArguments dsa = new DataSetArguments();
		
		for (String unit : Arrays.asList("CYLINDERS", "TRACKS" , "BLOCKS", "UNSPECIFIED")) {
			dsa.spaceUnits = unit;
			connection.createDataSet(PO_NAME, dsa);
		}
		
		assertThrows(ZOSUnsupportedOperationException.class, () -> connection.createDataSet(PO_NAME, MEMBER_NAME, new NullInputStream()));
		assertThrows(ZOSUnsupportedOperationException.class, () -> connection.createDataSetMember(PO_NAME, MEMBER_NAME));
		assertThrows(ZOSUnsupportedOperationException.class, () -> connection.createFolderHFS(HFS_PATH));
	}
	@Test
	public void testRetrieve() throws ConnectionException, IOException {
		try (InputStream is0 = ZoweConnectionTest.class.getClassLoader().getResourceAsStream("testresources/ds0.json")) {
			server.when(HttpRequest.request().withMethod(HTTP_GET).withPath(getUri(ZosmfPaths.DATASETS, ".*"))).respond(HttpResponse.response(IOUtils.toString(is0, StandardCharsets.UTF_8)));
		}
		
		assertThrows(ZOSUnsupportedOperationException.class, () -> connection.recallDataSetMember(PO_NAME, MEMBER_NAME));
		
		assertNotNull(connection.retrieveSequentialDataSet(PO_NAME));
		assertNotNull(connection.retrieveDataSetMember(PO_NAME, MEMBER_NAME));

	}
	@Test
	public void testExists() {
		assertThrows(ZOSUnsupportedOperationException.class, () -> connection.existsHFS(HFS_PATH));
		assertThrows(ZOSUnsupportedOperationException.class, () -> connection.existsHFSFile(HFS_PATH, PASS));
	}
	@Test
	public void testSave() throws ConnectionException {
		server.when(HttpRequest.request().withMethod(HTTP_PUT).withPath(getUri(ZosmfPaths.DATASETS, "/.*"))).respond(HttpResponse.response().withStatusCode(200));
		connection.saveDataSetMember(PO_NAME, MEMBER_NAME, new NullInputStream());

		assertThrows(ZOSUnsupportedOperationException.class, () -> connection.saveFileHFS(HFS_PATH, new NullInputStream(), FileType.ASCII));
		assertThrows(ZOSUnsupportedOperationException.class, () -> connection.saveFileHFS(HFS_PATH, new NullInputStream(), ""));
	}
	@Test
	public void testSubmit() throws ConnectionException, IOException {
		try (InputStream is0 = ZoweConnectionTest.class.getClassLoader().getResourceAsStream("testresources/jcl1.json")) {
			server.when(HttpRequest.request().withMethod(HTTP_PUT).withPath(getUri(ZosmfPaths.JOBS, ""))).respond(HttpResponse.response(IOUtils.toString(is0, StandardCharsets.UTF_8)));
		}
		connection.submitJob(IOUtils.toInputStream("//FOO JOB", Charset.defaultCharset()));
		
		assertThrows(ZOSUnsupportedOperationException.class, () -> connection.submitDataSetMember(JOB_NAME, MEMBER_NAME));
	}
	@Test
	public void testCancel() {
		assertThrows(ZOSUnsupportedOperationException.class, () -> connection.cancelJob(JOB_NAME));
	}
	@Test
	public void testChangePermissions() {
		assertThrows(ZOSUnsupportedOperationException.class, () -> connection.changePermissions(JOB_NAME, MEMBER_NAME));
	}
	@Test
	public void testGet() throws ConnectionException, IOException {
		try (InputStream is0 = ZoweConnectionTest.class.getClassLoader().getResourceAsStream("testresources/ds0.json")) {
			String body = IOUtils.toString(is0, StandardCharsets.UTF_8);
			
			server.when(HttpRequest.request().withMethod(HTTP_GET).withPath(getUri(ZosmfPaths.DATASETS, ".*"))).respond(HttpResponse.response(body));
			server.when(HttpRequest.request().withMethod(HTTP_GET).withPath(getUri(ZosmfPaths.FILES, ".*"))).respond(HttpResponse.response(body));
		}
		assertNotNull(connection.getDataSet(PO_NAME));
		assertNotNull(connection.getDataSetMember(PO_NAME, MEMBER_NAME));
		assertNotNull(connection.getDataSetMembers(PO_NAME));
		assertNotNull(connection.getFileHFS(HFS_PATH, FileType.ASCII));
		assertNotNull(connection.getHFSChildren(HFS_PATH, true));

		try (InputStream is0 = ZoweConnectionTest.class.getClassLoader().getResourceAsStream("testresources/jcl0.json")) {
			server.when(HttpRequest.request().withMethod(HTTP_GET).withPath(getUri(ZosmfPaths.JOBS, ".*"))).respond(HttpResponse.response(IOUtils.toString(is0, StandardCharsets.UTF_8)));
		}
		assertNotNull(connection.getJob(JOB_NAME));
		assertNotNull(connection.getJobs("*", JobStatus.ALL, JOB_NAME));
		assertThrows(ZOSUnsupportedOperationException.class, () -> assertNotNull(connection.getJobSpool(JOB_NAME)));
		assertNotNull(connection.getJobSteps(JOB_NAME));
		assertThrows(ZOSUnsupportedOperationException.class, () -> assertNotNull(connection.getJobStepSpool(JOB_NAME)));
	}
	@Test
	public void testGetNotFound() throws IOException {
		try (InputStream is0 = ZoweConnectionTest.class.getClassLoader().getResourceAsStream("testresources/ds1.json")) {
			String body = IOUtils.toString(is0, StandardCharsets.UTF_8);
			
			server.when(HttpRequest.request().withMethod(HTTP_GET).withPath(getUri(ZosmfPaths.DATASETS, ".*"))).respond(HttpResponse.response(body));
			server.when(HttpRequest.request().withMethod(HTTP_GET).withPath(getUri(ZosmfPaths.FILES, ".*"))).respond(HttpResponse.response(body));
		}
		
		assertThrows(ZOSFileNotFoundException.class, () -> connection.getDataSet(PO_NAME));
		assertThrows(ZOSFileNotFoundException.class, () -> connection.getDataSetMember(PO_NAME, MEMBER_NAME));
	}
	private static String getUri(ZosmfPaths path, String pattern) {
		return String.format("%s%s", path.getPath(), pattern);
	}
}
