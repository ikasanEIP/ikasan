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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jms.JMSException;
import javax.jms.MapMessage;

import org.apache.log4j.Logger;
import org.ikasan.common.Payload;
import org.ikasan.framework.component.Event;
import org.ikasan.framework.event.serialisation.DefaultMapMessageEventSerialiser;
import org.ikasan.framework.event.serialisation.EventDeserialisationException;

/**
 * Compatibility implementation for converting an Event and Payload generated 
 * from Ikasan 0.7.10 into the HEAD version of Event and Payload.
 * NOTE: A completely separate class should be implemented for converting 
 * Events/Payloads generated from HEAD back into 0.7.10.
 * 
 * @author Ikasan Development Team
 *
 */
public class ConversionMapMessageEventToHeadSerialiser extends DefaultMapMessageEventSerialiser
{
    /** Logger instance */
    private static Logger logger = Logger.getLogger(ConversionMapMessageEventToHeadSerialiser.class);

    static final String OLD_PAYLOAD_PREFIX = "payload_";

    static final String OLD_PAYLOAD_CONTENT_SUFFIX = "_content";
    static final String OLD_PAYLOAD_SRC_SYSTEM_SUFFIX = "_srcSystem";

    static final String OLD_PAYLOAD_ID_SUFFIX = "_id";
    static final String OLD_PAYLOAD_SPEC_SUFFIX = "_spec";
    
    static final String ENVELOPE_ID = "envelope_id";
    static final String ENVELOPE_PRIORITY = "envelope_priority";
    static final String ENVELOPE_TIMESTAMP = "envelope_timestamp";

    @Override
    protected Event demapEvent(MapMessage mapMessage, String moduleName,
            String componentName, List<Payload> payloads, List<String> eventFieldNames) throws JMSException 
    {
        String eventId = null;
        int priority = -1;
        Date timestamp = null;
        
        for(String fieldName : eventFieldNames)
        {
            if (fieldName.equals(EVENT_FIELD_ID))
            {
                eventId=mapMessage.getString(EVENT_FIELD_ID);
            }
            else if (fieldName.equals(ENVELOPE_ID))
            {
                eventId=mapMessage.getString(ENVELOPE_ID);
            }
            else if (fieldName.equals(EVENT_FIELD_PRIORITY))
            {
                priority=mapMessage.getInt(EVENT_FIELD_PRIORITY);
            }
            else if (fieldName.equals(ENVELOPE_PRIORITY))
            {
                priority=mapMessage.getInt(ENVELOPE_PRIORITY);
            }
            else if (fieldName.equals(EVENT_FIELD_TIMESTAMP))
            {
                timestamp=new Date(mapMessage.getLong(EVENT_FIELD_TIMESTAMP));
            }
            else if (fieldName.equals(ENVELOPE_TIMESTAMP))
            {
                timestamp=new Date(mapMessage.getLong(ENVELOPE_TIMESTAMP));
            }
            else
            {
                logger.debug("Ignoring fieldName [" + fieldName + "]");
            }
        }
        
        return new Event(eventId, priority, timestamp, payloads);
    }

