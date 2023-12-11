/* Vendored version of Quertz NBT 6.1 - https://github.com/Querz/NBT */
package net.sandrohc.schematic4j.nbt.io;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

import net.sandrohc.schematic4j.nbt.Deserializer;
import net.sandrohc.schematic4j.nbt.tag.Tag;

public class NBTDeserializer implements Deserializer<NamedTag> {

	private boolean compressed, littleEndian;

	public NBTDeserializer() {
		this(true);
	}

	public NBTDeserializer(boolean compressed) {
		this.compressed = compressed;
	}

	public NBTDeserializer(boolean compressed, boolean littleEndian) {
		this.compressed = compressed;
		this.littleEndian = littleEndian;
	}

	@Override
	public NamedTag fromStream(InputStream stream) throws IOException {
		NBTInput nbtIn;
		InputStream input;
		if (compressed) {
			input = new GZIPInputStream(stream);
		} else {
			input = stream;
		}

		if (littleEndian) {
			nbtIn = new LittleEndianNBTInputStream(input);
		} else {
			nbtIn = new NBTInputStream(input);
		}
		return nbtIn.readTag(Tag.DEFAULT_MAX_DEPTH);
	}
}
