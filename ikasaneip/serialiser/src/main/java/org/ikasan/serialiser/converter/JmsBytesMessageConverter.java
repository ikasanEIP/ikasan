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
package org.ikasan.serialiser.converter;

import org.ikasan.serialiser.model.JmsBytesMessageDefaultImpl;
import org.ikasan.spec.serialiser.Converter;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.MessageEOFException;

/**
 * Simple converter of a vendor specific BytesMessage to an Ikasan bytes message for serialisation.
 * @author Ikasan Development Team
 */
public class JmsBytesMessageConverter extends AbstractJmsMessageConverter<BytesMessage,BytesMessage> implements Converter<BytesMessage, BytesMessage>
{
    
    public BytesMessage convert(BytesMessage message)
    {
    	try {
            BytesMessage bytesMessage = super.populateMetaData(message);

            try
            {
                boolean moreData = true;
                while(moreData)
                {
                    bytesMessage.writeByte( message.readByte() );
                }
            }
            catch (MessageEOFException e)
            {
                // we have simply hit the end of the msg bytes
                return bytesMessage;
            }
            finally
            {
                bytesMessage.reset();
            }

            return bytesMessage;
        }
        catch (JMSException e)
        {
            throw new RuntimeException(e);
        }

    }

    public BytesMessage getTargetJmsMessage()
    {
        return new JmsBytesMessageDefaultImpl();
    }

}
