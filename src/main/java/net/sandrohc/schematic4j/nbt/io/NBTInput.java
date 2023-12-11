/* Vendored version of Quertz NBT 6.1 - https://github.com/Querz/NBT */
package net.sandrohc.schematic4j.nbt.io;

import java.io.IOException;

import net.sandrohc.schematic4j.nbt.tag.Tag;

public interface NBTInput {

	NamedTag readTag(int maxDepth) throws IOException;

	Tag<?> readRawTag(int maxDepth) throws IOException;
}
