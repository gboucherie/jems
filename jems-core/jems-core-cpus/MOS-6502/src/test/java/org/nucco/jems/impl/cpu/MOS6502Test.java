package org.nucco.jems.impl.cpu;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;

public class MOS6502Test extends AbstractMOS6502Test
{

    @Test
    public void testIllegal()
    {
        MOS6502State expected = new MOS6502State((short) 0xFF, (short) 0xFF, (short) 0xFF, (short) 0xFF, (short) 0xFF, (int) 0x0001);
        EasyMock.expect(memory.readByte(0x0000)).andReturn((short) 0x03);
        test((short) 0x03, (byte) 0, expected);
    }

    @Test
    public void test_BRK()
    {
        MOS6502State expected = new MOS6502State((short) 0xFF, (short) 0xFF, (short) 0xFF, (short) 0xFC, (short) 0xFF, (int) 0xA554);
        EasyMock.expect(memory.readByte(0x0000)).andReturn((short) 0x00);
        memory.writeByte(0x01FF, (short) 0x00);
        memory.writeByte(0x01FE, (short) 0x01);
        memory.writeByte(0x01FD, (short) 0xFF);
        EasyMock.expect(memory.readByte(0xFFFE)).andReturn((short) 0x54);
        EasyMock.expect(memory.readByte(0xFFFF)).andReturn((short) 0xA5);
        test((short) 0x00, (byte) 7, expected);
    }

    @Test
    public void test_ORA_IndirectX_Positive()
    {
        MOS6502State expected = new MOS6502State((short) 0x5F, (short) 0xFF, (short) 0xFF, (short) 0xFF, (short) 0x7D, (int) 0x0002);
        cpu.setA((short) 0x4F);
        test_IndirectX_read((short) 0x01, (byte) 6, expected, (short) 0x10);
    }

    @Test
    public void test_ORA_IndirectX_Zero()
    {
        MOS6502State expected = new MOS6502State((short) 0x00, (short) 0xFF, (short) 0xFF, (short) 0xFF, (short) 0x7F, (int) 0x0002);
        cpu.setA((short) 0x00);
        test_IndirectX_read((short) 0x01, (byte) 6, expected, (short) 0x00);
    }

    @Test
    public void test_ORA_IndirectX_Negative()
    {
        MOS6502State expected = new MOS6502State((short) 0xDB, (short) 0xFF, (short) 0xFF, (short) 0xFF, (short) 0xFD, (int) 0x0002);
        cpu.setA((short) 0xD3);
        test_IndirectX_read((short) 0x01, (byte) 6, expected, (short) 0x98);
    }

    @Test
    public void test_ORA_ZeroPage_Positive()
    {
        MOS6502State expected = new MOS6502State((short) 0x5F, (short) 0xFF, (short) 0xFF, (short) 0xFF, (short) 0x7D, (int) 0x0002);
        cpu.setA((short) 0x4F);
        test_ZeroPage_read((short) 0x05, (byte) 3, expected, (short) 0x10);
    }

    @Test
    public void test_ORA_ZeroPage_Zero()
    {
        MOS6502State expected = new MOS6502State((short) 0x00, (short) 0xFF, (short) 0xFF, (short) 0xFF, (short) 0x7F, (int) 0x0002);
        cpu.setA((short) 0x00);
        test_ZeroPage_read((short) 0x05, (byte) 3, expected, (short) 0x00);
    }

    @Test
    public void test_ORA_ZeroPage_Negative()
    {
        MOS6502State expected = new MOS6502State((short) 0xDB, (short) 0xFF, (short) 0xFF, (short) 0xFF, (short) 0xFD, (int) 0x0002);
        cpu.setA((short) 0x98);
        test_ZeroPage_read((short) 0x05, (byte) 3, expected, (short) 0xD3);
    }

    @Test
    public void test_PHP()
    {
        MOS6502State expected = new MOS6502State((short) 0xFF, (short) 0xFF, (short) 0xFF, (short) 0xFE, (short) 0x58, (int) 0x0001);
        cpu.setSR((short) 0x58);
        EasyMock.expect(memory.readByte(0x0000)).andReturn((short) 0x08);
        memory.writeByte(0x01FF, (short) 0x58);
        test((short) 0x08, (byte) 3, expected);
    }

    @Test
    public void test_ORA_Immediate_Positive()
    {
        MOS6502State expected = new MOS6502State((short) 0x5F, (short) 0xFF, (short) 0xFF, (short) 0xFF, (short) 0x7D, (int) 0x0002);
        cpu.setA((short) 0x4F);
        test_Immediate_read((short) 0x09, (byte) 2, expected, (short) 0x10);
    }

    @Test
    public void test_ORA_Immediate_Zero()
    {
        MOS6502State expected = new MOS6502State((short) 0x00, (short) 0xFF, (short) 0xFF, (short) 0xFF, (short) 0x7F, (int) 0x0002);
        cpu.setA((short) 0x00);
        test_Immediate_read((short) 0x09, (byte) 2, expected, (short) 0x00);
    }

    @Test
    public void test_ORA_Immediate_Negative()
    {
        MOS6502State expected = new MOS6502State((short) 0xDB, (short) 0xFF, (short) 0xFF, (short) 0xFF, (short) 0xFD, (int) 0x0002);
        cpu.setA((short) 0xD3);
        test_Immediate_read((short) 0x09, (byte) 2, expected, (short) 0x98);
    }

    @Test
    public void test_ORA_Absolute_Positive()
    {
        MOS6502State expected = new MOS6502State((short) 0x5F, (short) 0xFF, (short) 0xFF, (short) 0xFF, (short) 0x7D, (int) 0x0003);
        cpu.setA((short) 0x4F);
        test_Absolute_read((short) 0x0D, (byte) 4, expected, (short) 0x10);
    }

    @Test
    public void test_ORA_Absolute_Zero()
    {
        MOS6502State expected = new MOS6502State((short) 0x00, (short) 0xFF, (short) 0xFF, (short) 0xFF, (short) 0x7F, (int) 0x0003);
        cpu.setA((short) 0x00);
        test_Absolute_read((short) 0x0D, (byte) 4, expected, (short) 0x00);
    }

    @Test
    public void test_ORA_Absolute_Negative()
    {
        MOS6502State expected = new MOS6502State((short) 0xDB, (short) 0xFF, (short) 0xFF, (short) 0xFF, (short) 0xFD, (int) 0x0003);
        cpu.setA((short) 0xD3);
        test_Absolute_read((short) 0x0D, (byte) 4, expected, (short) 0x98);
    }

    @Test
    public void test_ORA_IndirectY_Positive()
    {
        MOS6502State expected = new MOS6502State((short) 0x5F, (short) 0xFF, (short) 0xFF, (short) 0xFF, (short) 0x7D, (int) 0x0002);
        cpu.setA((short) 0x4F);
        test_IndirectY_read((short) 0x11, (byte) 5, expected, (short) 0x10);
    }

    @Test
    public void test_ORA_IndirectY_Zero()
    {
        MOS6502State expected = new MOS6502State((short) 0x00, (short) 0xFF, (short) 0xFF, (short) 0xFF, (short) 0x7F, (int) 0x0002);
        cpu.setA((short) 0x00);
        test_IndirectY_read((short) 0x11, (byte) 5, expected, (short) 0x00);
    }

    @Test
    public void test_ORA_IndirectY_Negative()
    {
        MOS6502State expected = new MOS6502State((short) 0xDB, (short) 0xFF, (short) 0xFF, (short) 0xFF, (short) 0xFD, (int) 0x0002);
        cpu.setA((short) 0xD3);
        test_IndirectY_read((short) 0x11, (byte) 5, expected, (short) 0x98);
    }

    @Test
    public void test_ORA_ZeroPageX_Positive()
    {
        MOS6502State expected = new MOS6502State((short) 0x5F, (short) 0x08, (short) 0xFF, (short) 0xFF, (short) 0x7D, (int) 0x0002);
        cpu.setA((short) 0x4F);
        test_ZeroPageX_read((short) 0x15, (byte) 4, expected, (short) 0x10);
    }

    @Test
    public void test_ORA_ZeroPageX_Zero()
    {
        MOS6502State expected = new MOS6502State((short) 0x00, (short) 0x08, (short) 0xFF, (short) 0xFF, (short) 0x7F, (int) 0x0002);
        cpu.setA((short) 0x00);
        test_ZeroPageX_read((short) 0x15, (byte) 4, expected, (short) 0x00);
    }

    @Test
    public void test_ORA_ZeroPageX_Negative()
    {
        MOS6502State expected = new MOS6502State((short) 0xDB, (short) 0x08, (short) 0xFF, (short) 0xFF, (short) 0xFD, (int) 0x0002);
        cpu.setA((short) 0xD3);
        test_ZeroPageX_read((short) 0x15, (byte) 4, expected, (short) 0x98);
    }

    @Test
    public void test_ORA_AbsoluteY_Positive()
    {
        MOS6502State expected = new MOS6502State((short) 0x5F, (short) 0xFF, (short) 0xFF, (short) 0xFF, (short) 0x7D, (int) 0x0003);
        cpu.setA((short) 0x4F);
        test_AbsoluteY_read((short) 0x19, (byte) 4, expected, (short) 0x10);
    }

    @Test
    public void test_ORA_AbsoluteY_Zero()
    {
        MOS6502State expected = new MOS6502State((short) 0x00, (short) 0xFF, (short) 0xFF, (short) 0xFF, (short) 0x7F, (int) 0x0003);
        cpu.setA((short) 0x00);
        test_AbsoluteY_read((short) 0x19, (byte) 4, expected, (short) 0x00);
    }

    @Test
    public void test_ORA_AbsoluteY_Negative()
    {
        MOS6502State expected = new MOS6502State((short) 0xDB, (short) 0xFF, (short) 0xFF, (short) 0xFF, (short) 0xFD, (int) 0x0003);
        cpu.setA((short) 0xD3);
        test_AbsoluteY_read((short) 0x19, (byte) 4, expected, (short) 0x98);
    }

    @Test
    public void test_ORA_AbsoluteX_Positive()
    {
        MOS6502State expected = new MOS6502State((short) 0x5F, (short) 0x01, (short) 0xFF, (short) 0xFF, (short) 0x7D, (int) 0x0003);
        cpu.setA((short) 0x4F);
        test_AbsoluteX_read((short) 0x1D, (byte) 4, expected, (short) 0x10);
    }

    @Test
    public void test_ORA_AbsoluteX_Zero()
    {
        MOS6502State expected = new MOS6502State((short) 0x00, (short) 0x01, (short) 0xFF, (short) 0xFF, (short) 0x7F, (int) 0x0003);
        cpu.setA((short) 0x00);
        test_AbsoluteX_read((short) 0x1D, (byte) 4, expected, (short) 0x00);
    }

    @Test
    public void test_ORA_AbsoluteX_Negative()
    {
        MOS6502State expected = new MOS6502State((short) 0xDB, (short) 0x01, (short) 0xFF, (short) 0xFF, (short) 0xFD, (int) 0x0003);
        cpu.setA((short) 0xD3);
        test_AbsoluteX_read((short) 0x1D, (byte) 4, expected, (short) 0x98);
    }

    @Test
    public void test_AND_IndirectX_Positive()
    {
        MOS6502State expected = new MOS6502State((short) 0x08, (short) 0x01, (short) 0xFF, (short) 0xFF, (short) 0x7D, (int) 0x0002);
        cpu.setA((short) 0x08);
        test_IndirectX_read((short) 0x21, (byte) 6, expected, (short) 0xFF);
    }

    @Test
    public void test_AND_IndirectX_Zero()
    {
        MOS6502State expected = new MOS6502State((short) 0x00, (short) 0x01, (short) 0xFF, (short) 0xFF, (short) 0x7F, (int) 0x0002);
        cpu.setA((short) 0x00);
        test_IndirectX_read((short) 0x21, (byte) 6, expected, (short) 0xFF);
    }

