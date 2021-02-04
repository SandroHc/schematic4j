package net.sandrohc.schematic4j.exception;

public class SchematicBuilderException extends IllegalArgumentException {

	public SchematicBuilderException() {
	}

	public SchematicBuilderException(String message) {
		super(message);
	}

	public SchematicBuilderException(String message, Throwable cause) {
		super(message, cause);
	}

	public SchematicBuilderException(Throwable cause) {
		super(cause);
	}
}
