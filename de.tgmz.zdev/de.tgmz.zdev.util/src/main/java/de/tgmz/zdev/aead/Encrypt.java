/*********************************************************************
* Copyright (c) 10.10.2023 Thomas Zierer
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package de.tgmz.zdev.aead;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;

import com.google.crypto.tink.Aead;
import com.google.crypto.tink.CleartextKeysetHandle;
import com.google.crypto.tink.JsonKeysetReader;
import com.google.crypto.tink.KeysetHandle;
import com.google.crypto.tink.aead.AeadConfig;
import com.google.crypto.tink.subtle.Base64;

public final class Encrypt {
	public static void main(String[] args) throws Exception {
		File keyFile = new File("keyfile");
		byte[] associatedData = new byte[0];
		// Register all AEAD key types with the Tink runtime.
		AeadConfig.register();

		// Read the keyset into a KeysetHandle.
		KeysetHandle handle = null;
		try {
			handle = CleartextKeysetHandle.read(JsonKeysetReader.withFile(keyFile));
		} catch (GeneralSecurityException | IOException ex) {
			System.err.println("Cannot read keyset, got error: " + ex);
			System.exit(1);
		}

		// Get the primitive.
		Aead aead = null;
		try {
			aead = handle.getPrimitive(Aead.class);
		} catch (GeneralSecurityException ex) {
			System.err.println("Cannot create primitive, got error: " + ex);
			System.exit(1);
		}

		// Use the primitive to encrypt/decrypt files.
		byte[] plaintext = "hxskby".getBytes();
		byte[] ciphertext = aead.encrypt(plaintext, associatedData);
		
		System.out.println(Base64.encode(ciphertext));
	}

	private Encrypt() {
	}
}