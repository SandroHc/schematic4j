package net.sandrohc.schematic4j.schematic.types;

import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sandrohc.schematic4j.nbt.tag.CompoundTag;
import net.sandrohc.schematic4j.nbt.tag.Tag;

import static java.util.stream.Collectors.toMap;
import static net.sandrohc.schematic4j.utils.TagUtils.getString;
import static net.sandrohc.schematic4j.utils.TagUtils.unwrap;

/**
 * Represents an entity, like a creeper.
 */
public class SchematicEntity extends SchematicNamed {

	/**
	 * The position of the entity on the schematic.
	 */
	public SchematicEntityPos pos;

	/**
	 * The extra NBT data the entity is holding, like a wolf's owner.
	 */
	public Map<String, Object> data;

	public SchematicEntity(String name, SchematicEntityPos pos, Map<String, Object> data) {
		super(name);
		this.pos = pos;
		this.data = data;
	}

	public static @Nullable SchematicEntity fromNbt(Tag<?> nbtTag) {
		if (!(nbtTag instanceof CompoundTag)) {
			return null;
		}
		final CompoundTag nbt = ((CompoundTag) nbtTag);

		final String id = getString(nbt, "id").orElseGet(() -> nbt.getString("Id"));
		final SchematicEntityPos pos = SchematicEntityPos.from(nbt.get("Pos"));
		final Map<String, Object> extra = nbt.entrySet().stream()
				.filter(tag -> !tag.getKey().equals("id") && !tag.getKey().equals("Id") && !tag.getKey().equals("Pos"))
				.collect(toMap(Map.Entry::getKey, e -> unwrap(e.getValue()), (a, b) -> b, TreeMap::new));

		return new SchematicEntity(id, pos, extra);
	}

	/**
	 * The position of the entity on the schematic.
	 *
	 * @return The position of the entity
	 */
	public SchematicEntityPos pos() {
		return pos;
	}

	/**
	 * The extra NBT data the entity is holding, like a wolf's owner.
	 *
	 * @return The extra NBT data
	 */
	public Map<String, Object> data() {
		return data;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;

		SchematicEntity that = (SchematicEntity) o;

		if (!Objects.equals(pos, that.pos)) return false;
		return Objects.equals(data, that.data);
	}

	@Override
	public int hashCode() {
		int result = super.hashCode();
		result = 31 * result + (pos != null ? pos.hashCode() : 0);
		result = 31 * result + (data != null ? data.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "SchematicEntity[name=" + name + ", pos=" + pos + ", data=" + data + ']';
	}
}
