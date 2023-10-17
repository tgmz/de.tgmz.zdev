/*********************************************************************
* Copyright (c) 10.10.2023 Thomas Zierer
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package de.tgmz.zdev.filelock;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.cics.zos.model.Member;

import de.tgmz.zdev.zos.Client;
import de.tgmz.zdev.zos.NotConnectedException;

/**
 * Client to lock and unlock dataset members.
 */
public class FileLockClient {
	private static final Logger LOG = LoggerFactory.getLogger(FileLockClient.class);

	private static final FileLockClient INSTANCE = new FileLockClient();

	private boolean running = false;
    
    private List<Member> lockedMembers = new ArrayList<>();
    
	private FileLockClient() {
	}

	public static FileLockClient getInstance() {
		return INSTANCE;
	}

	public boolean isRunning() {
		return running;
	}

	public boolean start() throws NotConnectedException {
		LOG.info("Start filelock client");

		running = Client.available();

		LOG.debug("FileLockClient start was {} successful", (running ? "" : " NOT"));

		if (running) {
			Activator.getDefault().showInfo("FileLockClient started");
		} else {
			Activator.getDefault().showError("Start FileLockClient failed");
		}

		return running;
	}

	public void stop() {
		LOG.info("Stop filelock client");

		releaseAll();
		
		running = false;

		Activator.getDefault().showInfo("FileLockClient stopped");
	}

	public boolean reserve(Member member) throws FileLockException {
		LOG.debug("Reserve {} requested", member.getPath());

		if (!running) {
			return false;
		}

		try {
			Client.getInstance().enqueue(member.getParentPath(), member.getName());
		} catch (IOException | NotConnectedException e) {
			throw new FileLockException(e);
		}

		LOG.debug("Reserve {} succeeded", member.getPath());

		Activator.getDefault().showInfo(member.getPath() + " reserved");
		
		lockedMembers.add(member);
		
		return true;
	}

	public boolean release(Member member) throws FileLockException {
		LOG.debug("Release {} requested", member.getPath());

		try {
			Client.getInstance().dequeue(member.getParentPath(), member.getName());
		} catch (IOException | NotConnectedException e) {
			throw new FileLockException(e);
		}

		LOG.debug("Release {} succeeded", member.getPath());

		Activator.getDefault().showInfo(member.getPath() + " released");

		lockedMembers.remove(member);

		return true;
	}

	private void releaseAll() {
		// release() modifies lockedMembers so we must iterate over a copy to avoid ConcurrentModificationException
		for (Member m : lockedMembers.toArray(new Member[lockedMembers.size()])) {
			try {
				release(m);
			} catch (FileLockException e) {
				LOG.error("Cannot release member {}", m.getName(), e);
			}
		}
	}

}
