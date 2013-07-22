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

import org.apache.log4j.Logger;
import org.ikasan.consumer.jms.GenericJmsConsumer;
import org.ikasan.flow.visitorPattern.FlowElementImpl;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.ikasan.spec.flow.FlowElement;
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
    
    /** identifier if the cmoponent supported ConfiguredResource */
    String configuredResourceId;
    
    /** identifier if the cmoponent supported ConfiguredResource */
    CONFIGURATION configuration;
    
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
     * Setter for the actual component configuration
     * @param configuration
     */
    public void setConfiguration(CONFIGURATION configuration)
    {
        this.configuration = configuration;
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
            return new FlowElementImpl(name, component, transitions);
        }
        else if(transition != null)
        {
            return new FlowElementImpl(name, component, transition);
        }
        
        return new FlowElementImpl(name, component);
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
}