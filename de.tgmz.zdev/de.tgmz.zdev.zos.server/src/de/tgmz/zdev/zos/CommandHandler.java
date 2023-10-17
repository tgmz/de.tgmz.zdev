/*********************************************************************
* Copyright (c) 10.10.2023 Thomas Zierer
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package de.tgmz.zdev.zos;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.jzos.Enqueue;
import com.ibm.jzos.Exec;
import com.ibm.jzos.Messages;
import com.ibm.jzos.RcException;

/**
 * Handles requests to the file lock server.
 * The command string is ENQ|DEQ|STATUS[;<Dataset>[;Member]]
 */
public final class CommandHandler {
	private static final Logger LOG = LoggerFactory.getLogger(CommandHandler.class);
	private Map<String, Enqueue> enqueues = new TreeMap<>();

	public void run(Command cmd, String... parms) throws IOException {
		switch (cmd) {
		case STATUS:
			break;
		case ENQ:	// Enqueue Dataset(member)
			doEnq(parms);
				
			break;
		case DEQ:	// Dequeue Dataset(member)
			deDeq(parms);

			break;
		case EXEC:
			doExec(parms);
            
			break;
		default:
			break;
		}
	}

	private void doExec(String... parms) throws IOException {
		LOG.info("EXEC {}", (Object[]) parms);
		
		Exec exec = new Exec(parms[0]);
		
		exec.run();

		String line;
		
		while ((line = exec.readLine()) != null) {
			LOG.info(line);
		}
		
		LOG.info("{} {}", Messages.getString("Exec.ReturnCode"), exec.getReturnCode());
		
		List<?> errorLines = exec.getErrorLines();
		
		for (int i = 0; i < errorLines.size() && i < Short.MAX_VALUE; ++i) {
			LOG.error("{}", errorLines.get(i));
		}
	}

	private void deDeq(String... parms) {
		LOG.info("Dequeue {}", (Object[]) parms);
		
		String resourceName = getResourceName(parms);
			
		Enqueue oldEnq = enqueues.get(resourceName);
		
		if (oldEnq != null) {
			try {
				oldEnq.release();
			} catch (RcException e) {
				LOG.error("Returncode {}: {}: {}", e.getRc(), getMessage(e.getRc()), e.getMessage(), e);
			}
			
			enqueues.remove(resourceName);
		} else {
			LOG.warn("{} was not enqueued", (Object[]) parms);
		}
	}

	private void doEnq(String... parms) {
		LOG.info("Enqueue {}", (Object[]) parms);
		
		Enqueue newEnq = new Enqueue("SPFEDIT", getResourceName(parms));
		newEnq.setScope(Enqueue.ISGENQ_SCOPE_SYSTEMS);
		newEnq.setControl(Enqueue.ISGENQ_CONTROL_EXCLUSIVE);
		newEnq.setContentionActFail();
			
		try {
			LOG.debug("Waiting ...");
			Thread.sleep(250);
			LOG.debug("Obtaining enqueue");
			
			newEnq.obtain();
			LOG.debug("Enqueue obtained");
		} catch (RcException e) {
			switch (e.getRc()) {
			case Enqueue.ISGENQ_RSN_UNPROTECTEDQNAME:			// 263181
				break;
			case Enqueue.ISGENQ_RSN_NOTIMMEDIATELYAVAILABLE: 	// 263172: Opened in ISPF
			case Enqueue.ISGENQ_RSN_TASKOWNSEXCLUSIVE: 			// 263173: Locked by another z/Dev instance
				LOG.info("Returncode {}: {}", e.getRc(), e.getMessage());
				break;
			default:
				LOG.error("Returncode {}: {}: {}", e.getRc(), getMessage(e.getRc()), e.getMessage(), e);
			}
		} catch (InterruptedException e) {
			LOG.warn("Got interuption while waiting to enqueu", e);
			
			Thread.currentThread().interrupt();
		}
			
		byte[] token = newEnq.getEnqToken();
			
		if (token != null && token.length > 0) {
			enqueues.put(newEnq.getRName(), newEnq);
		}
	}

	/**
	 * Formats the dataset name for ENQ/DEQ.
	 * @param param dataset name and optional member name
	 * @return the formatted dataset name for ENQ/DEQ
	 */
	public static String getResourceName(String... param) {
		StringBuilder rname = new StringBuilder();

		//CHECKSTYLE DISABLE NeedBraces
		if (param.length == 1) {
			rname.append(param[0]);
			while (rname.length() < 44)
				rname.append(' ');
		} else {
			rname.append(param[0]);
			while (rname.length() < 44)
				 rname.append(' ');
			rname.append(param[1]);
			while (rname.length() < 52)
				 rname.append(' ');
		}
		//CHECKSTYLE ENABLE EmptyStatement
		
		return rname.toString();
	}
	
	private String getMessage(int rc) {
		Field[] f = Enqueue.class.getDeclaredFields();
		
		for (Field field : f) {
			if (Modifier.isPublic(field.getModifiers())
					&& Modifier.isFinal(field.getModifiers())
					&& Modifier.isStatic(field.getModifiers())) {
				try {
					if (field.getInt(null) == rc) {
						return field.getName();
					} 
				} catch (IllegalAccessException e) {
					LOG.error("Cannot get name for returncode {}", rc, e);
				}
			}
		}
		
		return null;
	}
}
