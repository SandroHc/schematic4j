/* Vendored version of Quertz NBT 6.1 - https://github.com/Querz/NBT */
package net.sandrohc.schematic4j.nbt.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.stream.Collectors;

import net.sandrohc.schematic4j.nbt.StringDeserializer;
import net.sandrohc.schematic4j.nbt.tag.Tag;

public class SNBTDeserializer implements StringDeserializer<Tag<?>> {

	@Override
	public Tag<?> fromReader(Reader reader) throws IOException {
		return fromReader(reader, Tag.DEFAULT_MAX_DEPTH);
	}

	public Tag<?> fromReader(Reader reader, int maxDepth) throws IOException {
		BufferedReader bufferedReader;
		if (reader instanceof BufferedReader) {
			bufferedReader = (BufferedReader) reader;
		} else {
			bufferedReader = new BufferedReader(reader);
		}
		return SNBTParser.parse(bufferedReader.lines().collect(Collectors.joining()), maxDepth);
	}
}
