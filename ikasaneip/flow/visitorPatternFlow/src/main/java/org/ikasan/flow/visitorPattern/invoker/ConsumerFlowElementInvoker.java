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
import org.ikasan.spec.component.endpoint.Consumer;
import org.ikasan.spec.component.transformation.Converter;
import org.ikasan.spec.flow.FlowElement;
import org.ikasan.spec.flow.FlowElementInvoker;
import org.ikasan.spec.flow.FlowEvent;
import org.ikasan.spec.flow.FlowEventListener;
import org.ikasan.spec.flow.FlowInvocationContext;
import org.ikasan.spec.replay.ReplayRecordService;

/**
 * A default implementation of the FlowElementInvoker for a consumer
 *
 * @author Ikasan Development Team
 */
@SuppressWarnings("unchecked")
public class ConsumerFlowElementInvoker extends AbstractFlowElementInvoker implements FlowElementInvoker<Consumer>
{
    /** do we have a converter in this consumer */
    Boolean hasConverter;

    /** handle to any internal converter for this consumer */
    Converter converter;
    
    
    	
    /**
     * Constructor
     * 
     * @param replayRecordService
     */
    public ConsumerFlowElementInvoker() 
    {
		super();
	}

	@Override
    public FlowElement invoke(FlowEventListener flowEventListener, String moduleName, String flowName, FlowInvocationContext flowInvocationContext, FlowEvent flowEvent, FlowElement<Consumer> flowElement)
    {
        flowInvocationContext.addInvokedComponentName(flowElement.getComponentName());
        notifyListenersBeforeElement(flowEventListener, moduleName, flowName, flowEvent, flowElement);

        if(hasConverter == null)
        {
            Consumer consumer = flowElement.getFlowComponent();
            converter = getAsConverter(consumer);
            if(converter == null)
            {
                hasConverter = Boolean.FALSE;
            }
            else
            {
                hasConverter = Boolean.TRUE;
            }
        }

        if(hasConverter)
        {
            flowEvent.setPayload(converter.convert(flowEvent.getPayload()));
        }

        notifyListenersAfterElement(flowEventListener, moduleName, flowName, flowEvent, flowElement);
        // sort out the next element
        FlowElement previousFlowElement = flowElement;
        flowElement = getDefaultTransition(flowElement);
        if (flowElement == null)
        {
            throw new InvalidFlowException("FlowElement [" + previousFlowElement.getComponentName()
                    + "] contains a Consumer, but it has no default transition! " + "Consumers should never be the last component in a flow");
        }

        return flowElement;
    }

    /**
     * method to aid testing
     * @param consumer
     * @return
     */
    protected Converter getAsConverter(Consumer consumer)
    {
        if(consumer instanceof Converter)
        {
            return ((Converter)consumer);
        }

        return null;
    }
}

