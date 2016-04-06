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
import org.ikasan.spec.component.transformation.Converter;
import org.ikasan.spec.flow.*;

/**
 * A default implementation of the FlowElementInvoker for a converter
 *
 * @author Ikasan Development Team
 */
@SuppressWarnings("unchecked")
public class ConverterFlowElementInvoker extends AbstractFlowElementInvoker implements FlowElementInvoker<Converter>
{
    /** does this broker require the full flowEvent or just the payload */
    Boolean requiresFullEventForInvocation;

    @Override
    public FlowElement invoke(FlowEventListener flowEventListener, String moduleName, String flowName, FlowInvocationContext flowInvocationContext, FlowEvent flowEvent, FlowElement<Converter> flowElement)
    {
        notifyListenersBeforeElement(flowEventListener, moduleName, flowName, flowEvent, flowElement);
        FlowElementInvocation<Object,?> flowElementInvocation = beginFlowElementInvocation(flowInvocationContext, flowElement, flowEvent);

        Converter converter = flowElement.getFlowComponent();
        setInvocationOnComponent(flowElementInvocation, converter);
        // we must unset the context whatever happens, so try/finally
        try
        {
            if (requiresFullEventForInvocation == null)
            {
                try
                {
                    // try with flowEvent and if successful mark this producer
                    // IKASAN-706 Simple fix for Converter that returns a FlowEvent object
                    converter.getClass().getMethod("convert", FlowEvent.class);
                    Object o = converter.convert(flowEvent);
                    if (o instanceof FlowEvent)
                    {
                        FlowEvent converterFlowEvent = (FlowEvent) o;
                        flowEvent.setPayload(converterFlowEvent.getPayload());
                    }
                    else
                    {
                        flowEvent.setPayload(o);
                    }
                    requiresFullEventForInvocation = Boolean.TRUE;
                }
                catch (java.lang.NoSuchMethodException e)
                {
                    flowEvent.setPayload(converter.convert(flowEvent.getPayload()));
                    requiresFullEventForInvocation = Boolean.FALSE;
                }
            }
            else
            {
                if (requiresFullEventForInvocation)
                {
                    // IKASAN-706 Simple fix for Broker that returns a FlowEvent object
                    Object o = converter.convert(flowEvent);
                    if (o instanceof FlowEvent)
                    {
                        FlowEvent converterFlowEvent = (FlowEvent) o;
                        flowEvent.setPayload(converterFlowEvent.getPayload());
                    }
                    else
                    {
                        flowEvent.setPayload(o);
                    }
                }
                else
                {
                    flowEvent.setPayload(converter.convert(flowEvent.getPayload()));
                }
            }
        }
        finally
        {
            unsetInvocationOnComponent(flowElementInvocation, converter);
        }
        endFlowElementInvocation(flowElementInvocation, flowElement, flowEvent);
        notifyListenersAfterElement(flowEventListener, moduleName, flowName, flowEvent, flowElement);
        // sort out the next element
        FlowElement previousFlowElement = flowElement;
        flowElement = getDefaultTransition(flowElement);
        if (flowElement == null)
        {
            throw new InvalidFlowException("FlowElement [" + previousFlowElement.getComponentName()
                    + "] contains a Converter, but it has no default transition! " + "Converters should never be the last component in a flow");
        }
        return flowElement;
    }

}

