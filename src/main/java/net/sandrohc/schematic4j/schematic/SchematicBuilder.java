package net.sandrohc.schematic4j.schematic;

import java.util.Collection;

import net.sandrohc.schematic4j.exception.SchematicBuilderException;
import net.sandrohc.schematic4j.schematic.types.SchematicBlock;
import net.sandrohc.schematic4j.schematic.types.SchematicBlockEntity;
import net.sandrohc.schematic4j.schematic.types.SchematicEntity;

public class SchematicBuilder {

	private Integer width;
	private Integer height;
	private Integer length;
	private SchematicBlock[][][] blocks;
	private Collection<SchematicBlockEntity> blockEntities;
	private Collection<SchematicEntity> entities;

	public SchematicBuilder() {
	}

	public SchematicBuilder width(int width) {
		this.width = width;
		return this;
	}

	public SchematicBuilder height(int height) {
		this.height = height;
		return this;
	}

	public SchematicBuilder length(int length) {
		this.length = length;
		return this;
	}

	public SchematicBuilder blocks(SchematicBlock[][][] blocks) {
		this.blocks = blocks;
		return this;
	}

	public SchematicBuilder blockEntities(Collection<SchematicBlockEntity> blockEntities) {
		this.blockEntities = blockEntities;
		return this;
	}

	public SchematicBuilder entities(Collection<SchematicEntity> entities) {
		this.entities = entities;
		return this;
	}

	public Schematic build() {
		if (width == null)
			throw new SchematicBuilderException("width must be set");
		if (height == null)
			throw new SchematicBuilderException("height must be set");
		if (length == null)
			throw new SchematicBuilderException("length must be set");

		return new SchematicImpl(width, height, length, blocks, blockEntities, entities);
	}
}
