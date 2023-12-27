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
package org.ikasan.nonfunctional.test.util;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.RedeliveryPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.jms.*;
import java.util.*;

/**
 * AMQ test utility methods.
 *
 * @author Ikasan Development Team
 */
public class AMQTestUtil
{
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    // broker URL
    String brokerUrl;

    /**
     * Constructor
     * @param brokerUrl
     */
    public AMQTestUtil(String brokerUrl)
    {
        this.brokerUrl = brokerUrl;
        if(brokerUrl == null)
        {
            throw new IllegalArgumentException("brokerUrl cannot be 'null'");
        }
    }

    public void publish(String message, String destination) throws JMSException
    {
        List<String> messages = new ArrayList<>();
        messages.add(message);
        publish(messages, destination);
    }

    public void publish(List<String> messages, String destination) throws JMSException
    {
        RedeliveryPolicy redeliveryPolicy = new RedeliveryPolicy();
        redeliveryPolicy.setMaximumRedeliveries(-1);
        publish(messages, destination, null, null, redeliveryPolicy);
    }

    public void publish(List<String> messages, String destination, RedeliveryPolicy redeliveryPolicy) throws JMSException
    {
        publish(messages, destination, null, null, redeliveryPolicy);
    }


    public void publish(List<String> messages, String destination, String username, String password, RedeliveryPolicy redeliveryPolicy)
            throws JMSException
    {
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(brokerUrl);
        connectionFactory.setRedeliveryPolicy(redeliveryPolicy);

        Connection connection = (username != null) ? connectionFactory.createConnection(username, password) : connectionFactory.createConnection();
        Session session = connection.createSession(false, 1);
        MessageProducer producer = session.createProducer( session.createTopic(destination) );

        for(String message:messages)
        {
            producer.send( session.createTextMessage(message) );
            logger.info("Published [" + message + "]");
        }
    }

    public MessageConsumer getJmsConsumer(String destination) throws JMSException
    {
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(brokerUrl);

        Connection connection = connectionFactory.createConnection();
        Session session = connection.createSession(false, 1);

        return session.createConsumer( session.createQueue(destination) );
    }
}
