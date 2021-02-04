package net.sandrohc.schematic4j.schematic.types;

public abstract class SchematicNamed {

	public final String name;

	public SchematicNamed(String name) {
		if (name == null)
			throw new IllegalArgumentException("name must not be null");

		this.name = name;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		SchematicNamed that = (SchematicNamed) o;

		return name.equals(that.name);
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

	@Override
	public String toString() {
		return "SchematicNamed(" + name + ')';
	}
}
