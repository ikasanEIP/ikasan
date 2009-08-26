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
package org.ikasan.common.factory;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.log4j.Logger;
import org.ikasan.common.Envelope;
import org.ikasan.common.MetaDataInterface;
import org.ikasan.common.Payload;
import org.ikasan.common.component.EnvelopeHelper;
import org.ikasan.common.component.EnvelopeOperationException;
import org.ikasan.common.component.PayloadHelper;
import org.ikasan.common.component.PayloadOperationException;
import org.ikasan.common.component.Spec;
import org.ikasan.common.component.UnknownMessageContentException;

/**
 * A Factory for providing JMS Messages
 * 
 * @author Ikasan Development Team
 */
public class JMSMessageFactoryImpl implements JMSMessageFactory
{
    /** Logger */
    private static Logger logger = Logger.getLogger(JMSMessageFactoryImpl.class);

    /** Payload Factory */
    private PayloadFactory payloadFactory;

    /**
     * Primary Payload As there can be multiple payloads for any one message there must always be a primary payload.
     * This is essentially a base payload to which all other payloads either have a business relationship, or all other
     * payloads are the same type of animal (i.e. same name, srcSystem, etc).
     */
    private static final int PRIMARY_PAYLOAD = 0;

    /** Valid message selector properties for all JMS Messages */
    private static Map<String, Object> defaultMessageSelector = new HashMap<String, Object>();
    {
        defaultMessageSelector.put(MetaDataInterface.ID, null);
        defaultMessageSelector.put(MetaDataInterface.NAME, null);
        defaultMessageSelector.put(MetaDataInterface.SRC_SYSTEM, null);
    }

    /**
     * Constructor
     * 
     * @param payloadFactory The payload factory to use
     */
    public JMSMessageFactoryImpl(PayloadFactory payloadFactory)
    {
        this.payloadFactory = payloadFactory;
    }

    /**
     * Helper method for creating a JMS TextMessage with the content of the specified payload index
     * 
     * @param payload The payload to convert to a Text Message
     * @param session The session
     * @return TextMessage - containing only the content of the incoming payload
     * @throws PayloadOperationException Exception if we could not convert
     */
    public TextMessage payloadToTextMessage(Payload payload, Session session) throws PayloadOperationException
    {
        return payloadToTextMessage(payload, session, null);
    }

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
            throws PayloadOperationException
    {
        try
        {
            logger.debug("Creating JMS TextMessage from incoming payload..."); //$NON-NLS-1$
            TextMessage textMessage = session.createTextMessage();
            textMessage.setText(new String(payload.getContent()));
            // Set required selector properties
            textMessage = (TextMessage) setMessageSelectorProperties(textMessage, payload, defaultMessageSelector,
                customMessageSelector);
            return textMessage;
        }
        catch (JMSException e)
        {
            throw new PayloadOperationException("Failed to convert Payload to TextMessage", e); //$NON-NLS-1$
        }
        catch (RuntimeException e)
        {
            throw new PayloadOperationException("Failed to convert Payload to TextMessage", e); //$NON-NLS-1$
        }
    }

    /**
     * Helper method to create a JMS MapMessage from an incoming payload instance
     * 
     * @param payload The payload to convert to a Map Message
     * @param session The session
     * @return MapMessage
     * @throws PayloadOperationException Exception if we could not convert Payload
     */
    public MapMessage payloadToMapMessage(Payload payload, Session session) throws PayloadOperationException
    {
        return payloadToMapMessage(payload, session, null);
    }

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
            throws PayloadOperationException
    {
        ArrayList<Payload> payloadList = new ArrayList<Payload>();
        payloadList.add(payload);
        return payloadsToMapMessage(payloadList, session, customMessageSelector);
    }

