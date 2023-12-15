package net.sandrohc.schematic4j.schematic.types;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

import org.checkerframework.checker.nullness.qual.NonNull;

public class SchematicBlock extends SchematicNamed {

	public static final SchematicBlock AIR = new SchematicBlock("minecraft:air");

	public final @NonNull String block;
	public final @NonNull Map<String, String> states;

	public SchematicBlock(@NonNull String block, @NonNull Map<String, String> states) {
		super(blockNameAndStatesToString(block, states));
		this.block = block;
		this.states = states;
	}

	public SchematicBlock(String blockAndStates) {
		this(extractBlockName(blockAndStates), extractBlockStates(blockAndStates));
	}

	public @NonNull String block() {
		return block;
	}

	public @NonNull Map<String, String> states() {
		return states;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;

		SchematicBlock that = (SchematicBlock) o;

		if (!block.equals(that.block)) return false;
		return states.equals(that.states);
	}

	@Override
	public int hashCode() {
		int result = super.hashCode();
		result = 31 * result + block.hashCode();
		result = 31 * result + states.hashCode();
		return result;
	}

	public static String extractBlockName(String blockAndStates) {
		int openingBracketPos = blockAndStates.indexOf('[');
		char lastChar = blockAndStates.charAt(blockAndStates.length() - 1);
		if (openingBracketPos != -1 && lastChar == ']') {
			return blockAndStates.substring(0, openingBracketPos);
		} else {
			return blockAndStates;
		}
	}

	public static Map<String, String> extractBlockStates(String blockAndStates) {
		int openingBracketPos = blockAndStates.indexOf('[');
		char lastChar = blockAndStates.charAt(blockAndStates.length() - 1);
		if (openingBracketPos == -1 || lastChar != ']') {
			return Collections.emptyMap();
		}

		final Map<String, String> states = new TreeMap<>();

		final String[] statesRaw = blockAndStates.substring(openingBracketPos + 1, blockAndStates.length() - 1).split(",");
		for (String state : statesRaw) {
			int separatorIndex = state.indexOf('=');
			if (separatorIndex != -1) {
				String name = state.substring(0, separatorIndex);
				String value = state.substring(separatorIndex + 1);
				states.put(name, value);
			} else {
				states.put(state, "");
			}
		}

		return states;
	}

	public static String blockNameAndStatesToString(@NonNull String block, Map<String, String> states) {
		if (states.isEmpty()) {
			return block;
		}

		final StringBuilder statesStr = new StringBuilder();
		for (Map.Entry<String, String> entry : states.entrySet()) {
			if (statesStr.length() > 0) {
				statesStr.append(',');
			}
			statesStr.append(entry.getKey()).append('=').append(entry.getValue());
		}

		return block + '[' + statesStr + ']';
	}
}
