package net.sandrohc.schematic4j.utils;

import java.util.Iterator;

import net.sandrohc.schematic4j.schematic.types.SchematicBlock;

public interface BlockIterator extends Iterator<SchematicBlock> {

	int x();
	int y();
	int z();

	int width();
	int height();
	int length();

}
