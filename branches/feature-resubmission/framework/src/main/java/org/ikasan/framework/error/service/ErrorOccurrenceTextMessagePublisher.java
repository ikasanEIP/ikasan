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
package org.ikasan.framework.error.service;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.xml.transform.TransformerException;

import org.apache.log4j.Logger;
import org.ikasan.common.xml.serializer.XMLSerializer;
import org.ikasan.framework.error.model.ErrorOccurrence;
import org.ikasan.framework.error.serialisation.ErrorOccurrenceXmlConverter;

public class ErrorOccurrenceTextMessagePublisher implements
		ErrorOccurrenceListener {

    /** Logger instance */
    private static Logger logger = Logger.getLogger(ErrorOccurrenceTextMessagePublisher.class);

    /** JMS destination topic or queue */
    private Destination errorOccurrenceChannel;

    /**
     * Constructor
     * 
	 * @param connectionFactory
	 * @param errorOccurrenceChannel
	 * @param errorOccurrenceXmlConverter
	 */
	public ErrorOccurrenceTextMessagePublisher(
			ConnectionFactory connectionFactory,
			Destination errorOccurrenceChannel,
			ErrorOccurrenceXmlConverter errorOccurrenceXmlConverter) {
		this.connectionFactory = connectionFactory;
		this.errorOccurrenceChannel = errorOccurrenceChannel;
		this.converter = errorOccurrenceXmlConverter;
	}

	/** JMS Connection Factory */
    private ConnectionFactory connectionFactory;
    
    /** JMS Message Time to live */
    private Long timeToLive;

	/** toXml serialiser for ErrorOccurrences **/
    private ErrorOccurrenceXmlConverter converter;
    
	/* (non-Javadoc)
	 * @see org.ikasan.framework.error.service.ErrorOccurrenceListener#notifyErrorOccurrence(org.ikasan.framework.error.model.ErrorOccurrence)
	 */
	public void notifyErrorOccurrence(ErrorOccurrence errorOccurrence) {
		logger.info("invoked");
		Connection connection = null;
        try
        {
            connection = connectionFactory.createConnection();
            Session session = connection.createSession(true, javax.jms.Session.AUTO_ACKNOWLEDGE);
            TextMessage message;
			try {
				message = session.createTextMessage(converter.toXml(errorOccurrence));
	            MessageProducer messageProducer = session.createProducer(errorOccurrenceChannel);
	            if (timeToLive != null)
	            {
	                messageProducer.setTimeToLive(timeToLive.longValue());
	            }
	            messageProducer.send(message);
	            logger.info("Successfully published ErrorOccurrence["+errorOccurrence.getId()+"] to ErrorOccurrenceChannel");

			} catch (TransformerException e) {
				logger.error("Could not transform ErrorOccurrence to XML ["+errorOccurrence+"]");
			}
        }
        catch (JMSException e)
        {
            handleJmsExcption(e);
        }
        finally
        {
            if (connection != null)
            {
                try
                {
                    connection.close();
                }
                catch (JMSException e)
                {
                    handleJmsExcption(e);
                }
            }
        }

	}

	private void handleJmsExcption(JMSException e) {
		//not really much we can do with this
		logger.error(e);
	}

    /**
     * Mutator for optional timeToLive
     * 
     * @param timeToLive
     */
    public void setTimeToLive(Long timeToLive) {
		this.timeToLive = timeToLive;
	}
}
