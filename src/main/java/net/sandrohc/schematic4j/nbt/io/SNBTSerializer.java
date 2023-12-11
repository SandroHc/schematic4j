/* Vendored version of Quertz NBT 6.1 - https://github.com/Querz/NBT */
package net.sandrohc.schematic4j.nbt.io;

import java.io.IOException;
import java.io.Writer;

import net.sandrohc.schematic4j.nbt.StringSerializer;
import net.sandrohc.schematic4j.nbt.tag.Tag;

public class SNBTSerializer implements StringSerializer<Tag<?>> {

	@Override
	public void toWriter(Tag<?> tag, Writer writer) throws IOException {
		SNBTWriter.write(tag, writer);
	}

	public void toWriter(Tag<?> tag, Writer writer, int maxDepth) throws IOException {
		SNBTWriter.write(tag, writer, maxDepth);
	}
}
