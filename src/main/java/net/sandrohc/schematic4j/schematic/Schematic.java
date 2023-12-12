package net.sandrohc.schematic4j.schematic;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sandrohc.schematic4j.SchematicFormat;
import net.sandrohc.schematic4j.schematic.types.*;
import net.sandrohc.schematic4j.utils.iterators.Arr3DIterator;

/**
 * A generic schematic.
 */
public interface Schematic {

	/**
	 * The schematic format.
	 *
	 * @return the schematic format
	 */
	@NonNull SchematicFormat format();

	/**
	 * The width of the schematic.
	 *
	 * @return the schematic width
	 */
	int width();

	/**
	 * The height of the schematic.
	 *
	 * @return the schematic height
	 */
	int height();

	/**
	 * The length of the schematic.
	 *
	 * @return the schematic length
	 */
	int length();

	/**
	 * The relative offset from the origin (0, 0, 0) of the schematic.
	 * <br>
	 * Values are stored in the format: [x, y, z]
	 *
	 * @return the schematic offset from the origin
	 */
	int[] offset();

	/**
	 * The block at the specified block.
	 * <p>
	 * Depending on the schematic format, each coordinate can also be negative and is relative to the schematic origin.
	 *
	 * @param x The X coordinate, can be a negative value
	 * @param y The X coordinate, can be a negative value
	 * @param z The X coordinate, can be a negative value
	 * @return block, or {@code null} if information is not available.
	 */
	@Nullable SchematicBlock block(int x, int y, int z);

	/**
	 * Iterator for iterating over the list of blocks.
	 *
	 * @return an iterator
	 */
	@NonNull Arr3DIterator<SchematicBlock> blocks();

	/**
	 * The list of tile/block entities, like chests and furnaces.
	 *
	 * @return list of block entities
	 */
	default @NonNull Collection<SchematicBlockEntity> blockEntities() {
		return Collections.emptyList();
	}

	/**
	 * The list of entities, like players.
	 *
	 * @return list of entities
	 */
	default @NonNull Collection<SchematicEntity> entities() {
		return Collections.emptyList();
	}

	/**
	 * The biome at the specified block.
	 *
	 * @param x the X coordinate
	 * @param y the Y coordinate
	 * @param z the Z coordinate
	 * @return biome, or {@code null} if information is not available.
	 */
	default @Nullable SchematicBiome biome(int x, int y, int z) {
		return null;
	}

	/**
	 * Iterator for iterating over the list of biomes.
	 *
	 * @return an iterator
	 */
	default @NonNull Arr3DIterator<SchematicBiome> biomes() {
		return new Arr3DIterator<>(null);
	}

	/**
	 * The name of the schematic.
	 *
	 * @return schematic name, or {@code null} if information is not available.
	 */
	default @Nullable String name() {
		return null;
	}

	/**
	 * The name of the author of the schematic.
	 *
	 * @return author, or {@code null} if information is not available.
	 */
	default @Nullable String author() {
		return null;
	}

	/**
	 * The date that this schematic was created on.
	 *
	 * @return creation date, or {@code null} if information is not available.
	 */
	default @Nullable LocalDateTime date() {
		return null;
	}

	/**
	 * The icon for representing this schematic. A Minecraft item.
	 *
	 * @return icon, or {@code null} if information is not available.
	 */
	default @Nullable SchematicItem icon() {
		return null;
	}


	/**
	 * The schematic format.
	 *
	 * @return the schematic format
	 * @deprecated Use {@link Schematic#format()} instead
	 */
	@Deprecated
	default SchematicFormat getFormat() {
		return format();
	}

	/**
	 * The width of the schematic.
	 *
	 * @return the schematic width
	 * @deprecated Use {@link Schematic#width()} instead
	 */
	@Deprecated
	default int getWidth() {
		return width();
	}

	/**
	 * The height of the schematic.
	 *
	 * @return the schematic height
	 * @deprecated Use {@link Schematic#height()} instead
	 */
	@Deprecated
	default int getHeight() {
		return height();
	}

	/**
	 * The length of the schematic.
	 *
	 * @return the schematic length
	 * @deprecated Use {@link Schematic#length()} instead
	 */
	@Deprecated
	default int getLength() {
		return length();
	}

	/**
	 * The relative offset from the origin (0, 0, 0) of the schematic.
	 * <br>
	 * Values are stored in the format: [x, y, z]
	 *
	 * @return the schematic offset from the origin
	 * @deprecated Use {@link Schematic#offset()} instead
	 */
	@Deprecated
	default int[] getOffset() {
		return offset();
	}

	/**
	 * The block at the specified block.
	 * <p>
	 * Depending on the schematic format, each coordinate can also be negative and is relative to the schematic origin.
	 *
	 * @param x The X coordinate, can be a negative value
	 * @param y The X coordinate, can be a negative value
	 * @param z The X coordinate, can be a negative value
	 * @return block, or {@code null} if information is not available.
	 * @deprecated Use {@link Schematic#block(int, int, int)} instead
	 */
	@Deprecated
	default SchematicBlock getBlock(int x, int y, int z) {
		return block(x, y, z);
	}

	/**
	 * Iterator for iterating over the list of blocks.
	 *
	 * @return an iterator
	 * @deprecated Use {@link Schematic#blocks()} instead
	 */
	@Deprecated
	default Arr3DIterator<SchematicBlock> getBlocks() {
		return blocks();
	}

	/**
	 * The list of tile/block entities, like chests and furnaces.
	 *
	 * @return list of block entities
	 * @deprecated Use {@link Schematic#blockEntities()} instead
	 */
	@Deprecated
	default Collection<SchematicBlockEntity> getBlockEntities() {
		return blockEntities();
	}

	/**
	 * The list of entities, like players.
	 *
	 * @return list of entities
	 * @deprecated Use {@link Schematic#entities()} instead
	 */
	@Deprecated
	default Collection<SchematicEntity> getEntities() {
		return entities();
	}

	/**
	 * The biome at the specified block.
	 *
	 * @param x the X coordinate
	 * @param y the Y coordinate
	 * @param z the Z coordinate
	 * @return biome, or {@code null} if information is not available.
	 * @deprecated Use {@link Schematic#biome(int, int, int)}  instead
	 */
	@Deprecated
	default SchematicBiome getBiome(int x, int y, int z) {
		return biome(x, y, z);
	}

	/**
	 * Iterator for iterating over the list of biomes.
	 *
	 * @return an iterator
	 * @deprecated Use {@link Schematic#biomes()} instead
	 */
	@Deprecated
	default Arr3DIterator<SchematicBiome> getBiomes() {
		return biomes();
	}

	/**
	 * The name of the schematic.
	 *
	 * @return schematic name, or {@code null} if information is not available.
	 * @deprecated Use {@link Schematic#name()}  instead
	 */
	@Deprecated
	default String getName() {
		return name();
	}
}
