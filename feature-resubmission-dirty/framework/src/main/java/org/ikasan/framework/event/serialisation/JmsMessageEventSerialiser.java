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
package org.ikasan.framework.event.serialisation;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.ikasan.framework.component.Event;

/**
 * Serialisation/Deserialisation interface for converting between <code>Event</code> and some specified <code>Message<code> implementation
 * 
 * @author Ikasan Development Team
 */
public interface JmsMessageEventSerialiser<T extends Message>
{
    /**
     * Deserialises a previously existing <code>Event</code> from a <code>MapMessage</code>
     * 
     * @param message - message to deserialise
     * @param moduleName - name of the module that is reconstituting the Event - this gets set on the Event
     * @param componentName - name of the component/initiator that is reconstituting the Event - this gets set on the
     *            Event
     * @return reconstituted <code>Event</code>
     * 
     * @throws J Exception if we could not deserialise the event
     */
    public Event fromMessage(T message, String moduleName, String componentName) throws JMSException;

    /**
     * Serialises an <code>Event</code> to a <code>MapMessage</code>
     * 
     * @param event The event to turn into a JMS MapMessage
     * @param session The session
     * @return Message - ready to go!
     * 
     * @throws EventSerialisationException Exception if we could not serialise the event
     */
    public T toMessage(Event event, Session session) throws JMSException;
}
