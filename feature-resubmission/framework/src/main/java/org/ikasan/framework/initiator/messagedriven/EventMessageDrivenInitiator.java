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
package org.ikasan.framework.initiator.messagedriven;

import javax.jms.JMSException;
import javax.jms.MapMessage;

import org.apache.log4j.Logger;
import org.ikasan.framework.component.Event;
import org.ikasan.framework.component.IkasanExceptionHandler;
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
    private JmsMessageEventSerialiser<MapMessage> jmsMessageEventSerialiser;

    /**
     * Constructor
     * 
     * @param moduleName - name of the module
     * @param name - name of this initiator
     * @param flow - flow to invoke
     * @param exceptionHandler - handler for Exceptions
     * @param jmsMessageEventSerialiser - The serialiser for the JMS message
     */
    public EventMessageDrivenInitiator(String moduleName, String name, Flow flow, IkasanExceptionHandler exceptionHandler,
            JmsMessageEventSerialiser<MapMessage> jmsMessageEventSerialiser)
    {
        super(moduleName, name, flow, exceptionHandler);
        this.jmsMessageEventSerialiser = jmsMessageEventSerialiser;
    }

    @Override
    protected Event handleMapMessage(MapMessage message) throws JMSException
    {
        Event event = jmsMessageEventSerialiser.fromMessage(message, moduleName, name);
        return event;
    }
    
    @Override
    protected Logger getLogger()
    {
        return logger;
    }
}
