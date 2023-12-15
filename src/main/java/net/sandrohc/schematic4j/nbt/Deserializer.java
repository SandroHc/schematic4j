/* Vendored version of Quertz NBT 6.1 - https://github.com/Querz/NBT */
package net.sandrohc.schematic4j.nbt;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;

/**
 * A generic NBT deserializer.
 *
 * @param <T> Type of the deserialized value
 */
public interface Deserializer<T> {

	/**
	 * Deserialize NBT from an input stream.
	 *
	 * @param stream The input stream containing the NBT data
	 * @return The deserialized NBT
	 * @throws IOException In case there's an error reading from the input stream
	 */
	T fromStream(InputStream stream) throws IOException;

	/**
	 * Deserialize NBT from a file.
	 *
	 * @param file The file containing the NBT data
	 * @return The deserialized NBT
	 * @throws IOException In case there's an error reading from the file
	 */
	default T fromFile(File file) throws IOException {
		try (BufferedInputStream bis = new BufferedInputStream(Files.newInputStream(file.toPath()))) {
			return fromStream(bis);
		}
	}

	/**
	 * Deserialize NBT from a byte array.
	 *
	 * @param data The byte array containing the NBT data
	 * @return The deserialized NBT
	 * @throws IOException In case there's an error reading from the byte array
	 */
	default T fromBytes(byte[] data) throws IOException {
		ByteArrayInputStream stream = new ByteArrayInputStream(data);
		return fromStream(stream);
	}

	/**
	 * Deserialize NBT from a JAR resource.
	 *
	 * @param clazz The class belonging to the JAR that contains the resource
	 * @param path The resource path containing the NBT data
	 * @return The deserialized NBT
	 * @throws IOException In case there's an error reading from the resource
	 */
	default T fromResource(Class<?> clazz, String path) throws IOException {
		try (InputStream stream = clazz.getClassLoader().getResourceAsStream(path)) {
			if (stream == null) {
				throw new IOException("resource \"" + path + "\" not found");
			}
			return fromStream(stream);
		}
	}

	/**
	 * Deserialize NBT from a URL.
	 *
	 * @param url The URL containing the NBT data
	 * @return The deserialized NBT
	 * @throws IOException In case there's an error reading from the URL
	 */
	default T fromURL(URL url) throws IOException {
		try (InputStream stream = url.openStream()) {
			return fromStream(stream);
		}
	}


}
