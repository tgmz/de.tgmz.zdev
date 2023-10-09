/*********************************************************************
* Copyright (c) 09.10.2023 Thomas Zierer
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package de.tgmz.zdev.outline.test;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import de.tgmz.zdev.outline.IOutlineParser;
import de.tgmz.zdev.outline.JclOutlineParser;
import de.tgmz.zdev.outline.MarkElement;
import de.tgmz.zdev.outline.PliOutlineParser;

@RunWith(value = Parameterized.class)
public class OutlineTest {
	private int expected;
	private Class<IOutlineParser> parserClass;
	private String resource;

	public OutlineTest(Class<IOutlineParser> parserClass, String resource, int expected) {
		super();
		this.parserClass = parserClass;
		this.resource = resource;
		this.expected = expected;
	}

	@Test
	public void testParse() throws IOException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		IOutlineParser parser = parserClass.getDeclaredConstructor(). newInstance();
		
		try (InputStream is = this.getClass().getClassLoader().getResourceAsStream(resource)) {
			String source = IOUtils.toString(is, StandardCharsets.UTF_8.name());
			MarkElement me = new MarkElement(null, null, "", 0, 0);
			
			assertEquals(expected, parser.parse(me, source).length);
		}
	}
	@Parameters(name = "{index}: Parser [{0}]")
	public static Collection<Object[]> data() {
		Object[][] data = new Object[][] {
				{ PliOutlineParser.class, "testresources/HELLOW.pli", 4},
				{ JclOutlineParser.class, "testresources/IEFBR14.jcl", 1},
				};
		return Arrays.asList(data);
	}
}
