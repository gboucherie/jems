package org.nucco.jems.impl.cpu;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.easymock.EasyMock;
import org.easymock.IMocksControl;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.nucco.jems.api.memory.Memory;

public class MOS6502Test
{

    private IMocksControl control;
    private Memory memory;

    private MOS6502 cpu;

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

    @Test
    public void testIllegal()
    {
        Assert.assertEquals(0, MOS6502.CYCLES[0x03]);
        MOS6502State expected = new MOS6502State((short) 0xFF, (short) 0xFF, (short) 0xFF, (short) 0xFF, (short) 0xFF, (int) 0x0001);
        EasyMock.expect(memory.readByte(0x0000)).andReturn((short) 0x03);
        control.replay();
        cpu.step();
        control.verify();
        MOS6502State result = new MOS6502State(cpu);
        Assert.assertEquals(expected, result);
    }

    @Test
    public void testBRK()
    {
        Assert.assertEquals(7, MOS6502.CYCLES[0x00]);
        MOS6502State expected = new MOS6502State((short) 0xFF, (short) 0xFF, (short) 0xFF, (short) 0xFC, (short) 0xFF, (int) 0xA554);
        EasyMock.expect(memory.readByte(0x0000)).andReturn((short) 0x00);
        memory.writeByte(0x01FF, (short) 0x00);
        memory.writeByte(0x01FE, (short) 0x01);
        memory.writeByte(0x01FD, (short) 0xFF);
        EasyMock.expect(memory.readByte(0xFFFE)).andReturn((short) 0x54);
        EasyMock.expect(memory.readByte(0xFFFF)).andReturn((short) 0xA5);
        control.replay();
        cpu.step();
        control.verify();
        MOS6502State result = new MOS6502State(cpu);
        Assert.assertEquals(expected, result);
    }

    @Test
    public void testDEYPositive()
    {
        Assert.assertEquals(2, MOS6502.CYCLES[0x88]);
        MOS6502State expected = new MOS6502State((short) 0xFF, (short) 0xFF, (short) 0x01, (short) 0xFF, (short) 0x7D, (int) 0x0001);
        cpu.setY((short) 0x02);
        EasyMock.expect(memory.readByte(0x0000)).andReturn((short) 0x88);
        control.replay();
        cpu.step();
        control.verify();
        MOS6502State after = new MOS6502State(cpu);
        Assert.assertEquals(expected, after);
    }

    @Test
    public void testDEYZero()
    {
        Assert.assertEquals(2, MOS6502.CYCLES[0x88]);
        MOS6502State expected = new MOS6502State((short) 0xFF, (short) 0xFF, (short) 0x00, (short) 0xFF, (short) 0x7F, (int) 0x0001);
        cpu.setY((short) 0x01);
        EasyMock.expect(memory.readByte(0x0000)).andReturn((short) 0x88);
        control.replay();
        cpu.step();
        control.verify();
        MOS6502State after = new MOS6502State(cpu);
        Assert.assertEquals(expected, after);
    }

    @Test
    public void testDEYNegative()
    {
        Assert.assertEquals(2, MOS6502.CYCLES[0x88]);
        MOS6502State expected = new MOS6502State((short) 0xFF, (short) 0xFF, (short) 0xFF, (short) 0xFF, (short) 0xFD, (int) 0x0001);
        cpu.setY((short) 0x00);
        EasyMock.expect(memory.readByte(0x0000)).andReturn((short) 0x88);
        control.replay();
        cpu.step();
        control.verify();
        MOS6502State after = new MOS6502State(cpu);
        Assert.assertEquals(expected, after);
    }

    @Test
    public void test_LDA_IndirectX_Positive()
    {
        Assert.assertEquals(6, MOS6502.CYCLES[0xA1]);
        MOS6502State expected = new MOS6502State((short) 0x45, (short) 0x15, (short) 0xFF, (short) 0xFF, (short) 0x7D, (int) 0x0002);
        cpu.setX((short) 0x15);
        EasyMock.expect(memory.readByte(0x0000)).andReturn((short) 0xA1);
        EasyMock.expect(memory.readByte(0x0001)).andReturn((short) 0x21);
        EasyMock.expect(memory.readByte(0x0036)).andReturn((short) 0x12);
        EasyMock.expect(memory.readByte(0x0037)).andReturn((short) 0x78);
        EasyMock.expect(memory.readByte(0x7812)).andReturn((short) 0x45);
        control.replay();
        cpu.step();
        control.verify();
        MOS6502State after = new MOS6502State(cpu);
        Assert.assertEquals(expected, after);
    }

