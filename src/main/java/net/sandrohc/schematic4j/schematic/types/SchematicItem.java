package net.sandrohc.schematic4j.schematic.types;

/**
 * Represents an item, like a stick.
 */
public class SchematicItem extends SchematicNamed {

	/**
	 * The item count, like a stack of 64 sticks.
	 */
	public int count;

	/**
	 * The damage amount.
	 */
	public int damage;

	public SchematicItem(String name, int count, int damage) {
		super(name);
		this.count = count;
		this.damage = damage;
	}

	/**
	 * The item count, like a stack of 64 sticks.
	 *
	 * @return The item count
	 */
	public int count() {
		return count;
	}

	/**
	 * The damage amount.
	 *
	 * @return The damage amount
	 */
	public int damage() {
		return damage;
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
		return getClass().getSimpleName() + "[name=" + name + ", count=" + count + ", damage=" + damage + "]";
	}
}
