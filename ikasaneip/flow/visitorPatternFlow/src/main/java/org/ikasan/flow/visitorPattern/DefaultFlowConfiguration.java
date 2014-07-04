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
package org.ikasan.flow.visitorPattern;

import org.ikasan.spec.component.endpoint.Consumer;
import org.ikasan.spec.configuration.ConfigurationService;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.ikasan.spec.configuration.DynamicConfiguredResource;
import org.ikasan.spec.flow.FlowElement;
import org.ikasan.spec.management.ManagedResource;

import java.util.ArrayList;
import java.util.List;

/**
 * Default implementation of a Flow
 * 
 * @author Ikasan Development Team
 */
public class DefaultFlowConfiguration implements FlowConfiguration
{
    private FlowElement<Consumer> consumerFlowElement;

    private FlowElement<?> leadFlowElement;

    /** */
    private List<FlowElement<ManagedResource>> managedReourceFlowElements =
        new ArrayList<FlowElement<ManagedResource>>();
    
    /** */
    private List<FlowElement<ConfiguredResource>> configuredReourceFlowElements =
        new ArrayList<FlowElement<ConfiguredResource>>();
    
    /** */
    private List<FlowElement<DynamicConfiguredResource>> dynamicConfiguredReourceFlowElements =
        new ArrayList<FlowElement<DynamicConfiguredResource>>();

    /** list of components */
    List<FlowElement<?>> flowElements = new ArrayList<FlowElement<?>>();
    
    private ConfigurationService configurationService;
    
    public DefaultFlowConfiguration(FlowElement<Consumer> consumerFlowElement, ConfigurationService configurationService)
    {
        this.consumerFlowElement = consumerFlowElement;
        if(consumerFlowElement == null)
        {
            throw new IllegalArgumentException("consumerFlowElement cannot be 'null'");
        }
        
        this.leadFlowElement = consumerFlowElement.getTransition(FlowElement.DEFAULT_TRANSITION_NAME);
        if(leadFlowElement == null)
        {
            throw new IllegalArgumentException("consumerFlowElement must have a valid flowElement default transition");
        }

        this.configurationService = configurationService;
        if(configurationService == null)
        {
            throw new IllegalArgumentException("configurationService cannot be 'null'");
        }
        
        for(FlowElement flowElement:getFlowElements())
        {
            Object flowComponent = flowElement.getFlowComponent();
            if(flowComponent instanceof ManagedResource)
            {
                this.managedReourceFlowElements.add(flowElement);
            }
            if(flowComponent instanceof ConfiguredResource)
            {
                this.configuredReourceFlowElements.add(flowElement);
            }
            if(flowComponent instanceof DynamicConfiguredResource)
            {
                this.dynamicConfiguredReourceFlowElements.add(flowElement);
            }
        }
    }

    public FlowElement<Consumer> getConsumerFlowElement()
    {
        return this.consumerFlowElement;
    }

    public List<FlowElement<ConfiguredResource>> getConfiguredResourceFlowElements()
    {
        return this.configuredReourceFlowElements;
    }

    public List<FlowElement<DynamicConfiguredResource>> getDynamicConfiguredResourceFlowElements()
    {
        return this.dynamicConfiguredReourceFlowElements;
    }

    public List<FlowElement<ManagedResource>> getManagedResourceFlowElements()
    {
        return this.managedReourceFlowElements;
    }
    
    public void configure(ConfiguredResource configuredResource)
    {
        this.configurationService.configure(configuredResource);
    }

    public List<FlowElement<?>> getFlowElements()
    {
        List<FlowElement<?>> result = new ArrayList<FlowElement<?>>();
        List<FlowElement<?>> elementsToVisit = new ArrayList<FlowElement<?>>();
        elementsToVisit.add(this.consumerFlowElement);
        while (!elementsToVisit.isEmpty())
        {
            FlowElement<?> thisFlowElement = elementsToVisit.get(0);
            elementsToVisit.remove(0);
            if (!result.contains(thisFlowElement))
            {
                result.add(thisFlowElement);
            }
            for (FlowElement<?> subsequentElement : thisFlowElement.getTransitions().values())
            {
                if (!result.contains(subsequentElement))
                {
                    elementsToVisit.add(subsequentElement);
                }
            }
        }
        return result;
    }

}
