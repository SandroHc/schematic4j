package net.sandrohc.schematic4j.parser;

import java.io.InputStream;

import net.sandrohc.schematic4j.nbt.io.NBTUtil;
import net.sandrohc.schematic4j.nbt.io.NamedTag;

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
}
