package net.sandrohc.schematic4j.schematic.types;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

public abstract class SchematicWithBlockState extends SchematicNamed {

	public final String block;
	public final Map<String, String> states;

	public SchematicWithBlockState(String nameAndBlockstate) {
		super(nameAndBlockstate);

		// Replace with a streaming approach - iterate over each token
		int openingBracketPos = nameAndBlockstate.indexOf('[');
		char lastChar = nameAndBlockstate.charAt(nameAndBlockstate.length() - 1);
		if (openingBracketPos != -1 && lastChar == ']') {
			this.block = nameAndBlockstate.substring(0, openingBracketPos);
			this.states = new TreeMap<>();
			final String[] states = nameAndBlockstate.substring(openingBracketPos + 1, nameAndBlockstate.length() - 1).split(",");
			for (String state : states) {
				int separatorIndex = state.indexOf('=');
				if (separatorIndex != -1) {
					String name = state.substring(0, separatorIndex);
					String value = state.substring(separatorIndex + 1);
					this.states.put(name, value);
				} else {
					this.states.put(state, "");
				}
			}
		} else {
			this.block = nameAndBlockstate;
			this.states = Collections.emptyMap();
		}
	}

	public String block() {
		return block;
	}

	public Map<String, String> states() {
		return states;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + '[' + block + ", states=" + states + ']';
	}
}
