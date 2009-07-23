package org.nucco.jems.impl.cpu;

import org.nucco.jems.api.memory.Memory;
import org.nucco.jems.impl.cpu.AbstractCPU;
import org.nucco.jems.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MOS6502 extends AbstractCPU
{

    private static final Logger LOG = LoggerFactory.getLogger(MOS6502.class);

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
        0, 6, 0, 0, 3, 3, 3, 0, 2, 0, 2, 0, 4, 4, 4, 0, // 80 .. 8F
        0, 6, 0, 0, 4, 4, 4, 0, 2, 5, 0, 0, 0, 5, 0, 0, // 90 .. 9F
        2, 6, 2, 0, 3, 3, 3, 0, 2, 2, 2, 0, 4, 4, 4, 0, // A0 .. AF
        0, 5, 0, 0, 4, 4, 4, 0, 0, 4, 0, 0, 4, 4, 4, 0, // B0 .. BF
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
            case STA_IZX:
                memory.writeByte(indx(), a);
                break;
            case STY_ZP:
                memory.writeByte(fetch(), y);
                break;
            case STA_ZP:
                memory.writeByte(fetch(), a);
                break;
            case STX_ZP:
                memory.writeByte(fetch(), x);
                break;
            case DEY:
                y = (short) ((y - 1) & BYTE_MASK);
                setNZ(y);
                break;
            case TXA:
                a = x;
                setNZ(a);
                break;
            case STY_ABS:
                memory.writeByte(fetchShort(), y);
                break;
            case STA_ABS:
                memory.writeByte(fetchShort(), a);
                break;
            case STX_ABS:
                memory.writeByte(fetchShort(), x);
                break;
            case STA_IZY:
                memory.writeByte(indywr(), a);
                break;
            case STY_ZPX:
                memory.writeByte((fetch() + x) & BYTE_MASK, y);
                break;
            case STA_ZPX:
                memory.writeByte((fetch() + x) & BYTE_MASK, a);
                break;
            case STX_ZPY:
                memory.writeByte((fetch() + y) & BYTE_MASK, x);
                break;
            case TYA:
                a = y;
                setNZ(a);
                break;
            case STA_ABY:
                memory.writeByte((fetchShort() + y) & SHORT_MASK, a);
                break;
            case STA_ABX:
                memory.writeByte((fetchShort() + x) & SHORT_MASK, a);
                break;
            case LDY_IMM:
            	y = fetch();
            	setNZ(y);
            	break;
            case LDA_IZX:
                a = memory.readByte(indx());
                setNZ(a);
                break;
            case LDX_IMM:
            	x = fetch();
            	setNZ(x);
            	break;
            case LDY_ZP:
            	y = memory.readByte(fetch());
            	setNZ(y);
            	break;
            case LDA_ZP:
                a = memory.readByte(fetch());
                setNZ(a);
                break;
            case LDX_ZP:
            	x = memory.readByte(fetch());
            	setNZ(x);
            	break;
            case TAY:
                y = a;
                setNZ(y);
                break;
            case LDA_IMM:
                a = fetch();
                setNZ(a);
                break;
            case TAX:
                x = a;
                setNZ(x);
                break;
            case LDY_ABS:
            	y = memory.readByte(fetchShort());
            	setNZ(y);
                break;
            case LDA_ABS:
                a = memory.readByte(fetchShort());
                setNZ(a);
                break;
            case LDX_ABS:
            	x = memory.readByte(fetchShort());
            	setNZ(x);
            	break;
            case LDA_IZY:
                a = memory.readByte(indyrd());
                setNZ(a);
                break;
            case LDA_ABY:
                a = memory.readByte(absrd(y));
                setNZ(a);
                break;
            case LDY_ABX:
            	y = memory.readByte(absrd(x));
            	setNZ(y);
                break;
            case LDA_ABX:
                a = memory.readByte(absrd(x));
                setNZ(a);
                break;
            case LDX_ABY:
            	x = memory.readByte(absrd(y));
            	setNZ(x);
            	break;
            case LDY_ZPX:
            	y = memory.readByte((fetch() + x) & BYTE_MASK);
            	setNZ(y);
            	break;
            case LDA_ZPX:
                a = memory.readByte((fetch() + x) & BYTE_MASK);
                setNZ(a);
                break;
            case LDX_ZPY:
            	x = memory.readByte((fetch() + y) & BYTE_MASK);
            	setNZ(x);
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
//        LOG.debug(this.toString());

        short result = memory.readByte(pc);
        pc = (pc + 1) & SHORT_MASK;
        return result;
    }

    /*
     * (non-Javadoc) Read two byte in memory at the address indicate by the pc
     * and pc + 1 register and increment pc by 1 twice.
     * 
     * @return the bytes at the pc and pc + 1 address in memory
     */
    private int fetchShort()
    {
        return fetch() | fetch() << 8;
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

    /*
     * (non-Javadoc) Construct and absolute address with two first bytes in pc
     * and x to it. Add one cycle if page cross.
     * 
     * @param offset the test value
     * 
     * @return an address
     */
    private int absrd(short offset)
    {
        int address = fetchShort();
        int result = (address + offset) & SHORT_MASK;
        if ((address & 0xFF00) != (result & 0xFF00))
        {
            // TODO: add one cycle
        }

        return result;
    }

    /*
     * (non-Javadoc) Implement the (inderect,x) addressing mode of MOS-6502
     * processor.
     * 
     * @return an address
     */
    private int indx()
    {
        short zp = (short) ((fetch() + x) & BYTE_MASK);
        return memory.readByte(zp) | (memory.readByte(zp + 1) & BYTE_MASK) << 8;
    }

    /*
     * (non-Javadoc) Implement the (inderect,x) addressing mode of MOS-6502
     * processor. Add on cycle if page crossed.
     * 
     * @return an address
     */
    private int indyrd()
    {
        int zp = fetch();
        int address = memory.readByte(zp) | memory.readByte((zp + 1) & BYTE_MASK) << 8;
        zp = address & 0xFF00;
        address = (address + y) & SHORT_MASK;
        if ((address & 0xFF00) != zp)
        {
            // TODO: add one cycle
        }

        return address;
    }

    private int indywr()
    {
        int zp = fetch();
        int address = memory.readByte(zp) | memory.readByte((zp + 1) & BYTE_MASK) << 8;
        address = (address + y) & SHORT_MASK;

        return address;
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
    public void setA(short a)
    {
        this.a = a;
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

    @Override
    public String toString()
    {   
        StringBuilder result = new StringBuilder();
        result.append("\nMOS-6502 current state:\n");
        result.append("\taccumulator:\t\t" + Util.hex((byte) a) + "\n");
        result.append("\tx register:\t\t" + Util.hex((byte) x) + "\n");
        result.append("\ty register:\t\t" + Util.hex((byte) y) + "\n");
        result.append("\tstack pointer:\t\t" + Util.hex((byte) sp) + "\n");
        result.append("\tprocessor status:\t" + Util.hex((byte) sr) + "\n");
        result.append("\tprogram counter:\t" + Util.hex((short) pc) + "\n");

        return result.toString();
    }

    private static final short BRK = 0x00;
    private static final short STA_IZX = 0x81;
    private static final short STY_ZP = 0x84;
    private static final short STA_ZP = 0x85;
    private static final short STX_ZP = 0x86;
    private static final short DEY = 0x88;
    private static final short TXA = 0x8A;
    private static final short STY_ABS = 0x8C;
    private static final short STA_ABS = 0x8D;
    private static final short STX_ABS = 0x8E;
    private static final short STA_IZY = 0x91;
    private static final short STY_ZPX = 0x94;
    private static final short STA_ZPX = 0x95;
    private static final short STX_ZPY = 0x96;
    private static final short TYA = 0x98;
    private static final short STA_ABY = 0x99;
    private static final short STA_ABX = 0x9D;
    private static final short LDY_IMM = 0xA0;
    private static final short LDA_IZX = 0xA1;
    private static final short LDX_IMM = 0xA2;
    private static final short LDY_ZP = 0xA4;
    private static final short LDA_ZP = 0xA5;
    private static final short LDX_ZP = 0xA6;
    private static final short TAY = 0xA8;
    private static final short LDA_IMM = 0xA9;
    private static final short TAX = 0xAA;
    private static final short LDY_ABS = 0xAC;
    private static final short LDA_ABS = 0xAD;
    private static final short LDX_ABS = 0xAE;
    private static final short LDA_IZY = 0xB1;
    private static final short LDY_ZPX = 0xB4;
    private static final short LDA_ZPX = 0xB5;
    private static final short LDX_ZPY = 0xB6;
    private static final short LDA_ABY = 0xB9;
    private static final short LDY_ABX = 0xBC;
    private static final short LDA_ABX = 0xBD;
    private static final short LDX_ABY = 0xBE;
    private static final short INY = 0xC8;
    private static final short DEX = 0xCA;
    private static final short INX = 0xE8;
    private static final short NOP = 0xEA;

}