    /**
     * Helper method to create a JMS MapMessage from an incoming payload ArrayList<Payload> instance
     * 
     * @param payloadList The list of payloads to convert to a Map Message
     * @param session The session
     * @return MapMessage
     * @throws PayloadOperationException Exception if we could not convert payloads
     */
    public MapMessage payloadsToMapMessage(List<Payload> payloadList, Session session) throws PayloadOperationException
    {
        return payloadsToMapMessage(payloadList, session, null);
    }

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
            Map<String, Object> customMessageSelector) throws PayloadOperationException
    {
        try
        {
            logger.debug("Creating JMS MapMessage from incoming payload(s)..."); //$NON-NLS-1$
            MapMessage mapMessage = session.createMapMessage();
            // We need to count the payloads to ensure mapMessage entries are
            // unique
            int payloadCounter = PRIMARY_PAYLOAD;
            // For each payload use introspection to
            // populate the mapMessage based on each getter
            for (Payload payload : payloadList)
            {
                // Get a map of the attributes
                Map<String, Object> payloadMap = PayloadHelper.getPayloadMap(payload);
                Iterator<String> it = payloadMap.keySet().iterator();
                while (it.hasNext())
                {
                    String key = it.next();
                    Object obj = payloadMap.get(key);
                    String keyName = MetaDataInterface.PAYLOAD_PREFIX + payloadCounter + "_" + key;
                    // Got data - can we populate the mapMessage with it
                    if (obj instanceof Boolean)
                    {
                        mapMessage.setBoolean(keyName, ((Boolean) obj).booleanValue());
                    }
                    else if (obj instanceof Byte)
                    {
                        mapMessage.setByte(keyName, ((Byte) obj).byteValue());
                    }
                    else if (obj instanceof Short)
                    {
                        mapMessage.setShort(keyName, ((Short) obj).shortValue());
                    }
                    else if (obj instanceof Character)
                    {
                        mapMessage.setChar(keyName, ((Character) obj).charValue());
                    }
                    else if (obj instanceof Integer)
                    {
                        mapMessage.setInt(keyName, ((Integer) obj).intValue());
                    }
                    else if (obj instanceof Long)
                    {
                        mapMessage.setLong(keyName, ((Long) obj).longValue());
                    }
                    else if (obj instanceof Float)
                    {
                        mapMessage.setFloat(keyName, ((Float) obj).floatValue());
                    }
                    else if (obj instanceof Double)
                    {
                        mapMessage.setDouble(keyName, ((Double) obj).doubleValue());
                    }
                    else if (obj instanceof String)
                    {
                        mapMessage.setString(keyName, (String) obj);
                    }
                    else if (obj instanceof byte[])
                    {
                        mapMessage.setBytes(keyName, (byte[]) obj);
                    }
                    else
                        logger.warn("Unsupported type found in " //$NON-NLS-1$
                                + "PayloadAttributes Map " //$NON-NLS-1$
                                + "on creation of JMS MapMessage [" //$NON-NLS-1$
                                + obj.getClass().getName() + "]. " //$NON-NLS-1$
                                + "This attribute will be ignored."); //$NON-NLS-1$
                }
                payloadCounter++;
            }
            mapMessage.setInt(Payload.PAYLOAD_COUNT_KEY, payloadCounter);
            if (payloadCounter > 1)
            {
                logger.debug("Successfully created JMS MapMessage with [" //$NON-NLS-1$
                        + payloadCounter + "] payloads."); //$NON-NLS-1$
            }
            else
            {
                logger.debug("Successfully created JMS MapMessage with [" //$NON-NLS-1$
                        + payloadCounter + "] payload."); //$NON-NLS-1$
            }
            // Set required selector properties
            mapMessage = (MapMessage) setMessageSelectorProperties(mapMessage, payloadList, defaultMessageSelector,
                customMessageSelector);
            return mapMessage;
        }
        catch (JMSException e)
        {
            throw new PayloadOperationException("Failed to create MapMessage from Payload", e); //$NON-NLS-1$
        }
        catch (RuntimeException e)
        {
            throw new PayloadOperationException("Failed to create MapMessage from Payload", e); //$NON-NLS-1$
        }
    }

    /**
     * Set selector properties on the message based on the names specified in the valid selector map and the payload
     * instances.
     * 
     * @param message The message to set the selector properties on
     * @param payloads The list of payloads
     * @param defaultSelector The default selector
     * @param customSelector The custom selector
     * @return message The enriched message
     * @throws PayloadOperationException Exception if we could not set the selector properties
     */
    public static Message setMessageSelectorProperties(Message message, List<Payload> payloads,
            Map<String, Object> defaultSelector, Map<String, Object> customSelector) throws PayloadOperationException
    {
        return setSelectorProperties(message, payloads.get(PRIMARY_PAYLOAD), defaultSelector, customSelector);
    }

    /**
     * Set selector properties on the message based on the names specified in the valid selector map and the payload
     * instances.
     * 
     * @param message The message to set the selector properties on
     * @param payload The payload
     * @param defaultSelector The default selector
     * @param customSelector The custom selector
     * @return message The enriched message
     * @throws PayloadOperationException Exception if we could not set the selector properties
     */
    public static Message setMessageSelectorProperties(Message message, Payload payload,
            Map<String, Object> defaultSelector, Map<String, Object> customSelector) throws PayloadOperationException
    {
        Message msg = message;
        try
        {
            // If the payload is null just return the message
            if (payload == null)
            {
                logger.warn("Received payload is null. " //$NON-NLS-1$
                        + "No JMS selector properties will be set. " //$NON-NLS-1$
                        + "Returning message unchanged."); //$NON-NLS-1$
                return msg;
            }
            msg = setSelectorProperties(msg, payload, defaultSelector, customSelector);
            logger.debug("Successfully added selector properties to " //$NON-NLS-1$
                    + "the JMS message."); //$NON-NLS-1$
            return msg;
        }
        catch (RuntimeException e)
        {
            throw new PayloadOperationException("Failed to add selector properties to JMS message", e); //$NON-NLS-1$
        }
    }

    /**
     * Set the selector properties on the Message
     * 
     * @param message The message to set the selector properties on
     * @param payload The payload
     * @param selector The selector to use
     * @param customSelector The custom selector
     * @return message The enriched message
     * @throws PayloadOperationException Exception if we could not set the selector properties
     */
    private static Message setSelectorProperties(Message message, Payload payload, Map<String, Object> selector,
            Map<String, Object> customSelector) throws PayloadOperationException
    {
        try
        {
            // Allow custom selectors to merge and override defaults
            // if they do override a default then warn
            // this provides flexibility with an "I told you so" attitude
            if (!(customSelector == null || customSelector.isEmpty()))
            {
                logger.info("Caller specified the following customSelector " //$NON-NLS-1$
                        + "JMS properies [" + customSelector + "]"); //$NON-NLS-1$ //$NON-NLS-2$
                Iterator<String> it = selector.keySet().iterator();
                while (it.hasNext())
                {
                    String key = it.next();
                    if (customSelector.containsKey(key)) logger.warn("customSelector propery [" + key //$NON-NLS-1$
                            + "] will override the default in the payload!"); //$NON-NLS-1$
                }
                selector.putAll(customSelector);
                logger.info("Caller specified customSelector JMS properies " //$NON-NLS-1$
                        + "have been merged into default JMS properties."); //$NON-NLS-1$
            }
            // Get a map of the payload attributes
            Map<String, Object> payloadMap = PayloadHelper.getPayloadMap(payload);
            // Iterate over the valid selector property names
            Iterator<String> it = selector.keySet().iterator();
            while (it.hasNext())
            {
                String key = it.next();
                // Try to get the "valid selector" name from the
                // incoming payload. If this is null, then try the
                // default value from the "valid selector" map.
                // If still null then simply dont set any property value.
                Object obj = payloadMap.get(key);
                if (obj == null)
                {
                    obj = selector.get(key);
                    // if not in the payload either then skip it
                    if (obj == null)
                    {
                        continue;
                    }
                }
                // Got data - add the property to the mapMessage
                if (obj instanceof Boolean)
                {
                    message.setBooleanProperty(key, ((Boolean) obj).booleanValue());
                }
                else if (obj instanceof Byte)
                {
                    message.setByteProperty(key, ((Byte) obj).byteValue());
                }
                else if (obj instanceof Short)
                {
                    message.setShortProperty(key, ((Short) obj).shortValue());
                }
                else if (obj instanceof Character)
                {
                    message.setStringProperty(key, ((Character) obj).toString());
                }
                else if (obj instanceof Integer)
                {
                    message.setIntProperty(key, ((Integer) obj).intValue());
                }
                else if (obj instanceof Long)
                {
                    message.setLongProperty(key, ((Long) obj).longValue());
                }
                else if (obj instanceof Float)
                {
                    message.setFloatProperty(key, ((Float) obj).floatValue());
                }
                else if (obj instanceof Double)
                {
                    message.setDoubleProperty(key, ((Double) obj).doubleValue());
                }
                else if (obj instanceof String)
                {
                    message.setStringProperty(key, (String) obj);
                }
                else if (obj instanceof byte[])
                {
                    message.setStringProperty(key, new String((byte[]) obj));
                }
                else
                    logger.warn("Unsupported type found in " //$NON-NLS-1$
                            + "the selector property to be set " //$NON-NLS-1$
                            + "on creation of JMS message. " //$NON-NLS-1$
                            + "Selector name [" + key + "] object class [" //$NON-NLS-1$ //$NON-NLS-2$
                            + obj.getClass().getName() + "]. " //$NON-NLS-1$
                            + "This attribute will be ignored."); //$NON-NLS-1$
            }
            logger.debug("Successfully added selector properties to " //$NON-NLS-1$
                    + "the JMS message."); //$NON-NLS-1$
            return message;
        }
        catch (JMSException e)
        {
            throw new PayloadOperationException("Failed to add selector properties to JMS message", e); //$NON-NLS-1$
        }
        catch (RuntimeException e)
        {
            throw new PayloadOperationException("Failed to add selector properties to JMS message", e); //$NON-NLS-1$
        }
    }

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
            PayloadOperationException
    {
        return this.envelopeToMapMessage(envelope, session, new HashMap<String, Object>());
    }

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
            throws EnvelopeOperationException, PayloadOperationException
    {
        try
        {
            logger.debug("Creating JMS MapMessage from incoming envelope..."); //$NON-NLS-1$
            MapMessage mapMessage = payloadsToMapMessage(envelope.getPayloads(), session);
            // Get a map of the attributes as native types
            Map<String, Object> envelopeMap = EnvelopeHelper.getEnvelopeMap(envelope, false);
            Iterator<String> it = envelopeMap.keySet().iterator();
            while (it.hasNext())
            {
                String key = it.next();
                Object obj = envelopeMap.get(key);
                // Got data - can we populate the mapMessage with it
                if (obj instanceof Boolean)
                {
                    mapMessage.setBoolean(MetaDataInterface.ENVELOPE_PREFIX + key, ((Boolean) obj).booleanValue());
                }
                else if (obj instanceof Byte)
                {
                    mapMessage.setByte(MetaDataInterface.ENVELOPE_PREFIX + key, ((Byte) obj).byteValue());
                }
                else if (obj instanceof Short)
                {
                    mapMessage.setShort(MetaDataInterface.ENVELOPE_PREFIX + key, ((Short) obj).shortValue());
                }
                else if (obj instanceof Character)
                {
                    mapMessage.setChar(MetaDataInterface.ENVELOPE_PREFIX + key, ((Character) obj).charValue());
                }
                else if (obj instanceof Integer)
                {
                    mapMessage.setInt(MetaDataInterface.ENVELOPE_PREFIX + key, ((Integer) obj).intValue());
                }
                else if (obj instanceof Long)
                {
                    mapMessage.setLong(MetaDataInterface.ENVELOPE_PREFIX + key, ((Long) obj).longValue());
                }
                else if (obj instanceof Float)
                {
                    mapMessage.setFloat(MetaDataInterface.ENVELOPE_PREFIX + key, ((Float) obj).floatValue());
                }
                else if (obj instanceof Double)
                {
                    mapMessage.setDouble(MetaDataInterface.ENVELOPE_PREFIX + key, ((Double) obj).doubleValue());
                }
                else if (obj instanceof String)
                {
                    mapMessage.setString(MetaDataInterface.ENVELOPE_PREFIX + key, (String) obj);
                }
                else if (obj instanceof byte[])
                {
                    mapMessage.setBytes(MetaDataInterface.ENVELOPE_PREFIX + key, (byte[]) obj);
                }
                else if (obj instanceof List)
                {
                    logger.debug("Assuming encountered List is Payload<List>. Ignoring..."); //$NON-NLS-1$
                }
                else
                    logger.warn("Unsupported type found in " //$NON-NLS-1$
                            + "EnvelopeAttributes Map " //$NON-NLS-1$
                            + "on creation of JMS MapMessage [" //$NON-NLS-1$
                            + obj.getClass().getName() + "]. " //$NON-NLS-1$
                            + "This attribute will be ignored."); //$NON-NLS-1$
            }
            logger.debug("Successfully created JMS MapMessage from Envelope."); //$NON-NLS-1$
            // Set required selector properties
            defaultMessageSelector.put(MetaDataInterface.ID, envelope.getId());
            defaultMessageSelector.put(MetaDataInterface.NAME, envelope.getName());
            defaultMessageSelector.put(MetaDataInterface.SRC_SYSTEM, envelope.getSrcSystem());
            mapMessage = (MapMessage) setSelectorProperties(mapMessage, envelope, defaultMessageSelector,
                customMessageSelector);
            return mapMessage;
        }
        catch (JMSException e)
        {
            throw new PayloadOperationException("Failed to create MapMessage from Envelope", e); //$NON-NLS-1$
        }
        catch (RuntimeException e)
        {
            throw new PayloadOperationException("Failed to create MapMessage from Envelope", e); //$NON-NLS-1$
        }
    }

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
            Map<String, Object> customMessageSelector) throws EnvelopeOperationException, PayloadOperationException
    {
        try
        {
            logger.debug("Creating JMS TextMessage from incoming envelope..."); //$NON-NLS-1$
            // If the number of payloads is more than one, then we can't support
            // this
            List<Payload> payloads = envelope.getPayloads();
            if (payloads.size() > 1)
            {
                throw new PayloadOperationException("JMS TextMessage with more than 1 payload, " + //$NON-NLS-1$
                        "is not supported, you will need to use another JMS Message construct, e.g. MapMessage"); //$NON-NLS-1$
            }
            TextMessage textMessage = payloadToTextMessage(payloads.get(0), session);
            // Get a map of the attributes as native types
            Map<String, Object> envelopeMap = EnvelopeHelper.getEnvelopeMap(envelope, false);
            Iterator<String> it = envelopeMap.keySet().iterator();
            while (it.hasNext())
            {
                String key = it.next();
                Object obj = envelopeMap.get(key);
                // Got data - can we populate the mapMessage with it
                if (obj instanceof Boolean)
                {
                    textMessage.setBooleanProperty(MetaDataInterface.ENVELOPE_PREFIX + key, ((Boolean) obj)
                        .booleanValue());
                }
                else if (obj instanceof Byte)
                {
                    textMessage.setByteProperty(MetaDataInterface.ENVELOPE_PREFIX + key, ((Byte) obj).byteValue());
                }
                else if (obj instanceof Short)
                {
                    textMessage.setShortProperty(MetaDataInterface.ENVELOPE_PREFIX + key, ((Short) obj).shortValue());
                }
                else if (obj instanceof Integer)
                {
                    textMessage.setIntProperty(MetaDataInterface.ENVELOPE_PREFIX + key, ((Integer) obj).intValue());
                }
                else if (obj instanceof Long)
                {
                    textMessage.setLongProperty(MetaDataInterface.ENVELOPE_PREFIX + key, ((Long) obj).longValue());
                }
                else if (obj instanceof Float)
                {
                    textMessage.setFloatProperty(MetaDataInterface.ENVELOPE_PREFIX + key, ((Float) obj).floatValue());
                }
                else if (obj instanceof Double)
                {
                    textMessage
                        .setDoubleProperty(MetaDataInterface.ENVELOPE_PREFIX + key, ((Double) obj).doubleValue());
                }
                else if (obj instanceof String)
                {
                    textMessage.setStringProperty(MetaDataInterface.ENVELOPE_PREFIX + key, (String) obj);
                }
                else if (obj instanceof List)
                {
                    logger.debug("Assuming encountered List is Payload<List>. Ignoring..."); //$NON-NLS-1$
                }
                else
                    logger.warn("Unsupported type found in " //$NON-NLS-1$
                            + "EnvelopeAttributes Map " //$NON-NLS-1$
                            + "on creation of JMS TextMessage [" //$NON-NLS-1$
                            + obj.getClass().getName() + "]. " //$NON-NLS-1$
                            + "This attribute will be ignored."); //$NON-NLS-1$
            }
            logger.debug("Successfully created JMS TextMessage from Envelope."); //$NON-NLS-1$
            // Set required selector properties
            defaultMessageSelector.put(MetaDataInterface.ID, envelope.getId());
            defaultMessageSelector.put(MetaDataInterface.NAME, envelope.getName());
            defaultMessageSelector.put(MetaDataInterface.SRC_SYSTEM, envelope.getSrcSystem());
            textMessage = (TextMessage) setSelectorProperties(textMessage, envelope, defaultMessageSelector,
                customMessageSelector);
            return textMessage;
        }
        catch (JMSException e)
        {
            throw new PayloadOperationException("Failed to create TextMessage from Envelope", e); //$NON-NLS-1$
        }
        catch (RuntimeException e)
        {
            throw new PayloadOperationException("Failed to create TextMessage from Envelope", e); //$NON-NLS-1$
        }
    }

    /**
     * Set the selector properties on the message
     * 
     * @param envelope The envelope to convert
     * @param message The message to set the properties on
     * @param selector The selector to use
     * @param customSelector The custom selector to use
     * @return Message
     * @throws EnvelopeOperationException Exception if there was an Envelope based problem
     */
    private static Message setSelectorProperties(Message message, Envelope envelope, Map<String, Object> selector,
            Map<String, Object> customSelector) throws EnvelopeOperationException
    {
        try
        {
            // Allow custom selectors to merge and override defaults
            // if they do override a default then warn
            // this provides flexibility with an "I told you so" attitude
            if (!(customSelector == null || customSelector.isEmpty()))
            {
                logger.info("Caller specified the following customSelector " //$NON-NLS-1$
                        + "JMS properies [" + customSelector + "]"); //$NON-NLS-1$//$NON-NLS-2$
                Iterator<String> it = selector.keySet().iterator();
                while (it.hasNext())
                {
                    String key = it.next();
                    if (customSelector.containsKey(key)) logger.warn("customSelector propery [" + key //$NON-NLS-1$
                            + "] will override the default in the payload!"); //$NON-NLS-1$
                }
                selector.putAll(customSelector);
                logger.info("Caller specified customSelector JMS properies " //$NON-NLS-1$
                        + "have been merged into default JMS properties."); //$NON-NLS-1$
            }
            // Get a map of the payload attributes
            Map<String, Object> envelopeMap = EnvelopeHelper.getEnvelopeMap(envelope, false);
            // Iterate over the valid selector property names
            Iterator<String> it = selector.keySet().iterator();
            while (it.hasNext())
            {
                String key = it.next();
                // Try to get the "valid selector" name from the
                // incoming payload. If this is null, then try the
                // default value from the "valid selector" map.
                // If still null then simply dont set any property value.
                Object obj = envelopeMap.get(key);
                if (obj == null)
                {
                    obj = selector.get(key);
                    // if not in the envelope either then skip it
                    if (obj == null)
                    {
                        continue;
                    }
                }
                // Got data - add the property to the mapMessage
                if (obj instanceof Boolean)
                {
                    message.setBooleanProperty(key, ((Boolean) obj).booleanValue());
                }
                else if (obj instanceof Byte)
                {
                    message.setByteProperty(key, ((Byte) obj).byteValue());
                }
                else if (obj instanceof Short)
                {
                    message.setShortProperty(key, ((Short) obj).shortValue());
                }
                else if (obj instanceof Character)
                {
                    message.setStringProperty(key, ((Character) obj).toString());
                }
                else if (obj instanceof Integer)
                {
                    message.setIntProperty(key, ((Integer) obj).intValue());
                }
                else if (obj instanceof Long)
                {
                    message.setLongProperty(key, ((Long) obj).longValue());
                }
                else if (obj instanceof Float)
                {
                    message.setFloatProperty(key, ((Float) obj).floatValue());
                }
                else if (obj instanceof Double)
                {
                    message.setDoubleProperty(key, ((Double) obj).doubleValue());
                }
                else if (obj instanceof String)
                {
                    message.setStringProperty(key, (String) obj);
                }
                else if (obj instanceof byte[])
                {
                    message.setStringProperty(key, new String((byte[]) obj));
                }
                else
                {
                    logger.warn("Unsupported type found in " //$NON-NLS-1$
                            + "the selector property to be set " //$NON-NLS-1$
                            + "on creation of JMS message. " //$NON-NLS-1$
                            + "Selector name [" + key + "] object class [" //$NON-NLS-1$//$NON-NLS-2$
                            + obj.getClass().getName() + "]. " //$NON-NLS-1$
                            + "This attribute will be ignored."); //$NON-NLS-1$
                }
            }
            logger.debug("Successfully added selector properties to " //$NON-NLS-1$
                    + "the JMS message."); //$NON-NLS-1$
            return message;
        }
        catch (JMSException e)
        {
            throw new EnvelopeOperationException("Failed to add selector properties to JMS message", e); //$NON-NLS-1$
        }
        catch (RuntimeException e)
        {
            throw new EnvelopeOperationException("Failed to add selector properties to JMS message", e); //$NON-NLS-1$
        }
    }

    /**
     * Helper method to create a PayloadList from JMS Message.
     * 
     * @param message The message to get the payloads from
     * @return List<Payload>
     * @throws PayloadOperationException Exception if there was a Payload based problem
     * @throws JMSException Exception if there was a JMS based problem
     * @throws UnknownMessageContentException Exception if the content of the message could not be dealt with
     */
    public List<Payload> fromMessage(Message message) throws PayloadOperationException, JMSException,
            UnknownMessageContentException
    {
        ArrayList<Payload> payloadList = new ArrayList<Payload>();
        //
        // get the payload based on the JMS implementation
        if (message instanceof TextMessage)
        {
            logger.debug("Retrieving payload from JMS TextMessage..."); //$NON-NLS-1$
            TextMessage tm = (TextMessage) message;
            // TODO - we have no way of knowing Spec, so use most likely
            Payload pl = payloadFactory.newPayload(MetaDataInterface.UNDEFINED, Spec.TEXT_XML,
                MetaDataInterface.UNDEFINED, tm.getText().getBytes());
            // Restore id, name and srcSystem from the JMS properties.
            // Not a great use of properties, but convenient
            String prop = tm.getStringProperty(MetaDataInterface.ID);
            if (prop != null)
            {
                pl.setId(prop);
            }
            prop = tm.getStringProperty(MetaDataInterface.NAME);
            if (prop != null)
            {
                pl.setName(prop);
            }
            prop = tm.getStringProperty(MetaDataInterface.SRC_SYSTEM);
            if (prop != null)
            {
                pl.setSrcSystem(prop);
            }
            payloadList.add(pl);
        }
        else if (message instanceof MapMessage)
        {
            logger.debug("Retrieving payload from JMS MapMessage..."); //$NON-NLS-1$
            // Does this message actually contain an envelope
            if (!containsPayload(message))
            {
                throw new UnknownMessageContentException("MapMessage does not contain a valid payload."); //$NON-NLS-1$
            }
            MapMessage mm = (MapMessage) message;
            Payload payload = null;
            int payloadInstance = 0;
            String key = null;
            String keyName = null;
            Enumeration<?> mapMessageEnum = mm.getMapNames();
            // Initialise the array list to that
            // of the number of incoming payloads
            int payloadCount = mm.getInt(Payload.PAYLOAD_COUNT_KEY);
            for (int i = 0; i < payloadCount; i++)
            {
                // TODO - set actual name, format, and spec
                payloadList.add(payloadFactory.newPayload(MetaDataInterface.UNDEFINED, Spec.TEXT_XML,
                    MetaDataInterface.UNDEFINED));
            }
            logger.debug("Created payload holding list for [" //$NON-NLS-1$
                    + payloadCount + "] payloads."); //$NON-NLS-1$
            // Iterate through all map entries
            while (mapMessageEnum.hasMoreElements())
            {
                // Get the key of each map entry
                key = (String) mapMessageEnum.nextElement();
                if (key.startsWith(MetaDataInterface.PAYLOAD_PREFIX))
                {
                    // Get length of payloadLiteral
                    int payloadLiteral = MetaDataInterface.PAYLOAD_PREFIX.length();
                    int payloadNumberEndPos = key.indexOf('_', payloadLiteral);
                    keyName = key.substring(payloadNumberEndPos + 1);
                    // Establish the payload number within the mapMessage
                    try
                    {
                        payloadInstance = new Integer(key.substring(payloadLiteral, payloadNumberEndPos)).intValue();
                        if (payloadInstance < 0) throw new NumberFormatException();
                        payload = payloadList.get(payloadInstance);
                        logger.debug("Updating payload holding instance [" //$NON-NLS-1$
                                + payloadInstance + "]."); //$NON-NLS-1$
                        // TODO - we should implement a PayloadInfo for the
                        // BeanInfo interface
                        BeanInfo info = Introspector.getBeanInfo(payload.getClass(), Object.class);
                        for (PropertyDescriptor pd : info.getPropertyDescriptors())
                        {
                            if (keyName.equals(pd.getName()))
                            {
                                //
                                // get the object from the MapMessage
                                Object obj = mm.getObject(key);
                                if (obj instanceof Boolean || obj instanceof Byte || obj instanceof Short
                                        || obj instanceof Character || obj instanceof Integer || obj instanceof Long
                                        || obj instanceof Float || obj instanceof Double || obj instanceof String
                                        || obj instanceof byte[])
                                {
                                    logger.debug("Object primative is [" //$NON-NLS-1$
                                            + obj.getClass().getName() + "]"); //$NON-NLS-1$
                                }
                                else
                                {
                                    throw new JMSException("Unsupported class [" //$NON-NLS-1$
                                            + obj.getClass().getName() + "] found in JMS MapMessage"); //$NON-NLS-1$
                                }
                                Method writeMethod = pd.getWriteMethod();
                                if (writeMethod==null){
                                	logger.warn("Message contains unmappable property ["+pd.getName()+"]");
                                } else{
                                	writeMethod.invoke(payload, obj);
                                }
                                break;
                            }
                        }
                    }
                    catch (Exception e)
                    {
                        // This is very bad
                        throw new PayloadOperationException("Unable to extract " //$NON-NLS-1$
                                + "payload from incoming JMS MapMessage. " //$NON-NLS-1$
                                + "Problem invoking setter for [" //$NON-NLS-1$
                                + keyName + "] ", e); //$NON-NLS-1$
                    }
                }
                else
                {
                    logger.debug("Ignoring key [" + key + "]..."); //$NON-NLS-1$ //$NON-NLS-2$
                }
            }
        }
        else
        {
            String msg = "Received unsupported JMS Message Type."; //$NON-NLS-1$
            throw new JMSException(msg);
        }
        return payloadList;
    }

    /**
     * Check content of the incoming Message to see if it contains a Payload.
     * 
     * @param message The message to check
     * @return true if the message contains a payload, else false
     * @throws JMSException Exception if we could not check the message
     */
    private static boolean containsPayload(Message message) throws JMSException
    {
        // TextMessage is can always be dealt with as a Payload
        if (message instanceof TextMessage)
        {
            return true;
        }
        // MapMessage can only support our definition of a Payload
        else if (message instanceof MapMessage)
        {
            Enumeration<?> mapMessageEnum = ((MapMessage) message).getMapNames();
            while (mapMessageEnum.hasMoreElements())
            {
                if (((String) mapMessageEnum.nextElement()).startsWith(MetaDataInterface.PAYLOAD_PREFIX))
                {
                    return true;
                }
            }
            return false;
        }
        return false;
    }
}
