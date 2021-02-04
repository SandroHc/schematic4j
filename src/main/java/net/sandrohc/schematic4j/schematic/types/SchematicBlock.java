package net.sandrohc.schematic4j.schematic.types;

public class SchematicBlock extends SchematicNamed {

	public SchematicBlock(String name) {
		super(name);
	}

	@Override
	public String toString() {
		return "SchematicBlock(" + name + ')';
	}

}
