/* 
 * $Id: InitiatorState.java 16808 2009-04-27 07:28:17Z mitcje $
 * $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/framework/src/main/java/org/ikasan/framework/initiator/InitiatorState.java $
 *
 * ====================================================================
 * Ikasan Enterprise Integration Platform
 * Copyright (c) 2003-2008 Mizuho International plc. and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the 
 * Free Software Foundation Europe e.V. Talstrasse 110, 40217 Dusseldorf, Germany 
 * or see the FSF site: http://www.fsfeurope.org/.
 * ====================================================================
 */
package org.ikasan.framework.initiator;

/**
 * Simple initiator state implementation.
 * 
 * @author Ikasan Development Team
 *
 */
public class InitiatorState
{
    /** Stopped State - just stopped */
    public static final InitiatorState STOPPED = 
        new InitiatorState("stopped", true, false, false, false);
    /** Running State - just running */
    public static final InitiatorState RUNNING = 
        new InitiatorState("running", false, true, false, false);
    /** Recovering State = running, but in recovery */
    public static final InitiatorState RECOVERING = 
        new InitiatorState("runningInRecovery", false, true, true, false);
    /** Error State - stopped, but in error */
    public static final InitiatorState ERROR = 
        new InitiatorState("stoppedInError", true, false, false, true);

    /** state name */
    private final String name;
    /** stopped state */
    private final boolean stopped;
    /** running state */
    private final boolean running;
    /** recovering state */
    private final boolean recovering;
    /** error state */
    private final boolean error;

    /**
     * Constructor
     * @param name 
     * @param stopped 
     * @param running
     * @param recovering
     * @param error
     */
    protected InitiatorState(final String name, final boolean stopped, 
            final boolean running, final boolean recovering, final boolean error)
    {
        this.name = name;
        this.stopped = stopped;
        this.running = running;
        this.recovering = recovering;
        this.error = error;
    }

    /**
     * Is this a stopped normally state
     * @return boolean
     */
    public boolean isStopped()
    {
        return this.stopped;
    }

    /**
     * Is this a running normally state
     * @return boolean
     */
    public boolean isRunning()
    {
        return this.running;
    }

    /**
     * Is this a running but in 'recovery' state
     * @return boolean
     */
    public boolean isRecovering()
    {
        return this.recovering;
    }

    /**
     * Is this a stopped in 'error' state
     * @return boolean
     */
    public boolean isError()
    {
        return this.error;
    }

    /**
     * Get the state name
     * @return name
     */
    public String getName()
    {
        return this.name;
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString(){
        StringBuffer sb = new StringBuffer(getClass().getName()+" [");
        sb.append("name=");
        sb.append(name);
        sb.append("]");
        return sb.toString();
    }
} 
