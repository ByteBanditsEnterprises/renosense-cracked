//Raddon On Top!

package org.apache.commons.lang3;

import java.util.*;

public class EnumUtils
{
    private static final String NULL_ELEMENTS_NOT_PERMITTED = "null elements not permitted";
    private static final String CANNOT_STORE_S_S_VALUES_IN_S_BITS = "Cannot store %s %s values in %s bits";
    private static final String S_DOES_NOT_SEEM_TO_BE_AN_ENUM_TYPE = "%s does not seem to be an Enum type";
    private static final String ENUM_CLASS_MUST_BE_DEFINED = "EnumClass must be defined.";
    
    private static <E extends Enum<E>> Class<E> asEnum(final Class<E> enumClass) {
        Validate.notNull(enumClass, "EnumClass must be defined.", new Object[0]);
        Validate.isTrue(enumClass.isEnum(), "%s does not seem to be an Enum type", enumClass);
        return enumClass;
    }
    
    private static <E extends Enum<E>> Class<E> checkBitVectorable(final Class<E> enumClass) {
        final E[] constants = (E[])asEnum((Class<Enum>)enumClass).getEnumConstants();
        Validate.isTrue(constants.length <= 64, "Cannot store %s %s values in %s bits", constants.length, enumClass.getSimpleName(), 64);
        return enumClass;
    }
    
    @SafeVarargs
    public static <E extends Enum<E>> long generateBitVector(final Class<E> enumClass, final E... values) {
        Validate.noNullElements(values);
        return generateBitVector(enumClass, (Iterable<? extends E>)Arrays.asList(values));
    }
    
    public static <E extends Enum<E>> long generateBitVector(final Class<E> enumClass, final Iterable<? extends E> values) {
        checkBitVectorable(enumClass);
        Validate.notNull(values);
        long total = 0L;
        for (final E constant : values) {
            Validate.notNull(constant, "null elements not permitted", new Object[0]);
            total |= 1L << constant.ordinal();
        }
        return total;
    }
    
    @SafeVarargs
    public static <E extends Enum<E>> long[] generateBitVectors(final Class<E> enumClass, final E... values) {
        asEnum(enumClass);
        Validate.noNullElements(values);
        final EnumSet<E> condensed = EnumSet.noneOf(enumClass);
        Collections.addAll(condensed, values);
        final long[] result = new long[(enumClass.getEnumConstants().length - 1) / 64 + 1];
        for (final E value : condensed) {
            final long[] array = result;
            final int n = value.ordinal() / 64;
            array[n] |= 1L << value.ordinal() % 64;
        }
        ArrayUtils.reverse(result);
        return result;
    }
    
    public static <E extends Enum<E>> long[] generateBitVectors(final Class<E> enumClass, final Iterable<? extends E> values) {
        asEnum(enumClass);
        Validate.notNull(values);
        final EnumSet<E> condensed = EnumSet.noneOf(enumClass);
        for (final E constant : values) {
            Validate.notNull(constant, "null elements not permitted", new Object[0]);
            condensed.add(constant);
        }
        final long[] result = new long[(enumClass.getEnumConstants().length - 1) / 64 + 1];
        for (final E value : condensed) {
            final long[] array = result;
            final int n = value.ordinal() / 64;
            array[n] |= 1L << value.ordinal() % 64;
        }
        ArrayUtils.reverse(result);
        return result;
    }
    
    public static <E extends Enum<E>> E getEnum(final Class<E> enumClass, final String enumName) {
        return getEnum(enumClass, enumName, (E)null);
    }
    
    public static <E extends Enum<E>> E getEnum(final Class<E> enumClass, final String enumName, final E defaultEnum) {
        if (enumName == null) {
            return defaultEnum;
        }
        try {
            return Enum.valueOf(enumClass, enumName);
        }
        catch (IllegalArgumentException ex) {
            return defaultEnum;
        }
    }
    
    public static <E extends Enum<E>> E getEnumIgnoreCase(final Class<E> enumClass, final String enumName) {
        return getEnumIgnoreCase(enumClass, enumName, (E)null);
    }
    
    public static <E extends Enum<E>> E getEnumIgnoreCase(final Class<E> enumClass, final String enumName, final E defaultEnum) {
        if (enumName == null || !enumClass.isEnum()) {
            return defaultEnum;
        }
        for (final E each : enumClass.getEnumConstants()) {
            if (each.name().equalsIgnoreCase(enumName)) {
                return each;
            }
        }
        return defaultEnum;
    }
    
    public static <E extends Enum<E>> List<E> getEnumList(final Class<E> enumClass) {
        return new ArrayList<E>((Collection<? extends E>)Arrays.asList(enumClass.getEnumConstants()));
    }
    
    public static <E extends Enum<E>> Map<String, E> getEnumMap(final Class<E> enumClass) {
        final Map<String, E> map = new LinkedHashMap<String, E>();
        for (final E e : enumClass.getEnumConstants()) {
            map.put(e.name(), e);
        }
        return map;
    }
    
    public static <E extends Enum<E>> boolean isValidEnum(final Class<E> enumClass, final String enumName) {
        return getEnum(enumClass, enumName) != null;
    }
    
    public static <E extends Enum<E>> boolean isValidEnumIgnoreCase(final Class<E> enumClass, final String enumName) {
        return getEnumIgnoreCase(enumClass, enumName) != null;
    }
    
    public static <E extends Enum<E>> EnumSet<E> processBitVector(final Class<E> enumClass, final long value) {
        checkBitVectorable(enumClass).getEnumConstants();
        return processBitVectors(enumClass, value);
    }
    
    public static <E extends Enum<E>> EnumSet<E> processBitVectors(final Class<E> enumClass, final long... values) {
        final EnumSet<E> results = EnumSet.noneOf((Class<E>)asEnum((Class<E>)enumClass));
        final long[] lvalues = ArrayUtils.clone((long[])Validate.notNull(values));
        ArrayUtils.reverse(lvalues);
        for (final E constant : enumClass.getEnumConstants()) {
            final int block = constant.ordinal() / 64;
            if (block < lvalues.length && (lvalues[block] & 1L << constant.ordinal() % 64) != 0x0L) {
                results.add(constant);
            }
        }
        return results;
    }
}
