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
package org.ikasan.framework.initiator.messagedriven;

import javax.jms.ConnectionFactory;
import javax.jms.Destination;

import org.ikasan.common.factory.PayloadFactory;
import org.ikasan.framework.component.IkasanExceptionHandler;
import org.ikasan.framework.error.service.ErrorLoggingService;
import org.ikasan.framework.event.exclusion.service.ExcludedEventService;
import org.ikasan.framework.event.serialisation.JmsMessageEventSerialiser;
import org.ikasan.framework.initiator.AbstractInitiator;
import org.ikasan.framework.initiator.messagedriven.spring.SpringMessageListenerContainer;
import org.ikasan.spec.flow.Flow;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.jms.support.destination.DestinationResolver;
import org.springframework.transaction.PlatformTransactionManager;

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
    
    /** The excludedFlowEvent service */
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
     * Configures the initiator to reuse the priority from the message on the created FlowEvent
     * 
     * Only applicable to RawMessageDrivenInitiators
     */
    private boolean respectPriority = false;

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
     * @param jmsMessageFlowEventSerialiser the jmsMessageFlowEventSerialiser to set
     */
    public void setFlowEventDeserialiser(JmsMessageEventSerialiser jmsMessageEventSerialiser)
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
     * @param excludedFlowEventService the excludedFlowEventService to set
     */   
	public void setExcludedEventService(
			ExcludedEventService excludedEventService) {
		this.excludedEventService = excludedEventService;
	}
	/**
	 * @param respectPriority the respectPriority to setS
	 */
	public void setRespectPriority(boolean respectPriority) {
		this.respectPriority = respectPriority;
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
                    "payloadFactory is mandatory for JmsMessageDrivenInitiator creation, if no JmsMessageFlowEventSerialiser has been set");
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
            ((RawMessageDrivenInitiator)thisInitiator).setRespectPriority(respectPriority);
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
