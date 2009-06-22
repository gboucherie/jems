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
    protected static final byte C_FLAG = 0x01; // 1: Carry occured
    protected static final byte Z_FLAG = 0x02; // 1: Result is zero
    protected static final byte I_FLAG = 0x04; // 1: Interrupts disabled
    protected static final byte D_FLAG = 0x08; // 1: Decimal mode 
    protected static final byte B_FLAG = 0x10; // Break [0 on stk after int]
    protected static final byte V_FLAG = 0x40; // 1: Overflow occured
    protected static final byte N_FLAG = -1; // 0x80; // 1: Result is negative

    // =============================================================
    // Indicates which values are negative or zero
    // =============================================================
    protected static final byte[] ZNTABLE = {
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

    protected byte A = 0x00; // accumulator
    protected byte X = 0x00; // x register
    protected byte Y = 0x00; // y register
    protected byte SP = 0x00; // stack pointer
    protected byte SR = 0x00; // processor status register
    protected short PC = 0x0000; // program counter

    protected void step(byte opcode)
    {
        switch (opcode)
        {
            case   -9: Y = (byte) (Y - 1); setNZ(Y); break; // 0x88: DEY
            case  -73: Y = (byte) (Y + 1); setNZ(Y); break; // 0xC8: INY
            case  -75: X = (byte) (X - 1); setNZ(X); break; // 0xCA: DEX
            case -105: X = (byte) (X + 1); setNZ(X); break; // 0xE8: INX
            case -107: break;                               // 0xEA: NOP

            default:
                illegal(opcode, PC, CPUname);
                break;
        }
    }

    private void setNZ(byte value)
    {
        SR = (byte) ((SR & ~(Z_FLAG | N_FLAG)) | ZNTABLE[value * -1 + 127]);
    }

}
