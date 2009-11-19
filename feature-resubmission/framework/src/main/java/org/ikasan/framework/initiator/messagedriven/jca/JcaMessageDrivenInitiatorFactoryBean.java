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
package org.ikasan.framework.initiator.messagedriven.jca;

import javax.resource.spi.ActivationSpec;
import javax.resource.spi.ResourceAdapter;

import org.ikasan.common.factory.PayloadFactory;
import org.ikasan.framework.component.IkasanExceptionHandler;
import org.ikasan.framework.event.serialisation.JmsMessageEventSerialiser;
import org.ikasan.framework.flow.Flow;
import org.ikasan.framework.initiator.messagedriven.EventMessageDrivenInitiator;
import org.ikasan.framework.initiator.messagedriven.JmsMessageDrivenInitiator;
import org.ikasan.framework.initiator.messagedriven.JmsMessageDrivenInitiatorImpl;
import org.ikasan.framework.initiator.messagedriven.RawMessageDrivenInitiator;
import org.ikasan.framework.initiator.messagedriven.jca.SpringMessageListenerContainer;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.jms.listener.endpoint.JmsActivationSpecConfig;
import org.springframework.jms.listener.endpoint.JmsActivationSpecFactory;
import org.springframework.jms.support.destination.DestinationResolver;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * This class helps create Message Driven Initiators
 *
 * @author Ikasan Development Team
 */
public class JcaMessageDrivenInitiatorFactoryBean implements FactoryBean, BeanNameAware
{
    /** The transaction manager */
    private PlatformTransactionManager transactionManager;

	/** The activation spec properties */
    private JmsActivationSpecConfig jmsActivationSpecConfig;

	/** The activation spec factory */
    private JmsActivationSpecFactory jmsActivationSpecFactory;

    /** DestinationResolver */
    private DestinationResolver destinationResolver;

    /** The activation spec instance */
    private ActivationSpec jmsActivationSpec;

    /** The resource adapter */
    private ResourceAdapter resourceAdapter;

    /** Name of this bean */
    private String name;

    /** Module name */
    private String moduleName;

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

    /** The message initiator */
    private JmsMessageDrivenInitiator initiator;

    /**
     * Configures the initiator to reuse the priority from the message on the created Event
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
     * @param transactionManager the transactionManager to set
     */
    public void setTransactionManager(PlatformTransactionManager transactionManager)
    {
        this.transactionManager = transactionManager;
    }

	public void setJmsActivationSpecConfig(JmsActivationSpecConfig jmsActivationSpecConfig)
	{
		this.jmsActivationSpecConfig = jmsActivationSpecConfig;
	}

	public void setJmsActivationSpecFactory(JmsActivationSpecFactory jmsActivationSpecFactory)
	{
		this.jmsActivationSpecFactory = jmsActivationSpecFactory;
	}

    /**
     * @param destinationResolver to set
     */
    public void setDestinationResolver(DestinationResolver destinationResolver)
    {
        this.destinationResolver = destinationResolver;
    }

	public void setJmsActivationSpec(ActivationSpec jmsActivationSpec) {
		this.jmsActivationSpec = jmsActivationSpec;
	}

	public void setResourceAdapter(ResourceAdapter resourceAdapter)
	{
		this.resourceAdapter = resourceAdapter;
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
     * @param payloadFactory the payloadFactory to set
     */
    public void setPayloadFactory(PayloadFactory payloadFactory)
    {
        this.payloadFactory = payloadFactory;
    }

    /**
     * @param exceptionHandler the exceptionHandler to set
     */
    public void setExceptionHandler(IkasanExceptionHandler exceptionHandler){
    	this.exceptionHandler = exceptionHandler;
    }

	/**
	 * @param respectPriority the respectPriority to setS
	 */
	public void setRespectPriority(boolean respectPriority)
	{
		this.respectPriority = respectPriority;
	}

    public Object getObject() throws Exception
    {
        if (this.initiator == null)
        {
            this.initiator = constructInitiator();
            if (this.transactionManager == null)
            {
                throw new IllegalArgumentException("transactionManager is mandatory for JmsMessageDrivenInitiator creation");
            }
            SpringMessageListenerContainer springMessageListenerContainer = new SpringMessageListenerContainer();
            //springMessageListenerContainer.setActivationSpecConfig(this.jmsActivationSpecConfig);
            springMessageListenerContainer.setActivationSpec(this.jmsActivationSpec);
            //springMessageListenerContainer.setActivationSpecFactory(this.jmsActivationSpecFactory);
            //springMessageListenerContainer.setDestinationResolver(this.destinationResolver);
            springMessageListenerContainer.setResourceAdapter(this.resourceAdapter);
            springMessageListenerContainer.setMessageListener(this.initiator);
            springMessageListenerContainer.setTransactionManager(this.transactionManager);
            ((JmsMessageDrivenInitiatorImpl) this.initiator).setMessageListenerContainer(springMessageListenerContainer);
            springMessageListenerContainer.setAutoStartup(false);
            springMessageListenerContainer.afterPropertiesSet();
        }
        return this.initiator;
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
            ((RawMessageDrivenInitiator)thisInitiator).setRespectPriority(respectPriority);
        }
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
