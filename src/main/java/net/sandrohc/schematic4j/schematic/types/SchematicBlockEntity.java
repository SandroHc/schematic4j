package net.sandrohc.schematic4j.schematic.types;

import java.util.Map;

public class SchematicBlockEntity extends SchematicNamed {

	public final SchematicBlockPos pos;
	public final Map<String, Object> extra;

	public SchematicBlockEntity(String name, SchematicBlockPos pos, Map<String, Object> extra) {
		super(name);
		this.pos = pos;
		this.extra = extra;
	}

	public SchematicBlockPos pos() {
		return pos;
	}

	public Map<String, Object> extra() {
		return extra;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;

		SchematicBlockEntity that = (SchematicBlockEntity) o;

		if (!pos.equals(that.pos)) return false;
		return extra.equals(that.extra);
	}

	@Override
	public int hashCode() {
		int result = super.hashCode();
		result = 31 * result + pos.hashCode();
		result = 31 * result + extra.hashCode();
		return result;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "[name=" + name + ", pos=" + pos + ", extra=" + extra + ']';
	}
}