    @Test
    public void test_LDA_IndirectX_Zero()
    {
        Assert.assertEquals(6, MOS6502.CYCLES[0xA1]);
        MOS6502State expected = new MOS6502State((short) 0x00, (short) 0x15, (short) 0xFF, (short) 0xFF, (short) 0x7F, (int) 0x0002);
        cpu.setX((short) 0x15);
        EasyMock.expect(memory.readByte(0x0000)).andReturn((short) 0xA1);
        EasyMock.expect(memory.readByte(0x0001)).andReturn((short) 0x21);
        EasyMock.expect(memory.readByte(0x0036)).andReturn((short) 0x12);
        EasyMock.expect(memory.readByte(0x0037)).andReturn((short) 0x78);
        EasyMock.expect(memory.readByte(0x7812)).andReturn((short) 0x00);
        control.replay();
        cpu.step();
        control.verify();
        MOS6502State after = new MOS6502State(cpu);
        Assert.assertEquals(expected, after);
    }

    @Test
    public void test_LDA_IndirectX_Negative()
    {
        Assert.assertEquals(6, MOS6502.CYCLES[0xA1]);
        MOS6502State expected = new MOS6502State((short) 0xA1, (short) 0x15, (short) 0xFF, (short) 0xFF, (short) 0xFD, (int) 0x0002);
        cpu.setX((short) 0x15);
        EasyMock.expect(memory.readByte(0x0000)).andReturn((short) 0xA1);
        EasyMock.expect(memory.readByte(0x0001)).andReturn((short) 0x21);
        EasyMock.expect(memory.readByte(0x0036)).andReturn((short) 0x12);
        EasyMock.expect(memory.readByte(0x0037)).andReturn((short) 0x78);
        EasyMock.expect(memory.readByte(0x7812)).andReturn((short) 0xA1);
        control.replay();
        cpu.step();
        control.verify();
        MOS6502State after = new MOS6502State(cpu);
        Assert.assertEquals(expected, after);
    }

    @Test
    public void test_LDA_ZeroPage_Positive()
    {
        Assert.assertEquals(3, MOS6502.CYCLES[0xA5]);
        MOS6502State expected = new MOS6502State((short) 0x45, (short) 0xFF, (short) 0xFF, (short) 0xFF, (short) 0x7D, (int) 0x0002);
        EasyMock.expect(memory.readByte(0x0000)).andReturn((short) 0xA5);
        EasyMock.expect(memory.readByte(0x0001)).andReturn((short) 0x21);
        EasyMock.expect(memory.readByte(0x0021)).andReturn((short) 0x45);
        control.replay();
        cpu.step();
        control.verify();
        MOS6502State after = new MOS6502State(cpu);
        Assert.assertEquals(expected, after);
    }

    @Test
    public void test_LDA_ZeroPage_Zero()
    {
        Assert.assertEquals(3, MOS6502.CYCLES[0xA5]);
        MOS6502State expected = new MOS6502State((short) 0x00, (short) 0xFF, (short) 0xFF, (short) 0xFF, (short) 0x7F, (int) 0x0002);
        EasyMock.expect(memory.readByte(0x0000)).andReturn((short) 0xA5);
        EasyMock.expect(memory.readByte(0x0001)).andReturn((short) 0x21);
        EasyMock.expect(memory.readByte(0x0021)).andReturn((short) 0x00);
        control.replay();
        cpu.step();
        control.verify();
        MOS6502State after = new MOS6502State(cpu);
        Assert.assertEquals(expected, after);
    }

    @Test
    public void test_LDA_ZeroPage_Negative()
    {
        Assert.assertEquals(3, MOS6502.CYCLES[0xA5]);
        MOS6502State expected = new MOS6502State((short) 0xA1, (short) 0xFF, (short) 0xFF, (short) 0xFF, (short) 0xFD, (int) 0x0002);
        EasyMock.expect(memory.readByte(0x0000)).andReturn((short) 0xA5);
        EasyMock.expect(memory.readByte(0x0001)).andReturn((short) 0x21);
        EasyMock.expect(memory.readByte(0x0021)).andReturn((short) 0xA1);
        control.replay();
        cpu.step();
        control.verify();
        MOS6502State after = new MOS6502State(cpu);
        Assert.assertEquals(expected, after);
    }