    @Override
    protected Payload demapPayload(int payloadOrdinal, MapMessage mapMessage, List<String> payloadFieldNames) 
        throws JMSException, EventDeserialisationException 
    {
        String payloadId = null;
        byte[] payloadContent = null;
        
        Map<String, String> payloadAttributes = new HashMap<String, String>();
        
        for(String fieldName : payloadFieldNames){
            
            // payload content
            if (fieldName.equals(PAYLOAD_PREFIX + payloadOrdinal + PAYLOAD_CONTENT_SUFFIX))
            {
                payloadContent=mapMessage.getBytes(PAYLOAD_PREFIX + payloadOrdinal +PAYLOAD_CONTENT_SUFFIX);
            }
            else if (fieldName.equals(OLD_PAYLOAD_PREFIX + payloadOrdinal + OLD_PAYLOAD_CONTENT_SUFFIX))
            {
                payloadContent=mapMessage.getBytes(OLD_PAYLOAD_PREFIX + payloadOrdinal + OLD_PAYLOAD_CONTENT_SUFFIX);
            }
            // payload id
            else if (fieldName.equals(PAYLOAD_PREFIX + payloadOrdinal + PAYLOAD_ID_SUFFIX))
            {
                payloadId=mapMessage.getString(PAYLOAD_PREFIX + payloadOrdinal + PAYLOAD_ID_SUFFIX);
            }
            else if (fieldName.equals(OLD_PAYLOAD_PREFIX + payloadOrdinal + OLD_PAYLOAD_ID_SUFFIX))
            {
                payloadId=mapMessage.getString(OLD_PAYLOAD_PREFIX + payloadOrdinal + OLD_PAYLOAD_ID_SUFFIX);
            }
            else if(fieldName.startsWith(PAYLOAD_PREFIX + payloadOrdinal + ATTRIBUTE_PREFIX))
            {
                //its a payload attribute
                payloadAttributes.put(fieldName.substring((PAYLOAD_PREFIX + payloadOrdinal + ATTRIBUTE_PREFIX).length()), mapMessage.getString(fieldName));
            }
            // default this to an attribute
            else if(fieldName.startsWith(OLD_PAYLOAD_PREFIX + payloadOrdinal + "_"))
            {
                payloadAttributes.put(fieldName.substring((OLD_PAYLOAD_PREFIX + payloadOrdinal + "_").length()), mapMessage.getString(fieldName));
            }
            else
            {
                throw new EventDeserialisationException("Unknown map entry ["+fieldName+"]");
            }
        }

        Payload payload = this.payloadFactory.newPayload(payloadId, payloadContent);
        
        //set any payload attributs
        for (String attributeName : payloadAttributes.keySet())
        {
            payload.setAttribute(attributeName, payloadAttributes.get(attributeName));
        }
        
        return payload;
    }

    @Override
    protected List<List<String>> groupMapNames(Enumeration<String> mapNames) 
    {
        List<List<String>> result = new ArrayList<List<String>>();
        
        List<String> eventFields = new ArrayList<String>();
        result.add(eventFields);
        
        Map<String, List<String>> payloadFieldNameLists = new HashMap<String, List<String>>();
        
        while(mapNames.hasMoreElements())
        {
            String mapName = mapNames.nextElement();
            
            if(mapName.startsWith(PAYLOAD_PREFIX) || mapName.startsWith(PAYLOAD_PREFIX.toLowerCase()))
            {
                //its some sort of payload field
                String fullPayloadPrefix = mapName.substring(0, mapName.indexOf('_', PAYLOAD_PREFIX.length()));
                if (payloadFieldNameLists.get(fullPayloadPrefix)==null)
                {
                    payloadFieldNameLists.put(fullPayloadPrefix, new ArrayList<String>());
                }
                
                List<String> payloadFields = payloadFieldNameLists.get(fullPayloadPrefix);
                payloadFields.add(mapName);
            } 
            else
            {
                //its some sort of event level field
                eventFields.add(mapName);
            }
        }

        List<String> fullPayloadPrefixes = new ArrayList<String>(payloadFieldNameLists.keySet());
        Collections.sort(fullPayloadPrefixes);
        
        for(int i=0; i < fullPayloadPrefixes.size(); i++)
        {
            String fullPayloadPrefix = fullPayloadPrefixes.get(i);
            if( !(fullPayloadPrefix.equals(PAYLOAD_PREFIX+i) || fullPayloadPrefix.equals(PAYLOAD_PREFIX.toLowerCase()+i)) )
            {
                throw new RuntimeException("Non-continuous payload sequence! was expecting ["+PAYLOAD_PREFIX+i+"] but got ["+fullPayloadPrefix+"]");
            }
            result.add(payloadFieldNameLists.get(fullPayloadPrefix));
        }
        return result;
    }

}
