package net.sandrohc.schematic4j.parser;

import net.sandrohc.schematic4j.exception.ParsingException;
import net.sandrohc.schematic4j.nbt.io.NamedTag;
import net.sandrohc.schematic4j.schematic.Schematic;

public interface Parser {

	Schematic parse(NamedTag root) throws ParsingException;

}
