/*
 * $Id$
 * $URL$
 * 
 * ====================================================================
 * Ikasan Enterprise Integration Platform
 * Copyright (c) 2003-2008 Mizuho International plc. and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the 
 * Free Software Foundation Europe e.V. Talstrasse 110, 40217 Dusseldorf, Germany 
 * or see the FSF site: http://www.fsfeurope.org/.
 * ====================================================================
 */
package org.ikasan.common.tools.jms;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.NamingException;

import java.util.List;
import java.util.Properties;
import java.io.*;

import org.ikasan.common.CommonException;
import org.ikasan.common.CommonRuntimeException;
import org.ikasan.common.Envelope;
import org.ikasan.common.Payload;
import org.ikasan.common.ServiceLocator;
import org.ikasan.common.component.EnvelopeOperationException;
import org.ikasan.common.component.PayloadOperationException;
import org.ikasan.common.ResourceLoader;
import org.ikasan.common.factory.JMSMessageFactory;

import org.apache.log4j.Logger;

/**
 * This is a standalone client for publishing to JMS Topics
 * 
 * Ensure the classpath for jboss-eap-4.3 is as follows
 * CLASSPATH=$JBOSS_HOME/client/javassist.jar
 * CLASSPATH=$JBOSS_HOME/client/jbossall-client.jar
 * CLASSPATH=$JBOSS_HOME/client/jboss-aop-jdk50.jar
 * CLASSPATH=$JBOSS_HOME/client/jboss-messaging-client.jar
 * CLASSPATH=$JBOSS_HOME/client/trove.jar
 * CLASSPATH=$CLASSPATH:$JBOSS_HOME/server/commons-cli-1.0.jar
 * CLASSPATH=$CLASSPATH:$JBOSS_HOME/server/commons-codec-1.3.jar
 * CLASSPATH=$CLASSPATH:$JBOSS_HOME/server/commons-lang-2.2.jar
 * CLASSPATH=$CLASSPATH:$JBOSS_HOME/server/commons-logging.jar
 * CLASSPATH=$CLASSPATH:$JBOSS_HOME/server/log4j-1.2.14.jar
 * NOTE: log4j.jar is version dependent use above or later
 * 
 * @author Jeff Mitchell (jeff.mitchell@ikasan.org)
 */
