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

import org.ikasan.flow.visitorPattern.InvalidFlowException;
import org.ikasan.spec.component.filter.Filter;
import org.ikasan.spec.flow.*;

/**
 * A default implementation of the FlowElementInvoker for a filter
 *
 * @author Ikasan Development Team
 */
@SuppressWarnings("unchecked")
public class FilterFlowElementInvoker extends AbstractFlowElementInvoker implements FlowElementInvoker<Filter>
{
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
            if (filter.filter(flowEvent.getPayload()) == null)
            {
                endFlowElementInvocation(flowElementInvocation, flowElement, flowEvent);
                if (flowInvocationContext.getFinalAction() == null
                        || flowInvocationContext.getFinalAction() != FinalAction.PUBLISH)
                {
                    flowInvocationContext.setFinalAction(FinalAction.FILTER);
                }
                return null;
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

}

