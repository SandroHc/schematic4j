package net.sandrohc.schematic4j.schematic.types;

import java.util.Comparator;

import org.checkerframework.checker.nullness.qual.NonNull;

public abstract class SchematicNamed implements Comparable<SchematicNamed> {

	public final @NonNull String name;

	public SchematicNamed(@NonNull String name) {
		this.name = name;
	}

	public String name() {
		return name;
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
		return getClass().getSimpleName() + '[' + name + ']';
	}

	@Override
	public int compareTo(@NonNull SchematicNamed o) {
		return Comparator.nullsLast(Comparator.<SchematicNamed, String>comparing(obj -> obj.name)).compare(this, o);
	}
}
