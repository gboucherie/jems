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
        7, 6, 0, 0, 0, 3, 0, 0, 3, 2, 0, 0, 0, 4, 0, 0, // 00 .. 0F
        0, 5, 0, 0, 0, 4, 0, 0, 0, 4, 0, 0, 0, 4, 0, 0, // 10 .. 1F
        6, 6, 0, 0, 3, 3, 0, 0, 4, 2, 0, 0, 4, 4, 0, 0, // 20 .. 2F
        0, 5, 0, 0, 0, 4, 0, 0, 0, 4, 0, 0, 0, 4, 0, 0, // 30 .. 3F
        0, 6, 0, 0, 0, 3, 0, 0, 3, 2, 0, 0, 3, 4, 0, 0, // 40 .. 4F
        0, 5, 0, 0, 0, 4, 0, 0, 0, 4, 0, 0, 0, 4, 0, 0, // 50 .. 5F
        6, 0, 0, 0, 0, 0, 0, 0, 4, 0, 0, 0, 5, 0, 0, 0, // 60 .. 6F
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, // 70 .. 7F
        0, 6, 0, 0, 3, 3, 3, 0, 2, 0, 2, 0, 4, 4, 4, 0, // 80 .. 8F
        0, 6, 0, 0, 4, 4, 4, 0, 2, 5, 2, 0, 0, 5, 0, 0, // 90 .. 9F
        2, 6, 2, 0, 3, 3, 3, 0, 2, 2, 2, 0, 4, 4, 4, 0, // A0 .. AF
        0, 5, 0, 0, 4, 4, 4, 0, 0, 4, 2, 0, 4, 4, 4, 0, // B0 .. BF
        0, 0, 0, 0, 0, 0, 5, 0, 2, 0, 2, 0, 0, 0, 6, 0, // C0 .. CF
        0, 0, 0, 0, 0, 0, 6, 0, 0, 0, 0, 0, 0, 0, 7, 0, // D0 .. DF
        0, 0, 0, 0, 0, 0, 5, 0, 2, 0, 2, 0, 0, 0, 6, 0, // E0 .. EF
        0, 0, 0, 0, 0, 0, 6, 0, 0, 0, 0, 0, 0, 0, 7, 0  // F0 .. FF
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

    protected static final int BIT_MASK = ~(N_FLAG | V_FLAG | Z_FLAG);

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

    private static final short DEFAULT_REGISTER_VALUE = 0xFF;
    private static final int DEFAULT_PC_VALUE = 0x0000;
    private static final int BREAK_ADDRRESS = 0xFFFE;
    private static final int STACK_ADDRESS = 0x0100;
    private static final byte SHIFT_8BITS = 8;
    private static final int PAGE_CROSS_TEST = 0xFF00;

    private short a = DEFAULT_REGISTER_VALUE; // accumulator
    private short x = DEFAULT_REGISTER_VALUE; // x register
    private short y = DEFAULT_REGISTER_VALUE; // y register
    private short sp = DEFAULT_REGISTER_VALUE; // stack pointer
    private short sr = DEFAULT_REGISTER_VALUE; // processor status register
    private int pc = DEFAULT_PC_VALUE; // program counter

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
        int address = 0;
        short value = 0;

        switch (opcode)
        {
            case BRK:
                pushShort(pc);
                push((short) (sr | B_FLAG));
                sr = (short) (sr | I_FLAG);
                pc = readShort(BREAK_ADDRRESS);
                break;
            case ORA_IZX:
                a |= memory.readByte(indx());
                setNZ(a);
                break;
            case ORA_ZP:
                a |= memory.readByte(fetch());
                setNZ(a);
                break;
            case PHP:
                push(sr);
                break;
            case ORA_IMM:
                a |= fetch();
                setNZ(a);
                break;
            case ORA_ABS:
                a |= memory.readByte(fetchShort());
                setNZ(a);
                break;
            case ORA_IZY:
                a |= memory.readByte(indyrd());
                setNZ(a);
                break;
            case ORA_ZPX:
                a |= memory.readByte(fetch() + x) & BYTE_MASK;
                setNZ(a);
                break;
            case ORA_ABY:
                a |= memory.readByte(absrd(y));
                setNZ(a);
                break;
            case ORA_ABX:
                a |= memory.readByte(absrd(x));
                setNZ(a);
                break;
            case JSR:
                address = fetchShort();
                pushShort((pc - 1) & SHORT_MASK);
                pc = address;
                break;
            case AND_IZX:
                a &= memory.readByte(indx());
                setNZ(a);
                break;
            case BIT_ZP:
                bit(fetch());
                break;
            case AND_ZP:
                a &= memory.readByte(fetch());
                setNZ(a);
                break;
            case PLP:
                sr = pop();
                break;
            case AND_IMM:
                a &= fetch();
                setNZ(a);
                break;
            case BIT_ABS:
                bit(fetchShort());
                break;
            case AND_ABS:
                a &= memory.readByte(fetchShort());
                setNZ(a);
                break;
            case AND_IZY:
                a &= memory.readByte(indyrd());
                setNZ(a);
                break;
            case AND_ZPX:
                a &= memory.readByte((fetch() + x) & BYTE_MASK);
                setNZ(a);
                break;
            case AND_ABY:
                a &= memory.readByte(absrd(y));
                setNZ(a);
                break;
            case AND_ABX:
                a &= memory.readByte(absrd(x));
                setNZ(a);
                break;
            case EOR_IZX:
                a ^= memory.readByte(indx());
                setNZ(a);
                break;
            case EOR_ZP:
                a ^= memory.readByte(fetch());
                setNZ(a);
                break;
            case PHA:
                push(a);
                break;
            case EOR_IMM:
                a ^= fetch();
                setNZ(a);
                break;
            case JMP_ABS:
                pc = fetchShort();
                break;
            case EOR_ABS:
                a ^= memory.readByte(fetchShort());
                setNZ(a);
                break;
            case EOR_IZY:
                a ^= memory.readByte(indyrd());
                setNZ(a);
                break;
            case EOR_ZPX:
                a ^= memory.readByte((fetch() + x) & BYTE_MASK);
                setNZ(a);
                break;
            case EOR_ABY:
                a ^= memory.readByte(absrd(y));
                setNZ(a);
                break;
            case EOR_ABX:
                a ^= memory.readByte(absrd(x));
                setNZ(a);
                break;
            case RTS:
                pc = (popShort() + 1) & SHORT_MASK;
                break;
            case PLA:
                a = pop();
                setNZ(a);
                break;
            case JMP_IND:
                address = fetchShort();
                pc = memory.readByte(address) | memory.readByte((address + 1) & SHORT_MASK) << SHIFT_8BITS;
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
            case TXS:
                sp = x;
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
            case TSX:
                x = sp;
                setNZ(x);
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
            case DEC_ZP:
                address = fetch();
                value = (short) ((memory.readByte(address) - 1) & BYTE_MASK);
                memory.writeByte(address, value);
                setNZ(value);
                break;
            case INY:
                y = (short) ((y + 1) & BYTE_MASK);
                setNZ(y);
                break;
            case DEX:
                x = (short) ((x - 1) & BYTE_MASK);
                setNZ(x);
                break;
            case DEC_ABS:
                address = fetchShort();
                value = (short) ((memory.readByte(address) - 1) & BYTE_MASK);
                memory.writeByte(address, value);
                setNZ(value);
                break;
            case DEC_ZPX:
                address = (fetch() + x) & BYTE_MASK;
                value = (short) ((memory.readByte(address) - 1) & BYTE_MASK);
                memory.writeByte(address, value);
                setNZ(value);
                break;
            case DEC_ABX:
                address = (fetchShort() + x) & SHORT_MASK;
                value = (short) ((memory.readByte(address) - 1) & BYTE_MASK);
                memory.writeByte(address, value);
                setNZ(value);
                break;
            case INC_ZP:
                address = fetch();
                value = (short) ((memory.readByte(address) + 1) & BYTE_MASK);
                memory.writeByte(address, value);
                setNZ(value);
                break;
            case INX:
                x = (short) ((x + 1) & BYTE_MASK);
                setNZ(x);
                break;
            case NOP:
                break;
            case INC_ABS:
                address = fetchShort();
                value = (short) ((memory.readByte(address) + 1) & BYTE_MASK);
                memory.writeByte(address, value);
                setNZ(value);
                break;
            case INC_ZPX:
                address = (fetch() + x) & BYTE_MASK;
                value = (short) ((memory.readByte(address) + 1) & BYTE_MASK);
                memory.writeByte(address, value);
                setNZ(value);
                break;
            case INC_ABX:
                address = (fetchShort() + x) & SHORT_MASK;
                value = (short) ((memory.readByte(address) + 1) & BYTE_MASK);
                memory.writeByte(address, value);
                setNZ(value);
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
        return fetch() | fetch() << SHIFT_8BITS;
    }

    /*
     * (non-Javadoc) Push a 8bits value on the stack and decrement one the stack
     * register.
     * 
     * @param value the 8bits value to push on the stack
     */
    private void push(short value)
    {
        memory.writeByte(STACK_ADDRESS + sp, value);
        sp = (short) ((sp - 1) & BYTE_MASK);
    }

    /*
     * (non-Javadoc) Push a 16bits value on the stack and decrement twice the
     * stack register.
     * 
     * @param value the 16bits value to push on the stack
     */
    private void pushShort(int value)
    {
        push((short) (value >> SHIFT_8BITS));
        push((short) value);
    }

    /*
     * (non-Javadoc) Pop a 8bits value from the stack and increment one the
     * stack register.
     */
    private short pop()
    {
        short value = memory.readByte(STACK_ADDRESS + sp);
        sp = (short) ((sp + 1) & BYTE_MASK);
        return value;
    }

    /*
     * (non-Javadoc) Pop a 16bits value from the stack and increment twice the
     * stack register.
     */
    private int popShort()
    {
        return pop() | pop() << SHIFT_8BITS;
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
     * and offset to it. Add one cycle if page cross.
     * 
     * @param offset the value to add at the address
     * 
     * @return an address
     */
    private int absrd(short offset)
    {
        int address = fetchShort();
        int result = (address + offset) & SHORT_MASK;
        if ((address & PAGE_CROSS_TEST) != (result & PAGE_CROSS_TEST))
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
        return memory.readByte(zp) | (memory.readByte(zp + 1) & BYTE_MASK) << SHIFT_8BITS;
    }

    /*
     * (non-Javadoc) Implement the (inderect),y addressing mode of MOS-6502
     * processor. Add on cycle if page crossed.
     * 
     * @return an address
     */
    private int indyrd()
    {
        int zp = fetch();
        int address = memory.readByte(zp) | memory.readByte((zp + 1) & BYTE_MASK) << SHIFT_8BITS;
        zp = address & PAGE_CROSS_TEST;
        address = (address + y) & SHORT_MASK;
        if ((address & PAGE_CROSS_TEST) != zp)
        {
            // TODO: add one cycle
        }

        return address;
    }

    /*
     * (non-Javadoc) Implement the (inderect),y addressing mode of MOS-6502
     * processor.
     * 
     * @return an address
     */
    private int indywr()
    {
        int zp = fetch();
        int address = memory.readByte(zp) | memory.readByte((zp + 1) & BYTE_MASK) << SHIFT_8BITS;
        address = (address + y) & SHORT_MASK;

        return address;
    }

    private int readShort(int address)
    {
        return memory.readByte(address) | (memory.readByte((address + 1) & SHORT_MASK) << SHIFT_8BITS);
    }

    private void bit(int address)
    {
        short value = memory.readByte(address);
        if ((a & value) == 0)
        {
            sr |= Z_FLAG;
        }
        sr |= (value & (N_FLAG | V_FLAG));
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

    /*
     * (non-Javadoc) for unit test only
     */
    public void setSP(short sp)
    {
        this.sp = sp;
    }

    /*
     * (non-Javadoc) for unit test only
     */
    public void setSR(short sr)
    {
        this.sr = sr;
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

    // Load/Store Operations
    private static final short LDA_IMM = 0xA9;
    private static final short LDA_ZP  = 0xA5;
    private static final short LDA_ZPX = 0xB5;
    private static final short LDA_ABS = 0xAD;
    private static final short LDA_ABX = 0xBD;
    private static final short LDA_ABY = 0xB9;
    private static final short LDA_IZX = 0xA1;
    private static final short LDA_IZY = 0xB1;

    private static final short LDX_IMM = 0xA2;
    private static final short LDX_ZP  = 0xA6;
    private static final short LDX_ZPY = 0xB6;
    private static final short LDX_ABS = 0xAE;
    private static final short LDX_ABY = 0xBE;

    private static final short LDY_IMM = 0xA0;
    private static final short LDY_ZP  = 0xA4;
    private static final short LDY_ZPX = 0xB4;
    private static final short LDY_ABS = 0xAC;
    private static final short LDY_ABX = 0xBC;

    private static final short STA_ZP  = 0x85;
    private static final short STA_ZPX = 0x95;
    private static final short STA_ABS = 0x8D;
    private static final short STA_ABX = 0x9D;
    private static final short STA_ABY = 0x99;
    private static final short STA_IZX = 0x81;
    private static final short STA_IZY = 0x91;

    private static final short STX_ZP  = 0x86;
    private static final short STX_ZPY = 0x96;
    private static final short STX_ABS = 0x8E;

    private static final short STY_ZP  = 0x84;
    private static final short STY_ZPX = 0x94;
    private static final short STY_ABS = 0x8C;


    // Register Transfers
    private static final short TAX = 0xAA;

    private static final short TAY = 0xA8;

    private static final short TXA = 0x8A;

    private static final short TYA = 0x98;


    // Stack Operations
    private static final short TSX = 0xBA;

    private static final short TXS = 0x9A;

    private static final short PHA = 0x48;

    private static final short PHP = 0x08;

    private static final short PLA = 0x68;

    private static final short PLP = 0x28;


    //Logical
    private static final short AND_IMM = 0x29;
    private static final short AND_ZP  = 0x25;
    private static final short AND_ZPX = 0x35;
    private static final short AND_ABS = 0x2D;
    private static final short AND_ABX = 0x3D;
    private static final short AND_ABY = 0x39;
    private static final short AND_IZX = 0x21;
    private static final short AND_IZY = 0x31;

    private static final short EOR_IMM = 0x49;
    private static final short EOR_ZP  = 0x45;
    private static final short EOR_ZPX = 0x55;
    private static final short EOR_ABS = 0x4D;
    private static final short EOR_ABX = 0x5D;
    private static final short EOR_ABY = 0x59;
    private static final short EOR_IZX = 0x41;
    private static final short EOR_IZY = 0x51;

    private static final short ORA_IMM = 0x09;
    private static final short ORA_ZP  = 0x05;
    private static final short ORA_ZPX = 0x15;
    private static final short ORA_ABS = 0x0D;
    private static final short ORA_ABX = 0x1D;
    private static final short ORA_ABY = 0x19;
    private static final short ORA_IZX = 0x01;
    private static final short ORA_IZY = 0x11;

    private static final short BIT_ZP  = 0x24;
    private static final short BIT_ABS = 0x2C;


    // Increments & Decrements
    private static final short INC_ZP  = 0xE6;
    private static final short INC_ZPX = 0xF6;
    private static final short INC_ABS = 0xEE;
    private static final short INC_ABX = 0xFE;

    private static final short INX = 0xE8;

    private static final short INY = 0xC8;

    private static final short DEC_ZP  = 0xC6;
    private static final short DEC_ZPX = 0xD6;
    private static final short DEC_ABS = 0xCE;
    private static final short DEC_ABX = 0xDE;

    private static final short DEX = 0xCA;

    private static final short DEY = 0x88;


    // Jumps & Calls
    private static final short JMP_ABS = 0x4C;
    private static final short JMP_IND = 0x6C;

    private static final short JSR = 0x20;

    private static final short RTS = 0x60;


    // System Functions
    private static final short BRK = 0x00;

    private static final short NOP = 0xEA;

}
