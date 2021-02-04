package net.sandrohc.schematic4j.schematic.types;

public class SchematicBiome extends SchematicWithBlockState {

	public SchematicBiome(String blockstate) {
		super(blockstate);
	}

	@Override
	public String toString() {
		return "SchematicBlock(" + name + ", states=" + states + ')';
	}

}
