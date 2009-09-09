/*
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
package org.ikasan.framework.plugins;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.ikasan.common.security.IkasanSecurityConf;
import org.ikasan.framework.component.Event;
import org.ikasan.framework.event.serialisation.EventSerialisationException;
import org.ikasan.framework.event.serialisation.JmsMessageEventSerialiser;
import org.ikasan.framework.messaging.jms.JndiDestinationFactory;
import org.ikasan.framework.plugins.invoker.PluginInvocationException;

/**
 * Plugin that knows how to publish an <code>Event</code> to JMS
 * 
 * @author Ikasan Development Team
 */
public class JMSEventPublisherPlugin implements EventInvocable
{
    /** Logger instance */
    private static Logger logger = Logger.getLogger(JMSEventPublisherPlugin.class);

    /** JMS destination topic or queue */
    private Destination destination;
    
    /** JMS destination factory to use if destination not directly supplied */
    private JndiDestinationFactory jndiDestinationFactory;

    /** JMS Connection Factory */
    private ConnectionFactory connectionFactory;

    /** Security Configuration */
    private IkasanSecurityConf ikasanSecurityConf;

    /** Converter to a MapMessage */
    private JmsMessageEventSerialiser jmsMessageEventSerialiser;

    /** JMS Message Time to live */
    private Long timeToLive;

    /** JMS Message Priority */
    private Integer priority;

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
     * Constructor
     * 
     * @param destination The destination for the message
     * @param connectionFactory The connection factory
     * @param jmsMessageEventSerialiser The JMS message serialiser
     * @param ikasanSecurityConf THe security configuration
     */
    public JMSEventPublisherPlugin(Destination destination, ConnectionFactory connectionFactory,
            JmsMessageEventSerialiser jmsMessageEventSerialiser, IkasanSecurityConf ikasanSecurityConf)
    {
        super();
        this.destination = destination;
        this.connectionFactory = connectionFactory;
        this.jmsMessageEventSerialiser = jmsMessageEventSerialiser;
        this.ikasanSecurityConf = ikasanSecurityConf;
    }

    /**
     * Constructor
     * 
     * @param jndiDestinationFactory used for looking up the destination on demand
     * @param connectionFactory The connection factory
     * @param jmsMessageEventSerialiser The JMS message serialiser
     * @param ikasanSecurityConf THe security configuration
     */
    public JMSEventPublisherPlugin(JndiDestinationFactory jndiDestinationFactory, ConnectionFactory connectionFactory,
            JmsMessageEventSerialiser jmsMessageEventSerialiser, IkasanSecurityConf ikasanSecurityConf)
    {
        super();
        this.jndiDestinationFactory = jndiDestinationFactory;
        this.connectionFactory = connectionFactory;
        this.jmsMessageEventSerialiser = jmsMessageEventSerialiser;
        this.ikasanSecurityConf = ikasanSecurityConf;
    }
    public void invoke(Event event) throws PluginInvocationException
    {
        Destination thisDestination;
        try
        {
            thisDestination = destination!=null?destination:jndiDestinationFactory.getDestination(true);
        }
        catch (NamingException e1)
        {
            throw new PluginInvocationException("NamingException caught whilst attempting to find destination with jndiName["+jndiDestinationFactory.getJndiName()+"], environment["+jndiDestinationFactory.getEnvironment()+"]", e1);
        }
        
        Connection connection = null;
        try
        {
            connection = createConnection();
            Session session = connection.createSession(true, javax.jms.Session.AUTO_ACKNOWLEDGE);
            MapMessage mapMessage = jmsMessageEventSerialiser.toMapMessage(event, session);
            MessageProducer messageProducer = session.createProducer(thisDestination);
            if (timeToLive != null)
            {
                messageProducer.setTimeToLive(timeToLive.longValue());
            }
            
            //use the configured priority if present, otherwise the Event priority
            //note MUST explicitly set priority on the messageProducer, as setting on the message gets ignored
            messageProducer.setPriority(priority!=null?priority:event.getPriority());
            messageProducer.send(mapMessage);
            logger.info("successfully sent message to destination [" + thisDestination + "]. " + event.idToString());
        }
        catch (JMSException e)
        {
            throw new PluginInvocationException("JMS Exception caught whilst publishing", e);
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
                    throw new PluginInvocationException("JMS Exception caught when closing connection", e);
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
            connection = connectionFactory.createConnection(ikasanSecurityConf.getJMSUsername(), ikasanSecurityConf
                .getJMSPassword());
        }
        else
        {
            connection = connectionFactory.createConnection();
        }
        return connection;
    }
}