    @Test
    public void test_LDA_Immediate_Positive()
    {
        Assert.assertEquals(2, MOS6502.CYCLES[0xA9]);
        MOS6502State expected = new MOS6502State((short) 0x45, (short) 0xFF, (short) 0xFF, (short) 0xFF, (short) 0x7D, (int) 0x0002);
        EasyMock.expect(memory.readByte(0x0000)).andReturn((short) 0xA9);
        EasyMock.expect(memory.readByte(0x0001)).andReturn((short) 0x45);
        control.replay();
        cpu.step();
        control.verify();
        MOS6502State after = new MOS6502State(cpu);
        Assert.assertEquals(expected, after);
    }

    @Test
    public void test_LDA_Immediate_Zero()
    {
        Assert.assertEquals(2, MOS6502.CYCLES[0xA9]);
        MOS6502State expected = new MOS6502State((short) 0x00, (short) 0xFF, (short) 0xFF, (short) 0xFF, (short) 0x7F, (int) 0x0002);
        EasyMock.expect(memory.readByte(0x0000)).andReturn((short) 0xA9);
        EasyMock.expect(memory.readByte(0x0001)).andReturn((short) 0x00);
        control.replay();
        cpu.step();
        control.verify();
        MOS6502State after = new MOS6502State(cpu);
        Assert.assertEquals(expected, after);
    }

    @Test
    public void test_LDA_Immediate_Negative()
    {
        Assert.assertEquals(2, MOS6502.CYCLES[0xA9]);
        MOS6502State expected = new MOS6502State((short) 0xA1, (short) 0xFF, (short) 0xFF, (short) 0xFF, (short) 0xFD, (int) 0x0002);
        EasyMock.expect(memory.readByte(0x0000)).andReturn((short) 0xA9);
        EasyMock.expect(memory.readByte(0x0001)).andReturn((short) 0xA1);
        control.replay();
        cpu.step();
        control.verify();
        MOS6502State after = new MOS6502State(cpu);
        Assert.assertEquals(expected, after);
    }

    @Test
    public void test_LDA_Absolute_Positive()
    {
        Assert.assertEquals(4, MOS6502.CYCLES[0xAD]);
        MOS6502State expected = new MOS6502State((short) 0x64, (short) 0xFF, (short) 0xFF, (short) 0xFF, (short) 0x7D, (int) 0x0003);
        EasyMock.expect(memory.readByte(0x0000)).andReturn((short) 0xAD);
        EasyMock.expect(memory.readByte(0x0001)).andReturn((short) 0x21);
        EasyMock.expect(memory.readByte(0x0002)).andReturn((short) 0x45);
        EasyMock.expect(memory.readByte(0x4521)).andReturn((short) 0x64);
        control.replay();
        cpu.step();
        control.verify();
        MOS6502State after = new MOS6502State(cpu);
        Assert.assertEquals(expected, after);
    }

    @Test
    public void test_LDA_Absolute_Zero()
    {
        Assert.assertEquals(4, MOS6502.CYCLES[0xAD]);
        MOS6502State expected = new MOS6502State((short) 0x00, (short) 0xFF, (short) 0xFF, (short) 0xFF, (short) 0x7F, (int) 0x0003);
        EasyMock.expect(memory.readByte(0x0000)).andReturn((short) 0xAD);
        EasyMock.expect(memory.readByte(0x0001)).andReturn((short) 0x21);
        EasyMock.expect(memory.readByte(0x0002)).andReturn((short) 0x45);
        EasyMock.expect(memory.readByte(0x4521)).andReturn((short) 0x00);
        control.replay();
        cpu.step();
        control.verify();
        MOS6502State after = new MOS6502State(cpu);
        Assert.assertEquals(expected, after);
    }

