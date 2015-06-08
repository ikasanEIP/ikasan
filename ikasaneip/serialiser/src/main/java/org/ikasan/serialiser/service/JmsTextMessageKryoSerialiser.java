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
package org.ikasan.serialiser.service;

import java.util.Enumeration;

import javax.jms.JMSException;
import javax.jms.TextMessage;

import org.ikasan.serialiser.model.JmsTextMessageDefaultImpl;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class JmsTextMessageKryoSerialiser extends Serializer<TextMessage>
{
    public void write(Kryo kryo, Output output, TextMessage message)
    {
        try
        {
        	JmsTextMessageDefaultImpl textMessage = this.convert(message);
            kryo.writeClassAndObject(output, textMessage);
        }
        catch (JMSException e)
        {
            throw new RuntimeException(e);
        }
    }

    public TextMessage read(Kryo kryo, Input input, Class<TextMessage> message)
    {
    	return (TextMessage)kryo.readClassAndObject(input);
    }
    
    private JmsTextMessageDefaultImpl convert(TextMessage message) throws JMSException
    {
    	JmsTextMessageDefaultImpl jmsTextMessageDefault = new JmsTextMessageDefaultImpl();
    	
    	jmsTextMessageDefault.setJMSCorrelationID(message.getJMSCorrelationID());
    	jmsTextMessageDefault.setJMSCorrelationIDAsBytes(message.getJMSCorrelationIDAsBytes());
    	jmsTextMessageDefault.setJMSDeliveryMode(message.getJMSDeliveryMode());
    	jmsTextMessageDefault.setJMSDestination(message.getJMSDestination());
    	jmsTextMessageDefault.setJMSExpiration(jmsTextMessageDefault.getJMSExpiration());
    	jmsTextMessageDefault.setJMSMessageID(message.getJMSMessageID());
    	jmsTextMessageDefault.setJMSPriority(message.getJMSPriority());
    	jmsTextMessageDefault.setJMSRedelivered(message.getJMSRedelivered());
    	jmsTextMessageDefault.setJMSReplyTo(message.getJMSReplyTo());
    	jmsTextMessageDefault.setJMSTimestamp(message.getJMSTimestamp());
    	jmsTextMessageDefault.setJMSType(jmsTextMessageDefault.getJMSType());
    	    	
    	Enumeration<String> names  = message.getPropertyNames();
    	
    	while(names.hasMoreElements())
    	{
    		String name = names.nextElement();

    		jmsTextMessageDefault.setObjectProperty(name, message.getObjectProperty(name));
    	}
    	
    	jmsTextMessageDefault.setText(message.getText());
    	
    	return jmsTextMessageDefault;
    }
}
