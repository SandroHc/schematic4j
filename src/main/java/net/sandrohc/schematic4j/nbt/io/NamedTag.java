/* Vendored version of Quertz NBT 6.1 - https://github.com/Querz/NBT */
package net.sandrohc.schematic4j.nbt.io;

import net.sandrohc.schematic4j.nbt.tag.Tag;

/**
 * A named tag.
 */
public class NamedTag {

	/**
	 * The name of the named tag.
	 */
	private String name;

	/**
	 * The inner tag.
	 */
	private Tag<?> tag;

	public NamedTag(String name, Tag<?> tag) {
		this.name = name;
		this.tag = tag;
	}

	/**
	 * Set a new name.
	 *
	 * @param name The new name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Set a new tag.
	 *
	 * @param tag The new tag
	 */
	public void setTag(Tag<?> tag) {
		this.tag = tag;
	}

	/**
	 * Get the named tag name.
	 *
	 * @return The named tag name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Get the named tag inner tag.
	 *
	 * @return The named tag inner tag
	 */
	public Tag<?> getTag() {
		return tag;
	}
}
