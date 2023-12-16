package net.sandrohc.schematic4j.parser;

import java.util.Map.Entry;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sandrohc.schematic4j.exception.ParsingException;
import net.sandrohc.schematic4j.nbt.tag.CompoundTag;
import net.sandrohc.schematic4j.nbt.tag.ListTag;
import net.sandrohc.schematic4j.nbt.tag.NumberTag;
import net.sandrohc.schematic4j.nbt.tag.Tag;
import net.sandrohc.schematic4j.schematic.Schematic;
import net.sandrohc.schematic4j.schematic.SchematicaSchematic;
import net.sandrohc.schematic4j.schematic.types.SchematicBlockEntity;
import net.sandrohc.schematic4j.schematic.types.SchematicEntity;
import net.sandrohc.schematic4j.schematic.types.SchematicItem;

import static net.sandrohc.schematic4j.utils.TagUtils.getByte;
import static net.sandrohc.schematic4j.utils.TagUtils.getByteArrayOrThrow;
import static net.sandrohc.schematic4j.utils.TagUtils.getCompound;
import static net.sandrohc.schematic4j.utils.TagUtils.getCompoundList;
import static net.sandrohc.schematic4j.utils.TagUtils.getCompoundOrThrow;
import static net.sandrohc.schematic4j.utils.TagUtils.getShort;
import static net.sandrohc.schematic4j.utils.TagUtils.getString;

/**
 * Parses Schematica files (<i>.schematic</i>).
 * <p>
 * Specification:<br>
 * - <a href="https://minecraft.fandom.com/wiki/Schematic_file_format">https://minecraft.fandom.com/wiki/Schematic_file_format</a>
 * - <a href="https://github.com/Lunatrius/Schematica/blob/master/src/main/java/com/github/lunatrius/schematica/world/schematic/SchematicAlpha.java">https://github.com/Lunatrius/Schematica/blob/master/src/main/java/com/github/lunatrius/schematica/world/schematic/SchematicAlpha.java</a>
 */
public class SchematicaParser implements Parser {

	private static final Logger log = LoggerFactory.getLogger(SchematicaParser.class);

	public static final String NBT_MATERIALS = "Materials";

	public static final String NBT_ICON = "Icon";
	public static final String NBT_ICON_ID = "id";
	public static final String NBT_ICON_COUNT = "Count";
	public static final String NBT_ICON_DAMAGE = "Damage";
	public static final String NBT_BLOCKS = "Blocks";
	public static final String NBT_DATA = "Data";
	public static final String NBT_ADD_BLOCKS = "AddBlocks";
	public static final String NBT_ADD_BLOCKS_SCHEMATICA = "Add";
	public static final String NBT_WIDTH = "Width";
	public static final String NBT_LENGTH = "Length";
	public static final String NBT_HEIGHT = "Height";
	public static final String NBT_MAPPING_SCHEMATICA = "SchematicaMapping";
	public static final String NBT_TILE_ENTITIES = "TileEntities";
	public static final String NBT_ENTITIES = "Entities";

	@Override
	public @NonNull Schematic parse(@Nullable CompoundTag nbt) throws ParsingException {
		log.debug("Parsing Schematica schematic");

		final SchematicaSchematic schematic = new SchematicaSchematic();
		if (nbt == null) {
			return schematic;
		}

		parseIcon(nbt, schematic);
		parseBlocks(nbt, schematic);
		parseBlockEntities(nbt, schematic);
		parseEntities(nbt, schematic);
		parseMaterials(nbt, schematic);

		return schematic;
	}

	private void parseIcon(CompoundTag root, SchematicaSchematic schematic) {
		log.trace("Parsing icon");
		getCompound(root, NBT_ICON).ifPresent(iconTag -> {
			schematic.icon = new SchematicItem(
					getString(iconTag, NBT_ICON_ID).orElse("minecraft:dirt"),
					getByte(iconTag, NBT_ICON_COUNT).orElse((byte) 1),
					getShort(iconTag, NBT_ICON_DAMAGE).orElse((short) 0)
			);
		});
	}

