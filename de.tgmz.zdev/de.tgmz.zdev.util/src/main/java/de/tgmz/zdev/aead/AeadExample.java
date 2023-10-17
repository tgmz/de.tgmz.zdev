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

import static java.nio.charset.StandardCharsets.UTF_8;

import com.google.crypto.tink.Aead;
import com.google.crypto.tink.CleartextKeysetHandle;
import com.google.crypto.tink.JsonKeysetReader;
import com.google.crypto.tink.KeysetHandle;
import com.google.crypto.tink.aead.AeadConfig;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.security.GeneralSecurityException;

public final class AeadExample {
	private static final String MODE_ENCRYPT = "encrypt";
	private static final String MODE_DECRYPT = "decrypt";

	public static void main(String[] args) throws Exception {
		if (args.length != 4 && args.length != 5) {
			System.err.printf("Expected 4 or 5 parameters, got %d\n", args.length);
			System.err.println("Usage: java AeadExample encrypt/decrypt key-file input-file output-file [associated-data]");
			System.exit(1);
		}
		String mode = args[0];
		File keyFile = new File(args[1]);
		File inputFile = new File(args[2]);
		File outputFile = new File(args[3]);
		byte[] associatedData = new byte[0];
		if (args.length == 5) {
			associatedData = args[4].getBytes(UTF_8);
		}
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
		if (MODE_ENCRYPT.equals(mode)) {
			byte[] plaintext = Files.readAllBytes(inputFile.toPath());
			byte[] ciphertext = aead.encrypt(plaintext, associatedData);
			try (FileOutputStream stream = new FileOutputStream(outputFile)) {
				stream.write(ciphertext);
			}
		} else if (MODE_DECRYPT.equals(mode)) {
			byte[] ciphertext = Files.readAllBytes(inputFile.toPath());
			byte[] plaintext = aead.decrypt(ciphertext, associatedData);
			try (FileOutputStream stream = new FileOutputStream(outputFile)) {
				stream.write(plaintext);
			}
		} else {
			System.err.println("The first argument must be either encrypt or decrypt, got: " + mode);
			System.exit(1);
		}

		System.exit(0);
	}

	private AeadExample() {
	}
}