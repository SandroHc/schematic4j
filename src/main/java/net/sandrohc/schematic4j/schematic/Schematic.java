package net.sandrohc.schematic4j.schematic;

import net.sandrohc.schematic4j.schematic.types.SchematicBlock;
import net.sandrohc.schematic4j.schematic.types.SchematicBlockEntity;
import net.sandrohc.schematic4j.schematic.types.SchematicEntity;
import net.sandrohc.schematic4j.schematic.types.SchematicPos;

public interface Schematic {

	int getWidth();

	int getHeight();

	int getLength();

	SchematicBlock getBlock(SchematicPos pos);

	SchematicBlockEntity getBlockEntity(SchematicPos pos);

	SchematicEntity getEntity(SchematicPos pos);

}
