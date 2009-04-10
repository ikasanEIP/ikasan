/* 
 * $Id: JMSMessageFactory.java 16606 2009-04-09 08:07:33Z mitcje $
 * $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/common/src/main/java/org/ikasan/common/factory/JMSMessageFactory.java $
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
