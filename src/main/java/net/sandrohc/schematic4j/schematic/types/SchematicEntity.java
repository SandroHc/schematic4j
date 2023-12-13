package net.sandrohc.schematic4j.schematic.types;

import java.util.Map;

public class SchematicEntity extends SchematicNamed {

	public final SchematicPosDouble pos;
	public final Map<String, Object> extra;

	public SchematicEntity(String name, SchematicPosDouble pos, Map<String, Object> extra) {
		super(name);
		this.pos = pos;
		this.extra = extra;
	}

	public SchematicPosDouble pos() {
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

		SchematicEntity that = (SchematicEntity) o;

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
		return "SchematicEntity[name=" + name + ", pos=" + pos + ", extra=" + extra + ']';
	}
}
