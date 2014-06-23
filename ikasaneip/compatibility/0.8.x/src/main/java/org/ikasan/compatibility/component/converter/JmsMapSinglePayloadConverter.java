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
package org.ikasan.compatibility.component.converter;

import org.apache.log4j.Logger;
import org.ikasan.spec.component.transformation.Converter;
import org.ikasan.spec.component.transformation.TransformationException;

import javax.jms.JMSException;
import javax.jms.MapMessage;

/**
 * Extract a single payload XML from the incoming JMS Map message from i8 or i7 formats.
 * @author Ikasan Development Team.
 */
public class JmsMapSinglePayloadConverter implements Converter<MapMessage,byte[]>
{
    /** JMS Map properties from ikasan0.8.x for accessing the payload */
    static String I8_PAYLOAD_0_CONTENT = String.valueOf("PAYLOAD_0_CONTENT");

    /** JMS Map properties from ikasan0.7.x for accessing the payload */
    static final String I7_PAYLOAD_0_CONTENT =  String.valueOf("payload_0_content");

    /**
     * Convert the incoming mapMessage into the first single payload content.
     * @param mapMessage
     * @return byte[]
     * @throws TransformationException
     */
    @Override
    public byte[] convert(MapMessage mapMessage) throws TransformationException
    {
        try
        {
            byte[] payload = getMessageContent(mapMessage, I8_PAYLOAD_0_CONTENT);
            if(payload == null)
            {
                payload = getMessageContent(mapMessage, I7_PAYLOAD_0_CONTENT);
                if(payload == null)
                {
                    throw new JMSException("Cannot retrieve payload from mapMessage in either i8 field " + I8_PAYLOAD_0_CONTENT + " or i7 field " + I7_PAYLOAD_0_CONTENT);
                }
            }

            return payload;
        }
        catch(JMSException e)
        {
            throw new TransformationException(e);
        }
    }

    /**
     * Extract the given payloadContent literal from the incoming mapMessage.
     * @param mapMessage
     * @param payloadContent
     * @return
     */
    protected byte[] getMessageContent(MapMessage mapMessage, String payloadContent)
        throws JMSException
    {
        return mapMessage.getBytes(payloadContent);
    }
}