    @Test
    public void test_AND_IndirectX_Negative()
    {
        MOS6502State expected = new MOS6502State((short) 0x98, (short) 0x01, (short) 0xFF, (short) 0xFF, (short) 0xFD, (int) 0x0002);
        cpu.setA((short) 0x98);
        test_IndirectX_read((short) 0x21, (byte) 6, expected, (short) 0xFF);
    }

    @Test
    public void test_BIT_ZeroPage_Zero()
    {
        MOS6502State expected = new MOS6502State((short) 0xC2, (short) 0xFF, (short) 0xFF, (short) 0xFF, (short) 0x02, (int) 0x0002);
        cpu.setA((short) 0xC2);
        cpu.setSR((short) 0x00);
        test_ZeroPage_read((short) 0x24, (byte) 3, expected, (short) 0x2C);
    }

    @Test
    public void test_BIT_ZeroPage_Overflow()
    {
        MOS6502State expected = new MOS6502State((short) 0xAC, (short) 0xFF, (short) 0xFF, (short) 0xFF, (short) 0x40, (int) 0x0002);
        cpu.setA((short) 0xAC);
        cpu.setSR((short) 0x00);
        test_ZeroPage_read((short) 0x24, (byte) 3, expected, (short) 0x5D);
    }

    @Test
    public void test_BIT_ZeroPage_Negative()
    {
        MOS6502State expected = new MOS6502State((short) 0x84, (short) 0xFF, (short) 0xFF, (short) 0xFF, (short) 0x80, (int) 0x0002);
        cpu.setA((short) 0x84);
        cpu.setSR((short) 0x00);
        test_ZeroPage_read((short) 0x24, (byte) 3, expected, (short) 0x80);
    }

    @Test
    public void test_BIT_ZeroPage_ZeroAndOverflow()
    {
        MOS6502State expected = new MOS6502State((short) 0xA2, (short) 0xFF, (short) 0xFF, (short) 0xFF, (short) 0x42, (int) 0x0002);
        cpu.setA((short) 0xA2);
        cpu.setSR((short) 0x00);
        test_ZeroPage_read((short) 0x24, (byte) 3, expected, (short) 0x5D);
    }

    @Test
    public void test_BIT_ZeroPage_ZeroAndNegative()
    {
        MOS6502State expected = new MOS6502State((short) 0x7F, (short) 0xFF, (short) 0xFF, (short) 0xFF, (short) 0x82, (int) 0x0002);
        cpu.setA((short) 0x7F);
        cpu.setSR((short) 0x00);
        test_ZeroPage_read((short) 0x24, (byte) 3, expected, (short) 0x80);
    }

    @Test
    public void test_BIT_ZeroPage_OverflowAndNegative()
    {
        MOS6502State expected = new MOS6502State((short) 0x3F, (short) 0xFF, (short) 0xFF, (short) 0xFF, (short) 0xC0, (int) 0x0002);
        cpu.setA((short) 0x3F);
        cpu.setSR((short) 0x00);
        test_ZeroPage_read((short) 0x24, (byte) 3, expected, (short) 0xCF);
    }

    @Test
    public void test_BIT_ZeroPage_ZeroAndOverflowAndNegative()
    {
        MOS6502State expected = new MOS6502State((short) 0x30, (short) 0xFF, (short) 0xFF, (short) 0xFF, (short) 0xC2, (int) 0x0002);
        cpu.setA((short) 0x30);
        cpu.setSR((short) 0x00);
        test_ZeroPage_read((short) 0x24, (byte) 3, expected, (short) 0xCF);
    }

    @Test
    public void test_AND_ZeroPage_Positive()
    {
        MOS6502State expected = new MOS6502State((short) 0x08, (short) 0xFF, (short) 0xFF, (short) 0xFF, (short) 0x7D, (int) 0x0002);
        cpu.setA((short) 0x08);
        test_ZeroPage_read((short) 0x25, (byte) 3, expected, (short) 0xFF);
    }

    @Test
    public void test_AND_ZeroPage_Zero()
    {
        MOS6502State expected = new MOS6502State((short) 0x00, (short) 0xFF, (short) 0xFF, (short) 0xFF, (short) 0x7F, (int) 0x0002);
        cpu.setA((short) 0x00);
        test_ZeroPage_read((short) 0x25, (byte) 3, expected, (short) 0xFF);
    }

    @Test
    public void test_AND_ZeroPage_Negative()
    {
        MOS6502State expected = new MOS6502State((short) 0x98, (short) 0xFF, (short) 0xFF, (short) 0xFF, (short) 0xFD, (int) 0x0002);
        cpu.setA((short) 0x98);
        test_ZeroPage_read((short) 0x25, (byte) 3, expected, (short) 0xFF);
    }

    @Test
    public void test_PLP()
    {
        MOS6502State expected = new MOS6502State((short) 0xFF, (short) 0xFF, (short) 0xFF, (short) 0x59, (short) 0x67, (int) 0x0001);
        cpu.setSP((short) 0x58);
        EasyMock.expect(memory.readByte(0x0000)).andReturn((short) 0x28);
        EasyMock.expect(memory.readByte(0x0158)).andReturn((short) 0x67);
        test((short) 0x28, (byte) 4, expected);
    }

    @Test
    public void test_AND_Immediate_Positive()
    {
        MOS6502State expected = new MOS6502State((short) 0x08, (short) 0xFF, (short) 0xFF, (short) 0xFF, (short) 0x7D, (int) 0x0002);
        cpu.setA((short) 0x08);
        test_Immediate_read((short) 0x29, (byte) 2, expected, (short) 0xFF);
    }

    @Test
    public void test_AND_Immediate_Zero()
    {
        MOS6502State expected = new MOS6502State((short) 0x00, (short) 0xFF, (short) 0xFF, (short) 0xFF, (short) 0x7F, (int) 0x0002);
        cpu.setA((short) 0x00);
        test_Immediate_read((short) 0x29, (byte) 2, expected, (short) 0xFF);
    }

    @Test
    public void test_AND_Immediate_Negative()
    {
        MOS6502State expected = new MOS6502State((short) 0x98, (short) 0xFF, (short) 0xFF, (short) 0xFF, (short) 0xFD, (int) 0x0002);
        cpu.setA((short) 0x98);
        test_Immediate_read((short) 0x29, (byte) 2, expected, (short) 0xFF);
    }

    @Test
    public void test_BIT_Absolute_Zero()
    {
        MOS6502State expected = new MOS6502State((short) 0xC2, (short) 0xFF, (short) 0xFF, (short) 0xFF, (short) 0x02, (int) 0x0003);
        cpu.setA((short) 0xC2);
        cpu.setSR((short) 0x00);
        test_Absolute_read((short) 0x2C, (byte) 4, expected, (short) 0x2C);
    }

    @Test
    public void test_BIT_Absolute_Overflow()
    {
        MOS6502State expected = new MOS6502State((short) 0xAC, (short) 0xFF, (short) 0xFF, (short) 0xFF, (short) 0x40, (int) 0x0003);
        cpu.setA((short) 0xAC);
        cpu.setSR((short) 0x00);
        test_Absolute_read((short) 0x2C, (byte) 4, expected, (short) 0x5D);
    }

    @Test
    public void test_BIT_Absolute_Negative()
    {
        MOS6502State expected = new MOS6502State((short) 0x84, (short) 0xFF, (short) 0xFF, (short) 0xFF, (short) 0x80, (int) 0x0003);
        cpu.setA((short) 0x84);
        cpu.setSR((short) 0x00);
        test_Absolute_read((short) 0x2C, (byte) 4, expected, (short) 0x80);
    }

    @Test
    public void test_BIT_Absolute_ZeroAndOverflow()
    {
        MOS6502State expected = new MOS6502State((short) 0xA2, (short) 0xFF, (short) 0xFF, (short) 0xFF, (short) 0x42, (int) 0x0003);
        cpu.setA((short) 0xA2);
        cpu.setSR((short) 0x00);
        test_Absolute_read((short) 0x2C, (byte) 4, expected, (short) 0x5D);
    }

    @Test
    public void test_BIT_Absolute_ZeroAndNegative()
    {
        MOS6502State expected = new MOS6502State((short) 0x7F, (short) 0xFF, (short) 0xFF, (short) 0xFF, (short) 0x82, (int) 0x0003);
        cpu.setA((short) 0x7F);
        cpu.setSR((short) 0x00);
        test_Absolute_read((short) 0x2C, (byte) 4, expected, (short) 0x80);
    }

    @Test
    public void test_BIT_Absolute_OverflowAndNegative()
    {
        MOS6502State expected = new MOS6502State((short) 0x3F, (short) 0xFF, (short) 0xFF, (short) 0xFF, (short) 0xC0, (int) 0x0003);
        cpu.setA((short) 0x3F);
        cpu.setSR((short) 0x00);
        test_Absolute_read((short) 0x2C, (byte) 4, expected, (short) 0xCF);
    }

    @Test
    public void test_BIT_Absolute_ZeroAndOverflowAndNegative()
    {
        MOS6502State expected = new MOS6502State((short) 0x30, (short) 0xFF, (short) 0xFF, (short) 0xFF, (short) 0xC2, (int) 0x0003);
        cpu.setA((short) 0x30);
        cpu.setSR((short) 0x00);
        test_Absolute_read((short) 0x2C, (byte) 4, expected, (short) 0xCF);
    }

    @Test
    public void test_AND_Absolute_Positive()
    {
        MOS6502State expected = new MOS6502State((short) 0x08, (short) 0xFF, (short) 0xFF, (short) 0xFF, (short) 0x7D, (int) 0x0003);
        cpu.setA((short) 0x08);
        test_Absolute_read((short) 0x2D, (byte) 4, expected, (short) 0xFF);
    }

    @Test
    public void test_AND_Absolute_Zero()
    {
        MOS6502State expected = new MOS6502State((short) 0x00, (short) 0xFF, (short) 0xFF, (short) 0xFF, (short) 0x7F, (int) 0x0003);
        cpu.setA((short) 0x00);
        test_Absolute_read((short) 0x2D, (byte) 4, expected, (short) 0xFF);
    }

    @Test
    public void test_AND_Absolute_Negative()
    {
        MOS6502State expected = new MOS6502State((short) 0x98, (short) 0xFF, (short) 0xFF, (short) 0xFF, (short) 0xFD, (int) 0x0003);
        cpu.setA((short) 0x98);
        test_Absolute_read((short) 0x2D, (byte) 4, expected, (short) 0xFF);
    }

    @Test
    public void test_AND_IndirectY_Positive()
    {
        MOS6502State expected = new MOS6502State((short) 0x15, (short) 0xFF, (short) 0x15, (short) 0xFF, (short) 0x7D, (int) 0x0002);
        cpu.setA((short) 0x15);
        test_IndirectY_read((short) 0x31, (byte) 5, expected, (short) 0xFF);
    }

    @Test
    public void test_AND_IndirectY_Zero()
    {
        MOS6502State expected = new MOS6502State((short) 0x00, (short) 0xFF, (short) 0x15, (short) 0xFF, (short) 0x7F, (int) 0x0002);
        cpu.setA((short) 0x00);
        test_IndirectY_read((short) 0x31, (byte) 5, expected, (short) 0xFF);
    }

    @Test
    public void test_AND_IndirectY_Negative()
    {
        MOS6502State expected = new MOS6502State((short) 0xA1, (short) 0xFF, (short) 0x15, (short) 0xFF, (short) 0xFD, (int) 0x0002);
        cpu.setA((short) 0xA1);
        test_IndirectY_read((short) 0x31, (byte) 5, expected, (short) 0xFF);
    }

