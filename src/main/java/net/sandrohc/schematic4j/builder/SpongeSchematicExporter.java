package net.sandrohc.schematic4j.builder;

import net.sandrohc.schematic4j.nbt.io.NamedTag;
import net.sandrohc.schematic4j.schematic.Schematic;

import java.io.OutputStream;

public class SpongeSchematicExporter extends SchematicExporter {//TODO
    public SpongeSchematicExporter(Schematic schematic) {
        super(schematic);
    }

    @Override
    public void toFile(String path) {

    }

    @Override
    public void toOutputStream(OutputStream os) {

    }

    @Override
    public byte[] toBytes() {
        return new byte[0];
    }

    @Override
    public NamedTag toNBT() {
        return null;
    }
}
