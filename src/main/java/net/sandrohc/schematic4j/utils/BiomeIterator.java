package net.sandrohc.schematic4j.utils;

import java.util.Iterator;

import net.sandrohc.schematic4j.schematic.types.SchematicBiome;

public interface BiomeIterator extends Iterator<SchematicBiome> {

	int x();
	int z();

	int width();
	int length();

}
