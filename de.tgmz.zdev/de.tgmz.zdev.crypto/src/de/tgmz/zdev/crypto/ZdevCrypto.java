/*********************************************************************
* Copyright (c) 10.10.2023 Thomas Zierer
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package de.tgmz.zdev.crypto;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;

import com.google.crypto.tink.Aead;
import com.google.crypto.tink.CleartextKeysetHandle;
import com.google.crypto.tink.JsonKeysetReader;
import com.google.crypto.tink.KeysetHandle;
import com.google.crypto.tink.aead.AeadConfig;
import com.google.crypto.tink.subtle.Base64;

/**
 * Utility for de- and encrypting strings.
 */
public class ZdevCrypto {
	private static final ZdevCrypto INSTANCE = new ZdevCrypto();
	private static byte[] associatedData = new byte[0];
	private Aead aead = null;

	private ZdevCrypto() {
		try (InputStream is = this.getClass().getClassLoader().getResourceAsStream("keyfile")) {
			AeadConfig.register();
			
			// Read the keyset into a KeysetHandle.
			KeysetHandle handle = CleartextKeysetHandle.read(JsonKeysetReader.withInputStream(is));

			// Get the primitive.
			aead = handle.getPrimitive(Aead.class);
		} catch (IOException | GeneralSecurityException e) {
			throw new ZdevCryptoException("Error reading keyfile", e);
		}
	}
	
	public static synchronized ZdevCrypto getInstance() {
		return INSTANCE;
	}
	
	public String decrypt(String s) throws GeneralSecurityException {
		//CHECKSTYLE DISABLE IllegalInstantiation FOR 1 LINES
		return new String(aead.decrypt(Base64.decode(s), associatedData), StandardCharsets.UTF_8);		
	}
	
	public String encrypt(String s) throws GeneralSecurityException {
		return Base64.encode(aead.encrypt(s.getBytes(StandardCharsets.UTF_8), associatedData));
	}
}
