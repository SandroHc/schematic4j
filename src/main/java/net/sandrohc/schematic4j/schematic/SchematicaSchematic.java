package net.sandrohc.schematic4j.schematic;

import java.util.Arrays;
import java.util.stream.Stream;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sandrohc.schematic4j.SchematicFormat;
import net.sandrohc.schematic4j.exception.SchematicBuilderException;
import net.sandrohc.schematic4j.schematic.types.SchematicBlock;
import net.sandrohc.schematic4j.schematic.types.SchematicBlockEntity;
import net.sandrohc.schematic4j.schematic.types.SchematicEntity;
import net.sandrohc.schematic4j.schematic.types.SchematicItem;

import static net.sandrohc.schematic4j.schematic.types.SchematicBlock.AIR;

/**
 * A Schematica schematic. Read more about it at <a href="https://minecraft.fandom.com/wiki/Schematic_file_format">https://minecraft.fandom.com/wiki/Schematic_file_format</a>
 * <br>
 * <h2>Implementations</h2>
 * <ul>
 *     <li><a href="https://github.com/EngineHub/WorldEdit/blob/master/worldedit-core/src/main/java/com/sk89q/worldedit/extent/clipboard/io/MCEditSchematicReader.java">WorldEdit</a></li>
 *     <li><a href="https://github.com/mcedit/pymclevel/blob/master/schematic.py">MCEdit</a></li>
 *     <li><a href="https://github.com/mcedit/mcedit2/blob/master/src/mceditlib/schematic.py">MCEdit2</a></li>
 *     <li><a href="https://github.com/Khroki/MCEdit-Unified/blob/master/pymclevel/schematic.py">MCEdit-Unified</a></li>
 *     <li><a href="https://github.com/Lunatrius/Schematica/blob/master/src/main/java/com/github/lunatrius/schematica/world/schematic/SchematicAlpha.java">Schematica</a></li>
 *     <li><a href="https://github.com/CzechPMDevs/BuilderTools">BuilderTools - PocketMine</a></li>
 * </ul>
 */
public class SchematicaSchematic implements Schematic {

	public static final String MATERIAL_CLASSIC = "Classic";
	public static final String MATERIAL_ALPHA = "Alpha";
	public static final String MATERIAL_STRUCTURE = "Structure";

	/**
	 * The schematic width, the X axis.
	 */
	public final int width;

	/**
	 * The schematic height, the Y axis.
	 */
	public final int height;

	/**
	 * The schematic length, the Z axis.
	 */
	public final int length;

	/**
	 * The unpacked list of blocks.
	 */
	public final @NonNull SchematicBlock[][][] blocks;

	/**
	 * The list of block/tile entities.
	 */
	public final @NonNull SchematicBlockEntity[] blockEntities;

	/**
	 * The list of entities.
	 */
	public final @NonNull SchematicEntity[] entities;

	/**
	 * The schematic icon, if available.
	 */
	public final @Nullable SchematicItem icon;

	/**
	 * The schematic materials, if available.
	 * <p>
	 * One of:
	 * <ul>
	 *     <li>{@link SchematicaSchematic#MATERIAL_CLASSIC MATERIAL_CLASSIC}</li>
	 *     <li>{@link SchematicaSchematic#MATERIAL_ALPHA MATERIAL_ALPHA}</li>
	 *     <li>{@link SchematicaSchematic#MATERIAL_STRUCTURE MATERIAL_STRUCTURE}</li>
	 * </ul>
	 */
	public final @Nullable String materials;

	public SchematicaSchematic(int width, int height, int length, @NonNull SchematicBlock[][][] blocks,
							   @NonNull SchematicBlockEntity[] blockEntities, @NonNull SchematicEntity[] entities,
							   @Nullable SchematicItem icon, @Nullable String materials) {

		this.width = width;
		this.height = height;
		this.length = length;
		this.blocks = blocks;
		this.blockEntities = blockEntities;
		this.entities = entities;
		this.icon = icon;
		this.materials = materials;
	}

