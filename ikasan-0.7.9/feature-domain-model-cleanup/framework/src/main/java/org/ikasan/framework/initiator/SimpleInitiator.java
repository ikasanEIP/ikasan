/*
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

import org.ikasan.common.Payload;
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
    
    public boolean initiate( String originationId, String payloadContent)
    {
        if (!available){
            throw new IllegalStateException("Initiator is not available for business");
        }
        
        Payload singlePayload = payloadFactory.newPayload(originationId, payloadContent.getBytes());  
        
        


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
