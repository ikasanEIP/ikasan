/*
 * $Id$
 * $URL$
 * 
 * ====================================================================
 * Ikasan Enterprise Integration Platform
 * Copyright (c) 2007-2008 Ikasan Ltd and individual contributors as indicated
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
package org.ikasan.framework.payload.service;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.NamingException;
import javax.resource.ResourceException;

import org.apache.log4j.Logger;
import org.ikasan.common.Payload;
import org.ikasan.common.component.PayloadOperationException;
import org.ikasan.common.factory.JMSMessageFactory;
import org.ikasan.common.security.IkasanSecurityConf;
import org.ikasan.framework.messaging.jms.JndiDestinationFactory;
import org.ikasan.framework.plugins.JMSEventPublisherPlugin;
import org.ikasan.framework.plugins.invoker.PluginInvocationException;

/**
 * Publishes a <code>Payload</code> to a JMS {@link Destination} either as a
 * {@link MapMessage} or {@link TextMessage}.
 * 
 * @author Ikasan Development Team
 * 
 */
public class JMSPayloadPublisher implements PayloadPublisher
{
    /** Logger instance */
    private static Logger logger = Logger.getLogger(JMSEventPublisherPlugin.class);

    /** JMS destination topic or queue */
    private Destination destination;

    /** JMS Connection Factory */
    private ConnectionFactory connectionFactory;

    /** Security Configuration */
    private IkasanSecurityConf ikasanSecurityConf;

    /** Converter to a <code>javax.jms.Message</code> */
    private JMSMessageFactory jmsMessageFactory;
    
    /** JMS destination factory to use if destination not directly supplied */
    private JndiDestinationFactory jndiDestinationFactory;

    /** JMS Message Time to live */
    private Long timeToLive;

    /** JMS Message Priority */
    private Integer priority;

    /** Flag for publishing {@link TextMessage} */
    private boolean textMessage = false;

    /**
     * Set the time to live
     * 
     * @param timeToLive the timeToLive to set
     */
    public void setTimeToLive(Long timeToLive)
    {
        this.timeToLive = timeToLive;
    }

    /**
     * Set the message priority
     * 
     * @param priority the message priority to set
     */
    public void setPriority(Integer priority)
    {
        this.priority = priority;
    }

    /**
     * Set whether to publish a {@link TextMessage} or a {@link MapMessage}.
     * 
     * @param textMessage the boolean flag to set.
     */
    public void setTextMessage(boolean textMessage)
    {
        this.textMessage = textMessage;
    }

    /**
     * Constructor
     * 
     * @param destination The destination for the message
     * @param connectionFactory The connection factory
     * @param jmsMessageFactory The JMS message serializer
     * @param ikasanSecurityConf THe security configuration
     */
    public JMSPayloadPublisher(Destination destination, ConnectionFactory connectionFactory, JMSMessageFactory jmsMessageFactory,
            IkasanSecurityConf ikasanSecurityConf)
    {
        super();
        this.destination = destination;
        this.connectionFactory = connectionFactory;
        this.jmsMessageFactory = jmsMessageFactory;
        this.ikasanSecurityConf = ikasanSecurityConf;
    }

    /**
     * Constructor
     * 
     * @param jndiDestinationFactory used for looking up the destination on demand
     * @param connectionFactory The connection factory
     * @param jmsMessageFactory The JMS message serializer
     * @param ikasanSecurityConf THe security configuration
     */
    public JMSPayloadPublisher(JndiDestinationFactory jndiDestinationFactory, ConnectionFactory connectionFactory, JMSMessageFactory jmsMessageFactory,
            IkasanSecurityConf ikasanSecurityConf)
    {
        super();
        this.jndiDestinationFactory = jndiDestinationFactory;
        this.connectionFactory = connectionFactory;
        this.jmsMessageFactory = jmsMessageFactory;
        this.ikasanSecurityConf = ikasanSecurityConf;
    }
    
    public void publish(Payload payload) throws ResourceException
    {
        Destination thisDestination;
        try
        {
            thisDestination = destination!=null?destination:jndiDestinationFactory.getDestination(true);
        }
        catch (NamingException e1)
        {
            throw new ResourceException("NamingException caught whilst attempting to find destination with jndiName["+jndiDestinationFactory.getJndiName()+"], environment["+jndiDestinationFactory.getEnvironment()+"]", e1);
        }
        
        Connection connection = null;
        try
        {
            connection = createConnection();
            Session session = connection.createSession(true, javax.jms.Session.AUTO_ACKNOWLEDGE);
            Message message;
            if (this.textMessage)
            {
                message = this.jmsMessageFactory.payloadToTextMessage(payload, session);
            }
            else
            {
                message = this.jmsMessageFactory.payloadToMapMessage(payload, session);
            }
            MessageProducer messageProducer = session.createProducer(thisDestination);
            if (timeToLive != null)
            {
                messageProducer.setTimeToLive(timeToLive.longValue());
            }
            if (this.priority != null)
            {
                messageProducer.setPriority(this.priority);
            }
            messageProducer.send(message);
            logger.info("Successfully sent message to destination [" + destination + "]. " + payload.getId());
        }
        catch (JMSException e)
        {
            throw new ResourceException("JMS Exception caught whilst publishing", e);
        }
        catch (PayloadOperationException e)
        {
            throw new ResourceException("EventSerialisationException caught whilst creating Message", e);
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
                    throw new ResourceException("JMS Exception caught when closing connection", e);
                }
            }
        }
    }

    /**
     * Creates a connection, security enabled if configured
     * 
     * @return Connection
     * @throws JMSException Exception if we could not connect
     */
    private Connection createConnection() throws JMSException
    {
        Connection connection;
        if (ikasanSecurityConf != null)
        {
            connection = connectionFactory.createConnection(ikasanSecurityConf.getJMSUsername(), ikasanSecurityConf.getJMSPassword());
        }
        else
        {
            connection = connectionFactory.createConnection();
        }
        return connection;
    }
}
