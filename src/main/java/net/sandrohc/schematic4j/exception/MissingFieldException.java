package net.sandrohc.schematic4j.exception;

import net.sandrohc.schematic4j.nbt.tag.Tag;

public class MissingFieldException extends ParsingException {

	public final Tag<?> tag;
	public final String field;
	public final Class<?> fieldType;

	public MissingFieldException(Tag<?> tag, String field, Class<?> fieldType) {
		super("Tag is missing field '" + field + "' of type " + fieldType.getSimpleName());
		this.tag = tag;
		this.field = field;
		this.fieldType = fieldType;
	}

	public MissingFieldException(Tag<?> tag, String field, Class<?> fieldType, Throwable cause) {
		super("Tag is missing field '" + field + "' of type " + fieldType.getSimpleName(), cause);
		this.tag = tag;
		this.field = field;
		this.fieldType = fieldType;
	}

}
