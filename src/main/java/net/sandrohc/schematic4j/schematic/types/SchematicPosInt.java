package net.sandrohc.schematic4j.schematic.types;

import java.util.Comparator;

public class SchematicPosInt implements Comparable<SchematicPosInt> {
	public final int x;
	public final int y;
	public final int z;

	public SchematicPosInt(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public SchematicPosInt(int[] pos) {
		this(pos[0], pos[1], pos[2]);
	}

	public SchematicPosInt(SchematicPosInt other) {
		this(other.x, other.y, other.z);
	}

	public static SchematicPosInt from(int x, int y, int z) {
		return new SchematicPosInt(x, y, z);
	}

	public static SchematicPosInt from(int[] pos) {
		return from(pos[0], pos[1], pos[2]);
	}

	public static SchematicPosInt from(SchematicPosInt other) {
		return new SchematicPosInt(other);
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

		SchematicPosInt that = (SchematicPosInt) o;

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
	public int compareTo(SchematicPosInt o) {
		return Comparator.nullsLast(
				Comparator.<SchematicPosInt>comparingInt(obj -> obj.x)
						.thenComparingInt(obj -> obj.y)
						.thenComparingInt(obj -> obj.z)
		).compare(this, o);
	}
}
