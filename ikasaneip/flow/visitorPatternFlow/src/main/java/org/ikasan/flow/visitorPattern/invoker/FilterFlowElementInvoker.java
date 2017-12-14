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
package org.ikasan.flow.visitorPattern.invoker;

import org.hibernate.internal.FilterConfiguration;
import org.ikasan.flow.visitorPattern.InvalidFlowException;
import org.ikasan.spec.component.filter.Filter;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.ikasan.spec.flow.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A default implementation of the FlowElementInvoker for a filter
 *
 * @author Ikasan Development Team
 */
@SuppressWarnings("unchecked")
public class FilterFlowElementInvoker extends AbstractFlowElementInvoker implements FlowElementInvoker<Filter>, ConfiguredResource<FilterInvokerConfiguration>
{
    /** Logger for this class */
    private static Logger logger = LoggerFactory.getLogger(FilterFlowElementInvoker.class);

    // unique id for this invoker configuration
    private String configuredResourceId;

    // configuration instance
    private FilterInvokerConfiguration configuration = new FilterInvokerConfiguration();

    @Override
    public String getInvokerType()
    {
        return FlowElementInvoker.FILTER;
    }

    @Override
    public FlowElement invoke(FlowEventListener flowEventListener, String moduleName, String flowName, FlowInvocationContext flowInvocationContext, FlowEvent flowEvent, FlowElement<Filter> flowElement)
    {
        notifyListenersBeforeElement(flowEventListener, moduleName, flowName, flowEvent, flowElement);
        FlowElementInvocation<Object, ?> flowElementInvocation = beginFlowElementInvocation(flowInvocationContext, flowElement, flowEvent);

        Filter filter = flowElement.getFlowComponent();
        setInvocationOnComponent(flowElementInvocation, filter);

        // we must unset the context whatever happens, so try/finally
        try
        {
            notifyFlowInvocationContextListenersSnapEvent(flowElement, flowEvent);

            if(this.configuration.isApplyFilter())
            {
                if (filter.filter(flowEvent.getPayload()) == null)
                {
                    if(this.configuration.isLogFiltered())
                    {
                        logger.info("Filtered FlowEvent Identifier[" + flowEvent.getIdentifier()
                                + "] + timestamp[" + flowEvent.getTimestamp()
                                + "] + payload[" + ((flowEvent.getPayload() != null) ? flowEvent.getPayload().toString() : "null") + "]");
                    }

                    endFlowElementInvocation(flowElementInvocation, flowElement, flowEvent);
                    if (flowInvocationContext.getFinalAction() == null
                            || flowInvocationContext.getFinalAction() != FinalAction.PUBLISH) {
                        flowInvocationContext.setFinalAction(FinalAction.FILTER);
                    }
                    return null;
                }
            }
        }
        finally
        {
            unsetInvocationOnComponent(flowElementInvocation, filter);
            endFlowElementInvocation(flowElementInvocation, flowElement, flowEvent);
        }

        notifyListenersAfterElement(flowEventListener, moduleName, flowName, flowEvent, flowElement);

        // sort out the next element
        FlowElement previousFlowElement = flowElement;
        flowElement = getDefaultTransition(flowElement);
        if (flowElement == null)
        {
            throw new InvalidFlowException("FlowElement [" + previousFlowElement.getComponentName()
                    + "] contains a Filter, but it has no default transition! " + "Filters should never be the last component in a flow");
        }
        return flowElement;
    }

    @Override
    public String getConfiguredResourceId()
    {
        return this.configuredResourceId;
    }

    @Override
    public void setConfiguredResourceId(String configuredResourceId)
    {
        this.configuredResourceId = configuredResourceId;
    }

    @Override
    public FilterInvokerConfiguration getConfiguration()
    {
        return this.configuration;
    }

    @Override
    public void setConfiguration(FilterInvokerConfiguration configuration)
    {
        this.configuration = configuration;
    }
}

