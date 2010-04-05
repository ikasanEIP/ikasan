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
package org.ikasan.tools.ttools;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.Topic;
import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.ikasan.common.CommonException;
import org.ikasan.common.Payload;
import org.ikasan.common.ResourceLoader;
import org.ikasan.common.factory.JMSMessageFactory;

/**
 * This is a standalone client for subscribing to JMS Topics
 * 
 * Ensure the classpath is as follows
 * CLASSPATH=$JBOSS_HOME/client/jboss-common-client.jar
 * CLASSPATH=$CLASSPATH:$JBOSS_HOME/client/commons-logging.jar
 * CLASSPATH=$CLASSPATH:$JBOSS_HOME/client/jboss-messaging-client.jar
 * 
 * TODO Nasty hack to get queues working should be refactored
 * 
 * @author Ikasan Development Team
 */
public class TSubscriber extends AbstractJMSHandler
{
    /** Logger for this class */
    private static Logger logger = Logger.getLogger(TSubscriber.class);
    /**
     * Factory for <code>Payload</code>s
     */
    private JMSMessageFactory jmsMessageFactory;

    /**
     * Main method, kicks off a subscription
     * 
     * Usage: args must have: -topicname <topic-name> if a durable subscription
     * is to be created, then args must have: -durable -clientid <client-id>
     * -subscriptionname <subscription-name>
     * 
     * @param args
     * @throws NamingException
     * @throws JMSException
     * @throws CommonException
     */
    public static void main(String[] args) throws NamingException, JMSException, CommonException
    {
        TSubscriber subscriber = new TSubscriber(args);
        subscriber.invoke();
    }

    /**
     * Default constructor
     * 
     * @param args
     * @throws NamingException
     */
    public TSubscriber(String[] args) throws NamingException
    {
        this.jmsMessageFactory = ResourceLoader.getInstance().getJMSMessageFactory();
        this.init(args);
    }

    /**
     * Default constructor
     * 
     * @param properties
     * 
     * @throws NamingException
     */
    public TSubscriber(Properties properties) throws NamingException
    {
        this.jmsMessageFactory = ResourceLoader.getInstance().getJMSMessageFactory();
        this.init(properties);
    }

    /**
     * Invocation of this class
     * 
     * @return List<Payload>
     * @throws NamingException
     * @throws JMSException
     * @throws CommonException
     */
    public List<Payload> invoke() throws NamingException, JMSException, CommonException
    {
        // Subscribe
        return this.subscribe();
    }

    /**
     * Subscribe to a message queue
     * 
     * @return List<Payload>
     * @throws NamingException
     * @throws JMSException
     * @throws CommonException
     */
    public List<Payload> subscribe() throws NamingException, JMSException, CommonException
    {
        MessageConsumer consumer = null;
        Session session = null;
        Connection con = null;
        List<Payload> payloads = new ArrayList<Payload>();
        int msgCount = 0;
        // define the mode we are working within
        String mode = "unknown";
        if (JMSToolsUtils.getWait() == JMSConstants.NO_WAIT)
        {
            mode = "no wait mode...";
        }
        else
        {
            if (JMSToolsUtils.getWait() == JMSConstants.WAIT_FOREVER)
            {
                mode = "indefinite wait mode ...";
            }
            else
            {
                mode = "wait mode [" + JMSToolsUtils.getWait() + "] millis...";
            }
        }
        try
        {
            con = getConnection(JMSToolsUtils.isAuthenticated());
            if (JMSToolsUtils.isQueue())
            {
                String queueName = JMSToolsUtils.getDestinationName();
                Queue sourceQueue = (Queue) this.ctx.lookup(JMSConstants.QUEUE_KEY_PREFIX + queueName);
                session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
                consumer = session.createConsumer(sourceQueue);
                con.start();
                Message inboundMessage = null;
                logger.info("Subscribed to queue [" + sourceQueue.getQueueName() + "] in " + mode); //$NON-NLS-1$//$NON-NLS-2$
                while ((inboundMessage = consumer.receive(JMSToolsUtils.getWait())) != null)
                {
                    msgCount++;
                    payloads = jmsMessageFactory.fromMessage(inboundMessage);
                    for (Payload payload : payloads)
                    {
                        logger.info("Subscribed to message containing payload " + msgCount + " " + payload.toString());
                    }
                }
                logger.info(" No more messages on queue"); //$NON-NLS-1$
            }
            else
            {
                String topicName = JMSToolsUtils.getDestinationName();
                Topic sourceTopic = (Topic) ctx.lookup(JMSConstants.TOPIC_KEY_PREFIX + topicName);
                if (JMSToolsUtils.isDurable())
                {
                    // Create a durable subscriber.
                    // JMX console ID will be set to 'clientID.subsciptionName'
                    // TODO - setClientID is throwing an illegalStateException -
                    // not sure why
                    con.setClientID(JMSToolsUtils.getClientID());
                    session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
                    String subscriptionName = JMSToolsUtils.getSubscriptionName();
                    consumer = session.createDurableSubscriber(sourceTopic, subscriptionName);
                }
                else
                {
                    session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
                    consumer = session.createConsumer(sourceTopic);
                }
                con.start();
                Message inboundMessage = null;
                logger.info("Subscribed to topic [" + sourceTopic.getTopicName() + "] in " + mode); //$NON-NLS-1$//$NON-NLS-2$
                while ((inboundMessage = consumer.receive(JMSToolsUtils.getWait())) != null)
                {
                    // logger.info("recieving messages");
                    msgCount++;
                    payloads = jmsMessageFactory.fromMessage(inboundMessage);
                    for (Payload payload : payloads)
                    {
                        // logger.info("Subscribed to message containing payload
                        // "
                        // + msgCount + " "
                        // + payload.toString());
                        logger.info("Payload number is [" + payload.getTargetSystems() + "]");
                    }
                }
                logger.info(" No more messages on topic"); //$NON-NLS-1$
            }
        }
        finally
        {
            logger.info("Cleaning up connection and session."); //$NON-NLS-1$
            if (con != null)
            {
                con.stop();
                logger.info(" Stopped Connection"); //$NON-NLS-1$
                if (session != null)
                {
                    session.close();
                    logger.info(" Closed Session"); //$NON-NLS-1$
                }
                else
                {
                    logger.info(" Session was null, therefore already closed."); //$NON-NLS-1$
                }
                con.close();
                logger.info(" Closed Connection"); //$NON-NLS-1$
            }
            else
            {
                logger.info(" Connection was null, therefore already closed."); //$NON-NLS-1$
            }
        }
        return payloads;
    }
}
