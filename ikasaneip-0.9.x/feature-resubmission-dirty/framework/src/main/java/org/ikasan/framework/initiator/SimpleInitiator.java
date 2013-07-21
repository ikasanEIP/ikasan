/*
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

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.ikasan.common.Payload;
import org.ikasan.common.component.Spec;
import org.ikasan.common.factory.PayloadFactory;
import org.ikasan.framework.component.Event;
import org.ikasan.framework.component.IkasanExceptionHandler;
import org.ikasan.framework.flow.Flow;

/**
 * Experimental implementation of <code>Initiator</code> that is invoked directly with content
 * 
 * 
 * @author Ikasan Development Team
 *
 */
public class SimpleInitiator extends AbstractInitiator implements Initiator
{
    public static final String SIMPLE_INITIATOR_TYPE = "SimpleInitiator";

    private static final Logger logger = Logger.getLogger(SimpleInitiator.class);
    
    /**
     * Is this open to business?
     */
    private boolean available = false;


    /**
     * factory for instantiating Payloads
     */
    private PayloadFactory payloadFactory;


    
    /**
     * Constructor
     * 
     * @param available
     * @param moduleName
     * @param payloadFactory
     * @param flow
     */
    public SimpleInitiator(String initiatorName, String moduleName, PayloadFactory payloadFactory, Flow flow, IkasanExceptionHandler exceptionHandler)
    {
        super(moduleName, initiatorName, flow, exceptionHandler);
        this.payloadFactory = payloadFactory;
    }
    
    public boolean initiate(String payloadName, Spec spec, String srcSystem, String payloadContent, String originationId)
    {
        if (!available){
            throw new IllegalStateException("Initiator is not available for business");
        }
        
        Payload singlePayload = payloadFactory.newPayload(originationId, payloadName, spec, srcSystem, payloadContent.getBytes());  
        

        List<Event>events = new ArrayList<Event>();
        events.add(new Event(moduleName, name, originationId, singlePayload));
        invokeFlow(events);
        return true;
        
    }
    
    /**
     * Accessor for available
     * 
     * @return
     */
    public boolean isAvailable()
    {
        return available;
    }

    /**
     * Setter for available
     * 
     * @param available
     */
    public void setAvailable(boolean available)
    {
        this.available = available;
    }


 



    /* (non-Javadoc)
     * @see org.ikasan.framework.initiator.Initiator#getFlow()
     */
    public Flow getFlow()
    {
        return flow;
    }



    /* (non-Javadoc)
     * @see org.ikasan.framework.initiator.Initiator#isError()
     */
    public boolean isError()
    {
        // Error/Recovery not supported
        //TODO - not supported should be expressed in InitiatorState heirarchy 
        return false;
    }

    /* (non-Javadoc)
     * @see org.ikasan.framework.initiator.Initiator#isRecovering()
     */
    public boolean isRecovering()
    {
        // Error/Recovery not supported
        //TODO - not supported should be expressed in InitiatorState heirarchy 
        return false;
    }

    /* (non-Javadoc)
     * @see org.ikasan.framework.initiator.Initiator#isRunning()
     */
    public boolean isRunning()
    {
        return isAvailable();
    }

    /* (non-Javadoc)
     * @see org.ikasan.framework.initiator.Initiator#start()
     */
    public void start() 
    {
        available = true;
    }

    /* (non-Javadoc)
     * @see org.ikasan.framework.initiator.Initiator#stop()
     */
    public void stop() 
    {
        available = false;
    }



    /* (non-Javadoc)
     * @see org.ikasan.framework.initiator.Initiator#getState()
     */
    public InitiatorState getState()
    {
        return available?InitiatorState.RUNNING:InitiatorState.STOPPED;
    }

    /* (non-Javadoc)
     * @see org.ikasan.framework.initiator.Initiator#getType()
     */
    public String getType()
    {
        return SIMPLE_INITIATOR_TYPE;
    }

	/* (non-Javadoc)
	 * @see org.ikasan.framework.initiator.AbstractInitiator#cancelRetryCycle()
	 */
	@Override
	protected void cancelRetryCycle() {
		
	}

	/* (non-Javadoc)
	 * @see org.ikasan.framework.initiator.AbstractInitiator#completeRetryCycle()
	 */
	@Override
	protected void completeRetryCycle() {
		
	}

	/* (non-Javadoc)
	 * @see org.ikasan.framework.initiator.AbstractInitiator#getLogger()
	 */
	@Override
	protected Logger getLogger() {
		return logger;
	}

	/* (non-Javadoc)
	 * @see org.ikasan.framework.initiator.AbstractInitiator#startInitiator()
	 */
	@Override
	protected void startInitiator() throws InitiatorOperationException {
	}

	/* (non-Javadoc)
	 * @see org.ikasan.framework.initiator.AbstractInitiator#startRetryCycle(java.lang.Integer, long)
	 */
	@Override
	protected void startRetryCycle(Integer maxAttempts, long delay)
			throws InitiatorOperationException {
		
	}

	/* (non-Javadoc)
	 * @see org.ikasan.framework.initiator.AbstractInitiator#stopInitiator()
	 */
	@Override
	protected void stopInitiator() throws InitiatorOperationException {
	}
	
	/* (non-Javadoc)
	 * @see org.ikasan.framework.initiator.Initiator#getRetryCount()
	 */
	public Integer getRetryCount() {
		// TODO Auto-generated method stub
		return null;
	}
}
