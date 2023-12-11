/* Vendored version of Quertz NBT 6.1 - https://github.com/Querz/NBT */
package net.sandrohc.schematic4j.nbt.tag;

public final class EndTag extends Tag<Void> {

	public static final byte ID = 0;
	public static final EndTag INSTANCE = new EndTag();

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