    @Test
    public void test_AND_ZeroPageX_Positive()
    {
        MOS6502State expected = new MOS6502State((short) 0x08, (short) 0x08, (short) 0xFF, (short) 0xFF, (short) 0x7D, (int) 0x0002);
        cpu.setA((short) 0x08);
        test_ZeroPageX_read((short) 0x35, (byte) 4, expected, (short) 0xFF);
    }

    @Test
    public void test_AND_ZeroPageX_Zero()
    {
        MOS6502State expected = new MOS6502State((short) 0x00, (short) 0x08, (short) 0xFF, (short) 0xFF, (short) 0x7F, (int) 0x0002);
        cpu.setA((short) 0x00);
        test_ZeroPageX_read((short) 0x35, (byte) 4, expected, (short) 0xFF);
    }

    @Test
    public void test_AND_ZeroPageX_Negative()
    {
        MOS6502State expected = new MOS6502State((short) 0x98, (short) 0x08, (short) 0xFF, (short) 0xFF, (short) 0xFD, (int) 0x0002);
        cpu.setA((short) 0x98);
        test_ZeroPageX_read((short) 0x35, (byte) 4, expected, (short) 0xFF);
    }

    @Test
    public void test_AND_AbsoluteY_Positive()
    {
        MOS6502State expected = new MOS6502State((short) 0x08, (short) 0xFF, (short) 0x01, (short) 0xFF, (short) 0x7D, (int) 0x0003);
        cpu.setA((short) 0x08);
        test_AbsoluteY_read((short) 0x39, (byte) 4, expected, (short) 0xFF);
    }

    @Test
    public void test_AND_AbsoluteY_Zero()
    {
        MOS6502State expected = new MOS6502State((short) 0x00, (short) 0xFF, (short) 0x01, (short) 0xFF, (short) 0x7F, (int) 0x0003);
        cpu.setA((short) 0x00);
        test_AbsoluteY_read((short) 0x39, (byte) 4, expected, (short) 0xFF);
    }

    @Test
    public void test_AND_AbsoluteY_Negative()
    {
        MOS6502State expected = new MOS6502State((short) 0x98, (short) 0xFF, (short) 0x01, (short) 0xFF, (short) 0xFD, (int) 0x0003);
        cpu.setA((short) 0x98);
        test_AbsoluteY_read((short) 0x39, (byte) 4, expected, (short) 0xFF);
    }

    @Test
    public void test_AND_AbsoluteX_Positive()
    {
        MOS6502State expected = new MOS6502State((short) 0x08, (short) 0x01, (short) 0xFF, (short) 0xFF, (short) 0x7D, (int) 0x0003);
        cpu.setA((short) 0x08);
        test_AbsoluteX_read((short) 0x3D, (byte) 4, expected, (short) 0xFF);
    }

    @Test
    public void test_AND_AbsoluteX_Zero()
    {
        MOS6502State expected = new MOS6502State((short) 0x00, (short) 0x01, (short) 0xFF, (short) 0xFF, (short) 0x7F, (int) 0x0003);
        cpu.setA((short) 0x00);
        test_AbsoluteX_read((short) 0x3D, (byte) 4, expected, (short) 0xFF);
    }

    @Test
    public void test_AND_AbsoluteX_Negative()
    {
        MOS6502State expected = new MOS6502State((short) 0x98, (short) 0x01, (short) 0xFF, (short) 0xFF, (short) 0xFD, (int) 0x0003);
        cpu.setA((short) 0x98);
        test_AbsoluteX_read((short) 0x3D, (byte) 4, expected, (short) 0xFF);
    }

    @Test
    public void test_EOR_IndirectX_Positive()
    {
        MOS6502State expected = new MOS6502State((short) 0x06, (short) 0x01, (short) 0xFF, (short) 0xFF, (short) 0x7D, (int) 0x0002);
        cpu.setA((short) 0xA5);
        test_IndirectX_read((short) 0x41, (byte) 6, expected, (short) 0xA3);
    }

    @Test
    public void test_EOR_IndirectX_Zero()
    {
        MOS6502State expected = new MOS6502State((short) 0x00, (short) 0x01, (short) 0xFF, (short) 0xFF, (short) 0x7F, (int) 0x0002);
        cpu.setA((short) 0xA5);
        test_IndirectX_read((short) 0x41, (byte) 6, expected, (short) 0xA5);
    }

    @Test
    public void test_EOR_IndirectX_Negative()
    {
        MOS6502State expected = new MOS6502State((short) 0xAF, (short) 0x01, (short) 0xFF, (short) 0xFF, (short) 0xFD, (int) 0x0002);
        cpu.setA((short) 0xA5);
        test_IndirectX_read((short) 0x41, (byte) 6, expected, (short) 0x0A);
    }

    @Test
    public void test_EOR_ZeroPage_Positive()
    {
        MOS6502State expected = new MOS6502State((short) 0x06, (short) 0xFF, (short) 0xFF, (short) 0xFF, (short) 0x7D, (int) 0x0002);
        cpu.setA((short) 0xA5);
        test_ZeroPage_read((short) 0x45, (byte) 3, expected, (short) 0xA3);
    }

    @Test
    public void test_EOR_ZeroPage_Zero()
    {
        MOS6502State expected = new MOS6502State((short) 0x00, (short) 0xFF, (short) 0xFF, (short) 0xFF, (short) 0x7F, (int) 0x0002);
        cpu.setA((short) 0xA5);
        test_ZeroPage_read((short) 0x45, (byte) 3, expected, (short) 0xA5);
    }

    @Test
    public void test_EOR_ZeroPage_Negative()
    {
        MOS6502State expected = new MOS6502State((short) 0xAF, (short) 0xFF, (short) 0xFF, (short) 0xFF, (short) 0xFD, (int) 0x0002);
        cpu.setA((short) 0xA5);
        test_ZeroPage_read((short) 0x45, (byte) 3, expected, (short) 0x0A);
    }

    @Test
    public void test_PHA()
    {
        MOS6502State expected = new MOS6502State((short) 0x58, (short) 0xFF, (short) 0xFF, (short) 0xFE, (short) 0xFF, (int) 0x0001);
        cpu.setA((short) 0x58);
        EasyMock.expect(memory.readByte(0x0000)).andReturn((short) 0x48);
        memory.writeByte(0x01FF, (short) 0x58);
        test((short) 0x48, (byte) 3, expected);
    }

    @Test
    public void test_EOR_Immediate_Positive()
    {
        MOS6502State expected = new MOS6502State((short) 0x06, (short) 0xFF, (short) 0xFF, (short) 0xFF, (short) 0x7D, (int) 0x0002);
        cpu.setA((short) 0xA5);
        test_Immediate_read((short) 0x49, (byte) 2, expected, (short) 0xA3);
    }

    @Test
    public void test_EOR_Immediate_Zero()
    {
        MOS6502State expected = new MOS6502State((short) 0x00, (short) 0xFF, (short) 0xFF, (short) 0xFF, (short) 0x7F, (int) 0x0002);
        cpu.setA((short) 0xA5);
        test_Immediate_read((short) 0x49, (byte) 2, expected, (short) 0xA5);
    }

    @Test
    public void test_EOR_Immediate_Negative()
    {
        MOS6502State expected = new MOS6502State((short) 0xAF, (short) 0xFF, (short) 0xFF, (short) 0xFF, (short) 0xFD, (int) 0x0002);
        cpu.setA((short) 0xA5);
        test_Immediate_read((short) 0x49, (byte) 2, expected, (short) 0x0A);
    }

    @Test
    public void test_EOR_Absolute_Positive()
    {
        MOS6502State expected = new MOS6502State((short) 0x06, (short) 0xFF, (short) 0xFF, (short) 0xFF, (short) 0x7D, (int) 0x0003);
        cpu.setA((short) 0xA5);
        test_Absolute_read((short) 0x4D, (byte) 4, expected, (short) 0xA3);
    }

    @Test
    public void test_EOR_Absolute_Zero()
    {
        MOS6502State expected = new MOS6502State((short) 0x00, (short) 0xFF, (short) 0xFF, (short) 0xFF, (short) 0x7F, (int) 0x0003);
        cpu.setA((short) 0xA5);
        test_Absolute_read((short) 0x4D, (byte) 4, expected, (short) 0xA5);
    }

    @Test
    public void test_EOR_Absolute_Negative()
    {
        MOS6502State expected = new MOS6502State((short) 0xAF, (short) 0xFF, (short) 0xFF, (short) 0xFF, (short) 0xFD, (int) 0x0003);
        cpu.setA((short) 0xA5);
        test_Absolute_read((short) 0x4D, (byte) 4, expected, (short) 0x0A);
    }

    @Test
    public void test_EOR_IndirectY_Positive()
    {
        MOS6502State expected = new MOS6502State((short) 0x06, (short) 0xFF, (short) 0x15, (short) 0xFF, (short) 0x7D, (int) 0x0002);
        cpu.setA((short) 0xA5);
        test_IndirectY_read((short) 0x51, (byte) 5, expected, (short) 0xA3);
    }

    @Test
    public void test_EOR_IndirectY_Zero()
    {
        MOS6502State expected = new MOS6502State((short) 0x00, (short) 0xFF, (short) 0x15, (short) 0xFF, (short) 0x7F, (int) 0x0002);
        cpu.setA((short) 0xA5);
        test_IndirectY_read((short) 0x51, (byte) 5, expected, (short) 0xA5);
    }

    @Test
    public void test_EOR_IndirectY_Negative()
    {
        MOS6502State expected = new MOS6502State((short) 0xAF, (short) 0xFF, (short) 0x15, (short) 0xFF, (short) 0xFD, (int) 0x0002);
        cpu.setA((short) 0xA5);
        test_IndirectY_read((short) 0x51, (byte) 5, expected, (short) 0x0A);
    }

    @Test
    public void test_EOR_ZeroPageX_Positive()
    {
        MOS6502State expected = new MOS6502State((short) 0x06, (short) 0x08, (short) 0xFF, (short) 0xFF, (short) 0x7D, (int) 0x0002);
        cpu.setA((short) 0xA5);
        test_ZeroPageX_read((short) 0x55, (byte) 4, expected, (short) 0xA3);
    }

    @Test
    public void test_EOR_ZeroPageX_Zero()
    {
        MOS6502State expected = new MOS6502State((short) 0x00, (short) 0x08, (short) 0xFF, (short) 0xFF, (short) 0x7F, (int) 0x0002);
        cpu.setA((short) 0xA5);
        test_ZeroPageX_read((short) 0x55, (byte) 4, expected, (short) 0xA5);
    }

    @Test
    public void test_EOR_ZeroPageX_Negative()
    {
        MOS6502State expected = new MOS6502State((short) 0xAF, (short) 0x08, (short) 0xFF, (short) 0xFF, (short) 0xFD, (int) 0x0002);
        cpu.setA((short) 0xA5);
        test_ZeroPageX_read((short) 0x55, (byte) 4, expected, (short) 0x0A);
    }

    @Test
    public void test_EOR_AbsoluteY_Positive()
    {
        MOS6502State expected = new MOS6502State((short) 0x06, (short) 0xFF, (short) 0x01, (short) 0xFF, (short) 0x7D, (int) 0x0003);
        cpu.setA((short) 0xA5);
        test_AbsoluteY_read((short) 0x59, (byte) 4, expected, (short) 0xA3);
    }

    @Test
    public void test_EOR_AbsoluteY_Zero()
    {
        MOS6502State expected = new MOS6502State((short) 0x00, (short) 0xFF, (short) 0x01, (short) 0xFF, (short) 0x7F, (int) 0x0003);
        cpu.setA((short) 0xA5);
        test_AbsoluteY_read((short) 0x59, (byte) 4, expected, (short) 0xA5);
    }

