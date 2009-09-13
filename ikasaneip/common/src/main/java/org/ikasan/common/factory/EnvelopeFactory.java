/* 
 * $Id$
 * $URL$
 *
 * =============================================================================
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
 * =============================================================================
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
