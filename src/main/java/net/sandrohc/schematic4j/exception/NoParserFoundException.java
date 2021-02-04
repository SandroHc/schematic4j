package net.sandrohc.schematic4j.exception;

public class NoParserFoundException extends ParsingException {

	public NoParserFoundException() {
		super("No suitable parser found");
	}

	public NoParserFoundException(Throwable cause) {
		super("No suitable parser found", cause);
	}

	public NoParserFoundException(Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super("No suitable parser found", cause, enableSuppression, writableStackTrace);
	}
}