    @Test
    public void test_EOR_AbsoluteY_Negative()
    {
        MOS6502State expected = new MOS6502State((short) 0xAF, (short) 0xFF, (short) 0x01, (short) 0xFF, (short) 0xFD, (int) 0x0003);
        cpu.setA((short) 0xA5);
        test_AbsoluteY_read((short) 0x59, (byte) 4, expected, (short) 0x0A);
    }

    @Test
    public void test_EOR_AbsoluteX_Positive()
    {
        MOS6502State expected = new MOS6502State((short) 0x06, (short) 0x01, (short) 0xFF, (short) 0xFF, (short) 0x7D, (int) 0x0003);
        cpu.setA((short) 0xA5);
        test_AbsoluteX_read((short) 0x5D, (byte) 4, expected, (short) 0xA3);
    }

    @Test
    public void test_EOR_AbsoluteX_Zero()
    {
        MOS6502State expected = new MOS6502State((short) 0x00, (short) 0x01, (short) 0xFF, (short) 0xFF, (short) 0x7F, (int) 0x0003);
        cpu.setA((short) 0xA5);
        test_AbsoluteX_read((short) 0x5D, (byte) 4, expected, (short) 0xA5);
    }

    @Test
    public void test_EOR_AbsoluteX_Negative()
    {
        MOS6502State expected = new MOS6502State((short) 0xAF, (short) 0x01, (short) 0xFF, (short) 0xFF, (short) 0xFD, (int) 0x0003);
        cpu.setA((short) 0xA5);
        test_AbsoluteX_read((short) 0x5D, (byte) 4, expected, (short) 0x0A);
    }

    @Test
    public void test_PLA_Positive()
    {
        Assert.assertEquals(4, MOS6502.CYCLES[0x68]);
        MOS6502State expected = new MOS6502State((short) 0x08, (short) 0xFF, (short) 0xFF, (short) 0x59, (short) 0x7D, (int) 0x0001);
        cpu.setSP((short) 0x58);
        EasyMock.expect(memory.readByte(0x0000)).andReturn((short) 0x68);
        EasyMock.expect(memory.readByte(0x0158)).andReturn((short) 0x08);
        test((short) 0x68, (byte) 4, expected);
    }

    @Test
    public void test_PLA_Zero()
    {
        Assert.assertEquals(4, MOS6502.CYCLES[0x68]);
        MOS6502State expected = new MOS6502State((short) 0x00, (short) 0xFF, (short) 0xFF, (short) 0x59, (short) 0x7F, (int) 0x0001);
        cpu.setSP((short) 0x58);
        EasyMock.expect(memory.readByte(0x0000)).andReturn((short) 0x68);
        EasyMock.expect(memory.readByte(0x0158)).andReturn((short) 0x00);
        test((short) 0x68, (byte) 4, expected);
    }

    @Test
    public void test_PLA_Negative()
    {
        Assert.assertEquals(4, MOS6502.CYCLES[0x68]);
        MOS6502State expected = new MOS6502State((short) 0x98, (short) 0xFF, (short) 0xFF, (short) 0x59, (short) 0xFD, (int) 0x0001);
        cpu.setSP((short) 0x58);
        EasyMock.expect(memory.readByte(0x0000)).andReturn((short) 0x68);
        EasyMock.expect(memory.readByte(0x0158)).andReturn((short) 0x98);
        test((short) 0x68, (byte) 4, expected);
    }

    @Test
    public void test_STA_IndirectX()
    {
        MOS6502State expected = new MOS6502State((short) 0xFF, (short) 0x11, (short) 0xFF, (short) 0xFF, (short) 0xFF, (int) 0x0002);
        test_IndirectX_write((short) 0x81, (byte) 6, expected, (short) 0xFF);
    }

    @Test
    public void test_STY_ZeroPage()
    {
        Assert.assertEquals(3, MOS6502.CYCLES[0x84]);
        MOS6502State expected = new MOS6502State((short) 0xFF, (short) 0xFF, (short) 0xFF, (short) 0xFF, (short) 0xFF, (int) 0x0002);
        EasyMock.expect(memory.readByte(0x0000)).andReturn((short) 0x84);
        EasyMock.expect(memory.readByte(0x0001)).andReturn((short) 0x45);
        memory.writeByte(0x0045, (short) 0xFF);
        test((short) 0x84, (byte) 3, expected);
    }

    @Test
    public void test_STA_ZeroPage()
    {
        MOS6502State expected = new MOS6502State((short) 0xFF, (short) 0xFF, (short) 0xFF, (short) 0xFF, (short) 0xFF, (int) 0x0002);
        EasyMock.expect(memory.readByte(0x0000)).andReturn((short) 0x85);
        EasyMock.expect(memory.readByte(0x0001)).andReturn((short) 0x45);
        memory.writeByte(0x0045, (short) 0xFF);
        test((short) 0x85, (byte) 3, expected);
    }

    @Test
    public void test_STX_ZeroPage()
    {
        MOS6502State expected = new MOS6502State((short) 0xFF, (short) 0xFF, (short) 0xFF, (short) 0xFF, (short) 0xFF, (int) 0x0002);
        EasyMock.expect(memory.readByte(0x0000)).andReturn((short) 0x86);
        EasyMock.expect(memory.readByte(0x0001)).andReturn((short) 0x45);
        memory.writeByte(0x0045, (short) 0xFF);
        test((short) 0x86, (byte) 3, expected);
    }

    @Test
    public void test_DEY_Positive()
    {
        MOS6502State expected = new MOS6502State((short) 0xFF, (short) 0xFF, (short) 0x01, (short) 0xFF, (short) 0x7D, (int) 0x0001);
        cpu.setY((short) 0x02);
        EasyMock.expect(memory.readByte(0x0000)).andReturn((short) 0x88);
        test((short) 0x88, (byte) 2, expected);
    }

    @Test
    public void test_DEY_Zero()
    {
        MOS6502State expected = new MOS6502State((short) 0xFF, (short) 0xFF, (short) 0x00, (short) 0xFF, (short) 0x7F, (int) 0x0001);
        cpu.setY((short) 0x01);
        EasyMock.expect(memory.readByte(0x0000)).andReturn((short) 0x88);
        test((short) 0x88, (byte) 2, expected);
    }

    @Test
    public void test_DEY_Negative()
    {
        MOS6502State expected = new MOS6502State((short) 0xFF, (short) 0xFF, (short) 0xFF, (short) 0xFF, (short) 0xFD, (int) 0x0001);
        cpu.setY((short) 0x00);
        EasyMock.expect(memory.readByte(0x0000)).andReturn((short) 0x88);
        test((short) 0x88, (byte) 2, expected);
    }

    @Test
    public void test_TXA_Positive()
    {
        MOS6502State expected = new MOS6502State((short) 0x45, (short) 0x45, (short) 0xFF, (short) 0xFF, (short) 0x7D, (int) 0x0001);
        cpu.setX((short) 0x45);
        EasyMock.expect(memory.readByte(0x0000)).andReturn((short) 0x8A);
        test((short) 0x8A, (byte) 2, expected);
    }

    @Test
    public void test_TXA_Zero()
    {
        MOS6502State expected = new MOS6502State((short) 0x00, (short) 0x00, (short) 0xFF, (short) 0xFF, (short) 0x7F, (int) 0x0001);
        cpu.setX((short) 0x00);
        EasyMock.expect(memory.readByte(0x0000)).andReturn((short) 0x8A);
        test((short) 0x8A, (byte) 2, expected);
    }

    @Test
    public void test_TXA_Negative()
    {
        MOS6502State expected = new MOS6502State((short) 0xA1, (short) 0xA1, (short) 0xFF, (short) 0xFF, (short) 0xFD, (int) 0x0001);
        cpu.setX((short) 0xA1);
        EasyMock.expect(memory.readByte(0x0000)).andReturn((short) 0x8A);
        test((short) 0x8A, (byte) 2, expected);
    }

    @Test
    public void test_STY_Absolute()
    {
        MOS6502State expected = new MOS6502State((short) 0xFF, (short) 0xFF, (short) 0xFF, (short) 0xFF, (short) 0xFF, (int) 0x0003);
        test_Absolute_write((short) 0x8C, (byte) 4, expected, (short) 0xFF);
    }

    @Test
    public void test_STA_Absolute()
    {
        MOS6502State expected = new MOS6502State((short) 0xFF, (short) 0xFF, (short) 0xFF, (short) 0xFF, (short) 0xFF, (int) 0x0003);
        test_Absolute_write((short) 0x8D, (byte) 4, expected, (short) 0xFF);
    }

    @Test
    public void test_STX_Absolute()
    {
        MOS6502State expected = new MOS6502State((short) 0xFF, (short) 0xFF, (short) 0xFF, (short) 0xFF, (short) 0xFF, (int) 0x0003);
        test_Absolute_write((short) 0x8E, (byte) 4, expected, (short) 0xFF);
    }

    @Test
    public void test_STA_IndirectY()
    {
        MOS6502State expected = new MOS6502State((short) 0xFF, (short) 0xFF, (short) 0x13, (short) 0xFF, (short) 0xFF, (int) 0x0002);
        test_IndirectY_write((short) 0x91, (byte) 6, expected, (short) 0xFF);
    }

    @Test
    public void test_STY_ZeroPageX()
    {
        MOS6502State expected = new MOS6502State((short) 0xFF, (short) 0x67, (short) 0xFF, (short) 0xFF, (short) 0xFF, (int) 0x0002);
        test_ZeroPageX_write((short) 0x94, (byte) 4, expected, expected.Y);
    }

    @Test
    public void test_STA_ZeroPageX()
    {
        MOS6502State expected = new MOS6502State((short) 0xFF, (short) 0x67, (short) 0xFF, (short) 0xFF, (short) 0xFF, (int) 0x0002);
        test_ZeroPageX_write((short) 0x95, (byte) 4, expected, expected.A);
    }

    @Test
    public void test_STX_ZeroPageY()
    {
        MOS6502State expected = new MOS6502State((short) 0xFF, (short) 0xFF, (short) 0x35, (short) 0xFF, (short) 0xFF, (int) 0x0002);
        test_ZeroPageY_write((short) 0x96, (byte) 4, expected, expected.X);
    }

    @Test
    public void test_TXY_Positive()
    {
        MOS6502State expected = new MOS6502State((short) 0x45, (short) 0xFF, (short) 0x45, (short) 0xFF, (short) 0x7D, (int) 0x0001);
        cpu.setY((short) 0x45);
        EasyMock.expect(memory.readByte(0x0000)).andReturn((short) 0x98);
        test((short) 0x98, (byte) 2, expected);
    }

    @Test
    public void test_TXY_Zero()
    {
        MOS6502State expected = new MOS6502State((short) 0x00, (short) 0xFF, (short) 0x00, (short) 0xFF, (short) 0x7F, (int) 0x0001);
        cpu.setY((short) 0x00);
        EasyMock.expect(memory.readByte(0x0000)).andReturn((short) 0x98);
        test((short) 0x98, (byte) 2, expected);
    }

    @Test
    public void test_TXY_Negative()
    {
        MOS6502State expected = new MOS6502State((short) 0xA1, (short) 0xFF, (short) 0xA1, (short) 0xFF, (short) 0xFD, (int) 0x0001);
        cpu.setY((short) 0xA1);
        EasyMock.expect(memory.readByte(0x0000)).andReturn((short) 0x98);
        test((short) 0x98, (byte) 2, expected);
    }

    @Test
    public void test_STA_AbsoluteY()
    {
        MOS6502State expected = new MOS6502State((short) 0xFF, (short) 0xFF, (short) 0x13, (short) 0xFF, (short) 0xFF, (int) 0x0003);
        test_AbsoluteY_write((short) 0x99, (byte) 5, expected, (short) 0xFF);
    }

