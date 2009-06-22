package org.nucco.jems.cpu.impl;

import org.nucco.jems.impl.cpu.Abstract8BitsCPU;


public class MOS6502 extends Abstract8BitsCPU
{

    // =============================================================
    // Timings for instructions. This is standard MC6502 T-States.
    // =============================================================
    protected static final byte[] CYCLES = {
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, // 00 .. 0F
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, // 10 .. 1F
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, // 00 .. 0F
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, // 00 .. 0F
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, // 00 .. 0F
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, // 00 .. 0F
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, // 00 .. 0F
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, // 00 .. 0F
        0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, // 80 .. 8F
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, // 90 .. 9F
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, // A0 .. AF
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, // B0 .. BF
        0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 2, 0, 0, 0, 0, 0, // C0 .. CF
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, // D0 .. DF
        0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 2, 0, 0, 0, 0, 0, // E0 .. EF
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 // F0 .. FF
     // 0  1  2  3  4  5  6  7  8  9  A  B  C  D  E  F
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
        Z_FLAG, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG,
        N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG,
        N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG,
        N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG,
        N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG,
        N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG,
        N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG,
        N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG, N_FLAG
    };

    private String CPUname = "MOS-6502";

    protected short A = 0x00; // accumulator
    protected short X = 0x00; // x register
    protected short Y = 0x00; // y register
    protected short SP = 0x00; // stack pointer
    protected short SR = 0x00; // processor status register
    protected int PC = 0x0000; // program counter

    protected void step(short opcode)
    {
        switch (opcode)
        {
            case 0x88: Y = (short) ((Y - 1) & 0xFF); setNZ(Y); break; // DEY
            case 0xC8: Y = (short) ((Y + 1) & 0xFF); setNZ(Y); break; // INY
            case 0xCA: X = (short) ((X - 1) & 0xFF); setNZ(X); break; // DEX
            case 0xE8: X = (short) ((X + 1) & 0xFF); setNZ(X); break; // INX
            case 0xEA: break;                                         // NOP

            default:
                illegal(opcode, PC, CPUname);
                break;
        }
    }

    private void setNZ(short value)
    {
        SR = (short) ((SR & ~(Z_FLAG | N_FLAG)) | ZNTABLE[value]);
    }

}
