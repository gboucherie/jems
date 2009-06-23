package org.nucco.jems.impl.cpu;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nucco.jems.api.memory.Memory;
import org.nucco.jems.impl.cpu.MOS6502;

@RunWith(JMock.class)
public class MOS6502Test
{

    private Mockery context = new JUnit4Mockery();

    private MOS6502 cpu;
    private Memory memory;

    @Before
    public void setUp()
    {
        memory = context.mock(Memory.class);
        cpu = new MOS6502(memory);
    }

    @Test
    public void testIllegal()
    {
        Assert.assertEquals(0, MOS6502.CYCLES[0x03]);
        MOS6502State expected = new MOS6502State((short) 0xFF, (short) 0xFF, (short) 0xFF, (short) 0xFF, (short) 0xFF, (int) 0x0001);
        context.checking(new Expectations()
        {
            {
                oneOf(memory).readByte(0x0000);
                will(returnValue((short) 0x03));
            }
        });
        cpu.step();
        MOS6502State result = new MOS6502State(cpu);
        Assert.assertEquals(expected, result);
    }

    @Test
    public void testBRK()
    {
        Assert.assertEquals(7, MOS6502.CYCLES[0x00]);
        MOS6502State expected = new MOS6502State((short) 0xFF, (short) 0xFF, (short) 0xFF, (short) 0xFF, (short) 0xFF, (int) 0x0001);
        context.checking(new Expectations()
        {
            {
                oneOf(memory).readByte(0x0000);
                will(returnValue((short) 0x00));
                oneOf(memory).writeByte(0x01FF, (short) 0x00);
                oneOf(memory).writeByte(0x01FE, (short) 0x01);
                oneOf(memory).writeByte(0x01FD, (short) 0xFF);
            }
        });
        cpu.step();
        MOS6502State result = new MOS6502State(cpu);
        Assert.assertEquals(expected, result);
    }

    @Test
    public void testDEYPositive()
    {
        Assert.assertEquals(2, MOS6502.CYCLES[0x88]);
        MOS6502State expected = new MOS6502State((short) 0xFF, (short) 0xFF, (short) 0x01, (short) 0xFF, (short) 0x7D, (int) 0x0001);
        cpu.setY((short) 0x02);
        context.checking(new Expectations()
        {
            {
                oneOf(memory).readByte(0x0000);
                will(returnValue((short) 0x88));
            }
        });
        cpu.step();
        MOS6502State after = new MOS6502State(cpu);
        Assert.assertEquals(expected, after);
    }

    @Test
    public void testDEYZero()
    {
        Assert.assertEquals(2, MOS6502.CYCLES[0x88]);
        MOS6502State expected = new MOS6502State((short) 0xFF, (short) 0xFF, (short) 0x00, (short) 0xFF, (short) 0x7F, (int) 0x0001);
        cpu.setY((short) 0x01);
        context.checking(new Expectations()
        {
            {
                oneOf(memory).readByte(0x0000);
                will(returnValue((short) 0x88));
            }
        });
        cpu.step();
        MOS6502State after = new MOS6502State(cpu);
        Assert.assertEquals(expected, after);
    }

    @Test
    public void testDEYNegative()
    {
        Assert.assertEquals(2, MOS6502.CYCLES[0x88]);
        MOS6502State expected = new MOS6502State((short) 0xFF, (short) 0xFF, (short) 0xFF, (short) 0xFF, (short) 0xFD, (int) 0x0001);
        cpu.setY((short) 0x00);
        context.checking(new Expectations()
        {
            {
                oneOf(memory).readByte(0x0000);
                will(returnValue((short) 0x88));
            }
        });
        cpu.step();
        MOS6502State after = new MOS6502State(cpu);
        Assert.assertEquals(expected, after);
    }

    @Test
    public void testINXPositive()
    {
        Assert.assertEquals(2, MOS6502.CYCLES[0xE8]);
        MOS6502State expected = new MOS6502State((short) 0xFF, (short) 0x01, (short) 0xFF, (short) 0xFF, (short) 0x7D, (int) 0x0001);
        cpu.setX((short) 0x00);
        context.checking(new Expectations()
        {
            {
                oneOf(memory).readByte(0x0000);
                will(returnValue((short) 0xE8));
            }
        });
        cpu.step();
        MOS6502State after = new MOS6502State(cpu);
        Assert.assertEquals(expected, after);
    }

    @Test
    public void testINXZero()
    {
        Assert.assertEquals(2, MOS6502.CYCLES[0xE8]);
        MOS6502State expected = new MOS6502State((short) 0xFF, (short) 0x00, (short) 0xFF, (short) 0xFF, (short) 0x7F, (int) 0x0001);
        cpu.setX((short) 0xFF);
        context.checking(new Expectations()
        {
            {
                oneOf(memory).readByte(0x0000);
                will(returnValue((short) 0xE8));
            }
        });
        cpu.step();
        MOS6502State after = new MOS6502State(cpu);
        Assert.assertEquals(expected, after);
    }

