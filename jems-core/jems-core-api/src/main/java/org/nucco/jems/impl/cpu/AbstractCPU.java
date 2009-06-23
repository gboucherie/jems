package org.nucco.jems.impl.cpu;

import org.nucco.jems.api.cpu.CPU;
import org.nucco.jems.api.memory.Memory;
import org.nucco.jems.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractCPU implements CPU
{

    private static final Logger LOG = LoggerFactory.getLogger(AbstractCPU.class);

    protected static final short BYTE_MASK = 0xFF;
    protected static final int SHORT_MASK = 0xFFFF;

    protected Memory memory;

    protected void illegal(short opcode, int pc, String cpuName)
    {
        LOG.error(cpuName + ": Illegal opcode: " + Util.hex((byte) opcode) + " at " + Util.hex((short) (pc - 1)));
    }

}
