package net.sandrohc.schematic4j.schematic.types;

public class SchematicPos {

	public int x;
	public int y;
	public int z;

	public SchematicPos(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public static SchematicPos from(int x, int y, int z) {
		return new SchematicPos(x, y, z);
	}

	public static SchematicPos from(int[] pos) {
		return from(pos[0], pos[1], pos[2]);
	}

	public static SchematicPos from(SchematicPos other) {
		return new SchematicPos(other.x, other.y, other.z);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		SchematicPos that = (SchematicPos) o;

		if (x != that.x) return false;
		if (y != that.y) return false;
		return z == that.z;
	}

	@Override
	public int hashCode() {
		int result = x;
		result = 31 * result + y;
		result = 31 * result + z;
		return result;
	}

	@Override
	public String toString() {
		return "SchematicPos(x=" + x + ", y=" + y + ", z=" + z + ')';
	}

}
