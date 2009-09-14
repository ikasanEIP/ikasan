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
package org.ikasan.framework.initiator.messagedriven;

import javax.jms.JMSException;
import javax.jms.TextMessage;

import org.apache.log4j.Logger;
import org.ikasan.common.MetaDataInterface;
import org.ikasan.common.Payload;
import org.ikasan.common.component.Spec;
import org.ikasan.common.factory.PayloadFactory;
import org.ikasan.framework.component.Event;
import org.ikasan.framework.flow.Flow;

/**
 * A <code>JmsMessageDrivenInitiator</code> implementation that seeks to create and fire new <code>Event</code>s based
 * on raw JMS messages.
 * 
 * This implementation places no expectation on the incoming message data. Do not use this for deserialising existing
 * <code>Event</code>s, rather use the <code>EventMessageDrivenInitiator</code>
 * 
 * @author Ikasan Development Team
 */
public class RawMessageDrivenInitiator extends JmsMessageDrivenInitiatorImpl
{
    /** Default Message Priority used by the Ikasan Raw Message Driven Initiator */
    private static final int DEFAULT_MESSAGE_PRIORITY = 4;

    /**
     * Logger instance for this class
     */
    private Logger logger = Logger.getLogger(RawMessageDrivenInitiator.class);
    
    /**
     * Factory for constructing Payloads
     */
    protected PayloadFactory payloadFactory;
    
    /**
     * Respect the priority of received messages by setting this on the Event
     */
    private boolean respectPriority;


	/**
     * Constructor
     * 
     * @param moduleName - name of the module
     * @param name - name of this initiator
     * @param flow - flow to invoke
     * @param payloadFactory - means for creating new <code>Payload</code>s
     */
    public RawMessageDrivenInitiator(String moduleName, String name, Flow flow, PayloadFactory payloadFactory)
    {
        super(moduleName, name, flow);
        this.payloadFactory = payloadFactory;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.ikasan.framework.initiator.messagedriven.JmsMessageDrivenInitiatorImpl#handleTextMessage(javax.jms.TextMessage
     * )
     */
    @Override
    protected Event handleTextMessage(TextMessage message) throws JMSException
    {
        // this is what the old code would have done with a TextMessage
        Payload payload = payloadFactory.newPayload(message.getJMSMessageID(), MetaDataInterface.UNDEFINED, Spec.TEXT_XML, MetaDataInterface.UNDEFINED, message.getText().getBytes());
        //
        Event event = new Event(moduleName, name, message.getJMSMessageID(), payload);
        // Reuse the message's priority if we are configured to respect it
        if (respectPriority)
        {
            event.setPriority(message.getJMSPriority());
        }
        else
        {
            event.setPriority(DEFAULT_MESSAGE_PRIORITY);
        }
        return event;
    }



    @Override
    protected Logger getLogger()
    {
        return logger;
    }
    

    /**
     * Respect the priority of received messages by setting this on the Event
	 * @param respectPriority
	 */
	public void setRespectPriority(boolean respectPriority) {
		this.respectPriority = respectPriority;
	}

	/**
	 * Accessor for respectPriority
	 * @return respectPriority
	 */
	public boolean isRespectPriority() {
		return respectPriority;
	}
}