    @Test
    public void test_TXS()
    {
        MOS6502State expected = new MOS6502State((short) 0xFF, (short) 0x13, (short) 0xFF, (short) 0x13, (short) 0xFF, (int) 0x0001);
        cpu.setX((short) 0x13);
        EasyMock.expect(memory.readByte(0x0000)).andReturn((short) 0x9A);
        test((short) 0x9A, (byte) 2, expected);
    }

    @Test
    public void test_STA_AbsoluteX()
    {
        MOS6502State expected = new MOS6502State((short) 0xFF, (short) 0x11, (short) 0xFF, (short) 0xFF, (short) 0xFF, (int) 0x0003);
        test_AbsoluteX_write((short) 0x9D, (byte) 5, expected, (short) 0xFF);
    }

    @Test
    public void test_LDY_Immediate_Positive()
    {
        MOS6502State expected = new MOS6502State((short) 0xFF, (short) 0xFF, (short) 0x15, (short) 0xFF, (short) 0x7D, (int) 0x0002);
        test_Immediate_read((short) 0xA0, (byte) 2, expected, (short) 0x15);
    }

    @Test
    public void test_LDY_Immediate_Zero()
    {
        MOS6502State expected = new MOS6502State((short) 0xFF, (short) 0xFF, (short) 0x00, (short) 0xFF, (short) 0x7F, (int) 0x0002);
        test_Immediate_read((short) 0xA0, (byte) 2, expected, (short) 0x00);
    }

    @Test
    public void test_LDY_Immediate_Negative()
    {
        MOS6502State expected = new MOS6502State((short) 0xFF, (short) 0xFF, (short) 0xA1, (short) 0xFF, (short) 0xFD, (int) 0x0002);
        test_Immediate_read((short) 0xA0, (byte) 2, expected, (short) 0xA1);
    }

    @Test
    public void test_LDA_IndirectX_Positive()
    {
        MOS6502State expected = new MOS6502State((short) 0x45, (short) 0x15, (short) 0xFF, (short) 0xFF, (short) 0x7D, (int) 0x0002);
        test_IndirectX_read((short) 0xA1, (byte) 6, expected, (short) 0x45);
    }

    @Test
    public void test_LDA_IndirectX_Zero()
    {
        MOS6502State expected = new MOS6502State((short) 0x00, (short) 0x15, (short) 0xFF, (short) 0xFF, (short) 0x7F, (int) 0x0002);
        test_IndirectX_read((short) 0xA1, (byte) 6, expected, (short) 0x00);
    }

    @Test
    public void test_LDA_IndirectX_Negative()
    {
        MOS6502State expected = new MOS6502State((short) 0xA1, (short) 0x15, (short) 0xFF, (short) 0xFF, (short) 0xFD, (int) 0x0002);
        test_IndirectX_read((short) 0xA1, (byte) 6, expected, (short) 0xA1);
    }

    @Test
    public void test_LDX_Immediate_Positive()
    {
        MOS6502State expected = new MOS6502State((short) 0xFF, (short) 0x15, (short) 0xFF, (short) 0xFF, (short) 0x7D, (int) 0x0002);
        test_Immediate_read((short) 0xA2, (byte) 2, expected, (short) 0x15);
    }

    @Test
    public void test_LDX_Immediate_Zero()
    {
        MOS6502State expected = new MOS6502State((short) 0xFF, (short) 0x00, (short) 0xFF, (short) 0xFF, (short) 0x7F, (int) 0x0002);
        test_Immediate_read((short) 0xA2, (byte) 2, expected, (short) 0x00);
    }

    @Test
    public void test_LDX_Immediate_Negative()
    {
        MOS6502State expected = new MOS6502State((short) 0xFF, (short) 0xA1, (short) 0xFF, (short) 0xFF, (short) 0xFD, (int) 0x0002);
        test_Immediate_read((short) 0xA2, (byte) 2, expected, (short) 0xA1);
    }

    @Test
    public void test_LDY_ZeroPage_Positive()
    {
        MOS6502State expected = new MOS6502State((short) 0xFF, (short) 0xFF, (short) 0x45, (short) 0xFF, (short) 0x7D, (int) 0x0002);
        test_ZeroPage_read((short) 0xA4, (byte) 3, expected, (short) 0x45);
    }

    @Test
    public void test_LDY_ZeroPage_Zero()
    {
        MOS6502State expected = new MOS6502State((short) 0xFF, (short) 0xFF, (short) 0x00, (short) 0xFF, (short) 0x7F, (int) 0x0002);
        test_ZeroPage_read((short) 0xA4, (byte) 3, expected, (short) 0x00);
    }

    @Test
    public void test_LDY_ZeroPage_Negative()
    {
        MOS6502State expected = new MOS6502State((short) 0xFF, (short) 0xFF, (short) 0xA1, (short) 0xFF, (short) 0xFD, (int) 0x0002);
        test_ZeroPage_read((short) 0xA4, (byte) 3, expected, (short) 0xA1);
    }

    @Test
    public void test_LDA_ZeroPage_Positive()
    {
        MOS6502State expected = new MOS6502State((short) 0x45, (short) 0xFF, (short) 0xFF, (short) 0xFF, (short) 0x7D, (int) 0x0002);
        test_ZeroPage_read((short) 0xA5, (byte) 3, expected, (short) 0x45);
    }

    @Test
    public void test_LDA_ZeroPage_Zero()
    {
        MOS6502State expected = new MOS6502State((short) 0x00, (short) 0xFF, (short) 0xFF, (short) 0xFF, (short) 0x7F, (int) 0x0002);
        test_ZeroPage_read((short) 0xA5, (byte) 3, expected, (short) 0x00);
    }

    @Test
    public void test_LDA_ZeroPage_Negative()
    {
        MOS6502State expected = new MOS6502State((short) 0xA1, (short) 0xFF, (short) 0xFF, (short) 0xFF, (short) 0xFD, (int) 0x0002);
        test_ZeroPage_read((short) 0xA5, (byte) 3, expected, (short) 0xA1);
    }

    @Test
    public void test_LDX_ZeroPage_Positive()
    {
        MOS6502State expected = new MOS6502State((short) 0xFF, (short) 0x45, (short) 0xFF, (short) 0xFF, (short) 0x7D, (int) 0x0002);
        test_ZeroPage_read((short) 0xA6, (byte) 3, expected, (short) 0x45);
    }

    @Test
    public void test_LDX_ZeroPage_Zero()
    {
        MOS6502State expected = new MOS6502State((short) 0xFF, (short) 0x00, (short) 0xFF, (short) 0xFF, (short) 0x7F, (int) 0x0002);
        test_ZeroPage_read((short) 0xA6, (byte) 3, expected, (short) 0x00);
    }

    @Test
    public void test_LDX_ZeroPage_Negative()
    {
        MOS6502State expected = new MOS6502State((short) 0xFF, (short) 0xA1, (short) 0xFF, (short) 0xFF, (short) 0xFD, (int) 0x0002);
        test_ZeroPage_read((short) 0xA6, (byte) 3, expected, (short) 0xA1);
    }

    @Test
    public void test_TAY_Positive()
    {
        MOS6502State expected = new MOS6502State((short) 0x45, (short) 0xFF, (short) 0x45, (short) 0xFF, (short) 0x7D, (int) 0x0001);
        cpu.setA((short) 0x45);
        EasyMock.expect(memory.readByte(0x0000)).andReturn((short) 0xA8);
        test((short) 0xA8, (byte) 2, expected);
    }

    @Test
    public void test_TAY_Zero()
    {
        MOS6502State expected = new MOS6502State((short) 0x00, (short) 0xFF, (short) 0x00, (short) 0xFF, (short) 0x7F, (int) 0x0001);
        cpu.setA((short) 0x00);
        EasyMock.expect(memory.readByte(0x0000)).andReturn((short) 0xA8);
        test((short) 0xA8, (byte) 2, expected);
    }

    @Test
    public void test_TAY_Negative()
    {
        MOS6502State expected = new MOS6502State((short) 0xA1, (short) 0xFF, (short) 0xA1, (short) 0xFF, (short) 0xFD, (int) 0x0001);
        cpu.setA((short) 0xA1);
        EasyMock.expect(memory.readByte(0x0000)).andReturn((short) 0xA8);
        test((short) 0xA8, (byte) 2, expected);
    }

    @Test
    public void test_LDA_Immediate_Positive()
    {
        MOS6502State expected = new MOS6502State((short) 0x45, (short) 0xFF, (short) 0xFF, (short) 0xFF, (short) 0x7D, (int) 0x0002);
        test_Immediate_read((short) 0xA9, (byte) 2, expected, (short) 0x45);
    }

    @Test
    public void test_LDA_Immediate_Zero()
    {
        MOS6502State expected = new MOS6502State((short) 0x00, (short) 0xFF, (short) 0xFF, (short) 0xFF, (short) 0x7F, (int) 0x0002);
        test_Immediate_read((short) 0xA9, (byte) 2, expected, (short) 0x00);
    }

    @Test
    public void test_LDA_Immediate_Negative()
    {
        MOS6502State expected = new MOS6502State((short) 0xA1, (short) 0xFF, (short) 0xFF, (short) 0xFF, (short) 0xFD, (int) 0x0002);
        test_Immediate_read((short) 0xA9, (byte) 2, expected, (short) 0xA1);
    }

    @Test
    public void test_TAX_Positive()
    {
        MOS6502State expected = new MOS6502State((short) 0x45, (short) 0x45, (short) 0xFF, (short) 0xFF, (short) 0x7D, (int) 0x0001);
        cpu.setA((short) 0x45);
        EasyMock.expect(memory.readByte(0x0000)).andReturn((short) 0xAA);
        test((short) 0xAA, (byte) 2, expected);
    }

    @Test
    public void test_TAX_Zero()
    {
        MOS6502State expected = new MOS6502State((short) 0x00, (short) 0x00, (short) 0xFF, (short) 0xFF, (short) 0x7F, (int) 0x0001);
        cpu.setA((short) 0x00);
        EasyMock.expect(memory.readByte(0x0000)).andReturn((short) 0xAA);
        test((short) 0xAA, (byte) 2, expected);
    }

    @Test
    public void test_TAX_Negative()
    {
        MOS6502State expected = new MOS6502State((short) 0xA1, (short) 0xA1, (short) 0xFF, (short) 0xFF, (short) 0xFD, (int) 0x0001);
        cpu.setA((short) 0xA1);
        EasyMock.expect(memory.readByte(0x0000)).andReturn((short) 0xAA);
        test((short) 0xAA, (byte) 2, expected);
    }

    @Test
    public void test_LDY_Absolute_Positive()
    {
        MOS6502State expected = new MOS6502State((short) 0xFF, (short) 0xFF, (short) 0x64, (short) 0xFF, (short) 0x7D, (int) 0x0003);
        test_Absolute_read((short) 0xAC, (byte) 4, expected, (short) 0x64);
    }

    @Test
    public void test_LDY_Absolute_Zero()
    {
        MOS6502State expected = new MOS6502State((short) 0xFF, (short) 0xFF, (short) 0x00, (short) 0xFF, (short) 0x7F, (int) 0x0003);
        test_Absolute_read((short) 0xAC, (byte) 4, expected, (short) 0x00);
    }

    @Test
    public void test_LDY_Absolute_Negative()
    {
        MOS6502State expected = new MOS6502State((short) 0xFF, (short) 0xFF, (short) 0xA1, (short) 0xFF, (short) 0xFD, (int) 0x0003);
        test_Absolute_read((short) 0xAC, (byte) 4, expected, (short) 0xA1);
    }

    @Test
    public void test_LDA_Absolute_Positive()
    {
        MOS6502State expected = new MOS6502State((short) 0x64, (short) 0xFF, (short) 0xFF, (short) 0xFF, (short) 0x7D, (int) 0x0003);
        test_Absolute_read((short) 0xAD, (byte) 4, expected, (short) 0x64);
    }

