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
package org.ikasan.component.endpoint.jms.consumer;

import javax.jms.*;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility converter for JMS Messages where we simply want to extract the payload content.
 *
 * @author Ikasan Development Team
 */
public class JmsMessageConverter
{
    /**
     * For the given Message try to extract the actual payload content and return as that content type.
     * This supports the following JMS Message conversions,
     *
     * TextMessage -> String
     * MapMessage -> Map
     * ObjectMessage -> Object
     *
     * All other types are simply returned as their native JMS message.
     *
     * @param message
     * @return
     * @throws JMSException
     */
    public static Object extractContent(Message message) throws JMSException
    {
        if(message instanceof TextMessage)
        {
            return ((TextMessage)message).getText();
        }
        else if(message instanceof MapMessage)
        {
            Map<String,Object> content = new HashMap<String,Object>();
            Enumeration<String> mapNames = ((MapMessage)message).getMapNames();

            while(mapNames.hasMoreElements())
            {
                String mapName = mapNames.nextElement();
                content.put(mapName, ((MapMessage)message).getObject(mapName));
            }

            return content;
        }
        else if(message instanceof ObjectMessage)
        {
            return ((ObjectMessage)message).getObject();
        }
        else if(message instanceof BytesMessage)
        {
            BytesMessage bytesMessage = (BytesMessage) message;
            long bytesLength = bytesMessage.getBodyLength();
            if(bytesLength > 0)
            {
                byte[] bytes = new byte[(int)bytesLength];
                bytesMessage.readBytes(bytes);
                return bytes;
            }

            return null;
        }
        else if(message instanceof StreamMessage)
        {
            // don't try and interpret the stream just return it as is
            return message;
        }
        else
        {
            return message;
        }
    }
}