    @Test
    public void test_LDA_Absolute_Negative()
    {
        Assert.assertEquals(4, MOS6502.CYCLES[0xAD]);
        MOS6502State expected = new MOS6502State((short) 0xA1, (short) 0xFF, (short) 0xFF, (short) 0xFF, (short) 0xFD, (int) 0x0003);
        EasyMock.expect(memory.readByte(0x0000)).andReturn((short) 0xAD);
        EasyMock.expect(memory.readByte(0x0001)).andReturn((short) 0x21);
        EasyMock.expect(memory.readByte(0x0002)).andReturn((short) 0x45);
        EasyMock.expect(memory.readByte(0x4521)).andReturn((short) 0xA1);
        control.replay();
        cpu.step();
        control.verify();
        MOS6502State after = new MOS6502State(cpu);
        Assert.assertEquals(expected, after);
    }

    @Test
    public void test_LDA_IndirectY_Positive()
    {
        Assert.assertEquals(5, MOS6502.CYCLES[0xB1]);
        MOS6502State expected = new MOS6502State((short) 0x45, (short) 0xFF, (short) 0x15, (short) 0xFF, (short) 0x7D, (int) 0x0002);
        cpu.setY((short) 0x15);
        EasyMock.expect(memory.readByte(0x0000)).andReturn((short) 0xB1);
        EasyMock.expect(memory.readByte(0x0001)).andReturn((short) 0x21);
        EasyMock.expect(memory.readByte(0x0021)).andReturn((short) 0x12);
        EasyMock.expect(memory.readByte(0x0022)).andReturn((short) 0x78);
        EasyMock.expect(memory.readByte(0x7827)).andReturn((short) 0x45);
        control.replay();
        cpu.step();
        control.verify();
        MOS6502State after = new MOS6502State(cpu);
        Assert.assertEquals(expected, after);
    }

    @Test
    public void test_LDA_IndirectY_Zero()
    {
        Assert.assertEquals(5, MOS6502.CYCLES[0xB1]);
        MOS6502State expected = new MOS6502State((short) 0x00, (short) 0xFF, (short) 0x15, (short) 0xFF, (short) 0x7F, (int) 0x0002);
        cpu.setY((short) 0x15);
        EasyMock.expect(memory.readByte(0x0000)).andReturn((short) 0xB1);
        EasyMock.expect(memory.readByte(0x0001)).andReturn((short) 0x21);
        EasyMock.expect(memory.readByte(0x0021)).andReturn((short) 0x12);
        EasyMock.expect(memory.readByte(0x0022)).andReturn((short) 0x78);
        EasyMock.expect(memory.readByte(0x7827)).andReturn((short) 0x00);
        control.replay();
        cpu.step();
        control.verify();
        MOS6502State after = new MOS6502State(cpu);
        Assert.assertEquals(expected, after);
    }

    @Test
    public void test_LDA_IndirectY_Negative()
    {
        Assert.assertEquals(5, MOS6502.CYCLES[0xB1]);
        MOS6502State expected = new MOS6502State((short) 0xA1, (short) 0xFF, (short) 0x15, (short) 0xFF, (short) 0xFD, (int) 0x0002);
        cpu.setY((short) 0x15);
        EasyMock.expect(memory.readByte(0x0000)).andReturn((short) 0xB1);
        EasyMock.expect(memory.readByte(0x0001)).andReturn((short) 0x21);
        EasyMock.expect(memory.readByte(0x0021)).andReturn((short) 0x12);
        EasyMock.expect(memory.readByte(0x0022)).andReturn((short) 0x78);
        EasyMock.expect(memory.readByte(0x7827)).andReturn((short) 0xA1);
        control.replay();
        cpu.step();
        control.verify();
        MOS6502State after = new MOS6502State(cpu);
        Assert.assertEquals(expected, after);
    }

    @Test
    public void test_LDA_ZeroPageX_Positive()
    {
        Assert.assertEquals(4, MOS6502.CYCLES[0xB5]);
        MOS6502State expected = new MOS6502State((short) 0x45, (short) 0x13, (short) 0xFF, (short) 0xFF, (short) 0x7D, (int) 0x0002);
        cpu.setX((short) 0x13);
        EasyMock.expect(memory.readByte(0x0000)).andReturn((short) 0xB5);
        EasyMock.expect(memory.readByte(0x0001)).andReturn((short) 0x21);
        EasyMock.expect(memory.readByte(0x0034)).andReturn((short) 0x45);
        control.replay();
        cpu.step();
        control.verify();
        MOS6502State after = new MOS6502State(cpu);
        Assert.assertEquals(expected, after);
    }

