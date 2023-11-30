/*********************************************************************
* Copyright (c) 10.10.2023 Thomas Zierer
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/

package de.tgmz.zdev.compile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;

import com.ibm.cics.common.util.IOUtils;
import com.ibm.cics.common.util.StringUtil;

import de.tgmz.zdev.domain.Item;
import de.tgmz.zdev.preferences.Language;
import de.tgmz.zdev.preferences.ZdevPreferenceConstants;

public class JclFactory {
	private static final JclFactory INSTANCE = new JclFactory();

	private JclFactory() {
	}
	
	public static JclFactory getInstance() {
		return INSTANCE;
	}
	
	public String createCompileStep(Item item, String... additional) throws IOException {
		String res;
		String syslib;
		String comp;
		
		switch (Language.fromDatasetName(item.getDsn())) {
		case COBOL:
			res = "compile/templates/cobol.txt";
			syslib = de.tgmz.zdev.preferences.Activator.getDefault().getPreferenceStore().getString(ZdevPreferenceConstants.SYSLIB_COB);
			comp = de.tgmz.zdev.preferences.Activator.getDefault().getPreferenceStore().getString(ZdevPreferenceConstants.COMP_COB);
			break;
		case ASSEMBLER:
			res = "compile/templates/assembler.txt";
			syslib = de.tgmz.zdev.preferences.Activator.getDefault().getPreferenceStore().getString(ZdevPreferenceConstants.SYSLIB_ASM);
			comp = de.tgmz.zdev.preferences.Activator.getDefault().getPreferenceStore().getString(ZdevPreferenceConstants.COMP_ASM);
			break;
		case C:
			res = "compile/templates/c.txt";
			syslib = de.tgmz.zdev.preferences.Activator.getDefault().getPreferenceStore().getString(ZdevPreferenceConstants.SYSLIB_C);
			comp = de.tgmz.zdev.preferences.Activator.getDefault().getPreferenceStore().getString(ZdevPreferenceConstants.COMP_C);
			break;
		case CPP:
			res = "compile/templates/cpp.txt";
			syslib = de.tgmz.zdev.preferences.Activator.getDefault().getPreferenceStore().getString(ZdevPreferenceConstants.SYSLIB_CPP);
			comp = de.tgmz.zdev.preferences.Activator.getDefault().getPreferenceStore().getString(ZdevPreferenceConstants.COMP_CPP);
			break;
		case PLI:
		default:	
			res = "compile/templates/pli.txt";
			syslib = de.tgmz.zdev.preferences.Activator.getDefault().getPreferenceStore().getString(ZdevPreferenceConstants.SYSLIB_PLI);
			comp = de.tgmz.zdev.preferences.Activator.getDefault().getPreferenceStore().getString(ZdevPreferenceConstants.COMP_PLI);
			break;
		}
		
		StringBuilder result;
		
		try(InputStream is = this.getClass().getClassLoader().getResourceAsStream(res)) {
			syslib = SyslibFactory.getInstance().createSyslib(syslib);
			
			String template = IOUtils.readInputStreamAsString(is, true, StandardCharsets.UTF_8.name());
		
			MessageFormat mf = new MessageFormat(template);
			
			Object[] o = new Object[] {
						item.getMember()
					  , item.getOption().isComp() ? "'," + item.getOption().getCompOption() + "'" : comp
					  , item.getOption().isDb2() ? "'," + item.getOption().getDb2Option() + "'" : ""
					  , item.getOption().isCics() ? "'," + item.getOption().getCicsOption() + "'" : ""
					  , item.getFullName()
					  , syslib
					  , de.tgmz.zdev.preferences.Activator.getDefault().getPreferenceStore().getString(ZdevPreferenceConstants.OBJLIB)
					};
			
			result = new StringBuilder(mf.format(o));
		}
		
		for (String s : additional) {
			result.append(System.lineSeparator());
			result.append(s);
		}
		
		return result.toString();
	}
	public String createBindStep(Item item, String... additional) throws IOException {
		StringBuilder result;
		
		try(InputStream is = this.getClass().getClassLoader().getResourceAsStream("compile/templates/link.txt")) {
			String template = IOUtils.readInputStreamAsString(is, true, StandardCharsets.UTF_8.name());
		
			MessageFormat mf = new MessageFormat(template);
			
			String bind = item.getOption().getBindOption();
			
			Object[] o = new Object[] {item.getMember()
					, de.tgmz.zdev.preferences.Activator.getDefault().getPreferenceStore().getString(ZdevPreferenceConstants.OBJLIB)
					, de.tgmz.zdev.preferences.Activator.getDefault().getPreferenceStore().getString(ZdevPreferenceConstants.LOADLIB)
					, StringUtil.hasContent(bind) ? "'," + bind + "'" : de.tgmz.zdev.preferences.Activator.getDefault().getPreferenceStore().getString(ZdevPreferenceConstants.LINK_OPTIONS)
					};
			
			result = new StringBuilder(mf.format(o));
		}
		for (String s : additional) {
			result.append(s);
		}

		return result.toString();
	}
}
