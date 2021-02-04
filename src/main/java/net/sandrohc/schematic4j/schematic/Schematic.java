package net.sandrohc.schematic4j.schematic;

import java.time.LocalDateTime;
import java.util.Collection;

import net.sandrohc.schematic4j.schematic.types.*;

public interface Schematic {

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
	 *
	 * @param pos the position
	 * @return block, or {@code null} if information is not available.
	 */
	SchematicBlock getBlock(SchematicPos<Integer> pos);

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
