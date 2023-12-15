/* Vendored version of Quertz NBT 6.1 - https://github.com/Querz/NBT */
package net.sandrohc.schematic4j.nbt.io;

import java.io.IOException;

import net.sandrohc.schematic4j.nbt.tag.Tag;

/**
 * A generic NBT input
 */
public interface NBTInput {

	/**
	 * Read a named tag from the input.
	 *
	 * @param maxDepth Maximum depth before failing deserialization
	 * @return The named tag read
	 * @throws IOException In case of error reading from the input
	 */
	NamedTag readTag(int maxDepth) throws IOException;

	/**
	 * Read a tag from the input.
	 *
	 * @param maxDepth Maximum depth before failing deserialization
	 * @return The tag read
	 * @throws IOException In case of error reading from the input
	 */
	Tag<?> readRawTag(int maxDepth) throws IOException;
}