    @Test
    public void test_LDA_ZeroPageX_Zero()
    {
        Assert.assertEquals(4, MOS6502.CYCLES[0xB5]);
        MOS6502State expected = new MOS6502State((short) 0x00, (short) 0x13, (short) 0xFF, (short) 0xFF, (short) 0x7F, (int) 0x0002);
        cpu.setX((short) 0x13);
        EasyMock.expect(memory.readByte(0x0000)).andReturn((short) 0xB5);
        EasyMock.expect(memory.readByte(0x0001)).andReturn((short) 0x21);
        EasyMock.expect(memory.readByte(0x0034)).andReturn((short) 0x00);
        control.replay();
        cpu.step();
        control.verify();
        MOS6502State after = new MOS6502State(cpu);
        Assert.assertEquals(expected, after);
    }

    @Test
    public void test_LDA_ZeroPageX_Negative()
    {
        Assert.assertEquals(4, MOS6502.CYCLES[0xB5]);
        MOS6502State expected = new MOS6502State((short) 0xA1, (short) 0x13, (short) 0xFF, (short) 0xFF, (short) 0xFD, (int) 0x0002);
        cpu.setX((short) 0x13);
        EasyMock.expect(memory.readByte(0x0000)).andReturn((short) 0xB5);
        EasyMock.expect(memory.readByte(0x0001)).andReturn((short) 0x21);
        EasyMock.expect(memory.readByte(0x0034)).andReturn((short) 0xA1);
        control.replay();
        cpu.step();
        control.verify();
        MOS6502State after = new MOS6502State(cpu);
        Assert.assertEquals(expected, after);
    }

    @Test
    public void test_LDA_AbsoluteY_Positive()
    {
        Assert.assertEquals(4, MOS6502.CYCLES[0xB9]);
        MOS6502State expected = new MOS6502State((short) 0x45, (short) 0xFF, (short) 0x13, (short) 0xFF, (short) 0x7D, (int) 0x0003);
        cpu.setY((short) 0x13);
        EasyMock.expect(memory.readByte(0x0000)).andReturn((short) 0xB9);
        EasyMock.expect(memory.readByte(0x0001)).andReturn((short) 0x21);
        EasyMock.expect(memory.readByte(0x0002)).andReturn((short) 0x78);
        EasyMock.expect(memory.readByte(0x7834)).andReturn((short) 0x45);
        control.replay();
        cpu.step();
        control.verify();
        MOS6502State after = new MOS6502State(cpu);
        Assert.assertEquals(expected, after);
    }

    @Test
    public void test_LDA_AbsoluteY_Zero()
    {
        Assert.assertEquals(4, MOS6502.CYCLES[0xB9]);
        MOS6502State expected = new MOS6502State((short) 0x00, (short) 0xFF, (short) 0x13, (short) 0xFF, (short) 0x7F, (int) 0x0003);
        cpu.setY((short) 0x13);
        EasyMock.expect(memory.readByte(0x0000)).andReturn((short) 0xB9);
        EasyMock.expect(memory.readByte(0x0001)).andReturn((short) 0x21);
        EasyMock.expect(memory.readByte(0x0002)).andReturn((short) 0x78);
        EasyMock.expect(memory.readByte(0x7834)).andReturn((short) 0x00);
        control.replay();
        cpu.step();
        control.verify();
        MOS6502State after = new MOS6502State(cpu);
        Assert.assertEquals(expected, after);
    }

    @Test
    public void test_LDA_AbsoluteY_Negative()
    {
        Assert.assertEquals(4, MOS6502.CYCLES[0xB9]);
        MOS6502State expected = new MOS6502State((short) 0xA1, (short) 0xFF, (short) 0x13, (short) 0xFF, (short) 0xFD, (int) 0x0003);
        cpu.setY((short) 0x13);
        EasyMock.expect(memory.readByte(0x0000)).andReturn((short) 0xB9);
        EasyMock.expect(memory.readByte(0x0001)).andReturn((short) 0x21);
        EasyMock.expect(memory.readByte(0x0002)).andReturn((short) 0x78);
        EasyMock.expect(memory.readByte(0x7834)).andReturn((short) 0xA1);
        control.replay();
        cpu.step();
        control.verify();
        MOS6502State after = new MOS6502State(cpu);
        Assert.assertEquals(expected, after);
    }

