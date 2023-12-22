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
    protected SchematicBlockPos offset = SchematicBlockPos.ZERO;
    protected SchematicBlockContainer blocks = new SchematicBlockContainer();
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
            blocks.put(pos, sblock);
            return this;
        });
    }

    /**
     * Adds a bunch of blocks at once to the schematic.
     */
    public <R extends SchematicBuilder> R blocks(Map<SchematicBlockPos, SchematicBlock> blocks) {
        blocks.forEach((pos, block) -> {
            this.blocks.put(pos, block);
        });
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
        if (name == null) {
            log.warn("Name is null");
            name = "Unnamed";
        }
        if (author == null) {
            log.warn("Author is null");
            name = "Unknown";
        }

        return toSchematic();
    }

    /**
     * Builds the schematic into a {@link Schematic} object.
     */
    protected abstract Schematic toSchematic();

    public final static class SchematicNamedBuilder<T extends SchematicNamed> {

        private final T obj;
        private final BiFunction<T, SchematicBlockPos, ? extends SchematicBuilder> registerer;

        private SchematicNamedBuilder(T obj, BiFunction<T, SchematicBlockPos, ? extends SchematicBuilder> registerer) {
            this.obj = obj;
            this.registerer = registerer;
        }

        /**
         * Adds this block to the schematic at the specified location.
         */
        public <R extends SchematicBuilder> R at(int x, int y, int z) throws SchematicBuilderException {
            return at(new SchematicBlockPos(x, y, z));
        }

        /**
         * Adds this block to the schematic at the specified location.
         */
        public <R extends SchematicBuilder> R at(SchematicBlockPos pos) throws SchematicBuilderException {
            if (pos.x < 0 || pos.y < 0 || pos.z < 0)
                throw new SchematicBuilderException("Positions cannot be negative. Use an offset instead.");
            return (R) registerer.apply(obj, pos);
        }
    }

    public final static class SchematicEntityBuilder {

        private final SchematicEntity entity;
        private final BiFunction<SchematicEntity, SchematicEntityPos, ? extends SchematicBuilder> registerer;

        private SchematicEntityBuilder(SchematicEntity entity,
                                      BiFunction<SchematicEntity, SchematicEntityPos, ? extends SchematicBuilder> registerer) {
            this.entity = entity;
            this.registerer = registerer;
        }

        /**
         * Sets the entity's NBT data (e.g. a wolf's owner).
         */
        public SchematicEntityBuilder nbt(Map<String, Object> nbt) {
            entity.data = nbt;
            return this;
        }

        /**
         * Adds this entity to the schematic at the specified location.
         */
        public <R extends SchematicBuilder> R at(float x, float y, float z) throws SchematicBuilderException {
            return at(new SchematicEntityPos(x, y, z));
        }

        /**
         * Adds this entity to the schematic at the specified location.
         */
        public <R extends SchematicBuilder> R at(SchematicEntityPos pos) throws SchematicBuilderException {
            if (pos.x < 0 || pos.y < 0 || pos.z < 0)
                throw new SchematicBuilderException("Positions cannot be negative. Use an offset instead.");
            return (R) registerer.apply(entity, pos);
        }
    }
}
