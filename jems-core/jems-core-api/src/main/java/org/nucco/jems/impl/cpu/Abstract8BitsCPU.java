package org.nucco.jems.impl.cpu;

import org.nucco.jems.api.cpu.CPU;
import org.nucco.jems.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Abstract8BitsCPU implements CPU
{

    private static final Logger LOG = LoggerFactory.getLogger(Abstract8BitsCPU.class);

    protected void illegal(short opcode, int PC, String CPUName)
    {
        LOG.error(CPUName + ": Illegal opcode: " + Util.hex((byte) opcode) + " at " + Util.hex((short) (PC - 1)));
    }

}