    @Test
    public void test_LDA_AbsoluteX_Positive()
    {
        Assert.assertEquals(4, MOS6502.CYCLES[0xBD]);
        MOS6502State expected = new MOS6502State((short) 0x45, (short) 0x13, (short) 0xFF, (short) 0xFF, (short) 0x7D, (int) 0x0003);
        cpu.setX((short) 0x13);
        EasyMock.expect(memory.readByte(0x0000)).andReturn((short) 0xBD);
        EasyMock.expect(memory.readByte(0x0001)).andReturn((short) 0x21);
        EasyMock.expect(memory.readByte(0x0002)).andReturn((short) 0x78);
        EasyMock.expect(memory.readByte(0x7834)).andReturn((short) 0x45);
        control.replay();
        cpu.step();
        control.verify();
        MOS6502State after = new MOS6502State(cpu);
        Assert.assertEquals(expected, after);
    }

    @Test
    public void test_LDA_AbsoluteX_Zero()
    {
        Assert.assertEquals(4, MOS6502.CYCLES[0xBD]);
        MOS6502State expected = new MOS6502State((short) 0x00, (short) 0x13, (short) 0xFF, (short) 0xFF, (short) 0x7F, (int) 0x0003);
        cpu.setX((short) 0x13);
        EasyMock.expect(memory.readByte(0x0000)).andReturn((short) 0xBD);
        EasyMock.expect(memory.readByte(0x0001)).andReturn((short) 0x21);
        EasyMock.expect(memory.readByte(0x0002)).andReturn((short) 0x78);
        EasyMock.expect(memory.readByte(0x7834)).andReturn((short) 0x00);
        control.replay();
        cpu.step();
        control.verify();
        MOS6502State after = new MOS6502State(cpu);
        Assert.assertEquals(expected, after);
    }

    @Test
    public void test_LDA_AbsoluteX_Negative()
    {
        Assert.assertEquals(4, MOS6502.CYCLES[0xBD]);
        MOS6502State expected = new MOS6502State((short) 0xA1, (short) 0x13, (short) 0xFF, (short) 0xFF, (short) 0xFD, (int) 0x0003);
        cpu.setX((short) 0x13);
        EasyMock.expect(memory.readByte(0x0000)).andReturn((short) 0xBD);
        EasyMock.expect(memory.readByte(0x0001)).andReturn((short) 0x21);
        EasyMock.expect(memory.readByte(0x0002)).andReturn((short) 0x78);
        EasyMock.expect(memory.readByte(0x7834)).andReturn((short) 0xA1);
        control.replay();
        cpu.step();
        control.verify();
        MOS6502State after = new MOS6502State(cpu);
        Assert.assertEquals(expected, after);
    }

    @Test
    public void testINXPositive()
    {
        Assert.assertEquals(2, MOS6502.CYCLES[0xE8]);
        MOS6502State expected = new MOS6502State((short) 0xFF, (short) 0x01, (short) 0xFF, (short) 0xFF, (short) 0x7D, (int) 0x0001);
        cpu.setX((short) 0x00);
        EasyMock.expect(memory.readByte(0x0000)).andReturn((short) 0xE8);
        control.replay();
        cpu.step();
        control.verify();
        MOS6502State after = new MOS6502State(cpu);
        Assert.assertEquals(expected, after);
    }

    @Test
    public void testINXZero()
    {
        Assert.assertEquals(2, MOS6502.CYCLES[0xE8]);
        MOS6502State expected = new MOS6502State((short) 0xFF, (short) 0x00, (short) 0xFF, (short) 0xFF, (short) 0x7F, (int) 0x0001);
        cpu.setX((short) 0xFF);
        EasyMock.expect(memory.readByte(0x0000)).andReturn((short) 0xE8);
        control.replay();
        cpu.step();
        control.verify();
        MOS6502State after = new MOS6502State(cpu);
        Assert.assertEquals(expected, after);
    }

    @Test
    public void testINXNegative()
    {
        Assert.assertEquals(2, MOS6502.CYCLES[0xE8]);
        MOS6502State expected = new MOS6502State((short) 0xFF, (short) 0x80, (short) 0xFF, (short) 0xFF, (short) 0xFD, (int) 0x0001);
        cpu.setX((short) 0x7F);
        EasyMock.expect(memory.readByte(0x0000)).andReturn((short) 0xE8);
        control.replay();
        cpu.step();
        control.verify();
        MOS6502State after = new MOS6502State(cpu);
        Assert.assertEquals(expected, after);
    }

