/*********************************************************************
* Copyright (c) 10.10.2023 Thomas Zierer
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package de.tgmz.zdev.zos.test;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

import de.tgmz.zdev.zos.Command;
import de.tgmz.zdev.zos.CommandHandler;

/**
 * Test case for {@link CommandHandler}
 */
public class CommandHandlerTest {
	private static final String DATASET = "HLQ.ZDEV.PLI";
	private static final String MEMBER = "MI013";
	
	private static final CommandHandler handler = new CommandHandler();

	@Test(expected = UnsatisfiedLinkError.class)
	public void testEnq() throws IOException {
		handler.run(Command.ENQ, DATASET, MEMBER);
	}

	@Test
	public void testDeq() throws IOException {
		handler.run(Command.DEQ, DATASET, MEMBER);
	}

	@Test(expected = UnsatisfiedLinkError.class)
	public void testExec() throws IOException {
		handler.run(Command.EXEC, "cmd");
	}
	@Test
	public void testGetResourceName() {
		assertEquals(DATASET + "                                " + MEMBER + "   ", CommandHandler.getResourceName(DATASET, MEMBER));
		assertEquals(DATASET + "                                ", CommandHandler.getResourceName(DATASET));
	}
}
