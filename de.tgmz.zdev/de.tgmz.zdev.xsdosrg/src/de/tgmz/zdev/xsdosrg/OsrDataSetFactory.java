/*********************************************************************
* Copyright (c) 10.10.2023 Thomas Zierer
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/

package de.tgmz.zdev.xsdosrg;

import java.io.FileNotFoundException;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.cics.core.comm.ConnectionException;
import com.ibm.cics.zos.model.DataEntry;
import com.ibm.cics.zos.model.DataSet;
import com.ibm.cics.zos.model.DataSet.NewDatasetType;
import com.ibm.cics.zos.model.DataSet.RecordFormat;
import com.ibm.cics.zos.model.DataSet.SpaceUnits;
import com.ibm.cics.zos.model.DataSetBuilder;
import com.ibm.cics.zos.model.Member;
import com.ibm.cics.zos.model.PartitionedDataSet;
import com.ibm.cics.zos.model.PermissionDeniedException;
import com.ibm.cics.zos.model.UpdateFailedException;

import de.tgmz.zdev.connection.ZdevConnectable;

/**
 * Creates a temporary osr file.
 */
public class OsrDataSetFactory {
	private static final Logger LOG = LoggerFactory.getLogger(OsrDataSetFactory.class);
	private static final OsrDataSetFactory INSTANCE = new OsrDataSetFactory();
	
	private OsrDataSetFactory() {
	}
	
	public Member create(String name) throws UpdateFailedException {
		Member result;
		DataSet dataSet;
		
		String userid = ZdevConnectable.getConnectable().getConnection().getConfiguration().getUserID();
		
		String xmlInfoDatasetName = userid.toUpperCase(Locale.ROOT) + ".TEMP.XSDOSRG";
		
		try {
			dataSet = ZdevConnectable.getConnectable().getDataSet(xmlInfoDatasetName);
			
			LOG.info("Xsdosrg dataset {} already allocated", dataSet);
		} catch (FileNotFoundException e) {
	    	DataSetBuilder dsb = new DataSetBuilder(xmlInfoDatasetName);
	    	dsb.recordLength = 252;
	    	dsb.datasetType = NewDatasetType.PDSE;
	    	dsb.directoryBlocks = 16;
	    	dsb.format = RecordFormat.FB;
	    	dsb.spaceUnits = SpaceUnits.TRACKS;
	    	dsb.primaryAllocation = 10;
	    	dsb.secondaryAllocation = 10;
	    	dsb.volume = "WRK";
	    	
	    	dataSet = ZdevConnectable.getConnectable().create(dsb);
	    	
			LOG.info("Xsdosrg dataset {} allocated", dataSet);
		}
		
		if (!(dataSet instanceof PartitionedDataSet)) {
			throw new UpdateFailedException("Target dataset not applicable");
		}
		
		try {
			result = ZdevConnectable.getConnectable().getDataSetMember((PartitionedDataSet) dataSet, name);
			
			LOG.info("Xsdosrg dataset member {} already allocated", name);
		} catch (FileNotFoundException e) {
			result = (Member) DataEntry.newFrom(xmlInfoDatasetName + "(" + name + ")", ZdevConnectable.getConnectable());
		} catch (PermissionDeniedException | ConnectionException e) {
			throw new UpdateFailedException("Target dataset not applicable");
		}
		
		return result;
	}
	
	public static OsrDataSetFactory getInstance() {
		return INSTANCE;
	}
}
