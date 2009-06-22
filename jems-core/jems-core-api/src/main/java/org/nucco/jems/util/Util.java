package org.nucco.jems.util;

public class Util
{

    protected static final String HEX_CHARS = "0123456789ABCDEF";

    /**
     * Converts a byte to a Hexadecimal String.
     * 
     * @param value
     *            The byte to convert
     * @return The Hexadecimal String
     */
    public static String hex(byte value)
    {
        return "" + HEX_CHARS.charAt((value & 0xF0) >> 4) + HEX_CHARS.charAt(value & 0x0F);
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
        return hex((byte) (value >> 8)) + hex((byte) value);
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
        return hex((short) (value >> 16)) + hex((short) value);
    }

}
