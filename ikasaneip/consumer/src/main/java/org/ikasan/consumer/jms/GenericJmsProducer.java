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

import org.apache.log4j.Logger;
import org.ikasan.spec.component.endpoint.EndpointException;
import org.ikasan.spec.component.endpoint.Producer;
import org.ikasan.spec.component.transformation.Converter;
import org.ikasan.spec.component.transformation.TransformationException;
import org.ikasan.spec.component.transformation.Translator;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.ikasan.spec.event.ManagedEventIdentifierService;
import org.ikasan.spec.flow.FlowEvent;
import org.ikasan.spec.management.ManagedIdentifierService;
import org.ikasan.spec.management.ManagedResource;
import org.ikasan.spec.management.ManagedResourceRecoveryManager;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.io.Serializable;
import java.util.Hashtable;
import java.util.Map;

/**
 * Implementation of a consumer based on the JMS specification.
 *
 * @author Ikasan Development Team
 */
public class GenericJmsProducer<T> implements Producer<T>, ManagedIdentifierService<ManagedEventIdentifierService>,
    ManagedResource, ConfiguredResource<GenericJmsProducerConfiguration>
{
    /** class logger */
    private static Logger logger = Logger.getLogger(GenericJmsProducer.class);
    
    /** JMS Connection Factory */
    protected ConnectionFactory connectionFactory;

    /** JMS Destination instance */
    protected Destination destination;

    /** JMS Connection */
    protected Connection connection;

    /** configured resource id */
    protected String configuredResourceId;
    
    /** JMS consumer configuration - default to vanilla instance */
    protected GenericJmsProducerConfiguration configuration = new GenericJmsProducerConfiguration();
    
    /** default event identifier service - can be overridden via the setter */
    protected ManagedEventIdentifierService<String,Message> managedEventIdentifierService = new JmsEventIdentifierServiceImpl();

    /** session has to be closed prior to connection being closed */
    protected Session session;

    /** determines whether this managed resource failure will fail the startup of the flow */
    protected boolean isCriticalOnStartup = true;
    
    /** destination resolver for locating and returning the configured destination instance */
    protected DestinationResolver destinationResolver;
    
    /** custom message property provider */
    protected CustomMessagePropertyProvider customMessagePropertyProvider;

    /**
     * Incoming object to JMS message converters.
     */
    protected Converter<String,TextMessage> stringToMessageConverter = new StringToMessage();
    protected Converter<Map<String,Object>,Message> mapToMessageConverter = new MapToMessage();
    protected Converter<byte[],Message> byteArrayToMessageConverter = new ByteArrayToMessage();
    protected Converter<Serializable,Message> serializableToMessageConverter = new SerializableToMessage();

    /**
     * Constructor.
     */
    public GenericJmsProducer()
    {
    }

    /**
     * Constructor.
     * @param connectionFactory
     * @param destination
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

    /**
     * Constructor.
     * @param connectionFactory
     * @param destinationResolver
     */
    public GenericJmsProducer(ConnectionFactory connectionFactory, DestinationResolver destinationResolver)
    {
        this.connectionFactory = connectionFactory;
        if(connectionFactory == null)
        {
            throw new IllegalArgumentException("connectionFactory cannot be 'null'");
        }
        
        this.destinationResolver = destinationResolver;
        if(destinationResolver == null)
        {
            throw new IllegalArgumentException("destinationResolver cannot be 'null'");
        }
    }

    protected Message convertToMessage(Object object)
    {
        if(object instanceof Message)
        {
            return (Message)object;
        }

        if(object instanceof Map)
        {
            try
            {
                return this.mapToMessageConverter.convert((Map)object);
            }
            catch(ClassCastException e)
            {
                throw new EndpointException("Cannot publish message of type["
                        + object.getClass().getName()
                        + " to JMS. Unable to find a registered converter!");
            }
        }
        else if(object instanceof byte[])
        {
            return this.byteArrayToMessageConverter.convert((byte[])object);
        }
        else if(object instanceof Serializable)
        {
            return this.serializableToMessageConverter.convert((Serializable)object);
        }

        throw new EndpointException("Cannot publish message of type["
                + object.getClass().getName()
                + " to JMS. Unable to find a registered converter!");
    }

    public void invoke(T message) throws EndpointException
    {
        MessageProducer messageProducer = null;
        Message jmsMessage = null;
                
        try
        {
            messageProducer = session.createProducer(destination);
            
            if(message instanceof FlowEvent)
            {
                jmsMessage = this.convertToMessage(((FlowEvent) message).getPayload());

                // carry the event identifier if available
                this.managedEventIdentifierService.setEventIdentifier(((FlowEvent<String,?>)message).getIdentifier(), jmsMessage);
            }
            else
            {
                jmsMessage = this.convertToMessage(message);
            }

            // pass original message to getMessageProperties to allow overridding classes to implement custom processing
            // and set whatever comes back as properties on the message
            setMessageProperties(jmsMessage, getMessageProperties(message));

            // allow programmatic overrride of properties
            if(customMessagePropertyProvider != null)
            {
                setMessageProperties(jmsMessage, customMessagePropertyProvider.getProperties(message));
            }

            // publish message
            messageProducer.send(jmsMessage);
            if(logger.isDebugEnabled())
            {
                logger.debug("Published [" + message.toString() + "]");
            }

        }
        catch(JMSException e)
        {
            throw new EndpointException(e);
        }
        finally
        {
            if(messageProducer != null)
            {
                try
                {
                    messageProducer.close();
                    messageProducer = null;
                }
                catch (JMSException e)
                {
                    logger.error("Failed to close session", e);
                }
            }
        }
    }

    /**
     * Get default properties to set on the published message.
     * @return
     */
    protected Map<String,?> getMessageProperties(T message)
    {
        return this.configuration.getProperties();
    }
    
    /**
     * Allow the setting of a custom message property provider.
     * @param customMessagePropertyProvider
     */
    public void setCustomMessagePropertyProvider(CustomMessagePropertyProvider customMessagePropertyProvider)
    {
        this.customMessagePropertyProvider = customMessagePropertyProvider;
    }
    
    /**
     * Set the specified properties in the message.
     * @param properties
     * @throws JMSException
     */
    protected void setMessageProperties(Message message, Map<String,?> properties) throws JMSException
    {
        if(properties != null)
        {
            for(Map.Entry<String,?> entry : properties.entrySet())
            {
                Object value = entry.getValue();
                if(value instanceof String)
                {
                    message.setStringProperty(entry.getKey(), (String)value);
                }
                else if(value instanceof Integer)
                {
                    message.setIntProperty(entry.getKey(), (Integer)value);
                }
                else if(value instanceof Boolean)
                {
                    message.setBooleanProperty(entry.getKey(), (Boolean)value);
                }
                else if(value instanceof Byte)
                {
                    message.setByteProperty(entry.getKey(), (Byte)value);
                }
                else if(value instanceof Double)
                {
                    message.setDoubleProperty(entry.getKey(), (Double)value);
                }
                else if(value instanceof Float)
                {
                    message.setFloatProperty(entry.getKey(), (Float)value);
                }
                else if(value instanceof Long)
                {
                    message.setLongProperty(entry.getKey(), (Long)value);
                }
                else if(value instanceof Short)
                {
                    message.setShortProperty(entry.getKey(), (Short)value);
                }
                else
                {
                    message.setObjectProperty(entry.getKey(), value);
                }
            }
        }
        
    }
    
    /**
     * Override the default producer event life identifier service
     * @param managedEventIdentifierService
     */
    public void setManagedIdentifierService(ManagedEventIdentifierService managedEventIdentifierService)
    {
        this.managedEventIdentifierService = managedEventIdentifierService;
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

    /* (non-Javadoc)
     * @see org.ikasan.spec.management.ManagedResource#startManagedResource()
     */
    public void startManagedResource()
    {
        ConnectionFactory _connectionFactory = null;

        try
        {
            if(this.configuration.isRemoteJNDILookup())
            {
                Context context = getInitialContext();
                if(this.configuration.getConnectionFactoryName() == null)
                {
                    throw new RuntimeException("ConnectionFactory name cannot be 'null' when using remoteJNDILookup");
                }
                _connectionFactory = (ConnectionFactory)context.lookup(this.configuration.getConnectionFactoryName());

                if(this.configuration.getDestinationName() == null)
                {
                    throw new RuntimeException("DestinationName name cannot be 'null' when using remoteJNDILookup");
                }
                this.destination = (Destination)context.lookup(this.configuration.getDestinationName());
            }
            else
            {
                if(this.connectionFactory == null)
                {
                    throw new RuntimeException("You must specify the remoteJNDILookup as true or provide a ConnectionFactory instance for this class.");
                }

                _connectionFactory = this.connectionFactory;
            }

            if(this.configuration.getUsername() != null && this.configuration.getUsername().trim().length() > 0)
            {
                connection = _connectionFactory.createConnection(this.configuration.getUsername(), this.configuration.getPassword());
            }
            else
            {
                connection = _connectionFactory.createConnection();
            }

            this.session = connection.createSession(this.configuration.isTransacted(), this.configuration.getAcknowledgement());
            if(this.destination == null)
            {
                if(destinationResolver == null)
                {
                    throw new RuntimeException("destination and destinationResolver are both 'null'. No means of resolving a destination.");
                }

                this.destination = this.destinationResolver.getDestination();
            }
        }
        catch(JMSException e)
        {
            throw new EndpointException(e);
        }
        catch (NamingException e)
        {
            throw new RuntimeException(e);
        }
    }

    /* (non-Javadoc)
     * @see org.ikasan.spec.management.ManagedResource#stopManagedResource()
     */
    public void stopManagedResource()
    {
        if(session != null)
        {
            try
            {
                session.close();
                session = null;
            }
            catch (JMSException e)
            {
                logger.error("Failed to close session", e);
            }
        }

        if(connection != null)
        {
            try
            {
                connection.close();
                connection = null;
            }
            catch (JMSException e)
            {
                throw new EndpointException(e);
            }
        }
        
        // if we are using a destinationResolver then clear the destination instance
        if(this.destinationResolver != null)
        {
            this.destination = null;
        }
    }

    public boolean isCriticalOnStartup()
    {
        return this.isCriticalOnStartup;
    }

    public void setCriticalOnStartup(boolean isCriticalOnStartup)
    {
        this.isCriticalOnStartup = isCriticalOnStartup;
    }

    public void setManagedResourceRecoveryManager(ManagedResourceRecoveryManager managedResourceRecoveryManager)
    {
        // dont check this by default
    }

    private InitialContext getInitialContext() throws NamingException
    {
        Hashtable<String,String> env = new Hashtable<String,String>();
        env.put(Context.PROVIDER_URL, this.configuration.getProviderURL());
        env.put(Context.INITIAL_CONTEXT_FACTORY, this.configuration.getInitialContextFactory());
        if(this.configuration.getUrlPackagePrefixes() != null)
        {
            env.put(Context.URL_PKG_PREFIXES, this.configuration.getUrlPackagePrefixes());
        }

        return new InitialContext(env);
    }

    /**
     * Specific converter implementation for converting String to JMS Text Message
     */
    protected class StringToMessage implements Converter<String,TextMessage>
    {
        @Override
        public TextMessage convert(String payload) throws TransformationException
        {
            try
            {
                return session.createTextMessage(payload);
            }
            catch(JMSException e)
            {
                throw new TransformationException(e);
            }
        }
    }

    /**
     * Specific converter implementation for converting String to JMS Text Message
     */
    protected class Jeff implements Translator<StringBuilder>
    {

        @Override
        public void translate(StringBuilder payload) throws TransformationException {
                payload.append("additional value");
        }
    }

    /**
     * Specific converter implementation for converting byte[] to Message
     */
    protected class ByteArrayToMessage implements Converter<byte[],Message>
    {
        @Override
        public Message convert(byte[] payload) throws TransformationException
        {
            try
            {
                BytesMessage bytesMsg = session.createBytesMessage();
                bytesMsg.writeBytes(payload);
                return bytesMsg;
            }
            catch(JMSException e)
            {
                throw new TransformationException(e);
            }
        }
    }

    /**
     * Specific converter implementation for converting any serializable object to Message
     */
    protected class SerializableToMessage implements Converter<Serializable,Message>
    {
        @Override
        public Message convert(Serializable payload) throws TransformationException
        {
            try
            {
                return session.createObjectMessage(payload);
            }
            catch(JMSException e)
            {
                throw new TransformationException(e);
            }
        }
    }

    /**
     * Specific converter implementation for converting any Map to Message
     */
    protected class MapToMessage implements Converter<Map<String,Object>,Message>
    {
        @Override
        public Message convert(Map<String,Object> payload) throws TransformationException
        {
            try
            {
                MapMessage mapMsg = session.createMapMessage();
                for(Map.Entry<String,Object> entry:payload.entrySet())
                {
                    mapMsg.setObject(entry.getKey(), entry.getValue());
                }
                return mapMsg;
            }
            catch(JMSException e)
            {
                throw new TransformationException(e);
            }
        }
    }
}
