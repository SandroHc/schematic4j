package net.sandrohc.schematic4j.schematic.types;

import java.util.Objects;

public class SchematicBlockEntity extends SchematicNamed {

	public final SchematicPos pos;

	public SchematicBlockEntity(String name, SchematicPos pos) {
		super(name);
		this.pos = pos;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;

		SchematicBlockEntity that = (SchematicBlockEntity) o;

		return Objects.equals(pos, that.pos);
	}

	@Override
	public int hashCode() {
		int result = super.hashCode();
		result = 31 * result + (pos != null ? pos.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "SchematicBlockEntity(name=" + name + ", pos=" + pos + ")";
	}

}
