package net.sandrohc.schematic4j.parser;

import java.io.InputStream;

import au.com.origin.snapshots.Expect;

import net.sandrohc.schematic4j.exception.ParsingException;
import net.sandrohc.schematic4j.nbt.io.NBTUtil;
import net.sandrohc.schematic4j.nbt.io.NamedTag;
import net.sandrohc.schematic4j.schematic.Schematic;

public class TestUtils {

	public static InputStream readResource(String file) {
		try {
			return TestUtils.class.getResourceAsStream(file);
		} catch (Exception e) {
			throw new RuntimeException("Failed to load resource: " + file, e);
		}
	}

	public static NamedTag nbtFromResource(String file) {
		final InputStream resource = readResource(file);
		try {
			return NBTUtil.Reader.read().from(resource);
		} catch (Exception e) {
			throw new RuntimeException("Failed to load resource into NBT: " + file, e);
		}
	}

	public static void assertSchematic(Expect expect, String file, Parser parser) throws ParsingException {
		final NamedTag nbt = nbtFromResource(file);
		final Schematic schem = parser.parse(nbt);
		expect.toMatchSnapshot(schem);
	}
}
