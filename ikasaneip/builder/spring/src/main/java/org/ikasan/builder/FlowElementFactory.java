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

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;
import org.ikasan.flow.event.DefaultReplicationFactory;
import org.ikasan.flow.visitorPattern.FlowElementImpl;
import org.ikasan.flow.visitorPattern.invoker.BrokerFlowElementInvoker;
import org.ikasan.flow.visitorPattern.invoker.ConcurrentSplitterFlowElementInvoker;
import org.ikasan.flow.visitorPattern.invoker.ConsumerFlowElementInvoker;
import org.ikasan.flow.visitorPattern.invoker.ConverterFlowElementInvoker;
import org.ikasan.flow.visitorPattern.invoker.FilterFlowElementInvoker;
import org.ikasan.flow.visitorPattern.invoker.MultiRecipientRouterConfiguration;
import org.ikasan.flow.visitorPattern.invoker.MultiRecipientRouterFlowElementInvoker;
import org.ikasan.flow.visitorPattern.invoker.ProducerFlowElementInvoker;
import org.ikasan.flow.visitorPattern.invoker.SequencerFlowElementInvoker;
import org.ikasan.flow.visitorPattern.invoker.SingleRecipientRouterFlowElementInvoker;
import org.ikasan.flow.visitorPattern.invoker.SplitterFlowElementInvoker;
import org.ikasan.flow.visitorPattern.invoker.TranslatorFlowElementInvoker;
import org.ikasan.spec.component.endpoint.Broker;
import org.ikasan.spec.component.endpoint.Consumer;
import org.ikasan.spec.component.endpoint.Producer;
import org.ikasan.spec.component.filter.Filter;
import org.ikasan.spec.component.routing.MultiRecipientRouter;
import org.ikasan.spec.component.routing.Router;
import org.ikasan.spec.component.routing.SingleRecipientRouter;
import org.ikasan.spec.component.sequencing.Sequencer;
import org.ikasan.spec.component.splitting.Splitter;
import org.ikasan.spec.component.transformation.Converter;
import org.ikasan.spec.component.transformation.Translator;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.ikasan.spec.flow.FlowElement;
import org.ikasan.spec.flow.FlowElementInvoker;
import org.ikasan.spec.replay.ReplayRecordService;
import org.springframework.beans.factory.FactoryBean;

/**
 * Spring based Factory Bean for the creation of FlowElements.
 * 
 * @author Ikasan Development Team
 * 
 */
public class FlowElementFactory<COMPONENT,CONFIGURATION> implements FactoryBean<FlowElement<?>>
{
    /** class logger */
    private static Logger logger = Logger.getLogger(FlowElementFactory.class);

    /** name of the flow element being instantiated */
    String name;

    /** POJO component being wrapped */
    COMPONENT component;

    /** flow element multiple transitions */
    Map<String,FlowElement<?>> transitions;
    
    /** flow element single transition */
    FlowElement<?> transition;
    
    /** identifier if the component supported ConfiguredResource */
    String configuredResourceId;
    
    /** The configuration */
    CONFIGURATION configuration;

    /** allow FE's to have their invoker behaviour configured */
    Object flowElementInvokerConfiguration;

    /** allow concurrency to be specified */
    int concurrentThreads;

    /** allow override of executor service */
    ExecutorService executorService;

    /** allow turning off context listeners at the component level */
    Boolean ignoreContextInvocation = false;

    /**
     * Setter for executor service override
     * @param executorService
     */
    public void setExecutorService(ExecutorService executorService)
    {
        this.executorService = executorService;
    }