	private void parseBlocks(CompoundTag root, SchematicaSchematic schematic) throws ParsingException {
		log.trace("Parsing blocks");

		schematic.width = (int) getShort(root, NBT_WIDTH).orElse((short) 0);
		schematic.height = (int) getShort(root, NBT_HEIGHT).orElse((short) 0);
		schematic.length = (int) getShort(root, NBT_LENGTH).orElse((short) 0);

		/* Mappings */
		final CompoundTag paletteTag = getCompoundOrThrow(root, NBT_MAPPING_SCHEMATICA);
		final int biggestId = paletteTag.values().stream().mapToInt(tag -> tag instanceof NumberTag ? ((NumberTag<?>) tag).asInt() : 0).max().orElse(0) + 1;
		log.trace("Palette size: {}, biggest ID: {}", paletteTag.size(), biggestId);
		final String[] palette = new String[biggestId];
		for (Entry<String, Tag<?>> entry : paletteTag) {
			final String blockName = entry.getKey();
			final int index = ((NumberTag<?>) entry.getValue()).asInt();
			palette[index] = blockName;
		}

		// Load the (optional) palette
		final byte[] blocksRaw = getByteArrayOrThrow(root, NBT_BLOCKS);
		final byte[] blockDataRaw = getByteArrayOrThrow(root, NBT_DATA);

		boolean extra = false;
		byte[] extraBlocks = null;
		if (root.containsKey(NBT_ADD_BLOCKS)) {
			extra = true;
			byte[] extraBlocksNibble = getByteArrayOrThrow(root, NBT_ADD_BLOCKS);
			extraBlocks = new byte[extraBlocksNibble.length * 2];
			for (int i = 0; i < extraBlocksNibble.length; i++) {
				extraBlocks[i * 2] = (byte) ((extraBlocksNibble[i] >> 4) & 0xF);
				extraBlocks[i * 2 + 1] = (byte) (extraBlocksNibble[i] & 0xF);
			}
		} else if (root.containsKey(NBT_ADD_BLOCKS_SCHEMATICA)) {
			extra = true;
			extraBlocks = getByteArrayOrThrow(root, NBT_ADD_BLOCKS_SCHEMATICA);
		}

		int totalVolume = blocksRaw.length;
		int expectedTotalVolume = schematic.width * schematic.height * schematic.length;
		if (totalVolume != expectedTotalVolume) {
			log.warn("Number of blocks does not match expected. Expected {} blocks, but got {}", expectedTotalVolume, totalVolume);
		}

		int[] blocks = new int[totalVolume];
		int[] blockMetadata = new int[totalVolume];

		for (int index = 0; index < totalVolume; index++) {
			final int blockId = (blocksRaw[index] & 0xFF) | (extra ? ((extraBlocks[index] & 0xFF) << 8) : 0);
			final int metadata = blockDataRaw[index] & 0xFF;

			blocks[index] = blockId;
			blockMetadata[index] = metadata;
		}

		schematic.blockIds = blocks;
		schematic.blockMetadata = blockMetadata;
		schematic.blockPalette = palette;
		log.debug("Loaded {} blocks", blocks.length);
	}

	private void parseBlockEntities(CompoundTag root, SchematicaSchematic schematic) {
		final ListTag<CompoundTag> blockEntitiesTag = getCompoundList(root, NBT_TILE_ENTITIES).orElse(null);
		if (blockEntitiesTag == null) {
			log.trace("No block entities found");
			return;
		}

		log.trace("Parsing block entities");
		final SchematicBlockEntity[] blockEntities = new SchematicBlockEntity[blockEntitiesTag.size()];

		int i = 0;
		for (CompoundTag blockEntityTag : blockEntitiesTag) {
			final SchematicBlockEntity blockEntity = SchematicBlockEntity.fromNbt(blockEntityTag);
			blockEntities[i++] = blockEntity;
		}

		schematic.blockEntities = blockEntities;
		log.debug("Loaded {} block entities", blockEntities.length);
	}

	private void parseEntities(CompoundTag root, SchematicaSchematic schematic) {
		final ListTag<CompoundTag> entitiesTag = getCompoundList(root, NBT_ENTITIES).orElse(null);
		if (entitiesTag == null) {
			log.trace("No entities found");
			return;
		}

		log.trace("Parsing entities");
		final SchematicEntity[] entities = new SchematicEntity[entitiesTag.size()];

		int i = 0;
		for (final CompoundTag entityTag : entitiesTag) {
			final SchematicEntity entity = SchematicEntity.fromNbt(entityTag);
			entities[i++] = entity;
		}

		schematic.entities = entities;
		log.debug("Loaded {} entities", entities.length);
	}

	private void parseMaterials(CompoundTag root, SchematicaSchematic schematic) {
		log.trace("Parsing materials");
		getString(root, NBT_MATERIALS).ifPresent(materials -> schematic.materials = materials);
	}

	@Override
	public String toString() {
		return "SchematicaParser";
	}
}
