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
package org.ikasan.sample.jmsDrivenPriceSrc.component.endpoint;

import java.util.Hashtable;

import javax.jms.JMSException;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.ikasan.spec.component.endpoint.EndpointException;
import org.ikasan.spec.component.endpoint.Producer;
import org.ikasan.spec.configuration.ConfiguredResource;

/**
 * Implementation of a producer which simply logs the incoming
 * StribngBuilder payload content.
 * 
 * @author Ikasan Development Team
 */
public class PriceProducer implements Producer<StringBuilder>, ConfiguredResource<JmsClientProducerConfiguration>
{
    /** Logger instance */
    private Logger logger = Logger.getLogger(PriceProducer.class);

    /** JMS producer configuration */
    private JmsClientProducerConfiguration configuration;
    
    /** configuration resource id */
    private String configuredResourceId;
    
    /**
     * Message invocation
     */
    public void invoke(StringBuilder payload) throws EndpointException 
    {
        logger.info("Producer invoked with [" + payload + "]");
        TopicConnection connection = null;
        
        try
        {
            Context ctx = createContext();
            Object connectionFactory = ctx.lookup(this.configuration.getConnectionFactory());
            TopicConnectionFactory tcf = (TopicConnectionFactory)connectionFactory;

            Object destinationName = ctx.lookup(this.configuration.getDestination());
            Topic destination = (Topic)destinationName;

//            connection = tcf.createTopicConnection();
            connection = tcf.createTopicConnection(this.configuration.getUsername(), this.configuration.getPassword());
            TopicSession session = connection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
            TopicPublisher publisher = session.createPublisher(destination);

            TextMessage message = session.createTextMessage();
            message.setText(new String(payload));
            publisher.publish(message);            
        }
        catch (NamingException e)
        {
            throw new EndpointException(e);
        }
        catch (JMSException e)
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

    private Context createContext() throws NamingException
    {
        Hashtable hashTable = new Hashtable();
        hashTable.put(this.configuration.INITIAL_CONTEXT_FACTORY, this.configuration.getInitialContextFactory());
        hashTable.put(this.configuration.PROVIDER_URL, this.configuration.getProviderUrl());
        hashTable.put(this.configuration.FACTORY_URL_PKGS, this.configuration.getFactoryUrl());
        
        return new InitialContext(hashTable);
    }

    public JmsClientProducerConfiguration getConfiguration()
    {
        return this.configuration;
    }

    public String getConfiguredResourceId()
    {
        return this.configuredResourceId;
    }

    public void setConfiguration(JmsClientProducerConfiguration configuration)
    {
        this.configuration = configuration;
    }

    public void setConfiguredResourceId(String configuredResourceId)
    {
        this.configuredResourceId = configuredResourceId;
    }
}
