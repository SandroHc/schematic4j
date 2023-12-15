package net.sandrohc.schematic4j.schematic.types;

import java.util.Comparator;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sandrohc.schematic4j.nbt.tag.CompoundTag;
import net.sandrohc.schematic4j.nbt.tag.Tag;

/**
 * Represents a block position.
 */
public class SchematicBlockPos implements Comparable<SchematicBlockPos> {

	public static final SchematicBlockPos ZERO = new SchematicBlockPos(0, 0, 0);

	/**
	 * The X coordinate.
	 */
	public final int x;

	/**
	 * The Y coordinate.
	 */
	public final int y;

	/**
	 * The Z coordinate.
	 */
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

	public static @Nullable SchematicBlockPos from(Tag<?> nbtTag) {
		if (!(nbtTag instanceof CompoundTag)) {
			return null;
		}
		final CompoundTag nbt = ((CompoundTag) nbtTag);
		final int x = nbt.getInt("x");
		final int y = nbt.getInt("y");
		final int z = nbt.getInt("z");
		return new SchematicBlockPos(x, y, z);
	}

	public static SchematicBlockPos from(SchematicBlockPos other) {
		return new SchematicBlockPos(other);
	}

	/**
	 * The X coordinate.
	 *
	 * @return The X coordinate
	 */
	public int x() {
		return x;
	}

	/**
	 * The Y coordinate.
	 *
	 * @return The Y coordinate
	 */
	public int y() {
		return y;
	}

	/**
	 * The Z coordinate.
	 *
	 * @return The Z coordinate
	 */
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
	public int compareTo(@NonNull SchematicBlockPos o) {
		return Comparator.nullsLast(
				Comparator.<SchematicBlockPos>comparingInt(obj -> obj.x)
						.thenComparingInt(obj -> obj.y)
						.thenComparingInt(obj -> obj.z)
		).compare(this, o);
	}
}