    @Test
    public void testNOP()
    {
        Assert.assertEquals(2, MOS6502.CYCLES[0xEA]);
        MOS6502State before = new MOS6502State((short) 0xFF, (short) 0xFF, (short) 0xFF, (short) 0xFF, (short) 0xFF, (int) 0x0001);
        EasyMock.expect(memory.readByte(0x0000)).andReturn((short) 0xEA);
        control.replay();
        cpu.step();
        control.verify();
        MOS6502State after = new MOS6502State(cpu);
        Assert.assertEquals(before, after);
    }

    @Test
    public void testDEXPositive()
    {
        Assert.assertEquals(2, MOS6502.CYCLES[0xCA]);
        MOS6502State expected = new MOS6502State((short) 0xFF, (short) 0x01, (short) 0xFF, (short) 0xFF, (short) 0x7D, (int) 0x0001);
        cpu.setX((short) 0x02);
        EasyMock.expect(memory.readByte(0x0000)).andReturn((short) 0xCA);
        control.replay();
        cpu.step();
        control.verify();
        MOS6502State after = new MOS6502State(cpu);
        Assert.assertEquals(expected, after);
    }

    @Test
    public void testDEXZero()
    {
        Assert.assertEquals(2, MOS6502.CYCLES[0xCA]);
        MOS6502State expected = new MOS6502State((short) 0xFF, (short) 0x00, (short) 0xFF, (short) 0xFF, (short) 0x7F, (int) 0x0001);
        cpu.setX((short) 0x01);
        EasyMock.expect(memory.readByte(0x0000)).andReturn((short) 0xCA);
        control.replay();
        cpu.step();
        control.verify();
        MOS6502State after = new MOS6502State(cpu);
        Assert.assertEquals(expected, after);
    }

    @Test
    public void testDEXNegative()
    {
        Assert.assertEquals(2, MOS6502.CYCLES[0xCA]);
        MOS6502State expected = new MOS6502State((short) 0xFF, (short) 0xFF, (short) 0xFF, (short) 0xFF, (short) 0xFD, (int) 0x0001);
        cpu.setX((short) 0x00);
        EasyMock.expect(memory.readByte(0x0000)).andReturn((short) 0xCA);
        control.replay();
        cpu.step();
        control.verify();
        MOS6502State after = new MOS6502State(cpu);
        Assert.assertEquals(expected, after);
    }

    @Test
    public void testINYPositive()
    {
        Assert.assertEquals(2, MOS6502.CYCLES[0xC8]);
        MOS6502State expected = new MOS6502State((short) 0xFF, (short) 0xFF, (short) 0x01, (short) 0xFF, (short) 0x7D, (int) 0x0001);
        cpu.setY((short) 0x00);
        EasyMock.expect(memory.readByte(0x0000)).andReturn((short) 0xC8);
        control.replay();
        cpu.step();
        control.verify();
        MOS6502State after = new MOS6502State(cpu);
        Assert.assertEquals(expected, after);
    }

    @Test
    public void testINYZero()
    {
        Assert.assertEquals(2, MOS6502.CYCLES[0xC8]);
        MOS6502State expected = new MOS6502State((short) 0xFF, (short) 0xFF, (short) 0x00, (short) 0xFF, (short) 0x7F, (int) 0x0001);
        cpu.setY((short) 0xFF);
        EasyMock.expect(memory.readByte(0x0000)).andReturn((short) 0xC8);
        control.replay();
        cpu.step();
        control.verify();
        MOS6502State after = new MOS6502State(cpu);
        Assert.assertEquals(expected, after);
    }

    @Test
    public void testINYNegative()
    {
        Assert.assertEquals(2, MOS6502.CYCLES[0xC8]);
        MOS6502State expected = new MOS6502State((short) 0xFF, (short) 0xFF, (short) 0x80, (short) 0xFF, (short) 0xFD, (int) 0x0001);
        cpu.setY((short) 0x7F);
        EasyMock.expect(memory.readByte(0x0000)).andReturn((short) 0xC8);
        control.replay();
        cpu.step();
        control.verify();
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
