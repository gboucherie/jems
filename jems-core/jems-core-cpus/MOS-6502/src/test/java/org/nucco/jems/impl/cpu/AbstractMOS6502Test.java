package org.nucco.jems.impl.cpu;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.easymock.EasyMock;
import org.easymock.IMocksControl;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.nucco.jems.api.memory.Memory;

public abstract class AbstractMOS6502Test
{

    private IMocksControl control;
    protected Memory memory;
    protected MOS6502 cpu;

    @Before
    public void setUp()
    {
        control = EasyMock.createControl();
        memory = control.createMock(Memory.class);
        cpu = new MOS6502(memory);
    }

    @After
    public void tearDown()
    {
        control.reset();
    }

    protected void test_Immediate_read(short opcode, byte cycle, MOS6502State expected, short read)
    {
        EasyMock.expect(memory.readByte(0x0000)).andReturn(opcode);
        EasyMock.expect(memory.readByte(0x0001)).andReturn(read);
        test(opcode, cycle, expected);
    }

    protected void test_ZeroPage_read(short opcode, byte cycle, MOS6502State expected, short read)
    {
        EasyMock.expect(memory.readByte(0x0000)).andReturn(opcode);
        EasyMock.expect(memory.readByte(0x0001)).andReturn((short) 0x57);
        EasyMock.expect(memory.readByte(0x0057)).andReturn(read);
        test(opcode, cycle, expected);
    }

    protected void test_ZeroPageX_read(short opcode, byte cycle, MOS6502State expected, short read)
    {
        expected.X = (short) 0x08;
        cpu.setX((short) 0x08);
        EasyMock.expect(memory.readByte(0x0000)).andReturn(opcode);
        EasyMock.expect(memory.readByte(0x0001)).andReturn((short) 0xF0);
        EasyMock.expect(memory.readByte(0x00F8)).andReturn(read);
        test(opcode, cycle, expected);
    }

    protected void test_ZeroPageX_write(short opcode, byte cycle, MOS6502State expected, short write)
    {
        expected.X = (short) 0x08;
        cpu.setX((short) 0x08);
        EasyMock.expect(memory.readByte(0x0000)).andReturn(opcode);
        EasyMock.expect(memory.readByte(0x0001)).andReturn((short) 0xF0);
        memory.writeByte(0x00F8, write);
        test(opcode, cycle, expected);
    }

    protected void test_ZeroPageY_read(short opcode, byte cycle, MOS6502State expected, short read)
    {
        expected.Y = (short) 0x08;
        cpu.setY((short) 0x08);
        EasyMock.expect(memory.readByte(0x0000)).andReturn(opcode);
        EasyMock.expect(memory.readByte(0x0001)).andReturn((short) 0xF0);
        EasyMock.expect(memory.readByte(0x00F8)).andReturn(read);
        test(opcode, cycle, expected);
    }

    protected void test_ZeroPageY_write(short opcode, byte cycle, MOS6502State expected, short write)
    {
        expected.Y = (short) 0x08;
        cpu.setY((short) 0x08);
        EasyMock.expect(memory.readByte(0x0000)).andReturn(opcode);
        EasyMock.expect(memory.readByte(0x0001)).andReturn((short) 0xF0);
        memory.writeByte(0x00F8, write);
        test(opcode, cycle, expected);
    }

    protected void test_Absolute_read(short opcode, byte cycle, MOS6502State expected, short read)
    {
        EasyMock.expect(memory.readByte(0x0000)).andReturn(opcode);
        EasyMock.expect(memory.readByte(0x0001)).andReturn((short) 0xF0);
        EasyMock.expect(memory.readByte(0x0002)).andReturn((short) 0x57);
        EasyMock.expect(memory.readByte(0x57F0)).andReturn(read);
        test(opcode, cycle, expected);
    }

    protected void test_Absolute_write(short opcode, byte cycle, MOS6502State expected, short write)
    {
        EasyMock.expect(memory.readByte(0x0000)).andReturn(opcode);
        EasyMock.expect(memory.readByte(0x0001)).andReturn((short) 0xF0);
        EasyMock.expect(memory.readByte(0x0002)).andReturn((short) 0x57);
        memory.writeByte(0x57F0, write);
        test(opcode, cycle, expected);
    }

    protected void test_AbsoluteX_read(short opcode, byte cycle, MOS6502State expected, short read)
    {
        expected.X = (short) 0x08;
        cpu.setX((short) 0x08);
        EasyMock.expect(memory.readByte(0x0000)).andReturn(opcode);
        EasyMock.expect(memory.readByte(0x0001)).andReturn((short) 0xF0);
        EasyMock.expect(memory.readByte(0x0002)).andReturn((short) 0x57);
        EasyMock.expect(memory.readByte(0x57F8)).andReturn(read);
        test(opcode, cycle, expected);
    }

    protected void test_AbsoluteX_write(short opcode, byte cycle, MOS6502State expected, short write)
    {
        expected.X = (short) 0x08;
        cpu.setX((short) 0x08);
        EasyMock.expect(memory.readByte(0x0000)).andReturn(opcode);
        EasyMock.expect(memory.readByte(0x0001)).andReturn((short) 0xF0);
        EasyMock.expect(memory.readByte(0x0002)).andReturn((short) 0x57);
        memory.writeByte(0x57F8, write);
        test(opcode, cycle, expected);
    }

