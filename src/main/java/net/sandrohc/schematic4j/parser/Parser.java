package net.sandrohc.schematic4j.parser;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sandrohc.schematic4j.exception.ParsingException;
import net.sandrohc.schematic4j.nbt.io.NamedTag;
import net.sandrohc.schematic4j.schematic.Schematic;

/**
 * A schematic parser.
 */
public interface Parser {

	/**
	 * Parses the input NBT into a schematic.
	 *
	 * @param root The input NBT.
	 * @return The parsed schematic.
	 * @throws ParsingException In case there is a parsing error
	 */
	@NonNull
	Schematic parse(@Nullable NamedTag root) throws ParsingException;

}
