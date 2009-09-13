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
import java.util.Map;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.ikasan.common.Envelope;
import org.ikasan.common.Payload;
import org.ikasan.common.component.EnvelopeOperationException;
import org.ikasan.common.component.PayloadOperationException;
import org.ikasan.common.component.UnknownMessageContentException;

/**
 * Creates JMS Messages based on the Session and various arguments
 * 
 * @author Ikasan Development Team
 */
public interface JMSMessageFactory
{
    /**
     * Helper method to create a JMS MapMessage from an incoming payload instance
     * 
     * @param payload The payload to convert to a Map Message
     * @param session The session
     * @return MapMessage
     * @throws PayloadOperationException Exception if we could not convert Payload
     */
    public MapMessage payloadToMapMessage(Payload payload, Session session) throws PayloadOperationException;

    /**
     * Helper method to create a JMS MapMessage from an incoming payload ArrayList<Payload> instance
     * 
     * @param payloadList The list of payloads to convert to a Map Message
     * @param session The session
     * @return MapMessage
     * @throws PayloadOperationException Exception if we could not convert payloads
     */
    public MapMessage payloadsToMapMessage(List<Payload> payloadList, Session session) throws PayloadOperationException;

    /**
     * Helper method to create a JMS MapMessage from an incoming payload ArrayList<Payload> instance
     * 
     * @param payloadList The list of payloads to convert to a Map Message
     * @param session The session
     * @param customMessageSelector - map of JMS properties and values to set
     * @return MapMessage
     * @throws PayloadOperationException - Exception if we could not convert payloads
     */
    public MapMessage payloadsToMapMessage(List<Payload> payloadList, Session session,
            Map<String, Object> customMessageSelector) throws PayloadOperationException;

    /**
     * Helper method to create a JMS MapMessage from an incoming payload instance
     * 
     * @param payload The payload to convert to a Map Message
     * @param session The session
     * @param customMessageSelector - map of JMS properties and values to set
     * @return MapMessage
     * @throws PayloadOperationException Exception if we could not convert Payload
     */
    public MapMessage payloadToMapMessage(Payload payload, Session session, Map<String, Object> customMessageSelector)
            throws PayloadOperationException;

    /**
     * Helper method for creating a JMS TextMessage with the content of the specified payload index
     * 
     * @param payload The payload to convert to a Text Message
     * @param session The session
     * @return TextMessage - containing only the content of the incoming payload
     * @throws PayloadOperationException Exception if we could not convert
     */
    public TextMessage payloadToTextMessage(Payload payload, Session session) throws PayloadOperationException;

    /**
     * Helper method for creating a JMS TextMessage with the content of the specified payload index
     * 
     * @param payload The payload to convert to a Text Message
     * @param session The session
     * @param customMessageSelector - map of JMS properties and values to set
     * @return TextMessage - containing only the content of the incoming payload
     * @throws PayloadOperationException - Exception if we could not convert
     */
    public TextMessage payloadToTextMessage(Payload payload, Session session, Map<String, Object> customMessageSelector)
            throws PayloadOperationException;

    /**
     * Converts an Envelope to a TextMessage
     * 
     * NOTE: At this stage we only support converting converting an envelope with one Payload
     * 
     * @param envelope The envelope to convert
     * @param session The session
     * @param customMessageSelector The custom message selector to use
     * @return TextMessage
     * @throws EnvelopeOperationException Exception if there was an Envelope based problem
     * @throws PayloadOperationException Exception if there was a Payload based problem
     */
    public TextMessage envelopeToTextMessage(Envelope envelope, Session session,
            Map<String, Object> customMessageSelector) throws EnvelopeOperationException, PayloadOperationException;

    /**
     * Converts an Envelope to a MapMessage
     * 
     * @param envelope The envelope to convert
     * @param session The session
     * @param customMessageSelector The custom message selector to use
     * @return MapMessage
     * @throws EnvelopeOperationException Exception if there was an Envelope based problem
     * @throws PayloadOperationException Exception if there was a Payload based problem
     */
    public MapMessage envelopeToMapMessage(Envelope envelope, Session session, Map<String, Object> customMessageSelector)
            throws EnvelopeOperationException, PayloadOperationException;

    /**
     * Converts an Envelope to a MapMessage
     * 
     * @param envelope The envelope to convert
     * @param session The session
     * @return MapMessage
     * @throws EnvelopeOperationException Exception if there was an Envelope based problem
     * @throws PayloadOperationException Exception if there was a Payload based problem
     */
    public MapMessage envelopeToMapMessage(Envelope envelope, Session session) throws EnvelopeOperationException,
            PayloadOperationException;

    /**
     * Extract a List of Payloads from a JMS Message
     * 
     * @param message The message to get the payloads from
     * @return List<Payload>
     * @throws PayloadOperationException Exception if there was a Payload based problem
     * @throws JMSException Exception if there was a JMS based problem
     * @throws UnknownMessageContentException Exception if the content of the message could not be dealt with
     */
    public List<Payload> fromMessage(Message message) throws PayloadOperationException, JMSException,
            UnknownMessageContentException;
}
