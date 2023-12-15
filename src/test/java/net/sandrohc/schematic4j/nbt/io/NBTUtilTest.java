package net.sandrohc.schematic4j.nbt.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.junit.jupiter.api.Test;

import net.sandrohc.schematic4j.nbt.tag.StringTag;
import net.sandrohc.schematic4j.nbt.tag.Tag;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

public class NBTUtilTest {

	public static final StringTag TAG = new StringTag("TEST");

	public static final boolean DEFAULT_COMPRESSED = true;
	public static final boolean DEFAULT_LITTLE_ENDIAN = false;

	protected static byte[] serialize(Tag<?> tag, boolean compressed, boolean littleEndian) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try (DataOutputStream dos = new DataOutputStream(baos)) {
			new NBTSerializer(compressed, littleEndian).toStream(new NamedTag(null, tag), dos);
		} catch (IOException ex) {
			ex.printStackTrace();
			fail(ex.getMessage());
		}
		return baos.toByteArray();
	}

	@Test
	public void testWriter_invalidParams() throws IOException {
		try {
			NBTUtil.Writer.write((NamedTag) null).to(new ByteArrayOutputStream());
			fail("did not check null tag");
		} catch (IllegalStateException ignored) {
		}

		try {
			NBTUtil.Writer.write(TAG).to((OutputStream) null);
			fail("did not check null output stream");
		} catch (IllegalStateException ignored) {
		}
	}

	@Test
	public void testWriter_compressionEnabled() throws IOException {
		final boolean compressed = true;
		final ByteArrayOutputStream os = new ByteArrayOutputStream();

		NBTUtil.Writer.write(TAG).compressed(compressed).to(os);

		assertThat(os.toByteArray()).isEqualTo(serialize(TAG, compressed, DEFAULT_LITTLE_ENDIAN));
	}

	@Test
	public void testWriter_compressionDisabled() throws IOException {
		final boolean compressed = false;
		final ByteArrayOutputStream os = new ByteArrayOutputStream();

		NBTUtil.Writer.write(TAG).compressed(compressed).to(os);

		assertThat(os.toByteArray()).isEqualTo(serialize(TAG, compressed, DEFAULT_LITTLE_ENDIAN));
	}

	@Test
	public void testReader_compressionEnabled() throws IOException {
		final boolean compressed = true;
		final InputStream is = new ByteArrayInputStream(serialize(TAG, compressed, DEFAULT_LITTLE_ENDIAN));

		final NamedTag tag = NBTUtil.Reader.read().from(is);

		assertThat(tag.getTag()).isEqualTo(TAG);
	}

	@Test
	public void testReader_compressionDisabled() throws IOException {
		final boolean compressed = false;
		final InputStream is = new ByteArrayInputStream(serialize(TAG, compressed, DEFAULT_LITTLE_ENDIAN));

		final NamedTag tag = NBTUtil.Reader.read().from(is);

		assertThat(tag.getTag()).isEqualTo(TAG);
	}


	@Test
	public void testWriter_littleEndian() throws IOException {
		final boolean littleEndian = true;
		final ByteArrayOutputStream os = new ByteArrayOutputStream();

		NBTUtil.Writer.write(TAG).littleEndian().to(os);

		assertThat(os.toByteArray()).isEqualTo(serialize(TAG, DEFAULT_COMPRESSED, littleEndian));
	}

	@Test
	public void testWriter_bigEndian() throws IOException {
		final boolean littleEndian = false;
		final ByteArrayOutputStream os = new ByteArrayOutputStream();

		NBTUtil.Writer.write(TAG).bigEndian().to(os);

		assertThat(os.toByteArray()).isEqualTo(serialize(TAG, DEFAULT_COMPRESSED, littleEndian));
	}

	@Test
	public void testReader_littleEndian() throws IOException {
		final boolean littleEndian = true;
		final InputStream is = new ByteArrayInputStream(serialize(TAG, DEFAULT_COMPRESSED, littleEndian));

		final NamedTag tag = NBTUtil.Reader.read().littleEndian().from(is);

		assertThat(tag.getTag()).isEqualTo(TAG);
	}

	@Test
	public void testReader_bigEndian() throws IOException {
		final boolean littleEndian = false;
		final InputStream is = new ByteArrayInputStream(serialize(TAG, DEFAULT_COMPRESSED, littleEndian));

		final NamedTag tag = NBTUtil.Reader.read().bigEndian().from(is);

		assertThat(tag.getTag()).isEqualTo(TAG);
	}
}
