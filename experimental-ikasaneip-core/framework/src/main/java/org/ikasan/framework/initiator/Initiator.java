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

import java.util.Date;

import org.ikasan.core.flow.Flow;
import org.ikasan.framework.component.IkasanExceptionHandler;

/**
 * Ikasan initiator interface defining the operations on an initiator.
 * 
 * @author Ikasan Development Team
 */
public interface Initiator
{
    /**
     * The flow initiator's name.
     * 
     * @return name
     */
    public String getName();
    
    /**
     * Returns the type of the initiator
     * 
     * @return type
     */
    public String getType();

    /**
     * Start this initiator.
     * 
     * @throws InitiatorOperationException Exception if we cannot start the Initiator
     */
    public void start() throws InitiatorOperationException;

    /**
     * Stop this initiator.
     * 
     * @throws InitiatorOperationException Exception if we cannot stop the Initiator
     */
    public void stop() throws InitiatorOperationException;

    
    
    /**
     * Returns the current state of the Initiator
     * 
     * @return InitiatorState representing the current state of the Initiators
     */
    public InitiatorState getState();
    
    /**
     * Is this initiator running.
     * 
     * @return boolean
     */
    public boolean isRunning();

    /**
     * Is this initiator in an error state. This error state also denotes this initiator as stopped.
     * 
     * @return boolean
     */
    public boolean isError();

    /**
     * Is this initiator in a recovering state. This recovering state also denotes this initiator as running.
     * 
     * @return boolean
     */
    public boolean isRecovering();

    /**
     * Returns the flow that this invokes
     * 
     * @return Flow
     */
    public Flow getFlow();
    
    /**
     * Accessor for retryCount
     * 
     * @return no of attempts made to retry
     */
    public Integer getRetryCount();
    
    /**
     * Accessor for exceptionHandler
     * 
     * @return ExceptionHandler if any
     */
    public IkasanExceptionHandler getExceptionHandler();
    
    public long getHandledEventCount();
    
    public Date getLastEventTime();
}
