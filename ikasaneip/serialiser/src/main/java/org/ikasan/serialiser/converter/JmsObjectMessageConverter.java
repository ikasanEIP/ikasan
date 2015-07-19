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

import org.ikasan.serialiser.model.JmsObjectMessageDefaultImpl;
import org.ikasan.spec.serialiser.Converter;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import java.util.Enumeration;

public class JmsObjectMessageConverter implements Converter<ObjectMessage, JmsObjectMessageDefaultImpl>
{   
    public JmsObjectMessageDefaultImpl convert(ObjectMessage message)
    {
		JmsObjectMessageDefaultImpl jmsObjectMessageDefault = new JmsObjectMessageDefaultImpl();
    	
    	try
    	{	    	
	    	jmsObjectMessageDefault.setJMSCorrelationID(message.getJMSCorrelationID());
	    	jmsObjectMessageDefault.setJMSCorrelationIDAsBytes(message.getJMSCorrelationIDAsBytes());
	    	jmsObjectMessageDefault.setJMSDeliveryMode(message.getJMSDeliveryMode());
	    	//jmsObjectMessageDefault.setJMSDestination(message.getJMSDestination());
	    	jmsObjectMessageDefault.setJMSExpiration(message.getJMSExpiration());
	    	jmsObjectMessageDefault.setJMSMessageID(message.getJMSMessageID());
	    	jmsObjectMessageDefault.setJMSPriority(message.getJMSPriority());
	    	jmsObjectMessageDefault.setJMSRedelivered(message.getJMSRedelivered());
	    	//jmsObjectMessageDefault.setJMSReplyTo(message.getJMSReplyTo());
	    	jmsObjectMessageDefault.setJMSTimestamp(message.getJMSTimestamp());
	    	jmsObjectMessageDefault.setJMSType(message.getJMSType());
	    	    	
	    	Enumeration<String> names  = message.getPropertyNames();
	    	
	    	while(names.hasMoreElements())
	    	{
	    		String name = names.nextElement();

                jmsObjectMessageDefault.setObjectProperty(name, message.getObjectProperty(name));
	    	}
	    	
	    	jmsObjectMessageDefault.setObject(message.getObject());
    	}
    	catch (JMSException e)
    	{
    		throw new RuntimeException(e);
    	}
    	
    	return jmsObjectMessageDefault;
    }
}