    @Test
    public void testINXNegative()
    {
        Assert.assertEquals(2, MOS6502.CYCLES[0xE8]);
        MOS6502State expected = new MOS6502State((short) 0xFF, (short) 0x80, (short) 0xFF, (short) 0xFF, (short) 0xFD, (int) 0x0001);
        cpu.setX((short) 0x7F);
        context.checking(new Expectations()
        {
            {
                oneOf(memory).readByte(0x0000);
                will(returnValue((short) 0xE8));
            }
        });
        cpu.step();
        MOS6502State after = new MOS6502State(cpu);
        Assert.assertEquals(expected, after);
    }

    @Test
    public void testNOP()
    {
        Assert.assertEquals(2, MOS6502.CYCLES[0xEA]);
        MOS6502State before = new MOS6502State((short) 0xFF, (short) 0xFF, (short) 0xFF, (short) 0xFF, (short) 0xFF, (int) 0x0001);
        context.checking(new Expectations()
        {
            {
                oneOf(memory).readByte(0x0000);
                will(returnValue((short) 0xEA));
            }
        });
        cpu.step();
        MOS6502State after = new MOS6502State(cpu);
        Assert.assertEquals(before, after);
    }

    @Test
    public void testDEXPositive()
    {
        Assert.assertEquals(2, MOS6502.CYCLES[0xCA]);
        MOS6502State expected = new MOS6502State((short) 0xFF, (short) 0x01, (short) 0xFF, (short) 0xFF, (short) 0x7D, (int) 0x0001);
        cpu.setX( (short) 0x02);
        context.checking(new Expectations()
        {
            {
                oneOf(memory).readByte(0x0000);
                will(returnValue((short) 0xCA));
            }
        });
        cpu.step();
        MOS6502State after = new MOS6502State(cpu);
        Assert.assertEquals(expected, after);
    }

    @Test
    public void testDEXZero()
    {
        Assert.assertEquals(2, MOS6502.CYCLES[0xCA]);
        MOS6502State expected = new MOS6502State((short) 0xFF, (short) 0x00, (short) 0xFF, (short) 0xFF, (short) 0x7F, (int) 0x0001);
        cpu.setX((short) 0x01);
        context.checking(new Expectations()
        {
            {
                oneOf(memory).readByte(0x0000);
                will(returnValue((short) 0xCA));
            }
        });
        cpu.step();
        MOS6502State after = new MOS6502State(cpu);
        Assert.assertEquals(expected, after);
    }

    @Test
    public void testDEXNegative()
    {
        Assert.assertEquals(2, MOS6502.CYCLES[0xCA]);
        MOS6502State expected = new MOS6502State((short) 0xFF, (short) 0xFF, (short) 0xFF, (short) 0xFF, (short) 0xFD, (int) 0x0001);
        cpu.setX((short) 0x00);
        context.checking(new Expectations()
        {
            {
                oneOf(memory).readByte(0x0000);
                will(returnValue((short) 0xCA));
            }
        });
        cpu.step();
        MOS6502State after = new MOS6502State(cpu);
        Assert.assertEquals(expected, after);
    }

    @Test
    public void testINYPositive()
    {
        Assert.assertEquals(2, MOS6502.CYCLES[0xC8]);
        MOS6502State expected = new MOS6502State((short) 0xFF, (short) 0xFF, (short) 0x01, (short) 0xFF, (short) 0x7D, (int) 0x0001);
        cpu.setY((short) 0x00);
        context.checking(new Expectations()
        {
            {
                oneOf(memory).readByte(0x0000);
                will(returnValue((short) 0xC8));
            }
        });
        cpu.step();
        MOS6502State after = new MOS6502State(cpu);
        Assert.assertEquals(expected, after);
    }

    @Test
    public void testINYZero()
    {
        Assert.assertEquals(2, MOS6502.CYCLES[0xC8]);
        MOS6502State expected = new MOS6502State((short) 0xFF, (short) 0xFF, (short) 0x00, (short) 0xFF, (short) 0x7F, (int) 0x0001);
        cpu.setY((short) 0xFF);
        context.checking(new Expectations()
        {
            {
                oneOf(memory).readByte(0x0000);
                will(returnValue((short) 0xC8));
            }
        });
        cpu.step();
        MOS6502State after = new MOS6502State(cpu);
        Assert.assertEquals(expected, after);
    }

    @Test
    public void testINYNegative()
    {
        Assert.assertEquals(2, MOS6502.CYCLES[0xC8]);
        MOS6502State expected = new MOS6502State((short) 0xFF, (short) 0xFF, (short) 0x80, (short) 0xFF, (short) 0xFD, (int) 0x0001);
        cpu.setY((short) 0x7F);
        context.checking(new Expectations()
        {
            {
                oneOf(memory).readByte(0x0000);
                will(returnValue((short) 0xC8));
            }
        });
        cpu.step();
        MOS6502State after = new MOS6502State(cpu);
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
