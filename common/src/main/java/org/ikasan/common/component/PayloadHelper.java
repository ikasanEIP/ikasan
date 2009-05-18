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
package org.ikasan.common.component;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;

import org.ikasan.common.Payload;
import org.ikasan.common.component.Encoding;
import org.ikasan.common.component.Format;
import org.ikasan.common.component.Priority;
import org.ikasan.common.component.Spec;
import org.ikasan.common.util.Codec;

// Imported log4j classes
import org.apache.commons.codec.DecoderException;
import org.apache.log4j.Logger;

/**
 * The PayloadHelper class provides general helpers around payload operations
 * from simple access of the payload attributes to managing the translations
 * between Payloads and JMS Messages.
 *
 * The examples below demonstrate how to use the helper for translation between
 * Payload to/from JMS Messages.
 *
 * Currently only JMS MapMessage and TextMessage are supported.
 * You are advised to use MapMessage as this provides a richer encapsulation
 * of data over the TextMessage.
 *
 * <p><h3>Example usage of the PayloadHelper</h3></p>
 * <b>Example 1. Single Payload to a JMS MapMessage</b></br>
 * The following example demonstrates the creation of a single
 * Payload and subsequent creation of a JMS MapMessage form that Payload.
 *
 * <p><code>
 * Payload payload = ResourceLoader.getInstance().newPayload();<br/>
 * payload.setContent("This is where your content goes".getBytes());<br/>
 * payload.setName("This is the name of your payload");<br/>
 * <br/>
 * PayloadHelper ph = new PayloadHelper();<br/>
 * MapMessage mapMessage = ph.payloadToMapMessage(payload, session);<br/>
 * </code></p>
 * The <code>mapMessage</code> can then be published to your desired topic/queue
 * and will contain all facets of the Payload Java object.
 * <p>
 * <b>Example 2. Multiple Payloads to a JMS MapMessage</b></br>
 * The following example demonstrates the creation of multiple payloads
 * to a single JMS MapMessage.
 * <p>Best Practice: MapMessages can support any number of
 * Payloads. However, the first payload (payload zero) will be the primary
 * payload which will be used to populate the JMS selector properties of this message.
 * All other payloads are subsidiary to the primary and should be related
 * to the primary as part of the business flow (ie. supporting data aspects of
 * the primary), or a convenient grouping of like payloads.
 *
 * <p><code>
 * Payload payload1 = ResourceLoader.getInstance().newPayload();<br/>
 * payload.setContent("This is content for payload1".getBytes());<br/>
 * payload.setName("payload1Name");<br/>
 * <br/>
 *
 * Payload payload2 = ResourceLoader.getInstance().newPayload("This is content for payload2".getBytes());<br/>
 * payload.setName("payload2Name");<br/>
 * <br/>
 * List<Payload> payloadList = new ArrayList<Payload>();<br/>
 * payloadList.add(payload1);<br/>
 * payloadList.add(payload2);<br/>
 * <br/>
 * PayloadHelper ph = new PayloadHelper();<br/>
 * MapMessage mapMessage = ph.payloadToMapMessage(payloadList, session);<br/>
 * </code></p>
 * The <code>mapMessage</code> can then be published to your desired topic/queue
 * and will contain all facets of all Payloads.
 * <p>
 * <b>Example 3. TextMessage or MapMessage to a List of Payloads</b></br>
 * The following example demonstrates the creation of a list of payload(s)
 * from either a JMS TextMessage or JMS MapMessage.
 *
 * <p><code>
 * List<Payload> payloadList = ph.messageToPayload(mapMessage);<br/>
 * for(Payload payload:payloadList)<br/>
 * {<br/>
 * &nbsp&nbsp&nbsp// payload content may well be encoded, simply call decode<br/>
 * &nbsp&nbsp&nbsptry<br/>
 * &nbsp&nbsp&nbsp{<br/>
 * &nbsp&nbsp&nbsp&nbsp&nbsp&nbsppayload = ph.decode(payload);<br/>
 * &nbsp&nbsp&nbsp}<br/>
 * &nbsp&nbsp&nbspcatch(DecoderException e)<br/>
 * &nbsp&nbsp&nbsp{<br/>
 * &nbsp&nbsp&nbsp&nbsp&nbsp&nbsp// handle exception<br/>
 * &nbsp&nbsp&nbsp}<br/>
 * }<br/>
 * </code></p>
 * <br/>
 * @author Jeff Mitchell
 */
public class PayloadHelper
{
    /**
     * Serialize ID
     */
    private static final long serialVersionUID = 1L;





    /**
     * The logger instance.
     */
    private static Logger logger = Logger.getLogger(PayloadHelper.class);
    


