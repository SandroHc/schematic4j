package net.sandrohc.schematic4j.schematic;

import java.time.LocalDateTime;
import java.util.Collection;

import net.sandrohc.schematic4j.SchematicFormat;
import net.sandrohc.schematic4j.schematic.types.*;
import net.sandrohc.schematic4j.utils.BiomeIterator;
import net.sandrohc.schematic4j.utils.BlockIterator;

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
	BlockIterator getBlocks();

	/**
	 * The list of tile/block entities.
	 *
	 * @return list of block entities
	 */
	Collection<SchematicBlockEntity> getBlockEntities();

	/**
	 * The list of entities.
	 *
	 * @return list of entities
	 */
	Collection<SchematicEntity> getEntities();

	/**
	 * The biome at the specified block.
	 *
	 * @param x the X coordinate
	 * @param z the Z coordinate
	 * @return biome, or {@code null} if information is not available.
	 */
	SchematicBiome getBiome(int x, int z);

	/**
	 * Iterator for iterating over the list of biomes.
	 *
	 * @return an iterator
	 */
	BiomeIterator getBiomes();

	/**
	 * The name of the schematic.
	 *
	 * @return schematic name, or {@code null} if information is not available.
	 */
	String getName();

	/**
	 * The name of the author of the schematic.
	 *
	 * @return author, or {@code null} if information is not available.
	 */
	String getAuthor();

	/**
	 * The date that this schematic was created on.
	 *
	 * @return creation date, or {@code null} if information is not available.
	 */
	LocalDateTime date();

}
