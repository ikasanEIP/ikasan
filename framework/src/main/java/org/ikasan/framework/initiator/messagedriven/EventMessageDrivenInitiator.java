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

import javax.jms.MapMessage;

import org.apache.log4j.Logger;
import org.ikasan.framework.component.Event;
import org.ikasan.framework.component.IkasanExceptionHandler;
import org.ikasan.framework.event.serialisation.EventSerialisationException;
import org.ikasan.framework.event.serialisation.JmsMessageEventSerialiser;
import org.ikasan.framework.flow.Flow;

/**
 * A <code>JmsMessageDrivenInitiator</code> implementation that seeks to recreate and fire an <code>Event</code>s based
 * on JMS messages.
 * 
 * This implementation expects that the incoming message data represents a previously serialised (for JMS publication)
 * <code>Event</code>.
 * 
 * TODO the Event serialisation and deserialisation code needs to be tightly bound
 * 
 * @author Ikasan Development Team
 */
public class EventMessageDrivenInitiator extends JmsMessageDrivenInitiatorImpl
{
    
    /**
     * Logger instance for this class
     */
    private Logger logger = Logger.getLogger(EventMessageDrivenInitiator.class);
    
    /** Deserialiser */
    private JmsMessageEventSerialiser jmsMessageEventSerialiser;

    /**
     * Constructor
     * 
     * @param moduleName - name of the module
     * @param name - name of this initiator
     * @param flow - flow to invoke
     * @param exceptionHandler for handlingExceptions
     * @param jmsMessageEventSerialiser - The serialiser for the JMS message
     */
    public EventMessageDrivenInitiator(String moduleName, String name, Flow flow, IkasanExceptionHandler exceptionHandler,
            JmsMessageEventSerialiser jmsMessageEventSerialiser)
    {
        super(moduleName, name, flow, exceptionHandler);
        this.jmsMessageEventSerialiser = jmsMessageEventSerialiser;
    }

    @Override
    protected Event handleMapMessage(MapMessage message) throws EventSerialisationException
    {
        Event event = jmsMessageEventSerialiser.fromMapMessage(message, moduleName, name);
        return event;
    }
    
    @Override
    protected Logger getLogger()
    {
        return logger;
    }
}
