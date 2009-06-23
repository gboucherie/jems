package org.nucco.jems.api.memory;

public interface Memory
{

    /**
     * Read a byte at the 16bits address specified.
     * 
     * @param address
     *            the 16bits address where to read
     * @return the read byte
     */
    short readByte(int address);

    /**
     * Write a byte at the 16bits address specified.
     * 
     * @param address
     *            the 16bits address where to write
     * @param value
     *            the value to write
     */
    void writeByte(int address, short value);

}
