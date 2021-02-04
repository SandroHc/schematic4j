package net.sandrohc.schematic4j.schematic.types;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class SchematicWithBlockState extends SchematicNamed {

	public final String block;
	public final Map<String, String> states;

	public SchematicWithBlockState(String blockstate) {
		super(blockstate);

		int openingBracketPos = blockstate.indexOf('[');
		if (openingBracketPos != -1 && blockstate.endsWith("]")) {
			this.block = blockstate.substring(0, openingBracketPos);

			final String[] states = blockstate.substring(openingBracketPos + 1, blockstate.length() - 1).split(",");
			this.states = Collections.unmodifiableMap(Arrays.stream(states)
					.map(a -> a.split("=", 2))
					.collect(Collectors.toMap(
							a -> a[0], // state name
							a -> a[1]  // state value
					)));
		} else {
			this.block = blockstate;
			this.states = Collections.emptyMap();
		}
	}

	@Override
	public String toString() {
		return "SchematicWithBlockState(" + name + ", states=" + states + ')';
	}

}
