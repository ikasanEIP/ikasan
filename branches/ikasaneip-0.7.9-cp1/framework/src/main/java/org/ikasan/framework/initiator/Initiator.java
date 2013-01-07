/*
 * $Id$
 * $URL$
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

import org.ikasan.framework.flow.Flow;
import org.ikasan.framework.initiator.InitiatorOperationException;

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
}