    protected void test_AbsoluteY_read(short opcode, byte cycle, MOS6502State expected, short read)
    {
        expected.Y = (short) 0x08;
        cpu.setY((short) 0x08);
        EasyMock.expect(memory.readByte(0x0000)).andReturn(opcode);
        EasyMock.expect(memory.readByte(0x0001)).andReturn((short) 0xF0);
        EasyMock.expect(memory.readByte(0x0002)).andReturn((short) 0x57);
        EasyMock.expect(memory.readByte(0x57F8)).andReturn(read);
        test(opcode, cycle, expected);
    }

    protected void test_AbsoluteY_write(short opcode, byte cycle, MOS6502State expected, short write)
    {
        expected.Y = (short) 0x08;
        cpu.setY((short) 0x08);
        EasyMock.expect(memory.readByte(0x0000)).andReturn(opcode);
        EasyMock.expect(memory.readByte(0x0001)).andReturn((short) 0xF0);
        EasyMock.expect(memory.readByte(0x0002)).andReturn((short) 0x57);
        memory.writeByte(0x57F8, write);
        test(opcode, cycle, expected);
    }

    protected void test_IndirectX_read(short opcode, byte cycle, MOS6502State expected, short read)
    {
        expected.X = (short) 0x08;
        cpu.setX((short) 0x08);
        EasyMock.expect(memory.readByte(0x0000)).andReturn(opcode);
        EasyMock.expect(memory.readByte(0x0001)).andReturn((short) 0x21);
        EasyMock.expect(memory.readByte(0x0029)).andReturn((short) 0x12);
        EasyMock.expect(memory.readByte(0x002A)).andReturn((short) 0x78);
        EasyMock.expect(memory.readByte(0x7812)).andReturn(read);
        test(opcode, cycle, expected);
    }

    protected void test_IndirectX_write(short opcode, byte cycle, MOS6502State expected, short write)
    {
        expected.X = (short) 0x08;
        cpu.setX((short) 0x08);
        EasyMock.expect(memory.readByte(0x0000)).andReturn(opcode);
        EasyMock.expect(memory.readByte(0x0001)).andReturn((short) 0x21);
        EasyMock.expect(memory.readByte(0x0029)).andReturn((short) 0x12);
        EasyMock.expect(memory.readByte(0x002A)).andReturn((short) 0x78);
        memory.writeByte(0x7812, write);
        test(opcode, cycle, expected);
    }

    protected void test_IndirectY_read(short opcode, byte cycle, MOS6502State expected, short read)
    {
        expected.Y = (short) 0x08;
        cpu.setY((short) 0x08);
        EasyMock.expect(memory.readByte(0x0000)).andReturn(opcode);
        EasyMock.expect(memory.readByte(0x0001)).andReturn((short) 0x21);
        EasyMock.expect(memory.readByte(0x0021)).andReturn((short) 0x12);
        EasyMock.expect(memory.readByte(0x0022)).andReturn((short) 0x78);
        EasyMock.expect(memory.readByte(0x781A)).andReturn(read);
        test(opcode, cycle, expected);
    }

    protected void test_IndirectY_write(short opcode, byte cycle, MOS6502State expected, short write)
    {
        expected.Y = (short) 0x08;
        cpu.setY((short) 0x08);
        EasyMock.expect(memory.readByte(0x0000)).andReturn(opcode);
        EasyMock.expect(memory.readByte(0x0001)).andReturn((short) 0x21);
        EasyMock.expect(memory.readByte(0x0021)).andReturn((short) 0x12);
        EasyMock.expect(memory.readByte(0x0022)).andReturn((short) 0x78);
        memory.writeByte(0x781A, write);
        test(opcode, cycle, expected);
    }

    protected void test(short opcode, byte cycle, MOS6502State expected)
    {
        Assert.assertEquals(cycle, MOS6502.CYCLES[opcode]);
        control.replay();
        cpu.step();
        control.verify();
        MOS6502State after = new MOS6502State(cpu);
        Assert.assertEquals(expected, after);
    }

    protected class MOS6502State
    {

        protected short A = 0x00;
        protected short X = 0x00;
        protected short Y = 0x00;
        protected short SP = 0x00;
        protected short SR = 0x00;
        protected int PC = 0x0000;

        public MOS6502State(MOS6502 cpu)
        {
            A = cpu.getA();
            X = cpu.getX();
            Y = cpu.getY();
            SP = cpu.getSp();
            SR = cpu.getSr();
            PC = cpu.getPc();
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
            return new EqualsBuilder().append(A, rhs.A).append(X, rhs.X).append(Y, rhs.Y).append(SP, rhs.SP).append(SR, rhs.SR).append(PC,
                    rhs.PC).isEquals();
        }

        @Override
        public String toString()
        {
            return new ToStringBuilder(this).append(A).append(X).append(Y).append(SP).append(SR).append(PC).toString();
        }

    }

}