    /**
     * Returns a Map of the payload attributes as their native types.
     *
     * @param payload
     * @return Map
     * @throws PayloadOperationException
     */
    public static Map<String,Object> getPayloadMap(Payload payload)
        throws PayloadOperationException
    {
        return getPayloadMap(payload, false);
    }

    /**
     * Returns a Map of the payload attributes either as their native types or
     * as Strings.
     *
     * @param payload incoming payload to map attributes
     * @param toString - determines whether the objects populate the map
     *            natively (false) or as Strings (true)
     * @return Map
     * @throws PayloadOperationException
     */
    public static Map<String,Object> getPayloadMap(Payload payload, boolean toString)
        throws PayloadOperationException
    {
        Map<String,Object> payloadMap = new HashMap<String,Object>();

        try
        {
            //
            // use introspection to get bean attributes
            BeanInfo info = Introspector.getBeanInfo(payload.getClass(), Object.class);

            //
            // iterate over the payload attributes
            for (PropertyDescriptor pd : info.getPropertyDescriptors())
            {
                Object [] methodParams = {};
                Object obj = pd.getReadMethod().invoke(payload, methodParams);

                //
                // no valid data to return
                if (obj == null) continue;

                //
                // got data - can we populate the map with it
                if (toString)
                {
                    if (obj instanceof Boolean   ||
                        obj instanceof Byte      ||
                        obj instanceof Short     ||
                        obj instanceof Character ||
                        obj instanceof Integer   ||
                        obj instanceof Long      ||
                        obj instanceof Float     ||
                        obj instanceof Double)
                    {
                        payloadMap.put(pd.getName(), obj.toString());
                    }
                    else if (obj instanceof String)
                    {
                        payloadMap.put(pd.getName(), obj);
                    }
                    else if (obj instanceof byte[])
                    {
                        payloadMap.put(pd.getName(), new String((byte[])obj));
                    }
                    else
                    {
                        logger.warn("Unsupported attribute found in " //$NON-NLS-1$
                                  + payload.getClass().getName()
                                  + " on creation of Map [" //$NON-NLS-1$
                                  + obj.getClass().getName() + "]. This " //$NON-NLS-1$
                                  + "attribute of the payload will be ignored."); //$NON-NLS-1$
                    }
                }
                else
                {
                    payloadMap.put(pd.getName(), obj);
                }
            }

            return payloadMap;
        }
        catch (IntrospectionException e)
        {
            throw new PayloadOperationException(e);
        }
        catch (InvocationTargetException e)
        {
            Throwable cause = e.getCause();
            if (cause != null)
            {
                // Go through the possible (checked) exceptions
                if (cause instanceof PayloadOperationException)
                {
                    throw (PayloadOperationException)cause;
                }

                throw new PayloadOperationException(cause);
            }

            throw new PayloadOperationException(e);
        }
        catch (IllegalAccessException e)
        {
            throw new PayloadOperationException(e);
        }

    }

