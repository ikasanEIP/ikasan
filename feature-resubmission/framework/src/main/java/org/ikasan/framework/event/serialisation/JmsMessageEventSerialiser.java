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
     * @throws EventDeserialisationException 
     * 
     * @throws JMSException if we could not deserialise the event
     */
    public Event fromMessage(T message, String moduleName, String componentName) throws JMSException, EventDeserialisationException;

    /**
     * Serialises an <code>Event</code> to a <code>MapMessage</code>
     * 
     * @param event The event to turn into a JMS MapMessage
     * @param session The session
     * @return Message - ready to go!
     * 
     * @throws EventDeserialisationException Exception if we could not serialise the event
     */
    public T toMessage(Event event, Session session) throws JMSException;
}