public class TPublisher
    extends AbstractJMSHandler
{
    /** Logger for this class */
    private static Logger logger = Logger.getLogger(TPublisher.class);
    
    /**
     * Main entry point for invocation of TPublisher.
     * This calls 'invoke' which creates a payload based on this
     * instance, subsequently creates an envelope and publishes the envelope.
     * 
     * @param args
     * @throws NamingException 
     * @throws JMSException 
     * @throws CommonException 
     */
    public static void main(String[] args) 
        throws NamingException, JMSException, CommonException
    {
        TPublisher publisher = new TPublisher(args);
        publisher.invoke();
    }

    /**
     * Default invocation of TPublisher which creates a payload based on this
     * instance, subsequently creates an envelope and publishes the envelope.
     * 
     * @throws NamingException 
     * @throws JMSException 
     * @throws CommonException 
     */
    public void invoke() 
        throws NamingException, JMSException, CommonException
    {
        Payload payload = this.createPayload();
        Envelope envelope = this.createEnvelope(payload); 
        this.publish(envelope);
    }

    /**
     * Default constructor
     * 
     * @throws NamingException 
     */
    public TPublisher()
        throws NamingException
    {
        this((String[]) null);
    }

    /**
     * Default constructor for command line args
     * 
     * @param args
     * @throws NamingException 
     */
    public TPublisher(String[] args)
        throws NamingException
    {
        this.init(args);
    }

    /**
     * Default constructor for properties args
     * @param properties 
     * 
     * @throws NamingException 
     */
    public TPublisher(Properties properties)
        throws NamingException
    {
        this.init(properties);
    }

    /**
     * Create a message for publication
     * 
     * @return Payload
     */
    private Payload createPayload()
    {
        String name = "TPublisherTest";
        String spec = "xml/text";
        String srcSystem = "testSrcSystem";
        byte[] content = new String("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
            + "<test>This is a test from the TPublisher class.</test>").getBytes();
        
        // Do we have a payload name?
        if(JMSToolsUtils.getPayloadName() != null)
            name = JMSToolsUtils.getPayloadName();

        // Do we have a payload spec?
        if(JMSToolsUtils.getPayloadSpec() != null)
            spec = JMSToolsUtils.getPayloadSpec();

        // Do we have a srcSystem?
        if(JMSToolsUtils.getPayloadSrcSystem() != null)
            srcSystem = JMSToolsUtils.getPayloadSrcSystem();

        // Can we get the payload from the events file?
        try
        {
            if(JMSToolsUtils.getEventsFile() != null)
                content = readFile(JMSToolsUtils.getEventsFile());
            else if(JMSToolsUtils.getPayloadContent() != null)
                content = JMSToolsUtils.getPayloadContent().getBytes();

            // else leave as default test msg

        }
        catch(IOException e)
        {
            throw new CommonRuntimeException(e);
        }

        // create and populate the payload instance
        // TODO Global service locator
        ServiceLocator serviceLocator = ResourceLoader.getInstance();
        Payload pl = serviceLocator.getPayloadFactory().newPayload(name,
            spec, srcSystem, content);

        if (JMSToolsUtils.getPayloadEncoding() != null)
            pl.setEncoding(JMSToolsUtils.getPayloadEncoding());

        logger.info("Created Payload"); //$NON-NLS-1$
        logger.info("Body [" + new String(content) + "]"); //$NON-NLS-1$
        return pl;
    }

    /**
     * Create a default envelope for the incoming payload
     * 
     * @param payload
     * @return Default envelope
     */
    private Envelope createEnvelope(final Payload payload)
    {
        // TODO Global service locator
        ServiceLocator serviceLocator = ResourceLoader.getInstance();
        return serviceLocator.getEnvelopeFactory().newEnvelope(payload);
    }
    
    /**
     * Publish object
     * @param envelope 
     * 
     * @throws NamingException 
     * @throws JMSException 
     * @throws CommonException 
     */
    public void publish(Envelope envelope) 
        throws NamingException, JMSException, CommonException
    {
        this.publishMsg(envelope);
    }
    
    /**
     * Publish object
     * @param payload 
     * 
     * @throws NamingException 
     * @throws JMSException 
     * @throws CommonException 
     */
    public void publish(Payload payload) 
        throws NamingException, JMSException, CommonException
    {
        this.publishMsg(payload);
    }
    
    /**
     * Publish object
     * @param payloads 
     * 
     * @throws NamingException 
     * @throws JMSException 
     * @throws CommonException 
     */
    public void publish(List<Payload> payloads) 
        throws NamingException, JMSException, CommonException
    {
        this.publishMsg(payloads);
    }
    
    /**
     * Publish object
     * 
     * @param object
     * @throws NamingException 
     * @throws JMSException 
     * @throws CommonException 
     */
    private void publishMsg(Object object) 
        throws NamingException, JMSException, CommonException
    {
        MessageProducer producer = null;
        Session session = null;
        Connection con = null;

        con = getConnection(JMSToolsUtils.isAuthenticated());
        
        if (JMSToolsUtils.isQueue())
        {
            publishToQueue(producer, session, con, this.ctx, object);
        }
        else
        {
            publishToTopic(producer, session, con, this.ctx, object);
        }

        logger.info("Publish Complete"); //$NON-NLS-1$

    }

    /**
     * Read in a file and return it as a byte array
     * 
     * @param filename
     * @return file contents as byte array
     * @throws IOException 
     */
    private static byte[] readFile(String filename)
        throws IOException
    {
        return ResourceLoader.getInstance().getAsByteArray(filename);
    }

    /**
     * Publish msg to queue
     * @param producer
     * @param session
     * @param con
     * @param context
     * @param object
     * @throws JMSException
     * @throws NamingException
     * @throws CommonException
     */
    private void publishToQueue(MessageProducer producer, Session session, Connection con, Context context,
            Object object)
        throws NamingException, JMSException, CommonException
    {
        Connection connection = con;
        Session queueSession = session;
        MessageProducer messageProducer = producer;

        try
        {
            String queueName = JMSToolsUtils.getTopicName();
            Queue targetQueue = (Queue) context.lookup(JMSConstants.QUEUE_KEY_PREFIX + queueName);
            queueSession = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            Message message = createMessage(object, queueSession);
            messageProducer = queueSession.createProducer(targetQueue);
            logger.info(" Producer Created for queue [" + queueName + "]"); //$NON-NLS-1$//$NON-NLS-2$

            // publish the payload as many times as requested
            for (int i = 1; i <= JMSToolsUtils.getPayloadPublishNumber(); i++)
            {
                messageProducer.send(targetQueue, message, DeliveryMode.PERSISTENT, Message.DEFAULT_PRIORITY,
                    Message.DEFAULT_TIME_TO_LIVE);

                logger.info("Publishing [" + i + "/" //$NON-NLS-1$//$NON-NLS-2$
                        + JMSToolsUtils.getPayloadPublishNumber() + "]."); //$NON-NLS-1$
            }
        }
        finally
        {
            logger.info("Cleaning up connection and session."); //$NON-NLS-1$
            if (connection != null)
            {
                connection.stop();
                logger.info(" Stopped Connection"); //$NON-NLS-1$

                if (queueSession != null)
                {
                    queueSession.close();
                    logger.info(" Closed Session"); //$NON-NLS-1$
                }
                else
                {
                    logger.info(" Session was null, therefore already closed."); //$NON-NLS-1$
                }

                connection.close();
                logger.info(" Closed Connection"); //$NON-NLS-1$
            }
            else
            {
                logger.info(" Connection was null, therefore already closed."); //$NON-NLS-1$
            }
        }
    }

    /**
     * Publish msg to topic
     * 
     * @param producer
     * @param session
     * @param con
     * @param context
     * @param object
     * 
     * @throws JMSException 
     * @throws NamingException 
     * @throws CommonException 
     */
    private void publishToTopic(MessageProducer producer, Session session, Connection con, Context context,
            Object object)
        throws JMSException, NamingException, CommonException
    {
        Connection connection = con;
        Session topicSession = session;
        MessageProducer messageProducer = producer;

        try
        {
            connection = getConnection(JMSToolsUtils.isAuthenticated());

            String topicName = JMSToolsUtils.getTopicName();
            Topic targetTopic = (Topic) context.lookup(JMSConstants.TOPIC_KEY_PREFIX + topicName);

            topicSession = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Message message = createMessage(object, topicSession);

            messageProducer = topicSession.createProducer(targetTopic);
            logger.info(" Producer Created for topic [" + topicName + "]"); //$NON-NLS-1$//$NON-NLS-2$

            // publish the payload as many times as requested
            for (int i = 1; i <= JMSToolsUtils.getPayloadPublishNumber(); i++)
            {
                messageProducer.send(targetTopic, message, DeliveryMode.PERSISTENT, Message.DEFAULT_PRIORITY,
                    Message.DEFAULT_TIME_TO_LIVE);

                logger.info("Publishing [" + i + "/" //$NON-NLS-1$//$NON-NLS-2$
                        + JMSToolsUtils.getPayloadPublishNumber() + "]."); //$NON-NLS-1$
            }
        }
        finally
        {
            logger.info("Cleaning up connection and session."); //$NON-NLS-1$
            if (connection != null)
            {
                connection.stop();
                logger.info(" Stopped Connection"); //$NON-NLS-1$

                if (topicSession != null)
                {
                    topicSession.close();
                    logger.info(" Closed Session"); //$NON-NLS-1$
                }
                else
                {
                    logger.info(" Session was null, therefore already closed."); //$NON-NLS-1$
                }

                connection.close();
                logger.info(" Closed Connection"); //$NON-NLS-1$
            }
            else
            {
                logger.info(" Connection was null, therefore already closed."); //$NON-NLS-1$
            }
        }
    }
    
    /**
     * Creates the message
     * 
     * @param object
     * @param session
     * @return Message
     * @throws PayloadOperationException
     * @throws EnvelopeOperationException
     */
    private Message createMessage(Object object, Session session) 
        throws PayloadOperationException, EnvelopeOperationException
    {
        ServiceLocator serviceLocator = ResourceLoader.getInstance();
        JMSMessageFactory jmsMessageFactory = serviceLocator.getJMSMessageFactory();
        Message message = null;
        if(JMSToolsUtils.getJmsMsgType().equals(JMSConstants.JMS_TEXT_MESSAGE_TYPE))
        {
            if (object instanceof Payload) // single payload
            {
                Payload payload = (Payload) object;
                message = jmsMessageFactory.payloadToTextMessage(payload, session);
            }
            else if (object instanceof List) // multiple payloads
            {
                logger.warn("Cannot publish multiple payloads in a JMS TEXT msg. Only first payload will be publised!");
                List<Payload>payloads = (List<Payload>) object;
                message = jmsMessageFactory.payloadToTextMessage(payloads.get(0), session);
            }
            else if (object instanceof Envelope) // envelope
            {
                Envelope envelope = (Envelope) object;
                message = jmsMessageFactory.envelopeToTextMessage(envelope, session, null);
            }
        }
        else
        {
            // default to Map Message
            if (object instanceof Payload) // single payload
            {
                Payload payload = (Payload) object;
                message = jmsMessageFactory.payloadToMapMessage(payload, session);
            }
            else if (object instanceof List) // multiple payloads
            {
                List<Payload>payloads = (List<Payload>) object;
                message = jmsMessageFactory.payloadsToMapMessage(payloads, session);
            }
            else if (object instanceof Envelope) // envelope
            {
                Envelope envelope = (Envelope) object;
                message = jmsMessageFactory.envelopeToMapMessage(envelope, session);
            }
        }

        return message;
    }
    
}
