package org.nucco.jems.api.cpu;

public interface CPU
{

    /**
     * Fetch in memory, at the address indicated by the pc register, the next
     * action to execute. Increment by one pc register and execute the action.
     */
    public void step();

}
