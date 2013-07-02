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
package org.ikasan.builder;

import java.util.ArrayList;
import java.util.List;

import org.ikasan.builder.FlowBuilder.FlowConfigurationBuilder;
import org.ikasan.spec.component.endpoint.Consumer;
import org.ikasan.spec.component.endpoint.Producer;
import org.ikasan.spec.component.transformation.Translator;
import org.ikasan.spec.configuration.ConfigurationService;
import org.ikasan.spec.event.EventFactory;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.flow.FlowElement;
import org.ikasan.spec.flow.FlowEventListener;
import org.ikasan.spec.module.Module;
import org.ikasan.spec.recovery.RecoveryManager;
import org.springframework.beans.factory.FactoryBean;

/**
 * @author Ikasan Development Team
 * 
 */
public class FlowFactory implements FactoryBean<Flow>
{
    /** name of the flow module owner */
    String moduleName;

    /** name of the flow being instantiated */
    String name;

    /** optional module description */
    String description;

    /** flow event listener */
    FlowEventListener flowEventListener;

    /** flow recovery manager instance */
    RecoveryManager recoveryManager;

    /** configuration service */
    ConfigurationService configurationService;

    /** flow element wiring */
    FlowConfigurationBuilder flowConfigurationBuilder;

    /** event factory */
    EventFactory eventFactory;

    /** track the order of defined flow elements until we need to build the flow */
    List<FlowElement> flowElements;
    
    /** consumer is always head of a flow configuration */
    FlowElement<Consumer> consumer;
    
    /**
     * Setter for moduleName
     * @param moduleName
     */
    public void setModuleName(String moduleName)
    {
        this.moduleName = moduleName;
    }

    /**
     * Setter for name
     * @param name
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * Allow override of default flowEventListener
     * @param flowEventListener
     */
    public void setFlowEventListener(FlowEventListener flowEventListener)
    {
        this.flowEventListener = flowEventListener;
    }

    /**
     * Allow override of default recoveryManager
     * @param recoveryManager
     */
    public void setRecoveryManager(RecoveryManager recoveryManager)
    {
        this.recoveryManager = recoveryManager;
    }

    /**
     * Allow overrideof default configurationService
     * @param configurationService
     */
    public void setConfigurationService(ConfigurationService configurationService)
    {
        this.configurationService = configurationService;
    }

    /**
     * Allow override of default event factory
     * @param eventFactory
     */
    public void setEventFactory(EventFactory eventFactory)
    {
        this.eventFactory = eventFactory;
    }

    /**
     * Setter for description
     * @param description
     */
    public void setDescription(String description)
    {
        this.description = description;
    }

    public void setConsumer(FlowElement<Consumer> consumer)
    {
        this.flowElements = new ArrayList<FlowElement>();
        this.consumer = consumer;
    }
    
    public void setTranslator(FlowElement<Translator> translator)
    {
        this.flowElements.add(translator);
    }
    
    public void setPublisher(FlowElement<Producer> producer)
    {
        this.flowElements.add(producer);
    }
    
    /*
     * (non-Javadoc)
     * @see org.springframework.beans.factory.FactoryBean#getObject()
     */
    public Flow getObject()
    {
        FlowBuilder flowBuilder = FlowBuilder.newFlow(name, moduleName);
        flowBuilder.withDescription(description);
        
        // override defaults if specified
        if(flowEventListener != null)
        {
            flowBuilder.withFlowListener(flowEventListener);
        }
        if(recoveryManager != null)
        {
            flowBuilder.withRecoveryManager(recoveryManager);
        }
        if(configurationService != null)
        {
            flowBuilder.withConfigurationService(configurationService);
        }
        if(eventFactory != null)
        {
            flowBuilder.withEventFactory(eventFactory);
        }

        FlowConfigurationBuilder flowConfigurationBuilder = flowBuilder.consumer(consumer);
        
        // wire the flow together
        for(FlowElement flowElement: flowElements)
        {
            if(flowElement.flowBuilder.
        }
        
        return flowBuilder
            .consumer("consumerName", null)
            .publisher("producerName", null)
            .build();
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.beans.factory.FactoryBean#getObjectType()
     */
    public Class<Module> getObjectType()
    {
        return Module.class;
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.beans.factory.FactoryBean#isSingleton()
     */
    public boolean isSingleton()
    {
        return false;
    }
}