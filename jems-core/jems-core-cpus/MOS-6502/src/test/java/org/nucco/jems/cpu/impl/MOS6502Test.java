package org.nucco.jems.cpu.impl;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.junit.Assert;
import org.junit.Test;

public class MOS6502Test extends MOS6502
{

    @Test
    public void testIllegal()
    {
        short opcode = 0x03;
        Assert.assertEquals(0, MOS6502.CYCLES[opcode]);
        MOS6502 cpu = new MOS6502();
        MOS6502State before = new MOS6502State(cpu);
        cpu.step(opcode);
        MOS6502State after = new MOS6502State(cpu);
        Assert.assertEquals(before, after);
    }

    @Test
    public void testNOP()
    {
        short opcode = 0xEA;
        Assert.assertEquals(2, MOS6502.CYCLES[opcode]);
        MOS6502 cpu = new MOS6502();
        MOS6502State before = new MOS6502State(cpu);
        cpu.step(opcode);
        MOS6502State after = new MOS6502State(cpu);
        Assert.assertEquals(before, after);
    }

    @Test
    public void testINX()
    {
        short opcode = 0xE8;
        Assert.assertEquals(2, MOS6502.CYCLES[opcode]);
        MOS6502 cpu = new MOS6502();
        MOS6502State expected = new MOS6502State((byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x00, (short) 0x0000);
        cpu.step(opcode);
        MOS6502State after = new MOS6502State(cpu);
        Assert.assertEquals(expected, after);

        // zero test
        expected = new MOS6502State((byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x02, (short) 0x0000);
        cpu = new MOS6502();
        cpu.x = 0xFF;
        cpu.step(opcode);
        after = new MOS6502State(cpu);
        Assert.assertEquals(expected, after);

        // negative test
        expected = new MOS6502State((short) 0x00, (short) 0x80, (short) 0x00, (short) 0x00, (short) 0x80, (int) 0x0000);
        cpu = new MOS6502();
        cpu.x = 0x7F;
        cpu.step(opcode);
        after = new MOS6502State(cpu);
        Assert.assertEquals(expected, after);
    }

    @Test
    public void testDEX()
    {
        short opcode = 0xCA;
        Assert.assertEquals(2, MOS6502.CYCLES[opcode]);
        MOS6502 cpu = new MOS6502();
        cpu.x = 0x02;
        MOS6502State expected = new MOS6502State((short) 0x00, (short) 0x01, (short) 0x00, (short) 0x00, (short) 0x00, (int) 0x0000);
        cpu.step(opcode);
        MOS6502State after = new MOS6502State(cpu);
        Assert.assertEquals(expected, after);

        // zero test
        expected = new MOS6502State((short) 0x00, (short) 0x00, (short) 0x00, (short) 0x00, (short) 0x02, (int) 0x0000);
        cpu = new MOS6502();
        cpu.x = 0x01;
        cpu.step(opcode);
        after = new MOS6502State(cpu);
        Assert.assertEquals(expected, after);

        // negative test
        expected = new MOS6502State((short) 0x00, (short) 0xFF, (short) 0x00, (short) 0x00, (short) 0x80, (int) 0x0000);
        cpu = new MOS6502();
        cpu.x = 0x00;
        cpu.step(opcode);
        after = new MOS6502State(cpu);
        Assert.assertEquals(expected, after);
    }

    @Test
    public void testINY()
    {
        short opcode = 0xC8;
        Assert.assertEquals(2, MOS6502.CYCLES[opcode]);
        MOS6502 cpu = new MOS6502();
        MOS6502State expected = new MOS6502State((short) 0x00, (short) 0x00, (short) 0x01, (short) 0x00, (short) 0x00, (int) 0x0000);
        cpu.step(opcode);
        MOS6502State after = new MOS6502State(cpu);
        Assert.assertEquals(expected, after);

        // zero test
        expected = new MOS6502State((short) 0x00, (short) 0x00, (short) 0x00, (short) 0x00, (short) 0x02, (int) 0x0000);
        cpu = new MOS6502();
        cpu.y = (byte) 0xFF;
        cpu.step(opcode);
        after = new MOS6502State(cpu);
        Assert.assertEquals(expected, after);

        // negative test
        expected = new MOS6502State((short) 0x00, (short) 0x00, (short) 0x80, (short) 0x00, (short) 0x80, (int) 0x0000);
        cpu = new MOS6502();
        cpu.y = 0x7F;
        cpu.step(opcode);
        after = new MOS6502State(cpu);
        Assert.assertEquals(expected, after);
    }

    @Test
    public void testDEY()
    {
        short opcode = 0x88;
        Assert.assertEquals(2, MOS6502.CYCLES[opcode]);
        MOS6502 cpu = new MOS6502();
        cpu.y = 0x02;
        MOS6502State expected = new MOS6502State((short) 0x00, (short) 0x00, (short) 0x01, (short) 0x00, (short) 0x00, (int) 0x0000);
        cpu.step(opcode);
        MOS6502State after = new MOS6502State(cpu);
        Assert.assertEquals(expected, after);

        // zero test
        expected = new MOS6502State((short) 0x00, (short) 0x00, (short) 0x00, (short) 0x00, (short) 0x02, (int) 0x0000);
        cpu = new MOS6502();
        cpu.y = 0x01;
        cpu.step(opcode);
        after = new MOS6502State(cpu);
        Assert.assertEquals(expected, after);

        // negative test
        expected = new MOS6502State((short) 0x00, (short) 0x00, (short) 0xFF, (short) 0x00, (short) 0x80, (int) 0x0000);
        cpu = new MOS6502();
        cpu.y = 0x00;
        cpu.step(opcode);
        after = new MOS6502State(cpu);
        Assert.assertEquals(expected, after);
    }

    private class MOS6502State
    {

        private short A = 0x00;
        private short X = 0x00;
        private short Y = 0x00;
        private short SP = 0x00;
        private short SR = 0x00;
        private int PC = 0x0000;

        public MOS6502State(MOS6502 cpu)
        {
            A = cpu.a;
            X = cpu.x;
            Y = cpu.y;
            SP = cpu.sp;
            SR = cpu.sr;
            PC = cpu.pc;
        }

        public MOS6502State(short A, short X, short Y, short SP, short SR, int PC)
        {
            this.A = A;
            this.X = X;
            this.Y = Y;
            this.SP = SP;
            this.SR = SR;
            this.PC = PC;
        }

        @Override
        public boolean equals(Object obj)
        {
            if (obj instanceof MOS6502State == false)
            {
                return false;
            }
            if (this == obj)
            {
                return true;
            }
            MOS6502State rhs = (MOS6502State) obj;
            return new EqualsBuilder()
                .append(A, rhs.A)
                .append(X, rhs.X)
                .append(Y, rhs.Y)
                .append(SP, rhs.SP)
                .append(SR, rhs.SR)
                .append(PC, rhs.PC)
                .isEquals();
        }

        @Override
        public String toString()
        {
            return new ToStringBuilder(this)
                .append(A)
                .append(X)
                .append(Y)
                .append(SP)
                .append(SR)
                .append(PC)
                .toString();
        }

    }
}
