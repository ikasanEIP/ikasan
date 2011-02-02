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

import org.apache.log4j.Logger;
import org.ikasan.spec.configuration.ConfigurationException;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.ikasan.spec.configuration.service.ConfigurationService;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.flow.FlowElement;
import org.ikasan.spec.flow.FlowElementInvoker;
import org.ikasan.spec.flow.FlowEvent;
import org.ikasan.spec.flow.FlowInvocationContext;
import org.ikasan.spec.management.ManagedResource;

/**
 * Default implementation of a Flow
 * 
 * @author Ikasan Development Team
 */
public class VisitingInvokerFlow implements Flow
{
    /** The logger instance. */
    private static Logger logger = Logger.getLogger(VisitingInvokerFlow.class);
    /**
     * Name of this flow
     */
    private String name;

    /**
     * Name of the module within which this flowExists
     */
    private String moduleName;

    /**
     * The first element in this flow
     */
    private FlowElement headElement;

    /**
     * Invoker for invoking this flow
     */
    private FlowElementInvoker flowElementInvoker;

    /** configuration service for this flow */
    @SuppressWarnings("unchecked")
    private ConfigurationService configurationService;
    
    /**
     * Constructor
     * 
     * @param name - name of this flow
     * @param moduleName - name of the module containing this flow
     * @param headElement - first element in the flow
     * @param visitingInvoker - invoker for this flow
     */
    @SuppressWarnings("unchecked")
    public VisitingInvokerFlow(String name, String moduleName, FlowElement headElement,
            FlowElementInvoker flowElementInvoker)
    {
        this.name = name;
        if(name == null)
        {
            throw new IllegalArgumentException("name cannot be 'null'");
        }
        
        this.moduleName = moduleName;
        if(moduleName == null)
        {
            throw new IllegalArgumentException("moduleName cannot be 'null'");
        }
        
        this.headElement = headElement;
        this.flowElementInvoker = flowElementInvoker;
    }

    @SuppressWarnings("unchecked")
    public void setConfigurationService(ConfigurationService configurationService)
    {
        this.configurationService = configurationService;
    }
    
    @SuppressWarnings("unchecked")
    public ConfigurationService getConfigurationService()
    {
        return this.configurationService;
    }
    
    public String getName()
    {
        return name;
    }
    

    /* (non-Javadoc)
     * @see org.ikasan.framework.flow.Flow#invoke(org.ikasan.framework.flow.FlowInvocationContext, org.ikasan.framework.component.Event)
     */
    public void invoke(FlowInvocationContext flowInvocationContext, FlowEvent flowEvent)
    {
        flowElementInvoker.invoke(flowInvocationContext, flowEvent, moduleName, name, headElement);
    }

    /**
     * Returns a breadth first listing of the flowElements within this flow
     * 
     * @return List<FlowElement>
     */
    public List<FlowElement<?>> getFlowElements()
    {
        List<FlowElement<?>> result = new ArrayList<FlowElement<?>>();
        List<FlowElement<?>> elementsToVisit = new ArrayList<FlowElement<?>>();
        elementsToVisit.add(headElement);
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
     * Set the module name
     * 
     * @param moduleName The name of the module to set
     */
    public void setModuleName(String moduleName)
    {
        this.moduleName = moduleName;
    }

    public String getModuleName()
    {
        return moduleName;
    }

    /* (non-Javadoc)
     * @see org.ikasan.framework.flow.Flow#start()
     */
    @SuppressWarnings("unchecked")
    public void start()
    {
        for(FlowElement flowElement:this.getFlowElements())
        {
            Object flowComponent = flowElement.getFlowComponent();

            // configure any components marked as configured resources
            if(flowComponent instanceof ConfiguredResource)
            {
                if(this.configurationService == null)
                {
                    throw new ConfigurationException("Component " + flowElement.getComponentName() +
                            " marked as a 'ConfiguredResource', but the configurationService has not been set on the module "
                            + this.moduleName + " flow " + this.name);
                }
                this.configurationService.configure((ConfiguredResource)flowComponent);
            }

            // start any component resources marked as managed resources
            if(flowComponent instanceof ManagedResource)
            {
                try
                {
                    ((ManagedResource)flowComponent).startManagedResource();
                }
                catch(RuntimeException e)
                {
                    logger.warn("Failed to start managed resource in component [" 
                            + flowElement.getComponentName() + "] " + e.getMessage(), e);
                }
            }
        }
    }

    /* (non-Javadoc)
     * @see org.ikasan.framework.flow.Flow#stop()
     */
    public void stop()
    {
        for(FlowElement flowElement:this.getFlowElements())
        {
            Object flowComponent = flowElement.getFlowComponent();
            if(flowComponent instanceof ManagedResource)
            {
                try
                {
                    ((ManagedResource)flowComponent).stopManagedResource();
                }
                catch(RuntimeException e)
                {
                    logger.warn("Failed to stop managed resource in component [" 
                            + flowElement.getComponentName() + "] " + e.getMessage(), e);
                }
            }
        }
    }

}
