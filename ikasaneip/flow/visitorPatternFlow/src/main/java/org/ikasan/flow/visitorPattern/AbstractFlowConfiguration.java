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

import java.util.ArrayList;
import java.util.List;

import org.ikasan.flow.visitorPattern.invoker.InvokerConfiguration;
import org.ikasan.spec.configuration.ConfigurationService;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.ikasan.spec.error.reporting.IsErrorReportingServiceAware;
import org.ikasan.spec.flow.FlowElement;
import org.ikasan.spec.management.ManagedResource;
import org.ikasan.spec.management.ManagedService;
import org.ikasan.spec.replay.ReplayRecordService;
import org.ikasan.spec.resubmission.ResubmissionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract implementation of a flow configuration
 * 
 * @author Ikasan Development Team
 */
public abstract class AbstractFlowConfiguration
{
    /** logger */
    private static Logger logger = LoggerFactory.getLogger(AbstractFlowConfiguration.class);

    /** handle on the lead flow element */
    protected FlowElement leadFlowElement;

    /** managed resources within the flow */
    protected List<FlowElement<ManagedResource>> managedReourceFlowElements =
        new ArrayList<FlowElement<ManagedResource>>();

    /** managed services within the flow */
    protected List<ManagedService> managedServices = new ArrayList<ManagedService>();

    /** configured resources within the Ikasan framework space */
    protected List<FlowElement<?>> flowElementInvokerConfiguredResources =
            new ArrayList<FlowElement<?>>();

    /** configured resources within the flow as depicted by the developer */
    protected List<FlowElement<ConfiguredResource>> configuredReourceFlowElements =
        new ArrayList<FlowElement<ConfiguredResource>>();

    /** errorReportingServiceAware resources within the flow */
    protected List<FlowElement<IsErrorReportingServiceAware>> errorReportingServiceAwareFlowElements =
            new ArrayList<FlowElement<IsErrorReportingServiceAware>>();

    /** handle to configuration service */
    protected ConfigurationService configurationService;
    
    /** handle to the re-submission service */
    protected ResubmissionService resubmissionService;
    
    /** handle to the replay service */
    protected ReplayRecordService replayRecordService;

    /**
     * Constructor
     * 
     * @param leadFlowElement
     * @param configurationService
     * @param resubmisionService
     */
    public AbstractFlowConfiguration(FlowElement leadFlowElement, ConfigurationService configurationService,
    		ResubmissionService resubmisionService, ReplayRecordService replayRecordService)
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
        
        // It is possible that the re-submission service is NULL.
        this.resubmissionService = resubmisionService;
        
        // It is possible that the replayRecordService is NULL.
        this.replayRecordService = replayRecordService;
        
        for(FlowElement flowElement:getFlowElements())
        {
            if(flowElement.getFlowElementInvoker() instanceof ManagedService)
            {
                this.managedServices.add( (ManagedService)flowElement.getFlowElementInvoker() );
            }

            if(flowElement.getFlowElementInvoker() instanceof ConfiguredResource)
            {
                this.flowElementInvokerConfiguredResources.add(flowElement);
            }

            Object flowComponent = flowElement.getFlowComponent();
            if(flowComponent instanceof ManagedResource)
            {
                this.managedReourceFlowElements.add(flowElement);
            }
            if(flowComponent instanceof ConfiguredResource)
            {
                this.configuredReourceFlowElements.add(flowElement);
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

    public List<FlowElement<?>> getFlowElementInvokerConfiguredResources()
    {
        return this.flowElementInvokerConfiguredResources;
    }

    public List<ManagedService> getManagedServices()
    {
        return this.managedServices;
    }

    public List<FlowElement<ConfiguredResource>> getDynamicConfiguredResourceFlowElements()
    {
        List<FlowElement<ConfiguredResource>> flowElements = new ArrayList<FlowElement<ConfiguredResource>>();

        for(FlowElement flowElement:flowElementInvokerConfiguredResources)
        {
            try
            {
                // read from persistence and configure if available
                ConfiguredResource configuredResource = (ConfiguredResource)flowElement.getFlowElementInvoker();
                this.configure(configuredResource);

                // is this dynamically configured
                if( flowElement.getFlowComponent() instanceof ConfiguredResource &&
                    ((ConfiguredResource<InvokerConfiguration>)flowElement.getFlowElementInvoker())
                        .getConfiguration().isDynamicConfiguration() )
                {
                    flowElements.add(flowElement);
                }
            }
            catch(RuntimeException e)
            {
                logger.error("Unable to determine if ConfiguredResource has DynamicConfiguration.", e);
            }

        }

        return flowElements;
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
    
  
    public void update(ConfiguredResource configuredResource)
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

	/**
	 * @return the resubmissionService
	 */
	public ResubmissionService getResubmissionService()
	{
		return resubmissionService;
	}

	/**
	 * @return the replayRecordService
	 */
	public ReplayRecordService getReplayRecordService() 
	{
		return replayRecordService;
	}

}