    /**
     * Getter for priority
     *
     * @param priority
     * @return Priority
     * @throws NoSuchElementException
     */
    public static Priority getPriority(final int priority)
        throws NoSuchElementException
    {
        logger.debug("Getting priority..."); //$NON-NLS-1$
        Priority[] p = Priority.values();
        for (Priority p1 : p)
        {
            if(p1.getLevel() == priority)
                return p1;
        }

        throw new NoSuchElementException("Unknown Priority for [" + priority + "]"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * Encoder for the payloads
     *
     * @param payloads
     * @param encoding
     * @return Payloads with encoded content
     */
    public static List<Payload> encode(final List<Payload> payloads, final String encoding)
    {
        for(Payload payload:payloads)
            payload = encode(payload, encoding);

        return payloads;
    }

    /**
     * Encoder for the payload content
     *
     * @param payload
     * @param encoding
     * @return Payload with encoded content
     */
    public static Payload encode(final Payload payload, final String encoding)
    {
        byte[] encodedContent = Codec.encode(payload.getContent(), encoding);
        payload.setEncoding(encoding);
        payload.setContent(encodedContent);
        return payload;
    }

    /**
     * Decoder for the payloads content
     *
     * @param payloads
     * @return Payloads with decoded content
     * @throws DecoderException
     */
    public static List<Payload> decode(final List<Payload> payloads)
        throws DecoderException
    {
        for(Payload payload:payloads)
        {
            payload = decode(payload);
        }

        return payloads;
    }

    /**
     * Decoder for the payload content
     *
     * @param payload
     * @return Payload with decoded content
     * @throws DecoderException
     */
    public static Payload decode(final Payload payload)
        throws DecoderException
    {
        try
        {
            byte[] decodedContent = Codec.decode(payload.getContent(), payload.getEncoding());
            payload.setContent(decodedContent);
            payload.setEncoding(Encoding.NOENC.toString());
        }
        catch(DecoderException e)
        {
            logger.error("Failed to decode the payload content for payload id [" //$NON-NLS-1$
                    + payload.getId() + "]", e); //$NON-NLS-1$
            throw e;
        }

        return payload;
    }

    /**
     * Getter for spec
     *
     * @param spec
     * @return Spec
     * @throws NoSuchElementException
     */
    public static Spec getSpec(final String spec)
        throws NoSuchElementException
    {
        logger.debug("Getting spec..."); //$NON-NLS-1$
        Spec[] s = Spec.values();
        for (Spec s1 : s)
        {
            if (s1.toString().equals(spec))
            {
                return s1;
            }
        }

        throw new NoSuchElementException("Unknown Spec for [" + spec + "]"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * Getter for encoding
     *
     * @param encoding
     * @return Encoding
     * @throws NoSuchElementException
     */
    public static Encoding getEncoding(final String encoding)
        throws NoSuchElementException
    {
        logger.debug("Getting encoding..."); //$NON-NLS-1$
        Encoding[] e = Encoding.values();
        for (Encoding e1 : e)
        {
            if (e1.toString().equals(encoding))
            {
                return e1;
            }
        }

        throw new NoSuchElementException("Unknown Encoding for [" + encoding + "]"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * Getter for format
     *
     * @param format
     * @return Format
     * @throws NoSuchElementException
     */
    public static Format getFormat(final String format)
        throws NoSuchElementException
    {
        logger.debug("Getting format..."); //$NON-NLS-1$
        Format[] f = Format.values();
        for (Format f1: f)
        {
            if (f1.toString().equals(format))
                return f1;
        }

        throw new NoSuchElementException("Unknown Format for [" + format + "]"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * Helper method to return the content of each payload in the given
     * list of payloads.
     *
     * @param payloads
     * @return List<byte[]> list of payload content
     * @throws PayloadOperationException
     * 
     * TODO (RD) check that this method always gets called with payloads from the same event,
     * if so, move this method to Event
     */
    public static List<byte[]> getPayloadsContent(List<Payload> payloads)
        throws PayloadOperationException
    {
        List<byte[]> payloadContent = new ArrayList<byte[]>();

        // Iterate over payloads and populate list with content
        try
        {
            for (Payload payload : payloads)
            {
                payloadContent.add(payload.getContent());
            }
        }
        catch (RuntimeException e)
        {
            throw new PayloadOperationException(
                "Failed to get Payload Content!", e); //$NON-NLS-1$
        }

        return payloadContent;
    }


    /**
     * Tell me how many payloads are present in this JMS message
     *
     * @param message
     * @return int - payload count
     * @throws JMSException
     */
    public static int getPayloadCount(Message message)
        throws JMSException
    {
        if (message instanceof MapMessage)
        {
            logger.debug("Message is a JMS MapMessage"); //$NON-NLS-1$
            MapMessage mm = (MapMessage)message;
            return mm.getInt(Payload.PAYLOAD_COUNT_KEY);
        }

        // All other JMS message types can only contain one payload
        return 1;
    }




    /**
     * Create a formatted string detailing all payload IDs
     * in the given payload list.
     *
     * TODO Rename this method to idsToString
     *
     * @param payloads
     * @return ids as a String
     */
    public static String idToString(List<Payload> payloads)
    {
        StringBuffer sb = new StringBuffer();
        for(Payload payload:payloads)
            sb.append(payload.idToString());

        return sb.toString();
    }

    /**
     * Get the attributes of the transported payload arraylist
     *
     * @param payloads
     * @return attributes of payloads
     * @throws PayloadOperationException
     */
    public static List<Map<String, Object>> getPayloadsAttributes(List<Payload> payloads)
        throws PayloadOperationException
    {
        List<Map<String, Object>> payloadAttributes = new ArrayList<Map<String, Object>>();

        try
        {
            // Iterate over payloads and populate list with content
            for (Payload payload : payloads)
            {
                // Need to get Map<String,String>
                boolean attributesToString = true;
                Map<String, Object> payloadMap = getPayloadMap(payload, attributesToString);
                payloadAttributes.add(payloadMap);
            }
        }
        catch (RuntimeException e)
        {
            throw new PayloadOperationException(
                "Failed to get Payload as an attribute map", e); //$NON-NLS-1$
        }

        return payloadAttributes;
    }




}
