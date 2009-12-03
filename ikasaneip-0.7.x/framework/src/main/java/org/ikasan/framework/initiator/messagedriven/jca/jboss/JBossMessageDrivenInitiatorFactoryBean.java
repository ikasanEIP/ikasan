/*
 * $Id
 * $URL$
 * 
 * =============================================================================
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
 * =============================================================================
 */
package org.ikasan.framework.initiator.messagedriven.jca.jboss;

import javax.jms.Destination;
import javax.jms.Session;

import org.ikasan.common.factory.PayloadFactory;
import org.ikasan.framework.event.serialisation.JmsMessageEventSerialiser;
import org.ikasan.framework.flow.Flow;
import org.ikasan.framework.initiator.messagedriven.jca.EventMessageDrivenInitiator;
import org.ikasan.framework.initiator.messagedriven.jca.JmsMessageDrivenInitiator;
import org.ikasan.framework.initiator.messagedriven.jca.JmsMessageDrivenInitiatorImpl;
import org.ikasan.framework.initiator.messagedriven.jca.MessageListenerContainer;
import org.ikasan.framework.initiator.messagedriven.jca.RawMessageDrivenInitiator;
import org.ikasan.framework.initiator.messagedriven.jca.jboss.JBossJmsActivationSpecConfig;
import org.ikasan.framework.initiator.messagedriven.jca.jboss.JBossResourceAdapterUtils;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.jms.support.destination.DestinationResolver;
import org.ikasan.framework.initiator.messagedriven.jca.spring.JmsMessageEndpointManager ;

/**
 * This class helps create Message Driven Initiators specifically for the JBoss
 * underlying JMS implementation.
 * 
 * @author Ikasan Development Team
 */
public class JBossMessageDrivenInitiatorFactoryBean implements FactoryBean, BeanNameAware
{
    /** specified name of an initiator */
    private String name;

    /** Module name */
    private String moduleName;

    /** Destination */
    private Destination destination;

    /** DestinationResolver */
    private DestinationResolver destinationResolver;

    /**
     * JndiName of the destination for use with the destinationResolver if
     * destination is not directly supplied
     */
    private String destinationName;

    /** The flow */
    private Flow flow;

    /** The event deserialiser */
    private JmsMessageEventSerialiser eventDeserialiser;

    /** The payload factory */
    private PayloadFactory payloadFactory;

    /** The type of object */
    private Class<? extends JmsMessageDrivenInitiatorImpl> objectType;

    /** The message initiator */
    private JmsMessageDrivenInitiator initiator;

    /** JMS message selector */
    private String messageSelector;

    /** JMS acknowledgement mode */
    private Integer acknowledgementMode = new Integer(Session.AUTO_ACKNOWLEDGE);

    /** Whether the subscription to JMS destination is durable. Default is true. */
    private Boolean subscriptionDurable = Boolean.TRUE;

    /**
     * Which JMS domain to use. Default is true for Publish/Subscribe domain
     * (Topics). Set to <code>false</code> for Point-to-Point domain (Queues).
     */
    private Boolean pubSubDomain = Boolean.TRUE;

    /**
     * Configures the initiator to reuse the priority from the message on the
     * created Event
     * 
     * Only applicable to RawMessageDrivenInitiators
     */
    private Boolean respectPriority = Boolean.FALSE;

    /** allow override for provider adapterJNDI */
    private String providerAdapterJNDI = "java:/DefaultJMSProvider";

    /** Username for JMS authentication */
    private String username;

    /** Password for JMS authentication */
    private String password;

    /** allow override for concurrent JMS sessions */
    private Integer maxMessages = new Integer(1);

    /** allow override for number of messages in a JMS fetch per transaction */
    private Integer maxSession = new Integer(1);

    /** allow override for JMS keepalive time in millis */
    private Integer keepAlive = new Integer(60000);

    /** allow override for JMS reconnect time in seconds */
    private Integer reconnectInterval = new Integer(10);

    /** deliver msgs on deployment */
    private Boolean deliveryActive = Boolean.FALSE;

    /** allow override use of DLQ */
    private Boolean useDLQ = Boolean.FALSE;

