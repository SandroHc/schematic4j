package net.sandrohc.schematic4j.schematic;

import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Stream;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sandrohc.schematic4j.SchematicFormat;
import net.sandrohc.schematic4j.schematic.types.SchematicBlock;
import net.sandrohc.schematic4j.schematic.types.SchematicBlockEntity;
import net.sandrohc.schematic4j.schematic.types.SchematicBlockPos;
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
	public int width;

	/**
	 * The schematic height, the Y axis.
	 */
	public int height;

	/**
	 * The schematic length, the Z axis.
	 */
	public int length;

	/**
	 * The unpacked list of block IDs.
	 */
	public int @NonNull [] blockIds = new int[0];

	/**
	 * The unpacked list of block metadata (used as discriminator before Minecraft's 1.7 block ID overhaul).
	 */
	public int @NonNull [] blockMetadata = new int[0];

	/**
	 * The unpacked list of blocks.
	 */
	public String @NonNull [] blockPalette = new String[0];

	/**
	 * The list of block/tile entities.
	 */
	public @NonNull SchematicBlockEntity @NonNull [] blockEntities = new SchematicBlockEntity[0];

	/**
	 * The list of entities.
	 */
	public @NonNull SchematicEntity @NonNull [] entities = new SchematicEntity[0];

	/**
	 * The schematic icon, if available.
	 */
	public @Nullable SchematicItem icon;

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
	public @Nullable String materials;

	public SchematicaSchematic() {
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
	public @NonNull SchematicBlockPos offset() {
		return SchematicBlockPos.ZERO;
	}

	@Override
	public @NonNull SchematicBlock block(int x, int y, int z) {
		final int blockIndex = posToIndex(x, y, z);
		if (blockIndex < 0 || blockIndex >= blockIds.length) {
			return AIR; // outside bounds
		}

		final int blockId = blockIds[blockIndex];
		String blockName = blockPalette[blockId];
		if (blockName == null) {
			blockName = "minecraft:legacy_id_" + blockId;
		}

		final int metadata = blockMetadata[blockIndex];
		final Map<String, String> states = new TreeMap<>();
		if (metadata != 0) {
			states.put("metadata", String.valueOf(metadata));
		}

		return new SchematicBlock(blockName, states);
	}

	/**
	 * The raw block ID data.
	 *
	 * @return The raw block data
	 */
	public int @NonNull [] blockIdData() {
		return blockIds;
	}

	/**
	 * The raw block metadata.
	 *
	 * @return The raw block data
	 */
	public int @NonNull [] blockMetadata() {
		return blockMetadata;
	}

	/**
	 * The raw block palette.
	 *
	 * @return The raw block palette
	 */
	public String @NonNull [] blockPalette() {
		return blockPalette;
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

	public int posToIndex(int x, int y, int z) {
		return x + (z * width) + (y * width * length);
	}

	public @NonNull SchematicBlockPos indexToPos(int index) {
		final int x = index % width;
		final int z = (index / width) % length;
		final int y = index / (width * length);
		return new SchematicBlockPos(x, y, z);
	}

	@Override
	public String toString() {
		return "SchematicSchematica[" +
				"name=" + name() +
				", width=" + width +
				", height=" + height +
				", length=" + length +
				']';
	}
}
