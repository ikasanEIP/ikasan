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
 * CLASSPATH=$CLASSPATH:$JBOSS_HOME/server/log4j-1.2.14.jar NOTE: log4j.jar is
 * version dependent use above or later
 * 
 * @author Jeff Mitchell (jeff.mitchell@ikasan.org)
 */
public class TPublisher extends AbstractJMSHandler
{
    /** Logger for this class */
    private static Logger logger = Logger.getLogger(TPublisher.class);

    /**
     * Main entry point for invocation of TPublisher. This calls 'invoke' which
     * creates a payload based on this instance, subsequently creates an
     * envelope and publishes the envelope.
     * 
     * @param args - The arguments passed into TPublisher
     * @throws NamingException - A JNDI lookup related exception
     * @throws JMSException - A JMS Exception
     * @throws CommonException - An Ikasan Common exception
     */
    public static void main(String[] args) throws NamingException, JMSException, CommonException
    {
        TPublisher publisher = new TPublisher(args);
        publisher.invoke();
    }

    /**
     * Default invocation of TPublisher which creates a payload based on this
     * instance, subsequently creates an envelope and publishes the envelope.
     * 
     * @throws NamingException - A JNDI lookup related exception
     * @throws JMSException - A JMS Exception
     * @throws CommonException - An Ikasan Common exception
     */
    public void invoke() throws NamingException, JMSException, CommonException
    {
        Payload payload = this.createPayload();
        Envelope envelope = this.createEnvelope(payload);
        this.publish(envelope);
    }

    /**
     * Default constructor
     * 
     * @throws NamingException - A JNDI lookup related exception
     */
    public TPublisher() throws NamingException
    {
        this((String[]) null);
    }

    /**
     * Default constructor that takes command line args passed in from main
     * 
     * @param args - The arguments passed into TPublisher
     * @throws NamingException - A JNDI lookup related exception
     */
    public TPublisher(String[] args) throws NamingException
    {
        this.init(args);
    }

    /**
     * Default constructor for properties args
     * 
     * @param properties - properties
     * 
     * @throws NamingException - A JNDI lookup related exception
     */
    public TPublisher(Properties properties) throws NamingException
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
        byte[] content = new String("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "<test>This is a test from the TPublisher class.</test>").getBytes();
        // Do we have a payload name?
        if (JMSToolsUtils.getPayloadName() != null)
        {
            name = JMSToolsUtils.getPayloadName();
        }
        // Do we have a payload spec?
        if (JMSToolsUtils.getPayloadSpec() != null)
        {
            spec = JMSToolsUtils.getPayloadSpec();
        }
        // Do we have a srcSystem?
        if (JMSToolsUtils.getPayloadSrcSystem() != null)
        {
            srcSystem = JMSToolsUtils.getPayloadSrcSystem();
        }
        // Can we get the payload from the events file?
        try
        {
            if (JMSToolsUtils.getEventsFile() != null)
            {
                content = readFile(JMSToolsUtils.getEventsFile());
            }
            else if (JMSToolsUtils.getPayloadContent() != null)
            {
                content = JMSToolsUtils.getPayloadContent().getBytes();
            }
            // else leave as default test msg
        }
        catch (IOException e)
        {
            throw new CommonRuntimeException(e);
        }
        // create and populate the payload instance
        // TODO Global service locator
        ServiceLocator serviceLocator = ResourceLoader.getInstance();
        Payload pl = serviceLocator.getPayloadFactory().newPayload(name, spec, srcSystem, content);
        if (JMSToolsUtils.getPayloadEncoding() != null)
        {
            pl.setEncoding(JMSToolsUtils.getPayloadEncoding());
        }
        logger.info("Created Payload"); //$NON-NLS-1$
        logger.info("Body [" + new String(content) + "]"); //$NON-NLS-1$
        return pl;
    }

    /**
     * Create a default envelope for the incoming payload
     * 
     * @param payload Payload to wrap an envelope around
     * @return Default envelope
     */
    private Envelope createEnvelope(final Payload payload)
    {
        // TODO Global service locator
        ServiceLocator serviceLocator = ResourceLoader.getInstance();
        return serviceLocator.getEnvelopeFactory().newEnvelope(payload);
    }

    /**
     * Publish envelope
     * 
     * @param envelope Envelope to publish
     * 
     * @throws NamingException - A JNDI lookup related exception
     * @throws JMSException - A JMS Exception
     * @throws CommonException - An Ikasan Common exception
     */
    public void publish(Envelope envelope) throws NamingException, JMSException, CommonException
    {
        this.publishMsg(envelope);
    }

    /**
     * Publish payload
     * 
     * @param payload - Payload to publish
     * 
     * @throws NamingException - A JNDI lookup related exception
     * @throws JMSException - A JMS Exception
     * @throws CommonException - An Ikasan Common exception
     */
    public void publish(Payload payload) throws NamingException, JMSException, CommonException
    {
        this.publishMsg(payload);
    }

