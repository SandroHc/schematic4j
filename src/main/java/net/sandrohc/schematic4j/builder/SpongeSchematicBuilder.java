package net.sandrohc.schematic4j.builder;

import net.sandrohc.schematic4j.nbt.io.NamedTag;
import net.sandrohc.schematic4j.schematic.Schematic;
import net.sandrohc.schematic4j.schematic.SpongeSchematic;

import java.io.OutputStream;

public class SpongeSchematicBuilder extends SchematicBuilder {//TODO

    private Integer version = 3;
    private Integer dataVersion;
    private SpongeSchematic.Metadata metadata;

    @Override
    public Schematic toSchematic() {//TODO
        SpongeSchematic schematic = new SpongeSchematic();

        schematic.version = version;
        schematic.dataVersion = dataVersion;
        metadata.name = metadata.name == null ? name : metadata.name;
        metadata.author = metadata.author == null ? author : metadata.author;
        schematic.metadata = metadata;
        //schematic.biomes = biomes;
        //schematic.biomePalette = biomePalette;
        //schematic.blocks = blocks;
        //schematic.blockPalette

        return schematic;
    }

    /**
     * Sets the Sponge Schematic Version. Default = 3.
     * @see SpongeSchematic
     */
    public SpongeSchematicBuilder version(Integer version) {
        this.version = version;
        return this;
    }

    /**
     * Sets the Minecraft data version.
     */
    public SpongeSchematicBuilder dataVersion(Integer dataVersion) {
        this.dataVersion = dataVersion;
        return this;
    }

    /**
     * Sets the schematic's metadata.
     */
    public SpongeSchematicBuilder metadata(SpongeSchematic.Metadata metadata) {
        this.metadata = metadata;
        return this;
    }
}
