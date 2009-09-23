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
package org.ikasan.framework.exception;

/**
 * Concrete implementation of the Ikasan Exception Definition interface.
 * 
 * @author Ikasan Development Team
 */
public class IkasanExceptionActionImpl implements IkasanExceptionAction
{
    /** Type of action to take */
    private IkasanExceptionActionType type;

    /** Delay in milliseconds before invoking above action type - default is no delay (0) */
    private Long delay;

    /**
     * maximum number of attempts of the action type before resorting to an emergencyAction (usually stop) - default
     * (-1) is infinite
     */
    private Integer maxAttempts;

    /**
     * Constructor
     * 
     * @param type The type of exception action
     */
    public IkasanExceptionActionImpl(IkasanExceptionActionType type)
    {
        this.type = type;
        this.delay = new Long(IkasanExceptionAction.DEFAULT_DELAY);
        this.maxAttempts = new Integer(IkasanExceptionAction.DEFAULT_MAX_ATTEMPTS);
    }

    /**
     * Constructor
     * 
     * @param type The type of exception action
     * @param delay The delay before we perform the action
     * @param maxAttempts The maximum number of attempts we try the action for
     */
    public IkasanExceptionActionImpl(IkasanExceptionActionType type, Long delay, Integer maxAttempts)
    {
        this.type = type;
        this.delay = delay;
        this.maxAttempts = maxAttempts;
    }

    /**
     * Get the type
     * 
     * @return the type
     */
    public IkasanExceptionActionType getType()
    {
        return this.type;
    }

    /**
     * Set the type of action
     * 
     * @param type The type of action
     */
    public void setType(IkasanExceptionActionType type)
    {
        this.type = type;
    }

    /**
     * Get the delay
     * 
     * @return the delay
     */
    public Long getDelay()
    {
        return this.delay;
    }

    /**
     * Set the delay
     * 
     * @param delay the delay to set
     */
    public void setDelay(Long delay)
    {
        this.delay = delay;
    }

    /**
     * Get the max attempts
     * 
     * @return the maxAttempts
     */
    public Integer getMaxAttempts()
    {
        return this.maxAttempts;
    }

    /**
     * Set the max attempts
     * 
     * @param maxAttempts the maxAttempts to set
     */
    public void setMaxAttempts(Integer maxAttempts)
    {
        this.maxAttempts = maxAttempts;
    }
    
    public String toString(){
        StringBuffer sb = new StringBuffer(getClass().getName()+" [");
        sb.append("type=");sb.append(type);sb.append(",");
        sb.append("delay=");sb.append(delay);sb.append(",");
        sb.append("maxAttempts=");sb.append(maxAttempts);
        sb.append("]");
        return sb.toString();
    }
}
