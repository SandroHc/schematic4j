package net.sandrohc.schematic4j.schematic.types;

public class SchematicBlock extends SchematicWithBlockState {

	public SchematicBlock(String blockstate) {
		super(blockstate);
	}

	@Override
	public String toString() {
		return "SchematicBlock(" + block + ", states=" + states + ')';
	}

}