    /* actual DLQ handler */
    private String dlqHandler;

    /* DLQ user */
    private String dlqUser;

    /* DLQ password */
    private String dlqPassword;

    /* DLQ client id */
    private String dlqClientId;    

    /* Force transacted. Only supported in JBoss 5.2.0GA */
    private Boolean forceTransacted = Boolean.FALSE;

    /**
     * @return the name
     */
    public String getName()
    {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * @return the moduleName
     */
    public String getModuleName()
    {
        return moduleName;
    }

    /**
     * @param moduleName the moduleName to set
     */
    public void setModuleName(String moduleName)
    {
        this.moduleName = moduleName;
    }

    /**
     * @return the destination
     */
    public Destination getDestination()
    {
        return destination;
    }

    /**
     * @param destination the destination to set
     */
    public void setDestination(Destination destination)
    {
        this.destination = destination;
    }

    /**
     * @return the destinationResolver
     */
    public DestinationResolver getDestinationResolver()
    {
        return destinationResolver;
    }

    /**
     * @param destinationResolver the destinationResolver to set
     */
    public void setDestinationResolver(DestinationResolver destinationResolver)
    {
        this.destinationResolver = destinationResolver;
    }

    /**
     * @return the destinationName
     */
    public String getDestinationName()
    {
        return destinationName;
    }

    /**
     * @param destinationName the destinationName to set
     */
    public void setDestinationName(String destinationName)
    {
        this.destinationName = destinationName;
    }

    /**
     * @return the flow
     */
    public Flow getFlow()
    {
        return flow;
    }

    /**
     * @param flow the flow to set
     */
    public void setFlow(Flow flow)
    {
        this.flow = flow;
    }

    /**
     * @return the eventDeserialiser
     */
    public JmsMessageEventSerialiser getEventDeserialiser()
    {
        return eventDeserialiser;
    }

    /**
     * @param eventDeserialiser the eventDeserialiser to set
     */
    public void setEventDeserialiser(JmsMessageEventSerialiser eventDeserialiser)
    {
        this.eventDeserialiser = eventDeserialiser;
    }

    /**
     * @return the payloadFactory
     */
    public PayloadFactory getPayloadFactory()
    {
        return payloadFactory;
    }

    /**
     * @param payloadFactory the payloadFactory to set
     */
    public void setPayloadFactory(PayloadFactory payloadFactory)
    {
        this.payloadFactory = payloadFactory;
    }

    /**
     * @return the initiator
     */
    public JmsMessageDrivenInitiator getInitiator()
    {
        return initiator;
    }

    /**
     * @param initiator the initiator to set
     */
    public void setInitiator(JmsMessageDrivenInitiator initiator)
    {
        this.initiator = initiator;
    }

    /**
     * @return the providerAdapterJNDI
     */
    public String getProviderAdapterJNDI()
    {
        return providerAdapterJNDI;
    }

    /**
     * @param providerAdapterJNDI the providerAdapterJNDI to set
     */
    public void setProviderAdapterJNDI(String providerAdapterJNDI)
    {
        this.providerAdapterJNDI = providerAdapterJNDI;
    }

    /**
     * @return the username
     */
    public String getUsername()
    {
        return username;
    }

    /**
     * @param username the username to set
     */
    public void setUsername(String username)
    {
        this.username = username;
    }

    /**
     * @return the password
     */
    public String getPassword()
    {
        return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password)
    {
        this.password = password;
    }

    /**
     * @return the maxMessages
     */
    public Integer getMaxMessages()
    {
        return maxMessages;
    }

    /**
     * @param maxMessages the maxMessages to set
     */
    public void setMaxMessages(Integer maxMessages)
    {
        this.maxMessages = maxMessages;
    }

    /**
     * @return the maxSession
     */
    public Integer getMaxSession()
    {
        return maxSession;
    }

    /**
     * @param maxSession the maxSession to set
     */
    public void setMaxSession(Integer maxSession)
    {
        this.maxSession = maxSession;
    }

    /**
     * @return the keepAlive
     */
    public Integer getKeepAlive()
    {
        return keepAlive;
    }

    /**
     * @param keepAlive the keepAlive to set
     */
    public void setKeepAlive(Integer keepAlive)
    {
        this.keepAlive = keepAlive;
    }

    /**
     * @return the reconnectInterval
     */
    public Integer getReconnectInterval()
    {
        return reconnectInterval;
    }

    /**
     * @param reconnectInterval the reconnectInterval to set
     */
    public void setReconnectInterval(Integer reconnectInterval)
    {
        this.reconnectInterval = reconnectInterval;
    }

    /**
     * @return the deliveryActive
     */
    public Boolean getDeliveryActive()
    {
        return deliveryActive;
    }

    /**
     * @param deliveryActive the deliveryActive to set
     */
    public void setDeliveryActive(Boolean deliveryActive)
    {
        this.deliveryActive = deliveryActive;
    }

    public Boolean getPubSubDomain()
    {
        return pubSubDomain;
    }

    public void setPubSubDomain(Boolean pubSubDomain)
    {
        this.pubSubDomain = pubSubDomain;
    }

    /**
     * @return the useDLQ
     */
    public Boolean getUseDLQ()
    {
        return useDLQ;
    }

    /**
     * @param useDLQ the useDLQ to set
     */
    public void setUseDLQ(Boolean useDLQ)
    {
        this.useDLQ = useDLQ;
    }

    /**
     * @return the dlqHandler
     */
    public String getDlqHandler()
    {
        return dlqHandler;
    }

    /**
     * @param dlqHandler the dlqHandler to set
     */
    public void setDlqHandler(String dlqHandler)
    {
        this.dlqHandler = dlqHandler;
    }

    /**
     * @return the dlqUser
     */
    public String getDlqUser()
    {
        return dlqUser;
    }

    /**
     * @param dlqUser the dlqUser to set
     */
    public void setDlqUser(String dlqUser)
    {
        this.dlqUser = dlqUser;
    }

    /**
     * @return the dlqPassword
     */
    public String getDlqPassword()
    {
        return dlqPassword;
    }

    /**
     * @param dlqPassword the dlqPassword to set
     */
    public void setDlqPassword(String dlqPassword)
    {
        this.dlqPassword = dlqPassword;
    }

    /**
     * @return the dlqClientId
     */
    public String getDlqClientId()
    {
        return dlqClientId;
    }

    /**
     * @param dlqClientId the dlqClientId to set
     */
    public void setDlqClientId(String dlqClientId)
    {
        this.dlqClientId = dlqClientId;
    }

    /**
     * @return the forceTransacted
     */
    public Boolean getForceTransacted()
    {
        return forceTransacted;
    }

    /**
     * @param forceTransacted the forceTransacted to set
     */
    public void setForceTransacted(Boolean forceTransacted)
    {
        this.forceTransacted = forceTransacted;
    }

    /**
     * @return the respectPriority
     */
    public Boolean getRespectPriority()
    {
        return respectPriority;
    }

    /**
     * @param respectPriority the respectPriority to set
     */
    public void setRespectPriority(Boolean respectPriority)
    {
        this.respectPriority = respectPriority;
    }

    /* (non-Javadoc)
     * @see org.springframework.beans.factory.FactoryBean#getObject()
     */
    public Object getObject() throws Exception
    {
        if (this.initiator == null)
        {
            JBossJmsActivationSpecConfig specConfig = constructActivationSpec();
            JBossJmsActivationSpecFactory specFactory = new JBossJmsActivationSpecFactory();
            
            // create Spring endpointManager
            JmsMessageEndpointManager endpointManager = new JmsMessageEndpointManager();
            endpointManager.setActivationSpecFactory(specFactory);
            endpointManager.setActivationSpecConfig(specConfig);
            endpointManager.setResourceAdapter(JBossResourceAdapterUtils.getResourceAdapter());

            this.initiator = constructInitiator(endpointManager);
            endpointManager.setMessageListener(this.initiator);
            endpointManager.setAutoStartup(this.deliveryActive.booleanValue());
//            endpointManager.setConnectionListener(this.connectionListener);
            endpointManager.afterPropertiesSet();
        }
        return this.initiator;
    }

    /**
     * Constructor for the JMS Activation Spec configuration.
     * @return JBossJmsActivationSpecConfig
     */
    private JBossJmsActivationSpecConfig constructActivationSpec()
    {
        if (destinationName == null && destination == null && destinationResolver == null)
        {
            throw new IllegalArgumentException("either destination or destinationResolver is mandatory for JmsMessageDrivenInitiator creation");
        }

        JBossJmsActivationSpecConfig specConfig = new JBossJmsActivationSpecConfig();

        // standard JMS

        specConfig.setDestinationName(this.destinationName);
        //TODO determin the pubSubDomain flag based on destination type.
        specConfig.setPubSubDomain(this.pubSubDomain.booleanValue());
        specConfig.setSubscriptionDurable(this.subscriptionDurable.booleanValue());
        
        String uniqueName = this.moduleName + '-' + this.flow.getName() + '-' + this.name;
        specConfig.setClientId(uniqueName);
        if(this.subscriptionDurable.booleanValue())
        {
            specConfig.setDurableSubscriptionName(uniqueName);
        }

        specConfig.setMessageSelector(this.messageSelector);
        specConfig.setAcknowledgeMode(this.acknowledgementMode.intValue());
        specConfig.setMaxConcurrency(this.maxMessages.intValue());
        specConfig.setPrefetchSize(this.maxSession.intValue());
        
        // JBoss specific JMS
        specConfig.setProviderAdapterJNDI(this.providerAdapterJNDI);
        specConfig.setUser(this.username);
        specConfig.setPassword(this.password);
        specConfig.setKeepAlive(this.keepAlive.intValue());
        specConfig.setReconnectInterval(this.reconnectInterval.intValue());
        specConfig.setUseDLQ(this.useDLQ.booleanValue());
        specConfig.setDlqHandler(this.dlqHandler);
        specConfig.setDlqUser(this.dlqUser);
        specConfig.setDlqPassword(this.dlqPassword);
        specConfig.setDlqClientId(this.dlqClientId);
        specConfig.setForceTransacted(this.forceTransacted.booleanValue());

        return specConfig;
    }

    /**
     * Constructor the JMS message driven initiator
     * 
     * @return A JMS message driven initiator
     */
    private JmsMessageDrivenInitiator constructInitiator(MessageListenerContainer messageListenerContainer)
    {
        if ((this.moduleName == null) || ("".equals(this.moduleName)))
        {
            throw new IllegalArgumentException("moduleName is mandatory for JmsMessageDrivenInitiator creation");
        }
        if ((this.name == null) || ("".equals(this.name)))
        {
            throw new IllegalArgumentException("name is mandatory for JmsMessageDrivenInitiator creation");
        }
        if (this.flow == null)
        {
            throw new IllegalArgumentException("flow is mandatory for JmsMessageDrivenInitiator creation");
        }
        if (this.eventDeserialiser == null)
        {
            if (this.payloadFactory == null)
            {
                throw new IllegalArgumentException(
                    "payloadFactory is mandatory for JmsMessageDrivenInitiator creation, if no JmsMessageEventSerialiser has been set");
            }
        }
        if (this.eventDeserialiser != null)
        {
            EventMessageDrivenInitiator eventMDI = new EventMessageDrivenInitiator(this.moduleName, this.name, this.flow, this.eventDeserialiser);
            eventMDI.setMessageListenerContainer(messageListenerContainer);
            return eventMDI;
        }
        RawMessageDrivenInitiator rawMDI = new RawMessageDrivenInitiator(this.moduleName, this.name, this.flow, this.payloadFactory);
        rawMDI.setRespectPriority(this.respectPriority.booleanValue());
        rawMDI.setMessageListenerContainer(messageListenerContainer);
        return rawMDI;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.beans.factory.FactoryBean#getObjectType()
     */
    public Class<?> getObjectType()
    {
        return objectType;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.beans.factory.FactoryBean#isSingleton()
     */
    public boolean isSingleton()
    {
        return true;
    }

    /**
     * Allow the bean name to set the initiator name only when the initiator
     * name has not been specified.
     * 
     * @param beanName
     */
    public void setBeanName(String beanName)
    {
        if (this.name == null)
        {
            this.name = beanName;
        }
    }
    
}
