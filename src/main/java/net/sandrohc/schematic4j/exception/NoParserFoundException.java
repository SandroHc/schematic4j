package net.sandrohc.schematic4j.exception;

import net.sandrohc.schematic4j.SchematicFormat;

public class NoParserFoundException extends ParsingException {

	public final SchematicFormat format;


	public NoParserFoundException() {
		super("No suitable parser found");
		this.format = null;
	}

	public NoParserFoundException(SchematicFormat format) {
		super("No suitable parser found for format " + format);
		this.format = format;
	}

	public NoParserFoundException(SchematicFormat format, Throwable cause) {
		super("No suitable parser found for format " + format, cause);
		this.format = format;
	}

	public NoParserFoundException(Throwable cause) {
		super("No suitable parser found", cause);
		this.format = null;
	}

	public NoParserFoundException(SchematicFormat format, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super("No suitable parser found for format " + format, cause, enableSuppression, writableStackTrace);
		this.format = format;
	}

}
