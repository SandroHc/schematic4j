package net.sandrohc.schematic4j.exception;

import net.sandrohc.schematic4j.SchematicFormat;

public class NoBuilderFoundException extends SchematicBuilderException {

    public final SchematicFormat format;
    
    public NoBuilderFoundException() {
        super("No suitable builder found");
        this.format = null;
    }

    public NoBuilderFoundException(SchematicFormat format) {
        super("No suitable builder found for format " + format);
        this.format = format;
    }

    public NoBuilderFoundException(SchematicFormat format, Throwable cause) {
        super("No suitable builder found for format " + format, cause);
        this.format = format;
    }

    public NoBuilderFoundException(Throwable cause) {
        super("No suitable builder found", cause);
        this.format = null;
    }
}
