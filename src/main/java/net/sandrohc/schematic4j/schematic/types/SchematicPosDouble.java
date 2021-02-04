package net.sandrohc.schematic4j.schematic.types;

public class SchematicPosDouble extends SchematicPos<Double> {

	public SchematicPosDouble(double x, double y, double z) {
		super(x, y, z);
	}

	public SchematicPosDouble(Double[] pos) {
		super(pos);
	}

	public SchematicPosDouble(SchematicPos<Double> other) {
		super(other);
	}


	public static SchematicPosDouble from(double x, double y, double z) {
		return new SchematicPosDouble(x, y, z);
	}

	public static SchematicPosDouble from(double[] pos) {
		return from(pos[0], pos[1], pos[2]);
	}

	public static SchematicPosDouble from(SchematicPosDouble other) {
		return new SchematicPosDouble(other);
	}

}
