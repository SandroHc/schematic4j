/* Vendored version of Quertz NBT 6.1 - https://github.com/Querz/NBT */
package net.sandrohc.schematic4j.nbt.io;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PushbackInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.GZIPInputStream;

import net.sandrohc.schematic4j.nbt.tag.Tag;

public final class NBTUtil {

	private NBTUtil() {
	}

	/**
	 * Writer helper that follows the builder pattern.
	 * <p>
	 * Usage example:
	 * <pre>{@code Writer.write(nbtTag)
	 *     .littleEndian()
	 *     .compressed(false)
	 *     .to("file.schematic")}</pre>
	 */
	public static class Writer {

		public final NamedTag tag;
		private boolean compressed = true;
		private boolean littleEndian = false;

		private Writer(NamedTag tag) {
			this.tag = tag;
		}

		public static Writer write(NamedTag tag) {
			return new Writer(tag);
		}

		public static Writer write(Tag<?> tag) {
			return new Writer(new NamedTag(null, tag));
		}

		/**
		 * Toggle compression for the output. GZIP compression is used.
		 *
		 * @param compressed Whether the output should be compressed or not
		 * @return The writer builder
		 */
		public Writer compressed(boolean compressed) {
			this.compressed = compressed;
			return this;
		}

		/**
		 * Write to the output as Little Endian. Usually reserved for network packets or some systems architectures.
		 *
		 * @return the writer builder
		 */
		public Writer littleEndian() {
			this.littleEndian = true;
			return this;
		}

		/**
		 * Write to the output as Big Endian. This is the default.
		 *
		 * @return the writer builder
		 */
		public Writer bigEndian() {
			this.littleEndian = false;
			return this;
		}

		/**
		 * Writes the NBT tag to an output stream. Terminal operator.
		 *
		 * @param os The output stream to write to
		 * @throws IOException In case of error writing to the output stream
		 */
		public void to(OutputStream os) throws IOException {
			if (tag == null)
				throw new IllegalStateException("tag must be set");
			if (os == null)
				throw new IllegalStateException("output must be set");

			new NBTSerializer(compressed, littleEndian).toStream(tag, os);
		}

		/**
		 * Writes the NBT tag to a file. Terminal operator.
		 *
		 * @param path The file path fo write to
		 * @throws IOException In case of error writing to the file
		 */
		public void to(Path path) throws IOException {
			try (OutputStream os = new BufferedOutputStream(Files.newOutputStream(path))) {
				to(os);
			}
		}

		/**
		 * Writes the NBT tag to a file. Terminal operator.
		 *
		 * @param file The file path fo write to
		 * @throws IOException In case of error writing to the file
		 */
		public void to(File file) throws IOException {
			to(file.toPath());
		}

		/**
		 * Writes the NBT tag to a file. Terminal operator.
		 *
		 * @param file The file path fo write to
		 * @throws IOException In case of error writing to the file
		 */
		public void to(String file) throws IOException {
			to(Paths.get(file));
		}
	}

	/**
	 * Reader helper that follows the builder pattern.
	 * <p>
	 * Usage example:
	 * <pre>{@code Reader.read()
	 *     .littleEndian()
	 *     .from("file.schematic")}</pre>
	 */
	public static class Reader {

		private boolean littleEndian = false;

		public Reader() {
		}

		public static Reader read() {
			return new Reader();
		}

		/**
		 * Read from the source as Little Endian. Usually reserved for network packets or some systems architectures.
		 *
		 * @return the reader builder
		 */
		public Reader littleEndian() {
			this.littleEndian = true;
			return this;
		}

		/**
		 * Read from the source as Big Endian. This is the default.
		 *
		 * @return the reader builder
		 */
		public Reader bigEndian() {
			this.littleEndian = false;
			return this;
		}

		/**
		 * Reads the NBT tag from an input stream. Terminal operator.
		 *
		 * @param is The input stream to read from
		 * @return The parsed NBT tag
		 * @throws IOException In case of error reading from the input stream
		 */
		public NamedTag from(InputStream is) throws IOException {
			return new NBTDeserializer(false/* ignored, will autodetect compression */, littleEndian)
					.fromStream(detectDecompression(is));
		}

		/**
		 * Reads the NBT tag from a byte array. Terminal operator.
		 *
		 * @param bytes The byte array
		 * @return The parsed NBT tag
		 * @throws IOException In case of error reading from the input stream
		 */
		public NamedTag from(byte[] bytes) throws IOException {
			return from(new ByteArrayInputStream(bytes));
		}

		/**
		 * Reads the NBT tag from a file. Terminal operator.
		 *
		 * @param path The file path to read from
		 * @return The parsed NBT tag
		 * @throws IOException In case of error reading from the file
		 */
		public NamedTag from(Path path) throws IOException {
			try (InputStream is = new BufferedInputStream(Files.newInputStream(path))) {
				return from(is);
			}
		}

		/**
		 * Reads the NBT tag from a file. Terminal operator.
		 *
		 * @param file The file path to read from
		 * @return The parsed NBT tag
		 * @throws IOException In case of error reading from the file
		 */
		public NamedTag from(File file) throws IOException {
			return from(file.toPath());
		}

		/**
		 * Reads the NBT tag from a file. Terminal operator.
		 *
		 * @param file The file path to read from
		 * @return The parsed NBT tag
		 * @throws IOException In case of error reading from the file
		 */
		public NamedTag from(String file) throws IOException {
			return from(Paths.get(file));
		}

		private static InputStream detectDecompression(InputStream is) throws IOException {
			PushbackInputStream pbis = new PushbackInputStream(is, 2);
			int signature = (pbis.read() & 0xFF) + (pbis.read() << 8);
			pbis.unread(signature >> 8);
			pbis.unread(signature & 0xFF);
			return signature == GZIPInputStream.GZIP_MAGIC ? new GZIPInputStream(pbis) : pbis;
		}
	}
}
