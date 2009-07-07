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
package org.ikasan.framework.initiator.messagedriven;

import javax.jms.ConnectionFactory;
import javax.jms.Destination;

import org.ikasan.common.factory.PayloadFactory;
import org.ikasan.framework.component.IkasanExceptionHandler;
import org.ikasan.framework.error.service.ErrorLoggingService;
import org.ikasan.framework.event.exclusion.service.ExcludedEventService;
import org.ikasan.framework.event.serialisation.JmsMessageEventSerialiser;
import org.ikasan.framework.flow.Flow;
import org.ikasan.framework.initiator.AbstractInitiator;
import org.ikasan.framework.initiator.messagedriven.spring.SpringMessageListenerContainer;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.jms.support.destination.DestinationResolver;
import org.springframework.transaction.PlatformTransactionManager;
import org.w3c.dom.events.EventException;

/**
 * This class helps create Message Driven Initiators
 * 
 * @author Ikasan Development Team
 */
public class MessageDrivenInitiatorFactoryBean implements FactoryBean, BeanNameAware
{
    /** Name of this bean */
    private String name;

    /** Module name */
    private String moduleName;

    /** Connection factory for this bean */
    private ConnectionFactory connectionFactory;

    /** Destination */
    private Destination destination;

    /** DestinationResolver */
    private DestinationResolver destinationResolver;

    /**
     * JndiName of the destination for use with the destinationResolver if
     * destination is not directly supplied
     */
    private String destinationName;

    /** The transaction manager */
    private PlatformTransactionManager transactionManager;

    /** The flow */
    private Flow flow;
    
    /** The Exception Handler */
    private IkasanExceptionHandler exceptionHandler;

    /** The event deserialiser */
    private JmsMessageEventSerialiser eventDeserialiser;

    /** The payload factory */
    private PayloadFactory payloadFactory;

    /** The type of object */
    private Class<? extends JmsMessageDrivenInitiatorImpl> objectType;
    
    /** The error logging service */
    private ErrorLoggingService errorLoggingService;
    
    /** The excludedEvent service */
    private ExcludedEventService excludedEventService;

    /** The message initiator */
    private JmsMessageDrivenInitiator initiator;

    /** Whether the subscription to JMS destination is durable. Default is true. */
    private boolean isSubscriptionDurable = true;

    /**
     * Which JMS domain to use. Default is true for Publish/Subscribe domain
     * (Topics). Set to <code>false</code> for Point-to-Point domain (Queues).
     */
    private boolean pubSubDomain = true;

    /**
     * @param moduleName the moduleName to set
     */
    public void setModuleName(String moduleName)
    {
        this.moduleName = moduleName;
    }

    /**
     * @param connectionFactory the connectionFactory to set
     */
    public void setConnectionFactory(ConnectionFactory connectionFactory)
    {
        this.connectionFactory = connectionFactory;
    }

    /**
     * Whether a subscription to jms destination is durable.
     * 
     * @param isSubscriptionDurable the boolean value to set
     */
    public void setIsSubscriptionDurable(boolean isSubscriptionDurable)
    {
        this.isSubscriptionDurable = isSubscriptionDurable;
    }

    /**
     * Which type of destination to resolve.
     * @param pubSubDomain <code>true</code> for pub/sub domain, <code>false</code> for point-to-point domain.
     */
    public void setPubSubDomain(boolean pubSubDomain)
    {
        this.pubSubDomain = pubSubDomain;
    }

    /**
     * @param destination the destination to set
     */
    public void setDestination(Destination destination)
    {
        this.destination = destination;
    }

    /**
     * @param destinationResolver to set
     */
    public void setDestinationResolver(DestinationResolver destinationResolver)
    {
        this.destinationResolver = destinationResolver;
    }

    /**
     * @param destinationName to set
     */
    public void setDestinationName(String destinationName)
    {
        this.destinationName = destinationName;
    }

    /**
     * @param transactionManager the transactionManager to set
     */
    public void setTransactionManager(PlatformTransactionManager transactionManager)
    {
        this.transactionManager = transactionManager;
    }

    /**
     * @param flow the flow to set
     */
    public void setFlow(Flow flow)
    {
        this.flow = flow;
    }

    /**
     * @param jmsMessageEventSerialiser the jmsMessageEventSerialiser to set
     */
    public void setEventDeserialiser(JmsMessageEventSerialiser jmsMessageEventSerialiser)
    {
        this.eventDeserialiser = jmsMessageEventSerialiser;
    }

