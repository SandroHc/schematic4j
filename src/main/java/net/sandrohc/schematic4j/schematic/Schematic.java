package net.sandrohc.schematic4j.schematic;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;

import net.sandrohc.schematic4j.SchematicFormat;
import net.sandrohc.schematic4j.schematic.types.*;
import net.sandrohc.schematic4j.utils.iterators.Arr2DIterator;
import net.sandrohc.schematic4j.utils.iterators.Arr3DIterator;

public interface Schematic {

	SchematicFormat getFormat();

	/**
	 * The width of the schematic.
	 *
	 * @return the schematic width
	 */
	int getWidth();

	/**
	 * The height of the schematic.
	 *
	 * @return the schematic height
	 */
	int getHeight();

	/**
	 * The length of the schematic.
	 *
	 * @return the schematic length
	 */
	int getLength();

	/**
	 * The relative offset of the schematic.
	 *
	 * @return the schematic length
	 */
	int[] getOffset();

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
	SchematicBlock getBlock(int x, int y, int z);

	/**
	 * Iterator for iterating over the list of blocks.
	 *
	 * @return an iterator
	 */
	Arr3DIterator<SchematicBlock> getBlocks();

	/**
	 * The list of tile/block entities.
	 *
	 * @return list of block entities
	 */
	default Collection<SchematicBlockEntity> getBlockEntities() {
		return Collections.emptyList();
	}

	/**
	 * The list of entities.
	 *
	 * @return list of entities
	 */
	default Collection<SchematicEntity> getEntities() {
		return Collections.emptyList();
	}

	/**
	 * The biome at the specified block.
	 *
	 * @param x the X coordinate
	 * @param z the Z coordinate
	 * @return biome, or {@code null} if information is not available.
	 */
	default SchematicBiome getBiome(int x, int z) {
		return null;
	}

	/**
	 * Iterator for iterating over the list of biomes.
	 *
	 * @return an iterator
	 */
	default Arr2DIterator<SchematicBiome> getBiomes() {
		return new Arr2DIterator<>(null, 0, 0);
	}

	/**
	 * The name of the schematic.
	 *
	 * @return schematic name, or {@code null} if information is not available.
	 */
	default String getName() {
		return null;
	}

	/**
	 * The name of the author of the schematic.
	 *
	 * @return author, or {@code null} if information is not available.
	 */
	default String author() {
		return null;
	}

	/**
	 * The date that this schematic was created on.
	 *
	 * @return creation date, or {@code null} if information is not available.
	 */
	default LocalDateTime date() {
		return null;
	}

	/**
	 * The icon for representing this schematic. A Minecraft item.
	 *
	 * @return icon, or {@code null} if information is not available.
	 */
	default SchematicItem icon() {
		return null;
	}

}
