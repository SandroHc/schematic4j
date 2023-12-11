/* Vendored version of Quertz NBT 6.1 - https://github.com/Querz/NBT */
package net.sandrohc.schematic4j.nbt.io;

import net.sandrohc.schematic4j.nbt.tag.Tag;

public class NamedTag {

	private String name;
	private Tag<?> tag;

	public NamedTag(String name, Tag<?> tag) {
		this.name = name;
		this.tag = tag;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setTag(Tag<?> tag) {
		this.tag = tag;
	}

	public String getName() {
		return name;
	}

	public Tag<?> getTag() {
		return tag;
	}
}