    /**
     * @param exceptionHandler the exceptionHandler to set
     */
    public void setExceptionHandler(IkasanExceptionHandler exceptionHandler){
    	this.exceptionHandler = exceptionHandler;
    }

    /**
     * @param payloadFactory the payloadFactory to set
     */
    public void setPayloadFactory(PayloadFactory payloadFactory)
    {
        this.payloadFactory = payloadFactory;
    }
    
    /**
     * @param excludedEventService the excludedEventService to set
     */   
	public void setExcludedEventService(
			ExcludedEventService excludedEventService) {
		this.excludedEventService = excludedEventService;
	}

    /**
     * @param errorLoggingService the errorLoggingService to set
     */   
	public void setErrorLoggingService(
			ErrorLoggingService errorLoggingService) {
		this.errorLoggingService = errorLoggingService;
	}
	
    public Object getObject() throws Exception
    {
        if (initiator == null)
        {
            initiator = constructInitiator();
            if (connectionFactory == null)
            {
                throw new IllegalArgumentException("connectionFactory is mandatory for JmsMessageDrivenInitiator creation");
            }
            if (destination == null && destinationResolver == null)
            {
                throw new IllegalArgumentException("either destination or destinationResolver is mandatory for JmsMessageDrivenInitiator creation");
            }
            if (transactionManager == null)
            {
                throw new IllegalArgumentException("transactionManager is mandatory for JmsMessageDrivenInitiator creation");
            }
            SpringMessageListenerContainer springMessageListenerContainer = new SpringMessageListenerContainer();
            springMessageListenerContainer.setBeanName(this.moduleName + "-" + this.name);
            springMessageListenerContainer.setConnectionFactory(connectionFactory);
            if (destination != null)
            {
                springMessageListenerContainer.setDestination(destination);
            }
            else
            {
                springMessageListenerContainer.setDestinationResolver(destinationResolver);
                springMessageListenerContainer.setDestinationName(destinationName);
                springMessageListenerContainer.setPubSubDomain(this.pubSubDomain);
            }
            springMessageListenerContainer.setMessageListener(initiator);
            springMessageListenerContainer.setTransactionManager(transactionManager);
            springMessageListenerContainer.setSessionTransacted(true);
            springMessageListenerContainer.setSubscriptionDurable(this.isSubscriptionDurable);
            springMessageListenerContainer.setDurableSubscriptionName(moduleName + "-" + name + "-durableSubscription");
            ((JmsMessageDrivenInitiatorImpl) initiator).setMessageListenerContainer(springMessageListenerContainer);
            springMessageListenerContainer.setAutoStartup(false);
            springMessageListenerContainer.afterPropertiesSet();
            
        }
        return initiator;
    }

    /**
     * Constructor the JMS message driven initiator
     * 
     * @return A JMS message driven initiator
     */
    private JmsMessageDrivenInitiator constructInitiator()
    {
        if ((moduleName == null) || ("".equals(moduleName)))
        {
            throw new IllegalArgumentException("moduleName is mandatory for JmsMessageDrivenInitiator creation");
        }
        if ((name == null) || ("".equals(name)))
        {
            throw new IllegalArgumentException("name is mandatory for JmsMessageDrivenInitiator creation");
        }
        if (flow == null)
        {
            throw new IllegalArgumentException("flow is mandatory for JmsMessageDrivenInitiator creation");
        }
        if (exceptionHandler == null)
        {
            throw new IllegalArgumentException("exceptionHandler is mandatory for JmsMessageDrivenInitiator creation");
        }
        if (eventDeserialiser == null)
        {
            if (payloadFactory == null)
            {
                throw new IllegalArgumentException(
                    "payloadFactory is mandatory for JmsMessageDrivenInitiator creation, if no JmsMessageEventSerialiser has been set");
            }
        }
        JmsMessageDrivenInitiator thisInitiator = null;
        if (eventDeserialiser != null)
        {
            thisInitiator = new EventMessageDrivenInitiator(moduleName, name, flow, exceptionHandler, eventDeserialiser);
        }
        else
        {
            thisInitiator = new RawMessageDrivenInitiator(moduleName, name, flow, exceptionHandler, payloadFactory);
        }
        ((AbstractInitiator)thisInitiator).setExcludedEventService(excludedEventService);
        ((AbstractInitiator)thisInitiator).setErrorLoggingService(errorLoggingService);
        return thisInitiator;
    }

    public Class<?> getObjectType()
    {
        return objectType;
    }

    public boolean isSingleton()
    {
        return true;
    }

    public void setBeanName(String beanName)
    {
        this.name = beanName;
    }


}