    @Test
    public void test_LDA_Absolute_Zero()
    {
        MOS6502State expected = new MOS6502State((short) 0x00, (short) 0xFF, (short) 0xFF, (short) 0xFF, (short) 0x7F, (int) 0x0003);
        test_Absolute_read((short) 0xAD, (byte) 4, expected, (short) 0x00);
    }

    @Test
    public void test_LDA_Absolute_Negative()
    {
        MOS6502State expected = new MOS6502State((short) 0xA1, (short) 0xFF, (short) 0xFF, (short) 0xFF, (short) 0xFD, (int) 0x0003);
        test_Absolute_read((short) 0xAD, (byte) 4, expected, (short) 0xA1);
    }

    @Test
    public void test_LDX_Absolute_Positive()
    {
        MOS6502State expected = new MOS6502State((short) 0xFF, (short) 0x64, (short) 0xFF, (short) 0xFF, (short) 0x7D, (int) 0x0003);
        test_Absolute_read((short) 0xAE, (byte) 4, expected, (short) 0x64);
    }

    @Test
    public void test_LDX_Absolute_Zero()
    {
        MOS6502State expected = new MOS6502State((short) 0xFF, (short) 0x00, (short) 0xFF, (short) 0xFF, (short) 0x7F, (int) 0x0003);
        test_Absolute_read((short) 0xAE, (byte) 4, expected, (short) 0x00);
    }

    @Test
    public void test_LDX_Absolute_Negative()
    {
        MOS6502State expected = new MOS6502State((short) 0xFF, (short) 0xA1, (short) 0xFF, (short) 0xFF, (short) 0xFD, (int) 0x0003);
        test_Absolute_read((short) 0xAE, (byte) 4, expected, (short) 0xA1);
    }

    @Test
    public void test_LDA_IndirectY_Positive()
    {
        MOS6502State expected = new MOS6502State((short) 0x45, (short) 0xFF, (short) 0x15, (short) 0xFF, (short) 0x7D, (int) 0x0002);
        test_IndirectY_read((short) 0xB1, (byte) 5, expected, (short) 0x45);
    }

    @Test
    public void test_LDA_IndirectY_Zero()
    {
        MOS6502State expected = new MOS6502State((short) 0x00, (short) 0xFF, (short) 0x15, (short) 0xFF, (short) 0x7F, (int) 0x0002);
        test_IndirectY_read((short) 0xB1, (byte) 5, expected, (short) 0x00);
    }

    @Test
    public void test_LDA_IndirectY_Negative()
    {
        MOS6502State expected = new MOS6502State((short) 0xA1, (short) 0xFF, (short) 0x15, (short) 0xFF, (short) 0xFD, (int) 0x0002);
        test_IndirectY_read((short) 0xB1, (byte) 5, expected, (short) 0xA1);
    }

    @Test
    public void test_LDY_ZeroPageX_Positive()
    {
        MOS6502State expected = new MOS6502State((short) 0xFF, (short) 0x13, (short) 0x45, (short) 0xFF, (short) 0x7D, (int) 0x0002);
        test_ZeroPageX_read((short) 0xB4, (byte) 4, expected, (short) 0x45);
    }

    @Test
    public void test_LDY_ZeroPageX_Zero()
    {
        MOS6502State expected = new MOS6502State((short) 0xFF, (short) 0x13, (short) 0x00, (short) 0xFF, (short) 0x7F, (int) 0x0002);
        test_ZeroPageX_read((short) 0xB4, (byte) 4, expected, (short) 0x00);
    }

    @Test
    public void test_LDY_ZeroPageX_Negative()
    {
        MOS6502State expected = new MOS6502State((short) 0xFF, (short) 0x13, (short) 0xA1, (short) 0xFF, (short) 0xFD, (int) 0x0002);
        test_ZeroPageX_read((short) 0xB4, (byte) 4, expected, (short) 0xA1);
    }

    @Test
    public void test_LDA_ZeroPageX_Positive()
    {
        MOS6502State expected = new MOS6502State((short) 0x45, (short) 0x13, (short) 0xFF, (short) 0xFF, (short) 0x7D, (int) 0x0002);
        test_ZeroPageX_read((short) 0xB5, (byte) 4, expected, (short) 0x45);
    }

    @Test
    public void test_LDA_ZeroPageX_Zero()
    {
        MOS6502State expected = new MOS6502State((short) 0x00, (short) 0x13, (short) 0xFF, (short) 0xFF, (short) 0x7F, (int) 0x0002);
        test_ZeroPageX_read((short) 0xB5, (byte) 4, expected, (short) 0x00);
    }

    @Test
    public void test_LDA_ZeroPageX_Negative()
    {
        MOS6502State expected = new MOS6502State((short) 0xA1, (short) 0x13, (short) 0xFF, (short) 0xFF, (short) 0xFD, (int) 0x0002);
        test_ZeroPageX_read((short) 0xB5, (byte) 4, expected, (short) 0xA1);
    }

    @Test
    public void test_LDX_ZeroPageY_Positive()
    {
        MOS6502State expected = new MOS6502State((short) 0xFF, (short) 0x45, (short) 0x13, (short) 0xFF, (short) 0x7D, (int) 0x0002);
        test_ZeroPageY_read((short) 0xB6, (byte) 4, expected, (short) 0x45);
    }

    @Test
    public void test_LDX_ZeroPageY_Zero()
    {
        MOS6502State expected = new MOS6502State((short) 0xFF, (short) 0x00, (short) 0x13, (short) 0xFF, (short) 0x7F, (int) 0x0002);
        test_ZeroPageY_read((short) 0xB6, (byte) 4, expected, (short) 0x00);
    }

    @Test
    public void test_LDX_ZeroPageY_Negative()
    {
        MOS6502State expected = new MOS6502State((short) 0xFF, (short) 0xA1, (short) 0x13, (short) 0xFF, (short) 0xFD, (int) 0x0002);
        test_ZeroPageY_read((short) 0xB6, (byte) 4, expected, (short) 0xA1);
    }

    @Test
    public void test_LDA_AbsoluteY_Positive()
    {
        MOS6502State expected = new MOS6502State((short) 0x45, (short) 0xFF, (short) 0x13, (short) 0xFF, (short) 0x7D, (int) 0x0003);
        test_AbsoluteY_read((short) 0xB9, (byte) 4, expected, (short) 0x45);
    }

    @Test
    public void test_LDA_AbsoluteY_Zero()
    {
        MOS6502State expected = new MOS6502State((short) 0x00, (short) 0xFF, (short) 0x13, (short) 0xFF, (short) 0x7F, (int) 0x0003);
        test_AbsoluteY_read((short) 0xB9, (byte) 4, expected, (short) 0x00);
    }

    @Test
    public void test_LDA_AbsoluteY_Negative()
    {
        MOS6502State expected = new MOS6502State((short) 0xA1, (short) 0xFF, (short) 0x13, (short) 0xFF, (short) 0xFD, (int) 0x0003);
        test_AbsoluteY_read((short) 0xB9, (byte) 4, expected, (short) 0xA1);
    }

    @Test
    public void test_TSX_Positive()
    {
        MOS6502State expected = new MOS6502State((short) 0xFF, (short) 0x13, (short) 0xFF, (short) 0x13, (short) 0x7D, (int) 0x0001);
        cpu.setSP((short) 0x13);
        EasyMock.expect(memory.readByte(0x0000)).andReturn((short) 0xBA);
        test((short) 0xBA, (byte) 2, expected);
    }

    @Test
    public void test_TSX_Zero()
    {
        MOS6502State expected = new MOS6502State((short) 0xFF, (short) 0x00, (short) 0xFF, (short) 0x00, (short) 0x7F, (int) 0x0001);
        cpu.setSP((short) 0x00);
        EasyMock.expect(memory.readByte(0x0000)).andReturn((short) 0xBA);
        test((short) 0xBA, (byte) 2, expected);
    }

    @Test
    public void test_TSX_Negative()
    {
        MOS6502State expected = new MOS6502State((short) 0xFF, (short) 0xA1, (short) 0xFF, (short) 0xA1, (short) 0xFD, (int) 0x0001);
        cpu.setSP((short) 0xA1);
        EasyMock.expect(memory.readByte(0x0000)).andReturn((short) 0xBA);
        test((short) 0xBA, (byte) 2, expected);
    }

    @Test
    public void test_LDY_AbsoluteX_Positive()
    {
        MOS6502State expected = new MOS6502State((short) 0xFF, (short) 0x13, (short) 0x45, (short) 0xFF, (short) 0x7D, (int) 0x0003);
        test_AbsoluteX_read((short) 0xBC, (byte) 4, expected, (short) 0x45);
    }

    @Test
    public void test_LDY_AbsoluteX_Zero()
    {
        MOS6502State expected = new MOS6502State((short) 0xFF, (short) 0x13, (short) 0x00, (short) 0xFF, (short) 0x7F, (int) 0x0003);
        test_AbsoluteX_read((short) 0xBC, (byte) 4, expected, (short) 0x00);
    }

    @Test
    public void test_LDY_AbsoluteX_Negative()
    {
        MOS6502State expected = new MOS6502State((short) 0xFF, (short) 0x13, (short) 0xA1, (short) 0xFF, (short) 0xFD, (int) 0x0003);
        test_AbsoluteX_read((short) 0xBC, (byte) 4, expected, (short) 0xA1);
    }

    @Test
    public void test_LDA_AbsoluteX_Positive()
    {
        MOS6502State expected = new MOS6502State((short) 0x45, (short) 0x13, (short) 0xFF, (short) 0xFF, (short) 0x7D, (int) 0x0003);
        test_AbsoluteX_read((short) 0xBD, (byte) 4, expected, (short) 0x45);
    }

    @Test
    public void test_LDA_AbsoluteX_Zero()
    {
        MOS6502State expected = new MOS6502State((short) 0x00, (short) 0x13, (short) 0xFF, (short) 0xFF, (short) 0x7F, (int) 0x0003);
        test_AbsoluteX_read((short) 0xBD, (byte) 4, expected, (short) 0x00);
    }

    @Test
    public void test_LDA_AbsoluteX_Negative()
    {
        MOS6502State expected = new MOS6502State((short) 0xA1, (short) 0x13, (short) 0xFF, (short) 0xFF, (short) 0xFD, (int) 0x0003);
        test_AbsoluteX_read((short) 0xBD, (byte) 4, expected, (short) 0xA1);
    }

    @Test
    public void test_LDX_AbsoluteY_Positive()
    {
        MOS6502State expected = new MOS6502State((short) 0xFF, (short) 0x45, (short) 0x13, (short) 0xFF, (short) 0x7D, (int) 0x0003);
        test_AbsoluteY_read((short) 0xBE, (byte) 4, expected, (short) 0x45);
    }

    @Test
    public void test_LDX_AbsoluteY_Zero()
    {
        MOS6502State expected = new MOS6502State((short) 0xFF, (short) 0x00, (short) 0x13, (short) 0xFF, (short) 0x7F, (int) 0x0003);
        test_AbsoluteY_read((short) 0xBE, (byte) 4, expected, (short) 0x00);
    }

    @Test
    public void test_LDX_AbsoluteY_Negative()
    {
        MOS6502State expected = new MOS6502State((short) 0xFF, (short) 0xA1, (short) 0x13, (short) 0xFF, (short) 0xFD, (int) 0x0003);
        test_AbsoluteY_read((short) 0xBE, (byte) 4, expected, (short) 0xA1);
    }

