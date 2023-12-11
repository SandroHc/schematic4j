package net.sandrohc.schematic4j.utils;

import java.util.Optional;

import net.sandrohc.schematic4j.exception.MissingFieldException;
import net.sandrohc.schematic4j.nbt.tag.ByteArrayTag;
import net.sandrohc.schematic4j.nbt.tag.ByteTag;
import net.sandrohc.schematic4j.nbt.tag.CompoundTag;
import net.sandrohc.schematic4j.nbt.tag.DoubleTag;
import net.sandrohc.schematic4j.nbt.tag.FloatTag;
import net.sandrohc.schematic4j.nbt.tag.IntArrayTag;
import net.sandrohc.schematic4j.nbt.tag.IntTag;
import net.sandrohc.schematic4j.nbt.tag.ListTag;
import net.sandrohc.schematic4j.nbt.tag.LongArrayTag;
import net.sandrohc.schematic4j.nbt.tag.LongTag;
import net.sandrohc.schematic4j.nbt.tag.NumberTag;
import net.sandrohc.schematic4j.nbt.tag.ShortTag;
import net.sandrohc.schematic4j.nbt.tag.StringTag;

public class TagUtils {

	public static Optional<Integer> getInt(CompoundTag tag, String key) {
		return Optional.ofNullable(tag.getIntTag(key)).map(NumberTag::asInt);
	}

	public static Optional<Short> getShort(CompoundTag tag, String key) {
		return Optional.ofNullable(tag.getShortTag(key)).map(NumberTag::asShort);
	}

	public static Optional<Byte> getByte(CompoundTag tag, String key) {
		return Optional.ofNullable(tag.getByteTag(key)).map(NumberTag::asByte);
	}

	public static Optional<Long> getLong(CompoundTag tag, String key) {
		return Optional.ofNullable(tag.getLongTag(key)).map(NumberTag::asLong);
	}

	public static Optional<Float> getFloat(CompoundTag tag, String key) {
		return Optional.ofNullable(tag.getFloatTag(key)).map(NumberTag::asFloat);
	}

	public static Optional<Double> getDouble(CompoundTag tag, String key) {
		return Optional.ofNullable(tag.getDoubleTag(key)).map(NumberTag::asDouble);
	}

	public static Optional<int[]> getIntArray(CompoundTag tag, String key) {
		return Optional.ofNullable(tag.getIntArrayTag(key)).map(IntArrayTag::getValue);
	}

	public static Optional<byte[]> getByteArray(CompoundTag tag, String key) {
		return Optional.ofNullable(tag.getByteArrayTag(key)).map(ByteArrayTag::getValue);
	}

	public static Optional<long[]> getLongArray(CompoundTag tag, String key) {
		return Optional.ofNullable(tag.getLongArrayTag(key)).map(LongArrayTag::getValue);
	}

	public static Optional<ListTag<FloatTag>> getFloatList(CompoundTag tag, String key) {
		return Optional.ofNullable(tag.getListTag(key)).map(ListTag::asFloatTagList);
	}

	public static Optional<ListTag<DoubleTag>> getDoubleList(CompoundTag tag, String key) {
		return Optional.ofNullable(tag.getListTag(key)).map(ListTag::asDoubleTagList);
	}

	public static Optional<ListTag<CompoundTag>> getCompoundList(CompoundTag tag, String key) {
		return Optional.ofNullable(tag.getListTag(key)).map(ListTag::asCompoundTagList);
	}

	public static Optional<String> getString(CompoundTag tag, String key) {
		return Optional.ofNullable(tag.getStringTag(key)).map(StringTag::getValue);
	}

	public static Optional<CompoundTag> getCompound(CompoundTag tag, String key) {
		return Optional.ofNullable(tag.getCompoundTag(key));
	}



	public static int getIntOrThrow(CompoundTag tag, String key) throws MissingFieldException {
		return getInt(tag, key).orElseThrow(() -> new MissingFieldException(tag, key, IntTag.class));
	}

	public static short getShortOrThrow(CompoundTag tag, String key) throws MissingFieldException {
		return getShort(tag, key).orElseThrow(() -> new MissingFieldException(tag, key, ShortTag.class));
	}

	public static byte getByteOrThrow(CompoundTag tag, String key) throws MissingFieldException {
		return getByte(tag, key).orElseThrow(() -> new MissingFieldException(tag, key, ByteTag.class));
	}

	public static long getLongOrThrow(CompoundTag tag, String key) throws MissingFieldException {
		return getLong(tag, key).orElseThrow(() -> new MissingFieldException(tag, key, LongTag.class));
	}

	public static float getFloatOrThrow(CompoundTag tag, String key) throws MissingFieldException {
		return getFloat(tag, key).orElseThrow(() -> new MissingFieldException(tag, key, FloatTag.class));
	}

	public static double getDoubleOrThrow(CompoundTag tag, String key) throws MissingFieldException {
		return getDouble(tag, key).orElseThrow(() -> new MissingFieldException(tag, key, DoubleTag.class));
	}

	public static int[] getIntArrayOrThrow(CompoundTag tag, String key) throws MissingFieldException {
		return getIntArray(tag, key).orElseThrow(() -> new MissingFieldException(tag, key, IntArrayTag.class));
	}

	public static byte[] getByteArrayOrThrow(CompoundTag tag, String key) throws MissingFieldException {
		return getByteArray(tag, key).orElseThrow(() -> new MissingFieldException(tag, key, ByteArrayTag.class));
	}

	public static long[] getLongArrayOrThrow(CompoundTag tag, String key) throws MissingFieldException {
		return getLongArray(tag, key).orElseThrow(() -> new MissingFieldException(tag, key, LongArrayTag.class));
	}

	public static ListTag<FloatTag> getFloatListOrThrow(CompoundTag tag, String key) throws MissingFieldException {
		return getFloatList(tag, key).orElseThrow(() -> new MissingFieldException(tag, key, ListTag.class));
	}

	public static ListTag<DoubleTag> getDoubleListOrThrow(CompoundTag tag, String key) throws MissingFieldException {
		return getDoubleList(tag, key).orElseThrow(() -> new MissingFieldException(tag, key, ListTag.class));
	}

	public static ListTag<CompoundTag> getCompoundListOrThrow(CompoundTag tag, String key) throws MissingFieldException {
		return getCompoundList(tag, key).orElseThrow(() -> new MissingFieldException(tag, key, ListTag.class));
	}

	public static String getStringOrThrow(CompoundTag tag, String key) throws MissingFieldException {
		return getString(tag, key).orElseThrow(() -> new MissingFieldException(tag, key, StringTag.class));
	}

	public static CompoundTag getCompoundOrThrow(CompoundTag tag, String key) throws MissingFieldException {
		return getCompound(tag, key).orElseThrow(() -> new MissingFieldException(tag, key, CompoundTag.class));
	}

}
