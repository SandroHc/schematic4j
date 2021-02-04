package net.sandrohc.schematic4j.parser;

import net.querz.nbt.io.NamedTag;

import net.sandrohc.schematic4j.schematic.Schematic;

public interface Parser {

	Schematic parse(NamedTag root);

}