    @Test
    public void test_INC_ZeroPage_Positive()
    {
        MOS6502State expected = new MOS6502State((short) 0xFF, (short) 0xFF, (short) 0xFF, (short) 0xFF, (short) 0x7D, (int) 0x0002);
        EasyMock.expect(memory.readByte(0x0000)).andReturn((short) 0xE6);
        EasyMock.expect(memory.readByte(0x0001)).andReturn((short) 0xE4);
        EasyMock.expect(memory.readByte(0x00E4)).andReturn((short) 0x1E);
        memory.writeByte(0x00E4, (short) 0x1F);
        test((short) 0xE6, (byte) 5, expected);
    }

    @Test
    public void test_INC_ZeroPage_Zero()
    {
        MOS6502State expected = new MOS6502State((short) 0xFF, (short) 0xFF, (short) 0xFF, (short) 0xFF, (short) 0x7F, (int) 0x0002);
        EasyMock.expect(memory.readByte(0x0000)).andReturn((short) 0xE6);
        EasyMock.expect(memory.readByte(0x0001)).andReturn((short) 0xE4);
        EasyMock.expect(memory.readByte(0x00E4)).andReturn((short) 0xFF);
        memory.writeByte(0x00E4, (short) 0x00);
        test((short) 0xE6, (byte) 5, expected);
    }

    @Test
    public void test_INC_ZeroPage_Negative()
    {
        MOS6502State expected = new MOS6502State((short) 0xFF, (short) 0xFF, (short) 0xFF, (short) 0xFF, (short) 0xFD, (int) 0x0002);
        EasyMock.expect(memory.readByte(0x0000)).andReturn((short) 0xE6);
        EasyMock.expect(memory.readByte(0x0001)).andReturn((short) 0xE4);
        EasyMock.expect(memory.readByte(0x00E4)).andReturn((short) 0x7F);
        memory.writeByte(0x00E4, (short) 0x80);
        test((short) 0xE6, (byte) 5, expected);
    }

    @Test
    public void test_INX_Positive()
    {
        MOS6502State expected = new MOS6502State((short) 0xFF, (short) 0x01, (short) 0xFF, (short) 0xFF, (short) 0x7D, (int) 0x0001);
        cpu.setX((short) 0x00);
        EasyMock.expect(memory.readByte(0x0000)).andReturn((short) 0xE8);
        test((short) 0xE8, (byte) 2, expected);
    }

    @Test
    public void test_INX_Zero()
    {
        MOS6502State expected = new MOS6502State((short) 0xFF, (short) 0x00, (short) 0xFF, (short) 0xFF, (short) 0x7F, (int) 0x0001);
        cpu.setX((short) 0xFF);
        EasyMock.expect(memory.readByte(0x0000)).andReturn((short) 0xE8);
        test((short) 0xE8, (byte) 2, expected);
    }

    @Test
    public void test_INX_Negative()
    {
        MOS6502State expected = new MOS6502State((short) 0xFF, (short) 0x80, (short) 0xFF, (short) 0xFF, (short) 0xFD, (int) 0x0001);
        cpu.setX((short) 0x7F);
        EasyMock.expect(memory.readByte(0x0000)).andReturn((short) 0xE8);
        test((short) 0xE8, (byte) 2, expected);
    }

    @Test
    public void test_NOP()
    {
        MOS6502State expected = new MOS6502State((short) 0xFF, (short) 0xFF, (short) 0xFF, (short) 0xFF, (short) 0xFF, (int) 0x0001);
        EasyMock.expect(memory.readByte(0x0000)).andReturn((short) 0xEA);
        test((short) 0xEA, (byte) 2, expected);
    }

    @Test
    public void test_DEX_Positive()
    {
        MOS6502State expected = new MOS6502State((short) 0xFF, (short) 0x01, (short) 0xFF, (short) 0xFF, (short) 0x7D, (int) 0x0001);
        cpu.setX((short) 0x02);
        EasyMock.expect(memory.readByte(0x0000)).andReturn((short) 0xCA);
        test((short) 0xCA, (byte) 2, expected);
    }

    @Test
    public void test_DEX_Zero()
    {
        MOS6502State expected = new MOS6502State((short) 0xFF, (short) 0x00, (short) 0xFF, (short) 0xFF, (short) 0x7F, (int) 0x0001);
        cpu.setX((short) 0x01);
        EasyMock.expect(memory.readByte(0x0000)).andReturn((short) 0xCA);
        test((short) 0xCA, (byte) 2, expected);
    }

    @Test
    public void test_DEX_Negative()
    {
        MOS6502State expected = new MOS6502State((short) 0xFF, (short) 0xFF, (short) 0xFF, (short) 0xFF, (short) 0xFD, (int) 0x0001);
        cpu.setX((short) 0x00);
        EasyMock.expect(memory.readByte(0x0000)).andReturn((short) 0xCA);
        test((short) 0xCA, (byte) 2, expected);
    }

    @Test
    public void test_INY_Positive()
    {
        MOS6502State expected = new MOS6502State((short) 0xFF, (short) 0xFF, (short) 0x01, (short) 0xFF, (short) 0x7D, (int) 0x0001);
        cpu.setY((short) 0x00);
        EasyMock.expect(memory.readByte(0x0000)).andReturn((short) 0xC8);
        test((short) 0xC8, (byte) 2, expected);
    }

    @Test
    public void test_INY_Zero()
    {
        MOS6502State expected = new MOS6502State((short) 0xFF, (short) 0xFF, (short) 0x00, (short) 0xFF, (short) 0x7F, (int) 0x0001);
        cpu.setY((short) 0xFF);
        EasyMock.expect(memory.readByte(0x0000)).andReturn((short) 0xC8);
        test((short) 0xC8, (byte) 2, expected);
    }

    @Test
    public void test_INY_Negative()
    {
        MOS6502State expected = new MOS6502State((short) 0xFF, (short) 0xFF, (short) 0x80, (short) 0xFF, (short) 0xFD, (int) 0x0001);
        cpu.setY((short) 0x7F);
        EasyMock.expect(memory.readByte(0x0000)).andReturn((short) 0xC8);
        test((short) 0xC8, (byte) 2, expected);
    }

    @Test
    public void test_INC_ZeroPageX_Positive()
    {
        MOS6502State expected = new MOS6502State((short) 0xFF, (short) 0x02, (short) 0xFF, (short) 0xFF, (short) 0x7D, (int) 0x0002);
        cpu.setX((short) 0x02);
        EasyMock.expect(memory.readByte(0x0000)).andReturn((short) 0xF6);
        EasyMock.expect(memory.readByte(0x0001)).andReturn((short) 0xE4);
        EasyMock.expect(memory.readByte(0x00E6)).andReturn((short) 0x1E);
        memory.writeByte(0x00E6, (short) 0x1F);
        test((short) 0xF6, (byte) 6, expected);
    }

    @Test
    public void test_INC_ZeroPageX_Zero()
    {
        MOS6502State expected = new MOS6502State((short) 0xFF, (short) 0x02, (short) 0xFF, (short) 0xFF, (short) 0x7F, (int) 0x0002);
        cpu.setX((short) 0x02);
        EasyMock.expect(memory.readByte(0x0000)).andReturn((short) 0xF6);
        EasyMock.expect(memory.readByte(0x0001)).andReturn((short) 0xE4);
        EasyMock.expect(memory.readByte(0x00E6)).andReturn((short) 0xFF);
        memory.writeByte(0x00E6, (short) 0x00);
        test((short) 0xF6, (byte) 6, expected);
    }

    @Test
    public void test_INC_ZeroPageX_Negative()
    {
        MOS6502State expected = new MOS6502State((short) 0xFF, (short) 0x02, (short) 0xFF, (short) 0xFF, (short) 0xFD, (int) 0x0002);
        cpu.setX((short) 0x02);
        EasyMock.expect(memory.readByte(0x0000)).andReturn((short) 0xF6);
        EasyMock.expect(memory.readByte(0x0001)).andReturn((short) 0xE4);
        EasyMock.expect(memory.readByte(0x00E6)).andReturn((short) 0x7F);
        memory.writeByte(0x00E6, (short) 0x80);
        test((short) 0xF6, (byte) 6, expected);
    }

    @Test
    public void test_INC_Absolute_Positive()
    {
        MOS6502State expected = new MOS6502State((short) 0xFF, (short) 0xFF, (short) 0xFF, (short) 0xFF, (short) 0x7D, (int) 0x0003);
        EasyMock.expect(memory.readByte(0x0000)).andReturn((short) 0xEE);
        EasyMock.expect(memory.readByte(0x0001)).andReturn((short) 0xE4);
        EasyMock.expect(memory.readByte(0x0002)).andReturn((short) 0x14);
        EasyMock.expect(memory.readByte(0x14E4)).andReturn((short) 0x1E);
        memory.writeByte(0x14E4, (short) 0x1F);
        test((short) 0xEE, (byte) 6, expected);
    }

    @Test
    public void test_INC_Absolute_Zero()
    {
        MOS6502State expected = new MOS6502State((short) 0xFF, (short) 0xFF, (short) 0xFF, (short) 0xFF, (short) 0x7F, (int) 0x0003);
        EasyMock.expect(memory.readByte(0x0000)).andReturn((short) 0xEE);
        EasyMock.expect(memory.readByte(0x0001)).andReturn((short) 0xE4);
        EasyMock.expect(memory.readByte(0x0002)).andReturn((short) 0x14);
        EasyMock.expect(memory.readByte(0x14E4)).andReturn((short) 0xFF);
        memory.writeByte(0x14E4, (short) 0x00);
        test((short) 0xEE, (byte) 6, expected);
    }

    @Test
    public void test_INC_Absolute_Negative()
    {
        MOS6502State expected = new MOS6502State((short) 0xFF, (short) 0xFF, (short) 0xFF, (short) 0xFF, (short) 0xFD, (int) 0x0003);
        EasyMock.expect(memory.readByte(0x0000)).andReturn((short) 0xEE);
        EasyMock.expect(memory.readByte(0x0001)).andReturn((short) 0xE4);
        EasyMock.expect(memory.readByte(0x0002)).andReturn((short) 0x14);
        EasyMock.expect(memory.readByte(0x14E4)).andReturn((short) 0x7F);
        memory.writeByte(0x14E4, (short) 0x80);
        test((short) 0xEE, (byte) 6, expected);
    }

    @Test
    public void test_INC_AbsoluteX_Positive()
    {
        MOS6502State expected = new MOS6502State((short) 0xFF, (short) 0x04, (short) 0xFF, (short) 0xFF, (short) 0x7D, (int) 0x0003);
        cpu.setX((short) 0x04);
        EasyMock.expect(memory.readByte(0x0000)).andReturn((short) 0xFE);
        EasyMock.expect(memory.readByte(0x0001)).andReturn((short) 0xE4);
        EasyMock.expect(memory.readByte(0x0002)).andReturn((short) 0x14);
        EasyMock.expect(memory.readByte(0x14E8)).andReturn((short) 0x1E);
        memory.writeByte(0x14E8, (short) 0x1F);
        test((short) 0xFE, (byte) 7, expected);
    }

    @Test
    public void test_INC_AbsoluteX_Zero()
    {
        MOS6502State expected = new MOS6502State((short) 0xFF, (short) 0x04, (short) 0xFF, (short) 0xFF, (short) 0x7F, (int) 0x0003);
        cpu.setX((short) 0x04);
        EasyMock.expect(memory.readByte(0x0000)).andReturn((short) 0xFE);
        EasyMock.expect(memory.readByte(0x0001)).andReturn((short) 0xE4);
        EasyMock.expect(memory.readByte(0x0002)).andReturn((short) 0x14);
        EasyMock.expect(memory.readByte(0x14E8)).andReturn((short) 0xFF);
        memory.writeByte(0x14E8, (short) 0x00);
        test((short) 0xFE, (byte) 7, expected);
    }

