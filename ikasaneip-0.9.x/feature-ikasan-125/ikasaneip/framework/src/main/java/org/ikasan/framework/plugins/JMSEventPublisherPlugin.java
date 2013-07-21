/*
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
package org.ikasan.framework.plugins;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.ikasan.common.security.IkasanSecurityConf;
import org.ikasan.framework.component.Event;
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
    private JmsMessageEventSerialiser<? extends Message> jmsMessageEventSerialiser;

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
            JmsMessageEventSerialiser<?> jmsMessageEventSerialiser, IkasanSecurityConf ikasanSecurityConf)
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
            JmsMessageEventSerialiser<?> jmsMessageEventSerialiser, IkasanSecurityConf ikasanSecurityConf)
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
            Message message = jmsMessageEventSerialiser.toMessage(event, session);
            MessageProducer messageProducer = session.createProducer(thisDestination);
            if (timeToLive != null)
            {
                messageProducer.setTimeToLive(timeToLive.longValue());
            }
            
            //use the configured priority if present, otherwise the Event priority
            //note MUST explicitly set priority on the messageProducer, as setting on the message gets ignored
            messageProducer.setPriority(priority!=null?priority:event.getPriority());
            messageProducer.send(message);
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
