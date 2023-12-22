package net.sandrohc.schematic4j.builder;

import net.sandrohc.schematic4j.nbt.io.NamedTag;
import net.sandrohc.schematic4j.schematic.Schematic;

import java.io.OutputStream;

public abstract class SchematicExporter {

    protected Schematic schematic;

    protected SchematicExporter(Schematic schematic) {
        this.schematic = schematic;
    }

    public static SchematicExporter from(Schematic schematic) {
        return schematic.export();
    }

    /**
     * Exports the schematic to a file.
     * @param path The file path.
     */
    public abstract void toFile(String path);

    /**
     * Exports the schematic to the specified output stream.
     */
    public abstract void toOutputStream(OutputStream os);

    /**
     * Exports the schematic to a byte array.
     */
    public abstract byte[] toBytes();

    /**
     * Exports the schematic to NBT data.
     */
    public abstract NamedTag toNBT();
}