    /**
     * Publish object
     * 
     * @param payloads - List of payloads to publish
     * 
     * @throws NamingException - A JNDI lookup related exception
     * @throws JMSException - A JMS Exception
     * @throws CommonException - An Ikasan Common exception
     */
    public void publish(List<Payload> payloads) throws NamingException, JMSException, CommonException
    {
        this.publishMsg(payloads);
    }

    /**
     * Publish object
     * 
     * @param object - Object to publish
     * 
     * @throws NamingException - A JNDI lookup related exception
     * @throws JMSException - A JMS Exception
     * @throws CommonException - An Ikasan Common exception
     */
    private void publishMsg(Object object) throws NamingException, JMSException, CommonException
    {
        publishMessage(this.ctx, object, JMSToolsUtils.isQueue());
        logger.info("Publish Complete"); //$NON-NLS-1$
    }

    /**
     * Read in a file and return it as a byte array
     * 
     * @param filename - File to read
     * @return file contents as byte array
     * @throws IOException - Exception if we cannot read from file
     */
    private static byte[] readFile(String filename) throws IOException
    {
        return ResourceLoader.getInstance().getAsByteArray(filename);
    }

    /**
     * Publish msg to topic or queue
     * 
     * @param context - Context
     * @param object - Object to publish
     * @param isQueue - boolean flag for it it's a queue we're publishing to
     * 
     * @throws NamingException - A JNDI lookup related exception
     * @throws JMSException - A JMS Exception
     * @throws CommonException - An Ikasan Common exception
     */
    private void publishMessage(Context context, Object object, boolean isQueue) throws NamingException, JMSException, CommonException
    {
        Connection connection = null;
        Session session = null;
        MessageProducer messageProducer = null;
        Message message = null;
        try
        {
            connection = getConnection(JMSToolsUtils.isAuthenticated());
            String destinationName = JMSToolsUtils.getDestinationName();
            Destination target;
            if (isQueue)
            {
                target = (Queue) context.lookup(JMSConstants.QUEUE_KEY_PREFIX + destinationName);
            }
            else
            {
                target = (Topic) context.lookup(JMSConstants.TOPIC_KEY_PREFIX + destinationName);
            }
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            // Publish the payload as many times as requested
            for (int i = 1; i <= JMSToolsUtils.getPayloadPublishNumber(); i++)
            {
                message = createMessage(object, session);
                messageProducer = session.createProducer(target);
                logger.info("Publishing [" + i + "/" + JMSToolsUtils.getPayloadPublishNumber() + "].");
                messageProducer.send(target, message, DeliveryMode.PERSISTENT, Message.DEFAULT_PRIORITY, Message.DEFAULT_TIME_TO_LIVE);
                logMessageId(message);
                messageProducer.close();
                logger.info("Closed Producer");
            }
        }
        finally
        {
            logger.info("Cleaning up producer, session and connection."); //$NON-NLS-1$
            if (messageProducer != null)
            {
                messageProducer.close();
                logger.info(" Closed Producer"); //$NON-NLS-1$
                if (session != null)
                {
                    session.close();
                    logger.info(" Closed Session"); //$NON-NLS-1$
                    if (connection != null)
                    {
                        connection.stop();
                        logger.info(" Stopped Connection"); //$NON-NLS-1$
                        connection.close();
                        logger.info(" Closed Connection"); //$NON-NLS-1$
                    }
                    else
                    {
                        logger.info(" Connection was null, therefore already closed."); //$NON-NLS-1$
                    }
                }
                else
                {
                    logger.info(" Session was null, therefore already closed."); //$NON-NLS-1$
                }
            }
            else
            {
                logger.info(" Producer was null, therefore already closed."); //$NON-NLS-1$
            }
        }
    }

    /**
     * Creates the message
     * 
     * @param object - Object to convert into a message
     * @param session - Message session
     * @return Message - The new message
     * @throws PayloadOperationException - Payload related exception
     * @throws EnvelopeOperationException - Envelope related exception
     */
    private Message createMessage(Object object, Session session) throws PayloadOperationException, EnvelopeOperationException
    {
        ServiceLocator serviceLocator = ResourceLoader.getInstance();
        JMSMessageFactory jmsMessageFactory = serviceLocator.getJMSMessageFactory();
        Message message = null;
        if (JMSToolsUtils.getJmsMsgType().equals(JMSConstants.JMS_TEXT_MESSAGE_TYPE))
        {
            if (object instanceof Payload) // single payload
            {
                Payload payload = (Payload) object;
                message = jmsMessageFactory.payloadToTextMessage(payload, session);
            }
            else if (object instanceof List) // multiple payloads
            {
                logger.warn("Cannot publish multiple payloads in a JMS TEXT msg. Only first payload will be publised!");
                List<Payload> payloads = (List<Payload>) object;
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
                List<Payload> payloads = (List<Payload>) object;
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

    /**
     * Log the message id
     * 
     * @param message - Message to get Id from
     */
    private void logMessageId(Message message)
    {
        if (message != null)
        {
            try
            {
                if (message.getJMSMessageID() != "")
                {
                    logger.info(message.getJMSMessageID());
                }
            }
            catch (JMSException e)
            {
                logger.warn("Could not get message id");
            }
        }
    }
}
