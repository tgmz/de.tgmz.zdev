/*********************************************************************
* Copyright (c) 10.10.2023 Thomas Zierer
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package de.tgmz.zdev.branding;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

import org.apache.commons.io.FilenameUtils;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProduct;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.StringConverter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.splash.BasicSplashHandler;
import org.osgi.framework.Constants;
import org.owasp.esapi.codecs.Codec;
import org.owasp.esapi.reference.DefaultEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tgmz.zdev.esapi.ZdevCodec;

/**
 * Handler for displaying the splash screen.
 */

public class CustomSplashHandler extends BasicSplashHandler {
	private static final String INCEPTION = "2015";
	private static final Logger LOG = LoggerFactory.getLogger(CustomSplashHandler.class);
	private static final String FONT_NAME = "Arial";
	private static final String MILESTONE_LOCATION = "milestone";
	private static final Codec<Character> codec = new ZdevCodec();

	@Override
	public void init(Shell splash) {
		super.init(splash);

		setForeground(getRGB("startupForegroundColor"));
		setProgressRect(getRectangle("startupProgressRect"));
		setMessageRect(getRectangle("startupMessageRect"));
		
		putText(getRectangle("startupTitleRect"), new Font(splash.getDisplay(), FONT_NAME, 20, SWT.BOLD), Activator.getString("title"));
		
		Path milestonePicture = getMilestonePicture();

		String versionString = Platform.getBundle(this.getClass().getPackage().getName()).getHeaders().get(Constants.BUNDLE_VERSION);

		if (milestonePicture != null) {
			versionString += " \"" + FilenameUtils.removeExtension(String.valueOf(milestonePicture.getFileName())) + "\"";
		} else {
			LOG.info("No milestone picture found. This is a release!");
		}
		
		LOG.debug("Use version string {}", versionString);

		putText(getRectangle("versionRect")
				, new Font(splash.getDisplay(), FONT_NAME, 10, SWT.BOLD)
				, "Version " + versionString);

		putText(getRectangle("copyrightRect")
				, new Font(splash.getDisplay(), FONT_NAME, 8, SWT.NORMAL)
				, Activator.getString("copyright", INCEPTION + " - " + Calendar.getInstance().get(Calendar.YEAR)));

		putPicture(getRectangle("builtOnRect"), Thread.currentThread().getContextClassLoader().getResource("builtOn.png"));
		
		if (milestonePicture != null) {
			try {
				putPicture(getRectangle("milestoneImageRect"), milestonePicture.toUri().toURL());
			} catch (MalformedURLException e) {
				LOG.warn("Cannot display milestonre picture", e);
			}
		}
	}

	/**
	 * Computes the path to the milestone picture. It comes handy to display a additional picture
	 * into the splash screen to identify a SNAPSHOT version quickly. If no picture is found in "milestone"
	 * folder the product is considered as a release.
	 * @return the path to the milestone picture
	 */
	private static Path getMilestonePicture() {
		// We use result in a stream so it must be, at least effectively, final
		final List<Path> result = new LinkedList<>();
		
		URL milestones = Thread.currentThread().getContextClassLoader().getResource(MILESTONE_LOCATION);

		LOG.debug("Milestone URL {}", milestones);

		try {
			URI milestonesAsFile = FileLocator.toFileURL(milestones).toURI();

			LOG.debug("Milestone URL resolves to {}", milestonesAsFile);
				
			// Ensure stream gets closed on all paths
			try (Stream<Path> filePathStream = Files.walk(Paths.get(milestonesAsFile))) {
			    filePathStream.forEachOrdered(pic -> {
		        	if (result.isEmpty() && isImage(pic)) { // No need to continue once we found a pic but we cannot break forEach()
		        		result.add(pic);
			        }
			    });
			}
		} catch (IOException | URISyntaxException e) {
			LOG.error("Cannot determine milestone, reason:", e);
		}
		
		return result.isEmpty() ? null : result.get(0);
	}

	private static boolean isImage(Path pic) {
        if (!Files.isRegularFile(pic)) {
        	return false;
        }

		try {
			String s = Files.probeContentType(pic);

			LOG.debug("File type of {} is {}", pic, s);

			return (s != null && s.toLowerCase(Locale.ROOT).startsWith("image"));
		} catch (IOException e) {
			LOG.error("Cannot determine type of {}, reason:", pic, e);
			
			return false;
		}
	}
	
	private static Rectangle getRectangle(String id) {
		IProduct product = Platform.getProduct();

		// During Tycho build product is null so we use some nonsense values here.
		String rectangleString = product != null ? product.getProperty(id) : "0,0,0,0"; 
		
		return StringConverter.asRectangle(rectangleString, new Rectangle(0, 0, 0, 0));
	}
	private static RGB getRGB(String id) {
		IProduct product = Platform.getProduct();

		// During Tycho build product is null so we use some nonsense values here.
		String s = product != null ? product.getProperty(id) : "0,0,0"; 
		
		return StringConverter.asRGB(s, new RGB(0, 0, 128));
	}
	private final void putText(Rectangle bounds, Font font, String text) {
		Label title = new Label(getContent(), SWT.LEFT);

		title.setForeground(getForeground());
		title.setBounds(bounds);
		title.setFont(font);
		title.setText(DefaultEncoder.getInstance().encodeForOS(codec, text));
	}
	private final void putPicture(Rectangle bounds, URL pictureUrl) {
		Label label = new Label(getContent(), SWT.LEFT);
		label.setBounds(bounds);
		label.setImage(ImageDescriptor.createFromURL(pictureUrl).createImage());
	}
}
