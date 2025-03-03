/*
 * Copyright 2024, 2025 Cosmetica
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cc.cosmetica.kupe.util;

import cc.cosmetica.kupe.api.maths.Dimensions;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

/**
 * Image utilities for Kupe library implementation.
 */
public class ImageUtilities {
	// This code to read image dimension headers is provided by GPT
	// while some immediately obvious issues have been addressed it may need further debugging

	public static Optional<Dimensions> getImageDimensions(InputStream inputStream) throws IOException {
		try (DataInputStream dataInputStream = new DataInputStream(new BufferedInputStream(inputStream))) {
			// Read the first 8 bytes to identify the image format
			byte[] header = new byte[8];
			dataInputStream.readFully(header);

			// Check if it's a PNG image
			if (isPng(header)) {
				return Optional.of(getPngDimensions(dataInputStream));
			}
			// Check if it's a JPEG image
			else if (isJpeg(header)) {
				return Optional.of(getJpegDimensions(dataInputStream));
			}
			// Unsupported image format
			else {
				return Optional.empty();
			}
		}
	}

	private static boolean isJpeg(byte[] header) {
		return header[0] == (byte) 0xFF && header[1] == (byte) 0xD8;
	}

	private static boolean isPng(byte[] header) {
		return header[0] == (byte) 0x89 && header[1] == (byte) 0x50 &&
				header[2] == (byte) 0x4E && header[3] == (byte) 0x47 &&
				header[4] == (byte) 0x0D && header[5] == (byte) 0x0A &&
				header[6] == (byte) 0x1A && header[7] == (byte) 0x0A;
	}

	private static Dimensions getJpegDimensions(DataInputStream dataInputStream) throws IOException {
		// Skip to the start of the next marker (after the SOI marker)
		while (dataInputStream.readUnsignedShort() != 0xFFD8) {
			; // no-op search
		}

		int marker;

		// Find the SOF (Start Of Frame) marker
		do {
			marker = dataInputStream.readUnsignedShort();
		} while (marker != 0xFFC0 && marker != 0xFFC2);

		// Skip over the marker length
		dataInputStream.skipBytes(2);

		// Read the image dimensions
		int height = dataInputStream.readUnsignedShort();
		int width = dataInputStream.readUnsignedShort();
		return new Dimensions(width, height);
	}

	private static Dimensions getPngDimensions(DataInputStream dataInputStream) throws IOException {
		// Skip over the PNG header and IHDR chunk header
		dataInputStream.skipBytes(8);

		// Read the width and height from the IHDR chunk
		int width = dataInputStream.readInt();
		int height = dataInputStream.readInt();

		return new Dimensions(width, height);
	}
}
