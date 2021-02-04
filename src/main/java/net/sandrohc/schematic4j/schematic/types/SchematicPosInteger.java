package net.sandrohc.schematic4j.schematic.types;

public class SchematicPosInteger extends SchematicPos<Integer> {

	public SchematicPosInteger(int x, int y, int z) {
		super(x, y, z);
	}

	public SchematicPosInteger(Integer[] pos) {
		super(pos);
	}

	public SchematicPosInteger(SchematicPos<Integer> other) {
		super(other);
	}


	public static SchematicPosInteger from(int x, int y, int z) {
		return new SchematicPosInteger(x, y, z);
	}

	public static SchematicPosInteger from(int[] pos) {
		return from(pos[0], pos[1], pos[2]);
	}

	public static SchematicPosInteger from(SchematicPosInteger other) {
		return new SchematicPosInteger(other);
	}

}
