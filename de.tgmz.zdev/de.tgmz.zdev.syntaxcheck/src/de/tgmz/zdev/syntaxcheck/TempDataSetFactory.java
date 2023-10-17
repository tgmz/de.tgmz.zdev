/*********************************************************************
* Copyright (c) 10.10.2023 Thomas Zierer
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/

package de.tgmz.zdev.syntaxcheck;

import java.security.SecureRandom;
import java.util.Locale;

import com.ibm.cics.core.comm.ConnectionException;
import com.ibm.cics.core.connections.ConnectionUtilities;
import com.ibm.cics.zos.model.DataEntry;
import com.ibm.cics.zos.model.DataSet;
import com.ibm.cics.zos.model.DataSet.NewDatasetType;
import com.ibm.cics.zos.model.DataSet.RecordFormat;
import com.ibm.cics.zos.model.DataSet.SpaceUnits;
import com.ibm.cics.zos.model.DataSetBuilder;
import com.ibm.cics.zos.model.Member;
import com.ibm.cics.zos.model.PermissionDeniedException;
import com.ibm.cics.zos.model.SequentialDataSet;
import com.ibm.cics.zos.model.UnsupportedOperationException;
import com.ibm.cics.zos.model.UpdateFailedException;

import de.tgmz.zdev.connection.ZdevConnectable;

/**
 * Factory for dealing with temporary datasets used for error feedback.
 */
public class TempDataSetFactory {
	private static final TempDataSetFactory INSTANCE = new TempDataSetFactory();
	private static final SecureRandom R = new SecureRandom();

	private TempDataSetFactory() {
	}
	
	public static TempDataSetFactory getInstance() {
		return INSTANCE;
	}
	
	public SequentialDataSet createErrorFeedback() throws UpdateFailedException {
		return (SequentialDataSet) create(0x3fff, NewDatasetType.PS, 0, RecordFormat.VB); //(hex) 3FFF == 16383
	}

	public Member createTempSrc(String mbr) throws UpdateFailedException {
		DataSet ds = create(80, NewDatasetType.PDSE, 10, RecordFormat.FB);
		
   		return new Member(ds.getFullPath(), mbr,  ZdevConnectable.getConnectable());
	}

	/**
	 * @param ds the dataset to delete. 
	 * We always want to delete the entire dataset not only the member so we enforce the use of dataset here instead of {@link DataEntry}
	 * @throws PermissionDeniedException
	 * @throws UnsupportedOperationException
	 * @throws ConnectionException
	 */
	public void delete(DataSet ds) throws PermissionDeniedException, UnsupportedOperationException, ConnectionException {
		if (ds != null) {
			ZdevConnectable.getConnectable().delete(ds);
		} 
	}
	
	private DataSet create(int recordLength, NewDatasetType datasetType, int directoryBlocks, RecordFormat format) throws UpdateFailedException {
		String userid = ConnectionUtilities.getUserID(ZdevConnectable.getConnectable());
		
		String tempDatasetName = userid.toUpperCase(Locale.ROOT) + ".ZDEVERR.T" + R.nextInt(1000000);
		
    	DataSetBuilder dsb = new DataSetBuilder(tempDatasetName);
    	dsb.recordLength = recordLength;
    	dsb.datasetType = datasetType;
    	dsb.directoryBlocks = directoryBlocks;
    	dsb.format = format;
    	dsb.spaceUnits = SpaceUnits.TRACKS;
    	dsb.primaryAllocation = 10;
    	dsb.secondaryAllocation = 10;
    	dsb.volume = "WRK";

    	return ZdevConnectable.getConnectable().create(dsb);
	}
}
