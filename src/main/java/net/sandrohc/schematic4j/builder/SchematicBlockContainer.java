package net.sandrohc.schematic4j.builder;

import net.sandrohc.schematic4j.schematic.types.SchematicBlock;
import net.sandrohc.schematic4j.schematic.types.SchematicBlockPos;

import java.util.HashMap;

public final class SchematicBlockContainer {

    private int width = 0;
    private int height = 0;
    private int length = 0;
    private final HashMap<SchematicBlockPos, SchematicBlock> blocks = new HashMap<>();

    /**
     * Returns the block situated at the provided position, or {@link SchematicBlock#AIR} if not present.
     */
    public SchematicBlock get(SchematicBlockPos pos) {
        return blocks.getOrDefault(pos, SchematicBlock.AIR);
    }

    /**
     * Checks if the provided position is in bounds of this block container.
     */
    public boolean isInBounds(SchematicBlockPos pos) {
        return pos.x < width && pos.y < height && pos.z < length;
    }

    void put(SchematicBlockPos pos, SchematicBlock block) {
        // extend dimensions if necessary
        if (pos.x >= width) width = pos.x + 1;
        if (pos.y >= height) height = pos.y + 1;
        if (pos.z >= length) length = pos.z + 1;

        blocks.put(pos, block);
    }

    /**
     * Returns the amount of blocks in this container.
     * @apiNote This does not necessarily equal the amount of blocks that are actually stored.
     */
    public int size() {
        return width * height * length;
    }

}
