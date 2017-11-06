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

import javax.jms.JMSException;
import javax.jms.Message;
import java.util.Enumeration;

/**
 * Abstract class for the common converter bits.
 * @author Ikasan Development Team
 */
public abstract class AbstractJmsMessageConverter<S extends Message, T extends Message>
{
	/**
	 * Populate the jms meta data from the source message to the target message.
	 * @param source
	 * @throws JMSException
	 */
	public T populateMetaData(S source) throws JMSException
	{
		T target = getTargetJmsMessage();

		// copy standard JMS fields
		target.setJMSCorrelationID(source.getJMSCorrelationID());
		target.setJMSCorrelationIDAsBytes(source.getJMSCorrelationIDAsBytes());
		target.setJMSDeliveryMode(source.getJMSDeliveryMode());
		target.setJMSExpiration(source.getJMSExpiration());
		target.setJMSMessageID(source.getJMSMessageID());
		target.setJMSPriority(source.getJMSPriority());
		target.setJMSRedelivered(source.getJMSRedelivered());
		target.setJMSTimestamp(source.getJMSTimestamp());
		target.setJMSType(source.getJMSType());

		// copy any other properties
		Enumeration<String> names  = source.getPropertyNames();
		while(names.hasMoreElements())
		{
			String name = names.nextElement();
			target.setObjectProperty(name, source.getObjectProperty(name));
		}

		return target;
    }

	public abstract T getTargetJmsMessage();
}
