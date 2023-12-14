package net.sandrohc.schematic4j.schematic.types;

import java.util.Comparator;

public class SchematicBlockPos implements Comparable<SchematicBlockPos> {
	public final int x;
	public final int y;
	public final int z;

	public SchematicBlockPos(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public SchematicBlockPos(int[] pos) {
		this(pos[0], pos[1], pos[2]);
	}

	public SchematicBlockPos(SchematicBlockPos other) {
		this(other.x, other.y, other.z);
	}

	public static SchematicBlockPos from(int x, int y, int z) {
		return new SchematicBlockPos(x, y, z);
	}

	public static SchematicBlockPos from(int[] pos) {
		return from(pos[0], pos[1], pos[2]);
	}

	public static SchematicBlockPos from(SchematicBlockPos other) {
		return new SchematicBlockPos(other);
	}

	public int x() {
		return x;
	}

	public int y() {
		return y;
	}

	public int z() {
		return z;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		SchematicBlockPos that = (SchematicBlockPos) o;

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
		return "(" + x + ", " + y + ", " + z + ')';
	}

	@Override
	public int compareTo(SchematicBlockPos o) {
		return Comparator.nullsLast(
				Comparator.<SchematicBlockPos>comparingInt(obj -> obj.x)
						.thenComparingInt(obj -> obj.y)
						.thenComparingInt(obj -> obj.z)
		).compare(this, o);
	}
}
