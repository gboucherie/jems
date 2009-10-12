package org.nucco.jems.util;

public final class Util
{

    protected static final String HEX_CHARS = "0123456789ABCDEF";

    private static final short FIRST_HEXA = 0xF0;
    private static final short LAST_HEXA = 0x0F;

    private static final byte SHIFT_4BITS = 4;
    private static final byte SHIFT_8BITS = 8;
    private static final byte SHIFT_16BITS = 16;

    private Util()
    {
    }

    /**
     * Converts a byte to a Hexadecimal String.
     * 
     * @param value
     *            The byte to convert
     * @return The Hexadecimal String
     */
    public static String hex(byte value)
    {
        return "" + HEX_CHARS.charAt((value & FIRST_HEXA) >> SHIFT_4BITS) + HEX_CHARS.charAt(value & LAST_HEXA);
    }

    /**
     * Converts a short to a Hexadecimal String
     * 
     * @param value
     *            The short to convert
     * @return The Hexadecimal String
     */
    public static String hex(short value)
    {
        return hex((byte) (value >> SHIFT_8BITS)) + hex((byte) value);
    }

    /**
     * Converts an int to a Hexadecimal String
     * 
     * @param value
     *            The int to convert
     * @return The Hexadecimal String
     */
    public static String hex(int value)
    {
        return hex((short) (value >> SHIFT_16BITS)) + hex((short) value);
    }

}