    /**
     * Setter for concurrent threads
     * @param concurrentThreads
     */
    public void setConcurrentThreads(int concurrentThreads)
    {
        this.concurrentThreads = concurrentThreads;
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
     * Setter for component.
     * @param component
     */
    public void setComponent(COMPONENT component)
    {
        this.component = component;
    }

    /**
     * Setter for transitions.
     * @param transitions
     */
    public void setTransitions(Map<String,FlowElement<?>> transitions)
    {
        this.transitions = transitions;
    }

    /**
     * Setter for transition.
     * @param transition
     */
    public void setTransition(FlowElement<?> transition)
    {
        this.transition = transition;
    }

    /**
     * Setter for configured resource identifier
     * @param configuredResourceId
     */
    public void setConfiguredResourceId(String configuredResourceId)
    {
        this.configuredResourceId = configuredResourceId;
    }

    /**
     * Allow the flow element invoker configuration to be mutated
     * @param flowElementInvokerConfiguration
     */
    public void setFlowElementInvokerConfiguration(Object flowElementInvokerConfiguration)
    {
        this.flowElementInvokerConfiguration = flowElementInvokerConfiguration;
    }

    /**
     * Setter for the actual component configuration
     * @param configuration
     */
    public void setConfiguration(CONFIGURATION configuration)
    {
        this.configuration = configuration;
    }

    public void setIgnoreContextInvocation(Boolean ignoreContextInvocation)
    {
        this.ignoreContextInvocation = ignoreContextInvocation;
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.beans.factory.FactoryBean#getObject()
     */
    public FlowElement<?> getObject()
    {
        // configure component as required
        if(configuredResourceId != null)
        {
            if(! (component instanceof ConfiguredResource) )
            {
                throw new IllegalArgumentException("Trying to configure a component not marked as a ConfiguredResource. Component [" + this.name + "] must either implement ConfiguredResource or remove the configuration.");
            }
            
            ConfiguredResource configuredResource = (ConfiguredResource)component;
            configuredResource.setConfiguredResourceId(configuredResourceId);
            
            if(configuration == null)
            {
                logger.warn("Component [" + name + "] is marked as a configured resource, but has no configuration!");
            }
            
            configuredResource.setConfiguration(configuration);
        }

        if(transitions != null)
        {
            return new FlowElementImpl(name, component, getFlowElementInvoker(component), transitions);
        }
        else if(transition != null)
        {
            return new FlowElementImpl(name, component, getFlowElementInvoker(component), transition);
        }
        
        return new FlowElementImpl(name, component, getFlowElementInvoker(component));
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.beans.factory.FactoryBean#getObjectType()
     */
    public Class<FlowElement> getObjectType()
    {
        return FlowElement.class;
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.beans.factory.FactoryBean#isSingleton()
     */
    public boolean isSingleton()
    {
        return false;
    }

    /**
     * Get the correct instance of an invoker based on the component type.
     * @param component the component
     * @return a FlowElementInvoker for the given component type
     */
    protected FlowElementInvoker getFlowElementInvoker(COMPONENT component)
    {
        FlowElementInvoker<?> flowElementInvoker;
        if(component instanceof Consumer)
        {
            flowElementInvoker = new ConsumerFlowElementInvoker();
        }
        else if(component instanceof Translator)
        {
            flowElementInvoker = new TranslatorFlowElementInvoker();
        }
        else if(component instanceof Converter)
        {
            flowElementInvoker = new ConverterFlowElementInvoker();
        }
        else if(component instanceof Producer)
        {
            flowElementInvoker = new ProducerFlowElementInvoker();
        }
        else if(component instanceof Broker)
        {
            flowElementInvoker = new BrokerFlowElementInvoker();
        }
        else if(component instanceof Router)
        {
            if(flowElementInvokerConfiguration == null)
            {
                flowElementInvokerConfiguration = new MultiRecipientRouterConfiguration();
            }
            else
            {
                if( !(flowElementInvokerConfiguration instanceof MultiRecipientRouterConfiguration) )
                {
                    throw new IllegalArgumentException("Invalid MultiRecipientRouter FlowInvoker Configuration. Requires MultiRecipientRouterConfiguration, but found " + flowElementInvokerConfiguration.getClass().getName());
                }
            }

            flowElementInvoker = new MultiRecipientRouterFlowElementInvoker(DefaultReplicationFactory.getInstance(), (MultiRecipientRouterConfiguration)flowElementInvokerConfiguration);
        }
        else if(component instanceof MultiRecipientRouter)
        {
            if(flowElementInvokerConfiguration == null)
            {
                flowElementInvokerConfiguration = new MultiRecipientRouterConfiguration();
            }
            else
            {
                if( !(flowElementInvokerConfiguration instanceof MultiRecipientRouterConfiguration) )
                {
                    throw new IllegalArgumentException("Invalid MultiRecipientRouter FlowInvoker Configuration. Requires MultiRecipientRouterConfiguration, but found " + flowElementInvokerConfiguration.getClass().getName());
                }
            }

            flowElementInvoker = new MultiRecipientRouterFlowElementInvoker(DefaultReplicationFactory.getInstance(), (MultiRecipientRouterConfiguration)flowElementInvokerConfiguration);
        }
        else if(component instanceof SingleRecipientRouter)
        {
            flowElementInvoker = new SingleRecipientRouterFlowElementInvoker();
        }
        else if(component instanceof Sequencer)
        {
            flowElementInvoker = new SequencerFlowElementInvoker();
        }
        else if(component instanceof Splitter)
        {
            if(executorService != null)
            {
                flowElementInvoker = new ConcurrentSplitterFlowElementInvoker(executorService);
            }
            else if(concurrentThreads > 0)
            {
                flowElementInvoker = new ConcurrentSplitterFlowElementInvoker( Executors.newFixedThreadPool(this.concurrentThreads) );
            }
            else
            {
                flowElementInvoker = new SplitterFlowElementInvoker();
            }
        }
        else if(component instanceof Filter)
        {
            flowElementInvoker = new FilterFlowElementInvoker();
        }
        else
        {
            throw new RuntimeException("Unknown FlowComponent type[" + component.getClass() + "]");
        }
        flowElementInvoker.setIgnoreContextInvocation(ignoreContextInvocation);

        return flowElementInvoker;
    }
}