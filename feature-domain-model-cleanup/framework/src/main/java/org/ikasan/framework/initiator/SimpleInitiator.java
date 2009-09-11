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

import org.ikasan.common.Payload;
import org.ikasan.common.component.Spec;
import org.ikasan.common.factory.PayloadFactory;
import org.ikasan.framework.component.Event;
import org.ikasan.framework.exception.IkasanExceptionAction;
import org.ikasan.framework.flow.Flow;
import org.springframework.beans.factory.BeanNameAware;

/**
 * Experimental implementation of <code>Initiator</code> that is invoked directly with content
 * 
 * 
 * @author Ikasan Development Team
 *
 */
public class SimpleInitiator implements Initiator, BeanNameAware
{
    public static final String SIMPLE_INITIATOR_TYPE = "SimpleInitiator";

    /**
     * Is this open to business?
     */
    private boolean available = false;

    /**
     * Name of Initiator
     */
    private String initiatorName;

    /**
     * Name of Module
     */
    private String moduleName;

    /**
     * factory for instantiatng Payloads
     */
    private PayloadFactory payloadFactory;

    /**
     * Flow to invoke
     */
    private Flow flow;   
    
    /**
     * Constructor
     * 
     * @param available
     * @param moduleName
     * @param payloadFactory
     * @param flow
     */
    public SimpleInitiator(String moduleName, PayloadFactory payloadFactory, Flow flow)
    {
        super();
        this.moduleName = moduleName;
        this.payloadFactory = payloadFactory;
        this.flow = flow;
    }
    
    public boolean initiate(String payloadName, Spec spec, String srcSystem, String originationId, String payloadContent)
    {
        if (!available){
            throw new IllegalStateException("Initiator is not available for business");
        }
        
        Payload singlePayload = payloadFactory.newPayload(originationId, payloadName, spec, srcSystem, payloadContent.getBytes());  
        
        


        Event event = new Event(moduleName, initiatorName, originationId, singlePayload);
        IkasanExceptionAction exceptionAction = flow.invoke(event);
        //TODO better error handling
        if (exceptionAction!=null){
            throw new RuntimeException("Could not invoke flow:"+flow.getName());
        }
        
        return (exceptionAction==null);
        
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


    /**
     * @param payloadFactory
     */
    public SimpleInitiator(PayloadFactory payloadFactory)
    {
        super();
        this.payloadFactory = payloadFactory;
    }



    /* (non-Javadoc)
     * @see org.ikasan.framework.initiator.Initiator#getFlow()
     */
    public Flow getFlow()
    {
        return flow;
    }

    /* (non-Javadoc)
     * @see org.ikasan.framework.initiator.Initiator#getName()
     */
    public String getName()
    {
        return initiatorName;
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
     * @see org.springframework.beans.factory.BeanNameAware#setBeanName(java.lang.String)
     */
    public void setBeanName(String beanName)
    {
        initiatorName = beanName;
        
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
	 * @see org.ikasan.framework.initiator.Initiator#getRetryCount()
	 */
	public Integer getRetryCount() {
		// TODO Auto-generated method stub
		return null;
	}
}
