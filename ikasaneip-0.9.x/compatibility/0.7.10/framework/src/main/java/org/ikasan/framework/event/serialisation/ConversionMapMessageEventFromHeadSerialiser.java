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

import java.util.List;
import java.util.Map;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Session;

import org.ikasan.common.Payload;
import org.ikasan.framework.component.Event;

/**
 * Compatibility implementation for converting an {@link Event} and {@link Payload} generated 
 * from HEAD version of Event and Payload to a {@link MapMessage} compatible with Ikasan 0.7.10. 
 * <p>
 * Note: this conversion <i>does not</i> take into account discrepancies in payload attribute names, for example,
 * if an Ikasan 0.8.0 payload is created with a <code>payloadName</code> attribute, this will be lost when converting
 * to an Ikasan 0.7.10 payload as <code>name</code> is not a known field on {@link Payload} and/or its parents; 0.7.x
 * deserialization uses reflection and therefore is looking for <code>name</code> attribute to set on a payload. 
 * 
 * 
 * @author Ikasan Development Team
 *
 */
public class ConversionMapMessageEventFromHeadSerialiser extends DefaultMapMessageEventSerialiser
{
    /** Map key name separator constant*/
    protected static final String SEPARATOR = "_";

    /** Prefix constant for payloads entries used in MapMessage map key values */
    protected final static String OLD_PAYLOAD_PREFIX = "payload_";

    /** Suffix constant used in MapMessage payload content key value */
    protected final static String OLD_PAYLOAD_CONTENT_SUFFIX = "_content";

    /** Suffix constant used in MapMessage payload id key value */
    protected final static String OLD_PAYLOAD_ID_SUFFIX = "_id";

    /** Number of payloads in a map message key */
    protected final static String OLD_PAYLOAD_COUNT_PROPERTY = "payloadCount";

    /* (non-Javadoc)
     * @see org.ikasan.framework.event.serialisation.DefaultMapMessageEventSerialiser#toMessage(org.ikasan.framework.component.Event, javax.jms.Session)
     */
    @Override
    public MapMessage toMessage(Event event, Session session) throws JMSException
    {
        MapMessage mapMessage = session.createMapMessage();
        List<Payload> payloads = event.getPayloads();
        int payloadCounter = 0;
        for (Payload payload : payloads)
        {
            mapMessage.setBytes(OLD_PAYLOAD_PREFIX + payloadCounter + OLD_PAYLOAD_CONTENT_SUFFIX, payload.getContent());
            mapMessage.setString(OLD_PAYLOAD_PREFIX + payloadCounter + OLD_PAYLOAD_ID_SUFFIX, payload.getId());
            
            for (Map.Entry<String, String> attribute : payload.getAttributeMap().entrySet())
            {
                mapMessage.setString(OLD_PAYLOAD_PREFIX + payloadCounter + SEPARATOR + attribute.getKey(), attribute.getValue());
            }
            payloadCounter = payloadCounter + 1;
        }
        mapMessage.setInt(OLD_PAYLOAD_COUNT_PROPERTY, payloadCounter);
        mapMessage.setJMSPriority(event.getPriority());
        return mapMessage;
    }
}
