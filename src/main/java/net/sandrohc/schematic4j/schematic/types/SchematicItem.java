package net.sandrohc.schematic4j.schematic.types;

public class SchematicItem extends SchematicNamed {

	public final int count;

	public SchematicItem(String name, int count) {
		super(name);
		this.count = count;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;

		SchematicItem that = (SchematicItem) o;

		return count == that.count;
	}

	@Override
	public int hashCode() {
		int result = super.hashCode();
		result = 31 * result + count;
		return result;
	}

	@Override
	public String toString() {
		return "SchematicItem(name=" + name + ", count=" + count + ")";
	}

}
