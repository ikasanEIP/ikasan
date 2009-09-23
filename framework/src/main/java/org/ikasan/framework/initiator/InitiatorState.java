/* 
 * $Id$
 * $URL$
 *
 * ====================================================================
 * Ikasan Enterprise Integration Platform
 * 
 * Distributed under the Modified BSD License.
 * Copyright notice: The copyright for this software and a full listing 
 * of individual contributors are as shown in the packaged copyright.txt 
 * file. 
 * 
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 *
 *  - Redistributions of source code must retain the above copyright notice, 
 *    this list of conditions and the following disclaimer.
 *
 *  - Redistributions in binary form must reproduce the above copyright notice, 
 *    this list of conditions and the following disclaimer in the documentation 
 *    and/or other materials provided with the distribution.
 *
 *  - Neither the name of the ORGANIZATION nor the names of its contributors may
 *    be used to endorse or promote products derived from this software without 
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE 
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE 
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR 
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER 
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE 
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
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
