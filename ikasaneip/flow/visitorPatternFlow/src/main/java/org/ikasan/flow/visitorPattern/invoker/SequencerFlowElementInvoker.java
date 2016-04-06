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
import org.ikasan.spec.component.sequencing.Sequencer;
import org.ikasan.spec.flow.*;

import java.util.List;

/**
 * A default implementation of the FlowElementInvoker for a sequencer
 *
 * @author Ikasan Development Team
 */
@SuppressWarnings("unchecked")
public class SequencerFlowElementInvoker extends AbstractFlowElementInvoker implements FlowElementInvoker<Sequencer>
{
    @Override
    public FlowElement invoke(FlowEventListener flowEventListener, String moduleName, String flowName, FlowInvocationContext flowInvocationContext, FlowEvent flowEvent, FlowElement<Sequencer> flowElement)
    {
        notifyListenersBeforeElement(flowEventListener, moduleName, flowName, flowEvent, flowElement);
        FlowElementInvocation flowElementInvocation = beginFlowElementInvocation(flowInvocationContext, flowElement, flowEvent);

        Sequencer sequencer = flowElement.getFlowComponent();
        setInvocationOnComponent(flowElementInvocation, sequencer);
        // we must unset the context whatever happens, so try/finally
        List payloads;
        try
        {
            payloads = sequencer.sequence(flowEvent.getPayload());
        }
        finally
        {
            unsetInvocationOnComponent(flowElementInvocation, sequencer);
        }
        endFlowElementInvocation(flowElementInvocation, flowElement, flowEvent);

        FlowElement nextFlowElement = getDefaultTransition(flowElement);
        if (nextFlowElement == null)
        {
            throw new InvalidFlowException("FlowElement [" + flowElement.getComponentName() + "] contains a Sequencer, but it has no default transition! "
                    + "Sequencers should never be the last component in a flow");
        }
        if (payloads != null)
        {
            for (Object payload : payloads)
            {
                flowEvent.setPayload(payload);
                notifyListenersAfterElement(flowEventListener, moduleName, flowName, flowEvent, flowElement);

                FlowElement nextFlowElementInRoute = nextFlowElement;
                while (nextFlowElementInRoute != null)
                {
                    nextFlowElementInRoute = nextFlowElementInRoute.getFlowElementInvoker().invoke(flowEventListener, moduleName, flowName, flowInvocationContext, flowEvent, nextFlowElementInRoute);
                }
            }
        }
        return null;
    }

}

