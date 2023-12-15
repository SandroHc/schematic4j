package net.sandrohc.schematic4j.schematic.types;

import java.util.Comparator;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sandrohc.schematic4j.nbt.tag.DoubleTag;
import net.sandrohc.schematic4j.nbt.tag.ListTag;
import net.sandrohc.schematic4j.nbt.tag.Tag;

public class SchematicEntityPos implements Comparable<SchematicEntityPos> {
	public final double x;
	public final double y;
	public final double z;

	public SchematicEntityPos(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public SchematicEntityPos(double[] pos) {
		this(pos[0], pos[1], pos[2]);
	}

	public SchematicEntityPos(SchematicEntityPos other) {
		this(other.x, other.y, other.z);
	}

	public static SchematicEntityPos from(double x, double y, double z) {
		return new SchematicEntityPos(x, y, z);
	}

	public static SchematicEntityPos from(double[] pos) {
		return from(pos[0], pos[1], pos[2]);
	}

	@SuppressWarnings("unchecked")
	public static @Nullable SchematicEntityPos from(Tag<?> nbtTag) {
		if (!(nbtTag instanceof ListTag<?>)) {
			return null;
		}
		final ListTag<DoubleTag> nbt = ((ListTag<DoubleTag>) nbtTag);
		final double x = nbt.get(0).asDouble();
		final double y = nbt.get(1).asDouble();
		final double z = nbt.get(2).asDouble();
		return new SchematicEntityPos(x, y, z);
	}

	public static SchematicEntityPos from(SchematicEntityPos other) {
		return new SchematicEntityPos(other);
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

		SchematicEntityPos that = (SchematicEntityPos) o;

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

	@Override
	public int compareTo(@NonNull SchematicEntityPos o) {
		return Comparator.nullsLast(
				Comparator.<SchematicEntityPos>comparingDouble(obj -> obj.x)
						.thenComparingDouble(obj -> obj.y)
						.thenComparingDouble(obj -> obj.z)
		).compare(this, o);
	}
}
