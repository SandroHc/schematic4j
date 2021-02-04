package net.sandrohc.schematic4j.schematic.types;

public abstract class SchematicPos<T> {

	public final T x;
	public final T y;
	public final T z;

	public SchematicPos(T x, T y, T z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public SchematicPos(T[] pos) {
		this(pos[0], pos[1], pos[2]);
	}

	public SchematicPos(SchematicPos<T> other) {
		this(other.x, other.y, other.z);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		SchematicPos<?> that = (SchematicPos<?>) o;

		if (!x.equals(that.x)) return false;
		if (!y.equals(that.y)) return false;
		return z.equals(that.z);
	}

	@Override
	public int hashCode() {
		int result = x.hashCode();
		result = 31 * result + y.hashCode();
		result = 31 * result + z.hashCode();
		return result;
	}

	@Override
	public String toString() {
		return "Pos(" + x + ", " + y + ", " + z + ')';
	}

}