	@Override
	public @NonNull SchematicFormat format() {
		return SchematicFormat.SCHEMATICA;
	}

	@Override
	public int width() {
		return width;
	}

	@Override
	public int height() {
		return height;
	}

	@Override
	public int length() {
		return length;
	}

	@Override
	public int[] offset() {
		return new int[]{0, 0, 0};
	}

	@Override
	public @NonNull SchematicBlock block(int x, int y, int z) {
		if ((x < 0 || x >= width) || (y < 0 || y >= height) || (z < 0 || z >= length)) {
			return AIR; // outside bounds
		}

		return blocks[x][y][z];
	}

	/**
	 * The raw block data.
	 *
	 * @return The raw block data
	 */
	public @NonNull SchematicBlock[][][] blockData() {
		return blocks;
	}

	@Override
	public @NonNull Stream<SchematicBlockEntity> blockEntities() {
		return Arrays.stream(blockEntities);
	}

	/**
	 * The raw block entity data.
	 *
	 * @return The raw block entity data
	 */
	public @NonNull SchematicBlockEntity[] blockEntityData() {
		return blockEntities;
	}

	@Override
	public @NonNull Stream<SchematicEntity> entities() {
		return Arrays.stream(entities);
	}

	/**
	 * The raw entity data.
	 *
	 * @return The raw entity data
	 */
	public @NonNull SchematicEntity[] entityData() {
		return entities;
	}

	@Override
	public @Nullable SchematicItem icon() {
		return icon;
	}

	/**
	 * The schematic materials, if available.
	 * <p>
	 * One of:
	 * <ul>
	 *     <li>{@link SchematicaSchematic#MATERIAL_CLASSIC MATERIAL_CLASSIC}</li>
	 *     <li>{@link SchematicaSchematic#MATERIAL_ALPHA MATERIAL_ALPHA}</li>
	 *     <li>{@link SchematicaSchematic#MATERIAL_STRUCTURE MATERIAL_STRUCTURE}</li>
	 * </ul>
	 *
	 * @return The schematic materials, if available
	 */
	public @Nullable String materials() {
		return materials;
	}

	@Override
	public String toString() {
		return "SchematicSchematica[" +
				"name=" + name() +
				", icon=" + icon +
				", width=" + width +
				", height=" + height +
				", length=" + length +
				']';
	}

	public static class Builder {
		private Integer width;
		private Integer height;
		private Integer length;
		private SchematicBlock[][][] blocks = new SchematicBlock[0][0][0];
		private SchematicBlockEntity[] blockEntities = new SchematicBlockEntity[0];
		private SchematicEntity[] entities = new SchematicEntity[0];
		private SchematicItem icon;
		private String materials;

		public Builder() {
		}

		public Builder icon(SchematicItem icon) {
			this.icon = icon;
			return this;
		}

		public Builder width(int width) {
			this.width = width;
			return this;
		}

		public Builder height(int height) {
			this.height = height;
			return this;
		}

		public Builder length(int length) {
			this.length = length;
			return this;
		}

		public Builder blocks(@NonNull SchematicBlock[][][] blocks) {
			this.blocks = blocks;
			return this;
		}

		public Builder blockEntities(@NonNull SchematicBlockEntity[] blockEntities) {
			this.blockEntities = blockEntities;
			return this;
		}

		public Builder entities(@NonNull SchematicEntity[] entities) {
			this.entities = entities;
			return this;
		}

		public Builder materials(String materials) {
			this.materials = materials;
			return this;
		}

		public SchematicaSchematic build() {
			if (width == null)
				throw new SchematicBuilderException("width must be set");
			if (height == null)
				throw new SchematicBuilderException("height must be set");
			if (length == null)
				throw new SchematicBuilderException("length must be set");
			if (blocks == null)
				throw new SchematicBuilderException("blocks must be set");

			return new SchematicaSchematic(width, height, length, blocks, blockEntities, entities, icon, materials);
		}
	}
}
