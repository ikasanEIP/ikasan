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

import org.ikasan.spec.configuration.ConfigurationService;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.ikasan.spec.configuration.DynamicConfiguredResource;
import org.ikasan.spec.error.reporting.IsErrorReportingServiceAware;
import org.ikasan.spec.flow.FlowElement;
import org.ikasan.spec.management.ManagedResource;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract implementation of a flow configuration
 * 
 * @author Ikasan Development Team
 */
public class AbstractFlowConfiguration
{
    /** handle on the lead flow element */
    protected FlowElement leadFlowElement;

    /** managed resources within the flow */
    protected List<FlowElement<ManagedResource>> managedReourceFlowElements =
        new ArrayList<FlowElement<ManagedResource>>();

    /** configured resources within the flow */
    protected List<FlowElement<ConfiguredResource>> configuredReourceFlowElements =
        new ArrayList<FlowElement<ConfiguredResource>>();

    /** dynamically configured resources within the flow */
    protected List<FlowElement<DynamicConfiguredResource>> dynamicConfiguredReourceFlowElements =
        new ArrayList<FlowElement<DynamicConfiguredResource>>();

    /** errorReportingServiceAware resources within the flow */
    protected List<FlowElement<IsErrorReportingServiceAware>> errorReportingServiceAwareFlowElements =
            new ArrayList<FlowElement<IsErrorReportingServiceAware>>();

    /** handle to configuration service */
    protected ConfigurationService configurationService;

    /**
     * Constructor
     * @param leadFlowElement
     * @param configurationService
     */
    public AbstractFlowConfiguration(FlowElement leadFlowElement, ConfigurationService configurationService)
    {
        this.leadFlowElement = leadFlowElement;
        if(leadFlowElement == null)
        {
            throw new IllegalArgumentException("leadFlowElement cannot be 'null'");
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
            if(flowComponent instanceof IsErrorReportingServiceAware)
            {
                this.errorReportingServiceAwareFlowElements.add(flowElement);
            }
        }
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

    public List<FlowElement<IsErrorReportingServiceAware>> getErrorReportingServiceAwareFlowElements()
    {
        return this.errorReportingServiceAwareFlowElements;
    }

    public void configure(ConfiguredResource configuredResource)
    {
        this.configurationService.configure(configuredResource);
    }
    
  
    public void update(DynamicConfiguredResource configuredResource)
    {
        this.configurationService.update(configuredResource);
    }

    public List<FlowElement<?>> getFlowElements()
    {
        List<FlowElement<?>> result = new ArrayList<FlowElement<?>>();
        List<FlowElement<?>> elementsToVisit = new ArrayList<FlowElement<?>>();
        elementsToVisit.add(this.leadFlowElement);
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
