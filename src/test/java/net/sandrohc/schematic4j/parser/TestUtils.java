package net.sandrohc.schematic4j.parser;

import java.io.InputStream;

import au.com.origin.snapshots.Expect;

import net.sandrohc.schematic4j.exception.ParsingException;
import net.sandrohc.schematic4j.nbt.io.NBTUtil;
import net.sandrohc.schematic4j.nbt.io.NamedTag;
import net.sandrohc.schematic4j.nbt.tag.CompoundTag;
import net.sandrohc.schematic4j.schematic.Schematic;

public class TestUtils {

	public static InputStream readResource(String file) {
		try {
			return TestUtils.class.getResourceAsStream(file);
		} catch (Exception e) {
			throw new RuntimeException("Failed to load resource: " + file, e);
		}
	}

	public static CompoundTag nbtFromResource(String file) {
		final InputStream resource = readResource(file);
		try {
			NamedTag nbt = NBTUtil.Reader.read().from(resource);
			return (CompoundTag) nbt.getTag();
		} catch (Exception e) {
			throw new RuntimeException("Failed to load resource into NBT: " + file, e);
		}
	}

	public static void assertSchematic(Expect expect, String file, Parser parser) throws ParsingException {
		final CompoundTag nbt = nbtFromResource(file);
		final Schematic schem = parser.parse(nbt);
		expect.toMatchSnapshot(schem);
	}
}
