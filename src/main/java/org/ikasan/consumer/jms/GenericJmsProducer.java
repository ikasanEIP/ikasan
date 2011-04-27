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
package org.ikasan.consumer.jms;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;

import org.ikasan.spec.component.endpoint.EndpointException;
import org.ikasan.spec.component.endpoint.Producer;
import org.ikasan.spec.configuration.ConfiguredResource;

/**
 * Implementation of a consumer based on the JMS specification.
 *
 * @author Ikasan Development Team
 */
public class GenericJmsProducer implements Producer<Message>, ConfiguredResource<GenericJmsProducerConfiguration>
{
    /** JMS Connection Factory */
    private ConnectionFactory connectionFactory;

    /** JMS Destination instance */
    private Destination destination;

    /** JMS Connection */
    private Connection connection;

    /** configured resource id */
    private String configuredResourceId;
    
    /** JMS consumer configuration */
    private GenericJmsProducerConfiguration configuration;
    
    /**
     * Constructor
     * @param connectionFactory
     * @param destination
     * @param flowEventFactory
     */
    public GenericJmsProducer(ConnectionFactory connectionFactory, Destination destination)
    {
        this.connectionFactory = connectionFactory;
        if(connectionFactory == null)
        {
            throw new IllegalArgumentException("connectionFactory cannot be 'null'");
        }
        
        this.destination = destination;
        if(destination == null)
        {
            throw new IllegalArgumentException("destination cannot be 'null'");
        }
    }
    
    public void invoke(Message message) throws EndpointException
    {
        try
        {
            if(this.configuration.getUsername() != null && this.configuration.getUsername().trim().length() > 0)
            {
                connection = connectionFactory.createConnection(this.configuration.getUsername(), this.configuration.getPassword());
            }
            else
            {
                connection = connectionFactory.createConnection();
            }

            Session session = connection.createSession(this.configuration.isTransacted(), this.configuration.getAcknowledgement());
            MessageProducer messageProducer = session.createProducer(destination);
            messageProducer.send(message);
        }
        catch(JMSException e)
        {
            throw new EndpointException(e);
        }
        finally
        {
            if(connection != null)
            {
                try
                {
                    connection.close();
                }
                catch (JMSException e)
                {
                    throw new EndpointException(e);
                }
            }
        }
    }
    
    public GenericJmsProducerConfiguration getConfiguration()
    {
        return this.configuration;
    }

    public String getConfiguredResourceId()
    {
        return this.configuredResourceId;
    }

    public void setConfiguration(GenericJmsProducerConfiguration configuration)
    {
        this.configuration = configuration;
    }

    public void setConfiguredResourceId(String configuredResourceId)
    {
        this.configuredResourceId = configuredResourceId;
    }

}
