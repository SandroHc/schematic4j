/* Vendored version of Quertz NBT 6.1 - https://github.com/Querz/NBT */
package net.sandrohc.schematic4j.nbt.tag;

/**
 * An end NBT tag. Used to represent the lack of value, like a {@code null}.
 */
public final class EndTag extends Tag<Void> {

	/**
	 * The end tag discriminator.
	 */
	public static final byte ID = 0;

	/**
	 * The default value.
	 */
	public static final EndTag INSTANCE = new EndTag();

	/**
	 * An end tag.
	 */
	private EndTag() {
		super(null);
	}

	@Override
	public byte getID() {
		return ID;
	}

	@Override
	protected Void checkValue(Void value) {
		return value;
	}

	@Override
	public String valueToString(int maxDepth) {
		return "\"end\"";
	}

	@Override
	public EndTag clone() {
		return INSTANCE;
	}
}
