package net.sandrohc.schematic4j.schematic.types;

import java.util.Map;
import java.util.TreeMap;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sandrohc.schematic4j.nbt.tag.CompoundTag;
import net.sandrohc.schematic4j.nbt.tag.Tag;

import static java.util.stream.Collectors.toMap;
import static net.sandrohc.schematic4j.utils.TagUtils.unwrap;

public class SchematicBlockEntity extends SchematicNamed {

	public final SchematicBlockPos pos;
	public final Map<String, Object> data;

	public SchematicBlockEntity(String name, SchematicBlockPos pos, Map<String, Object> data) {
		super(name);
		this.pos = pos;
		this.data = data;
	}

	public static @Nullable SchematicBlockEntity fromNbt(Tag<?> nbtTag) {
		if (!(nbtTag instanceof CompoundTag)) {
			return null;
		}
		final CompoundTag nbt = ((CompoundTag) nbtTag);

		final String id = nbt.getString("id");
		final SchematicBlockPos pos = SchematicBlockPos.from(nbt);
		final Map<String, Object> extra = nbt.entrySet().stream()
				.filter(tag -> !tag.getKey().equals("id") &&
						!tag.getKey().equals("x") &&
						!tag.getKey().equals("y") &&
						!tag.getKey().equals("z"))
				.collect(toMap(Map.Entry::getKey, e -> unwrap(e.getValue()), (a, b) -> b, TreeMap::new));

		return new SchematicBlockEntity(id, pos, extra);
	}

	public SchematicBlockPos pos() {
		return pos;
	}

	public Map<String, Object> extra() {
		return data;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;

		SchematicBlockEntity that = (SchematicBlockEntity) o;

		if (!pos.equals(that.pos)) return false;
		return data.equals(that.data);
	}

	@Override
	public int hashCode() {
		int result = super.hashCode();
		result = 31 * result + pos.hashCode();
		result = 31 * result + data.hashCode();
		return result;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "[name=" + name + ", pos=" + pos + ", data=" + data + ']';
	}
}
