/* 
 * $Id: EnvelopeFactory.java 16606 2009-04-09 08:07:33Z mitcje $
 * $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/common/src/main/java/org/ikasan/common/factory/EnvelopeFactory.java $
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
package org.ikasan.common.factory;

import java.util.List;

import javax.jms.JMSException;
import javax.jms.Message;

import org.ikasan.common.Envelope;
import org.ikasan.common.Payload;
import org.ikasan.common.component.EnvelopeOperationException;
import org.ikasan.common.component.PayloadOperationException;
import org.ikasan.common.component.UnknownMessageContentException;

/**
 * The Factory that provides envelopes
 * 
 * @author Ikasan Development Team
 */
public interface EnvelopeFactory
{
    /**
     * Create a new instance of the Envelope for the incoming Payload
     * 
     * @param payload The payload to create the envelop from
     * @return Envelope
     */
    public Envelope newEnvelope(Payload payload);

    /**
     * Create a new instance of the Envelope for incoming Payload List
     * 
     * @param payloads The payloads to create the new envelope from
     * @return Envelope
     */
    public Envelope newEnvelope(List<Payload> payloads);

    /**
     * Set the envelope concrete implementation class
     * 
     * @param envelopeImplClass the envelopeImplClass to set
     */
    public void setEnvelopeImplClass(final Class<? extends Envelope> envelopeImplClass);

    /**
     * Get the envelope concrete implementation class
     * 
     * @return the envelopeImplClass
     */
    public Class<? extends Envelope> getEnvelopeImplClass();

    /**
     * Converts a message into an Envelope
     * 
     * @param message The JMS message to create the envelope from
     * @return Envelope
     * @throws UnknownMessageContentException Exception if the message content is unknown
     * @throws EnvelopeOperationException Exception if there is a problem, with creating the envelope
     * @throws PayloadOperationException Exception if there is a problem with creating the payload
     * @throws JMSException Exception if there is a JMS related problem
     */
    public Envelope fromMessage(Message message) throws UnknownMessageContentException, EnvelopeOperationException,
            PayloadOperationException, JMSException;
}