    @Test
    public void test_INC_AbsoluteX_Negative()
    {
        MOS6502State expected = new MOS6502State((short) 0xFF, (short) 0x04, (short) 0xFF, (short) 0xFF, (short) 0xFD, (int) 0x0003);
        cpu.setX((short) 0x04);
        EasyMock.expect(memory.readByte(0x0000)).andReturn((short) 0xFE);
        EasyMock.expect(memory.readByte(0x0001)).andReturn((short) 0xE4);
        EasyMock.expect(memory.readByte(0x0002)).andReturn((short) 0x14);
        EasyMock.expect(memory.readByte(0x14E8)).andReturn((short) 0x7F);
        memory.writeByte(0x14E8, (short) 0x80);
        test((short) 0xFE, (byte) 7, expected);
    }

    @Test
    public void test_DEC_ZeroPage_Positive()
    {
        MOS6502State expected = new MOS6502State((short) 0xFF, (short) 0xFF, (short) 0xFF, (short) 0xFF, (short) 0x7D, (int) 0x0002);
        EasyMock.expect(memory.readByte(0x0000)).andReturn((short) 0xC6);
        EasyMock.expect(memory.readByte(0x0001)).andReturn((short) 0xE4);
        EasyMock.expect(memory.readByte(0x00E4)).andReturn((short) 0x1E);
        memory.writeByte(0x00E4, (short) 0x1D);
        test((short) 0xC6, (byte) 5, expected);
    }

    @Test
    public void test_DEC_ZeroPage_Zero()
    {
        MOS6502State expected = new MOS6502State((short) 0xFF, (short) 0xFF, (short) 0xFF, (short) 0xFF, (short) 0x7F, (int) 0x0002);
        EasyMock.expect(memory.readByte(0x0000)).andReturn((short) 0xC6);
        EasyMock.expect(memory.readByte(0x0001)).andReturn((short) 0xE4);
        EasyMock.expect(memory.readByte(0x00E4)).andReturn((short) 0x01);
        memory.writeByte(0x00E4, (short) 0x00);
        test((short) 0xC6, (byte) 5, expected);
    }

    @Test
    public void test_DEC_ZeroPage_Negative()
    {
        MOS6502State expected = new MOS6502State((short) 0xFF, (short) 0xFF, (short) 0xFF, (short) 0xFF, (short) 0xFD, (int) 0x0002);
        EasyMock.expect(memory.readByte(0x0000)).andReturn((short) 0xC6);
        EasyMock.expect(memory.readByte(0x0001)).andReturn((short) 0xE4);
        EasyMock.expect(memory.readByte(0x00E4)).andReturn((short) 0x00);
        memory.writeByte(0x00E4, (short) 0xFF);
        test((short) 0xC6, (byte) 5, expected);
    }

    @Test
    public void test_DEC_ZeroPageX_Positive()
    {
        MOS6502State expected = new MOS6502State((short) 0xFF, (short) 0x02, (short) 0xFF, (short) 0xFF, (short) 0x7D, (int) 0x0002);
        cpu.setX((short) 0x02);
        EasyMock.expect(memory.readByte(0x0000)).andReturn((short) 0xD6);
        EasyMock.expect(memory.readByte(0x0001)).andReturn((short) 0xE4);
        EasyMock.expect(memory.readByte(0x00E6)).andReturn((short) 0x1E);
        memory.writeByte(0x00E6, (short) 0x1D);
        test((short) 0xD6, (byte) 6, expected);
    }

    @Test
    public void test_DEC_ZeroPageX_Zero()
    {
        MOS6502State expected = new MOS6502State((short) 0xFF, (short) 0x02, (short) 0xFF, (short) 0xFF, (short) 0x7F, (int) 0x0002);
        cpu.setX((short) 0x02);
        EasyMock.expect(memory.readByte(0x0000)).andReturn((short) 0xD6);
        EasyMock.expect(memory.readByte(0x0001)).andReturn((short) 0xE4);
        EasyMock.expect(memory.readByte(0x00E6)).andReturn((short) 0x01);
        memory.writeByte(0x00E6, (short) 0x00);
        test((short) 0xD6, (byte) 6, expected);
    }

    @Test
    public void test_DEC_ZeroPageX_Negative()
    {
        MOS6502State expected = new MOS6502State((short) 0xFF, (short) 0x02, (short) 0xFF, (short) 0xFF, (short) 0xFD, (int) 0x0002);
        cpu.setX((short) 0x02);
        EasyMock.expect(memory.readByte(0x0000)).andReturn((short) 0xD6);
        EasyMock.expect(memory.readByte(0x0001)).andReturn((short) 0xE4);
        EasyMock.expect(memory.readByte(0x00E6)).andReturn((short) 0x00);
        memory.writeByte(0x00E6, (short) 0xFF);
        test((short) 0xD6, (byte) 6, expected);
    }

    @Test
    public void test_DEC_Absolute_Positive()
    {
        MOS6502State expected = new MOS6502State((short) 0xFF, (short) 0xFF, (short) 0xFF, (short) 0xFF, (short) 0x7D, (int) 0x0003);
        EasyMock.expect(memory.readByte(0x0000)).andReturn((short) 0xCE);
        EasyMock.expect(memory.readByte(0x0001)).andReturn((short) 0xE4);
        EasyMock.expect(memory.readByte(0x0002)).andReturn((short) 0x14);
        EasyMock.expect(memory.readByte(0x14E4)).andReturn((short) 0x1E);
        memory.writeByte(0x14E4, (short) 0x1D);
        test((short) 0xCE, (byte) 6, expected);
    }

    @Test
    public void test_DEC_Absolute_Zero()
    {
        MOS6502State expected = new MOS6502State((short) 0xFF, (short) 0xFF, (short) 0xFF, (short) 0xFF, (short) 0x7F, (int) 0x0003);
        EasyMock.expect(memory.readByte(0x0000)).andReturn((short) 0xCE);
        EasyMock.expect(memory.readByte(0x0001)).andReturn((short) 0xE4);
        EasyMock.expect(memory.readByte(0x0002)).andReturn((short) 0x14);
        EasyMock.expect(memory.readByte(0x14E4)).andReturn((short) 0x01);
        memory.writeByte(0x14E4, (short) 0x00);
        test((short) 0xCE, (byte) 6, expected);
    }

    @Test
    public void test_DEC_Absolute_Negative()
    {
        MOS6502State expected = new MOS6502State((short) 0xFF, (short) 0xFF, (short) 0xFF, (short) 0xFF, (short) 0xFD, (int) 0x0003);
        EasyMock.expect(memory.readByte(0x0000)).andReturn((short) 0xCE);
        EasyMock.expect(memory.readByte(0x0001)).andReturn((short) 0xE4);
        EasyMock.expect(memory.readByte(0x0002)).andReturn((short) 0x14);
        EasyMock.expect(memory.readByte(0x14E4)).andReturn((short) 0x00);
        memory.writeByte(0x14E4, (short) 0xFF);
        test((short) 0xCE, (byte) 6, expected);
    }

    @Test
    public void test_DEC_AbsoluteX_Positive()
    {
        MOS6502State expected = new MOS6502State((short) 0xFF, (short) 0x04, (short) 0xFF, (short) 0xFF, (short) 0x7D, (int) 0x0003);
        cpu.setX((short) 0x04);
        EasyMock.expect(memory.readByte(0x0000)).andReturn((short) 0xDE);
        EasyMock.expect(memory.readByte(0x0001)).andReturn((short) 0xE4);
        EasyMock.expect(memory.readByte(0x0002)).andReturn((short) 0x14);
        EasyMock.expect(memory.readByte(0x14E8)).andReturn((short) 0x1E);
        memory.writeByte(0x14E8, (short) 0x1D);
        test((short) 0xDE, (byte) 7, expected);
    }

    @Test
    public void test_DEC_AbsoluteX_Zero()
    {
        MOS6502State expected = new MOS6502State((short) 0xFF, (short) 0x04, (short) 0xFF, (short) 0xFF, (short) 0x7F, (int) 0x0003);
        cpu.setX((short) 0x04);
        EasyMock.expect(memory.readByte(0x0000)).andReturn((short) 0xDE);
        EasyMock.expect(memory.readByte(0x0001)).andReturn((short) 0xE4);
        EasyMock.expect(memory.readByte(0x0002)).andReturn((short) 0x14);
        EasyMock.expect(memory.readByte(0x14E8)).andReturn((short) 0x01);
        memory.writeByte(0x14E8, (short) 0x00);
        test((short) 0xDE, (byte) 7, expected);
    }

    @Test
    public void test_DEC_AbsoluteX_Negative()
    {
        MOS6502State expected = new MOS6502State((short) 0xFF, (short) 0x04, (short) 0xFF, (short) 0xFF, (short) 0xFD, (int) 0x0003);
        cpu.setX((short) 0x04);
        EasyMock.expect(memory.readByte(0x0000)).andReturn((short) 0xDE);
        EasyMock.expect(memory.readByte(0x0001)).andReturn((short) 0xE4);
        EasyMock.expect(memory.readByte(0x0002)).andReturn((short) 0x14);
        EasyMock.expect(memory.readByte(0x14E8)).andReturn((short) 0x00);
        memory.writeByte(0x14E8, (short) 0xFF);
        test((short) 0xDE, (byte) 7, expected);
    }

    @Test
    public void test_JMP_Absolute()
    {
        MOS6502State expected = new MOS6502State((short) 0xFF, (short) 0xFF, (short) 0xFF, (short) 0xFF, (short) 0xFF, (int) 0x14E4);
        EasyMock.expect(memory.readByte(0x0000)).andReturn((short) 0x4C);
        EasyMock.expect(memory.readByte(0x0001)).andReturn((short) 0xE4);
        EasyMock.expect(memory.readByte(0x0002)).andReturn((short) 0x14);
        test((short) 0x4C, (byte) 3, expected);
    }

    @Test
    public void test_JMP_Indirect()
    {
        MOS6502State expected = new MOS6502State((short) 0xFF, (short) 0xFF, (short) 0xFF, (short) 0xFF, (short) 0xFF, (int) 0x7802);
        EasyMock.expect(memory.readByte(0x0000)).andReturn((short) 0x6C);
        EasyMock.expect(memory.readByte(0x0001)).andReturn((short) 0xE4);
        EasyMock.expect(memory.readByte(0x0002)).andReturn((short) 0x14);
        EasyMock.expect(memory.readByte(0x14E4)).andReturn((short) 0x02);
        EasyMock.expect(memory.readByte(0x14E5)).andReturn((short) 0x78);
        test((short) 0x6C, (byte) 5, expected);
    }

    @Test
    public void test_JSR()
    {
        MOS6502State expected = new MOS6502State((short) 0xFF, (short) 0xFF, (short) 0xFF, (short) 0xFD, (short) 0xFF, (int) 0xF1E4);
        EasyMock.expect(memory.readByte(0x0000)).andReturn((short) 0x20);
        EasyMock.expect(memory.readByte(0x0001)).andReturn((short) 0xE4);
        EasyMock.expect(memory.readByte(0x0002)).andReturn((short) 0xF1);
        memory.writeByte(0x01FF, (short) 0x00);
        memory.writeByte(0x01FE, (short) 0x02);
        test((short) 0x20, (byte) 6, expected);
    }

    @Test
    public void test_RTS()
    {
        MOS6502State expected = new MOS6502State((short) 0xFF, (short) 0xFF, (short) 0xFF, (short) 0xC9, (short) 0xFF, (int) 0xF1E5);
        cpu.setSP((short) 0xC7);
        EasyMock.expect(memory.readByte(0x0000)).andReturn((short) 0x60);
        EasyMock.expect(memory.readByte(0x01C7)).andReturn((short) 0xE4);
        EasyMock.expect(memory.readByte(0x01C8)).andReturn((short) 0xF1);
        test((short) 0x60, (byte) 6, expected);
    }

}
