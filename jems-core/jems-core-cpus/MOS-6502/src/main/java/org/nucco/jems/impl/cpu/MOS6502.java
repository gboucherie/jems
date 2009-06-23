package org.nucco.jems.impl.cpu;

import org.nucco.jems.api.memory.Memory;
import org.nucco.jems.impl.cpu.AbstractCPU;

public class MOS6502 extends AbstractCPU
{

    // =============================================================
    // Timings for instructions. This is standard MC6502 T-States.
    // =============================================================
    protected static final byte[] CYCLES = {
        7, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, // 00 .. 0F
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, // 10 .. 1F
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, // 20 .. 2F
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, // 30 .. 3F
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, // 40 .. 4F
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, // 50 .. 5F
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, // 60 .. 6F
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, // 70 .. 7F
        0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, // 80 .. 8F
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, // 90 .. 9F
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, // A0 .. AF
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, // B0 .. BF
        0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 2, 0, 0, 0, 0, 0, // C0 .. CF
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, // D0 .. DF
        0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 2, 0, 0, 0, 0, 0, // E0 .. EF
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 // F0 .. FF
    // ,0, 1, 2, 3, 4, 5, 6, 7, 8, 9, A, B, C, D, E, F
    };

    // 6502 status flags:
    protected static final short C_FLAG = 0x01; // 1: Carry occured
    protected static final short Z_FLAG = 0x02; // 1: Result is zero
    protected static final short I_FLAG = 0x04; // 1: Interrupts disabled
    protected static final short D_FLAG = 0x08; // 1: Decimal mode
    protected static final short B_FLAG = 0x10; // Break [0 on stk after int]
    protected static final short V_FLAG = 0x40; // 1: Overflow occured
    protected static final short N_FLAG = 0x80; // 1: Result is negative

    // =============================================================
    // Indicates which values are negative or zero
    // =============================================================
    protected static final short[] ZNTABLE = {
        Z_FLAG, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, //
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, //
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, //
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, //
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, //
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, //
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, //
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, //
        N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, //
        N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, //
        N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, //
        N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, //
        N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, //
        N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, //
        N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, //
        N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG //
    };

    private String cpuName = "MOS-6502";

    private short a = 0xFF; // accumulator
    private short x = 0xFF; // x register
    private short y = 0xFF; // y register
    private short sp = 0xFF; // stack pointer
    private short sr = 0xFF; // processor status register
    private int pc = 0x0000; // program counter

    public MOS6502(Memory memory)
    {
        this.memory = memory;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.nucco.jems.api.cpu.CPU#step()
     */
    @Override
    public void step()
    {
        step(fetch());
    }

    /**
     * Execute action corresponding to the opcode value.
     * 
     * @param opcode
     *            the action to execute
     */
    private void step(short opcode)
    {
        switch (opcode)
        {
            case BRK:
                pushShort(pc);
                push((short) (sr | B_FLAG));
                sr = (short) (sr | I_FLAG);
                pc = readShort(0xFFFE);
                break;
            case DEY:
                y = (short) ((y - 1) & BYTE_MASK);
                setNZ(y);
                break;
            case INY:
                y = (short) ((y + 1) & BYTE_MASK);
                setNZ(y);
                break;
            case DEX:
                x = (short) ((x - 1) & BYTE_MASK);
                setNZ(x);
                break;
            case INX:
                x = (short) ((x + 1) & BYTE_MASK);
                setNZ(x);
                break;
            case NOP:
                break;

            default:
                illegal(opcode, pc, cpuName);
                break;
        }
    }

    /*
     * (non-Javadoc) Read byte in memory at the address indicate by the pc
     * register and increment pc by 1.
     * 
     * @return the byte at the pc address in memory
     */
    private short fetch()
    {
        short result = memory.readByte(pc);
        pc = (pc + 1) & SHORT_MASK;
        return result;
    }

    /*
     * (non-Javadoc) Push a 8bits value on the stack and decrement one the stack
     * register.
     * 
     * @param value the 8bits value to push on the stack
     */
    private void push(short value)
    {
        memory.writeByte(0x0100 + sp, value);
        sp = (short) ((sp - 1) & BYTE_MASK);
    }

    /*
     * (non-Javadoc) Push a 16bits value on the stack and decrement twice the
     * stack
     * 
     * @param value the 16bits value to push on the stack
     */
    private void pushShort(int value)
    {
        push((short) (value >> 8));
        push((short) value);
    }

    /*
     * (non-Javadoc) Set the negative or the zero flag if value is respectively
     * negative or zero value.
     * 
     * @param value the test value
     */
    private void setNZ(short value)
    {
        sr = (short) ((sr & ~(Z_FLAG | N_FLAG)) | ZNTABLE[value]);
    }

    private int readShort(int address)
    {
        return memory.readByte(address) | (memory.readByte((address + 1) & SHORT_MASK) << 8);
    }

    public short getA()
    {
        return a;
    }

    public short getX()
    {
        return x;
    }

    public short getY()
    {
        return y;
    }

    public short getSp()
    {
        return sp;
    }

    public short getSr()
    {
        return sr;
    }

    public int getPc()
    {
        return pc;
    }

    /*
     * (non-Javadoc) for unit test only
     */
    public void setX(short x)
    {
        this.x = x;
    }

    /*
     * (non-Javadoc) for unit test only
     */
    public void setY(short y)
    {
        this.y = y;
    }

    private static final short BRK = 0x00;
    private static final short DEY = 0x88;
    private static final short INY = 0xC8;
    private static final short DEX = 0xCA;
    private static final short INX = 0xE8;
    private static final short NOP = 0xEA;

}
