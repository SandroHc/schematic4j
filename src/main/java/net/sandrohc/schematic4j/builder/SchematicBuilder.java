package net.sandrohc.schematic4j.builder;

import net.sandrohc.schematic4j.SchematicFormat;
import net.sandrohc.schematic4j.exception.NoBuilderFoundException;
import net.sandrohc.schematic4j.exception.SchematicBuilderException;
import net.sandrohc.schematic4j.schematic.Schematic;
import net.sandrohc.schematic4j.schematic.types.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

/**
 * A utility class for building schematics.
 */
public abstract class SchematicBuilder {

    protected SchematicBuilder() {}

    protected SchematicFormat format;
    protected String name;
    protected String author;
    protected Integer width;
    protected Integer height;
    protected Integer length;
    protected SchematicBlockPos offset = SchematicBlockPos.ZERO;
    protected SchematicBlock[][][] blocks = new SchematicBlock[][][]{};
    protected List<SchematicBlockEntity> blockEntities = new ArrayList<>();
    protected List<SchematicEntity> entities = new ArrayList<>();

    private static final Logger log = LoggerFactory.getLogger(SchematicBuilder.class);

    /**
     * Initializes a builder for a schematic. You can use this function to generate a schematic file.
     * @param format The Schematic format.
     * @return The builder.
     * @throws NoBuilderFoundException If no builder was found for the desired schematic format.
     */
    public static SchematicBuilder create(SchematicFormat format) throws NoBuilderFoundException {
        SchematicBuilder builder = format.createBuilder();
        builder.format = format;
        return builder;
    }

    /**
     * Sets the name of the schematic.
     */
    public <R extends SchematicBuilder> R name(String name) {
        this.name = name;
        return (R) this;
    }

    /**
     * Sets the author of the schematic.
     */
    public <R extends SchematicBuilder> R author(String author) {
        this.author = author;
        return (R) this;
    }

    /**
     * Sets the dimensions of the schematic.
     * @param width Schematic width (X axis)
     * @param height Schematic height (Y axis)
     * @param length Schematic length (Z axis)
     */
    public final <R extends SchematicBuilder> R dimensions(int width, int height, int length) {
        this.width = width;
        this.height = height;
        this.length = length;
        return (R) this;
    }

    /**
     * Sets the schematic width (X axis).
     */
    public final <R extends SchematicBuilder> R width(int width) {
        this.width = width;
        return (R) this;
    }

    /**
     * Sets the schematic height (Y axis).
     */
    public final <R extends SchematicBuilder> R height(int height) {
        this.height = height;
        return (R) this;
    }

    /**
     * Sets the schematic length (Z axis).
     */
    public final <R extends SchematicBuilder> R length(int length) {
        this.length = length;
        return (R) this;
    }

    /**
     * Sets the schematic's offset.
     */
    public SchematicBuilder offset(int x, int y, int z) {
        this.offset = new SchematicBlockPos(x, y, z);
        return this;
    }

    /**
     * Sets the schematic's offset.
     */
    public SchematicBuilder offset(SchematicBlockPos pos) {
        this.offset = pos;
        return this;
    }

    /**
     * Sets a block at a specific location.
     * @param block A block string with states, for example {@code minecraft:grass_block[snowy=true]}
     */
    public SchematicNamedBuilder<SchematicBlock> block(String block) {
        return new SchematicNamedBuilder<>(new SchematicBlock(block), (sblock, pos) -> {
            blocks[pos.x][pos.y][pos.z] = sblock;
            return this;
        });
    }

    /**
     * Defines the blocks inside the schematic.
     */
    public <R extends SchematicBuilder> R blocks(SchematicBlock[][][] blocks) {
        this.blocks = blocks;
        return (R) this;
    }

    /**
     * Sets a block entity at a specific location.
     * @param block The block type, for example {@code minecraft:chest}
     * @param nbt The NBT data
     */
    public SchematicNamedBuilder<SchematicBlockEntity> blockEntity(String block, Map<String, Object> nbt) {
        return new SchematicNamedBuilder<>(new SchematicBlockEntity(block, null, nbt), (blockEntity, pos) -> {
            blockEntity.pos = pos;
            blockEntities.add(blockEntity);
            return this;
        });
    }

    /**
     * Adds an entity to a specific location.
     * @param entity The entity type, for example {@code minecraft:creeper}
     */
    public SchematicEntityBuilder entity(String entity) {
        return new SchematicEntityBuilder(new SchematicEntity(entity, null, null), (sentity, pos) -> {
            sentity.pos = pos;
            entities.add(sentity);
            return this;
        });
    }

    /**
     * Builds the schematic into a {@link Schematic} object.
     */
    public final Schematic build() throws SchematicBuilderException {
        if (width == null || height == null || length == null) {
            throw new SchematicBuilderException("Dimensions missing");
        }
        if (name == null) {
            log.warn("Name is null");
            name = "Unnamed";
        }
        if (author == null) {
            log.warn("Author is null");
            name = "Unknown";
        }
        if (width * height * length != blocks.length * 3) {
            throw new SchematicBuilderException("Schematic size does not match block size");
        }

        return toSchematic();
    }

    /**
     * Builds the schematic into a {@link Schematic} object.
     */
    protected abstract Schematic toSchematic();

    public final static class SchematicNamedBuilder<T extends SchematicNamed> {

        private final T obj;
        private SchematicBlockPos pos;
        private final BiFunction<T, SchematicBlockPos, ? extends SchematicBuilder> registerer;

        private SchematicNamedBuilder(T obj, BiFunction<T, SchematicBlockPos, ? extends SchematicBuilder> registerer) {
            this.obj = obj;
            this.registerer = registerer;
        }

        /**
         * Adds this block to the schematic at the specified location.
         */
        public <R extends SchematicBuilder> R at(int x, int y, int z) {
            this.pos = new SchematicBlockPos(x, y, z);
            return (R) registerer.apply(obj, pos);
        }

        /**
         * Adds this block to the schematic at the specified location.
         */
        public <R extends SchematicBuilder> R at(SchematicBlockPos pos) {
            this.pos = pos;
            return (R) registerer.apply(obj, pos);
        }
    }

    public final static class SchematicEntityBuilder {

        private final SchematicEntity entity;
        private SchematicEntityPos pos;
        private final BiFunction<SchematicEntity, SchematicEntityPos, ? extends SchematicBuilder> registerer;
        private Map<String, Object> nbt = new HashMap<>();

        private SchematicEntityBuilder(SchematicEntity entity,
                                      BiFunction<SchematicEntity, SchematicEntityPos, ? extends SchematicBuilder> registerer) {
            this.entity = entity;
            this.registerer = registerer;
        }

        /**
         * Sets the entity's NBT data (e.g. a wolf's owner).
         */
        public SchematicEntityBuilder nbt(Map<String, Object> nbt) {
            this.nbt = nbt;
            return this;
        }

        /**
         * Adds this entity to the schematic at the specified location.
         */
        public <R extends SchematicBuilder> R at(float x, float y, float z) {
            this.pos = new SchematicEntityPos(x, y, z);
            return (R) registerer.apply(entity, pos);
        }

        /**
         * Adds this entity to the schematic at the specified location.
         */
        public <R extends SchematicBuilder> R at(SchematicEntityPos pos) {
            this.pos = pos;
            return (R) registerer.apply(entity, pos);
        }
    }
}
