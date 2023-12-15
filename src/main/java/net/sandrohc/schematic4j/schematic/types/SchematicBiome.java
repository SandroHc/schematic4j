package net.sandrohc.schematic4j.schematic.types;

public class SchematicBiome extends SchematicBlock {

	public static final SchematicBiome AIR = new SchematicBiome("minecraft:air");

	public SchematicBiome(String blockstate) {
		super(blockstate);
	}
}
