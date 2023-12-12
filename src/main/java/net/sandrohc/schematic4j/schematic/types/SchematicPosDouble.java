package net.sandrohc.schematic4j.schematic.types;

public class SchematicPosDouble {
	public final double x;
	public final double y;
	public final double z;

	public SchematicPosDouble(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public SchematicPosDouble(double[] pos) {
		this(pos[0], pos[1], pos[2]);
	}

	public SchematicPosDouble(SchematicPosDouble other) {
		this(other.x, other.y, other.z);
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

	public double x() {
		return x;
	}

	public double y() {
		return y;
	}

	public double z() {
		return z;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		SchematicPosDouble that = (SchematicPosDouble) o;

		if (Double.compare(x, that.x) != 0) return false;
		if (Double.compare(y, that.y) != 0) return false;
		return Double.compare(z, that.z) == 0;
	}

	@Override
	public int hashCode() {
		int result;
		long temp;
		temp = Double.doubleToLongBits(x);
		result = (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(y);
		result = 31 * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(z);
		result = 31 * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	@Override
	public String toString() {
		return "(" + x + ", " + y + ", " + z + ')';
	}
}
