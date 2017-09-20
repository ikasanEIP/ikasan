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
package org.ikasan.flow.context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ikasan.spec.flow.FlowElement;
import org.ikasan.spec.flow.FlowElementInvocation;
import org.ikasan.spec.flow.FlowEvent;
import org.ikasan.spec.flow.FlowInvocationContext;
import org.ikasan.spec.flow.FlowInvocationContextListener;

import java.util.List;

/**
 * Simple Listener that will log the FlowInvocationContext component timing statistics
 *
 * @author Ikasan Development Team
 */
public class ComponentTimingLoggingListener implements FlowInvocationContextListener
{
    // protected so we can override the logger in the test
    protected static Logger logger = LoggerFactory.getLogger(ComponentTimingLoggingListener.class);

    @Override
    public void endFlow(FlowInvocationContext flowInvocationContext)
    {
        if (flowInvocationContext.getElementInvocations() != null && !flowInvocationContext.getElementInvocations().isEmpty())
        {
            StringBuilder sb = new StringBuilder("Flow Invocation: ");
            List<FlowElementInvocation> invocations = flowInvocationContext.getElementInvocations();
            Object identifier = invocations.get(invocations.size() - 1).getBeforeIdentifier();
            sb.append("ID [").append(identifier).append("] ");
            // log start and end epoch times
            sb.append("Start [").append(flowInvocationContext.getFlowStartTimeMillis()).append("] ");
            sb.append("End [").append(flowInvocationContext.getFlowEndTimeMillis()).append("] ");

            for (FlowElementInvocation invocation : invocations)
            {
                // log each element timing
                sb.append("[Element [").append(invocation.getFlowElement().getComponentName()).append("]");
                sb.append(" Time [").append(invocation.getEndTimeMillis() - invocation.getStartTimeMillis()).append("ms]");
                if (invocation.getCustomMetrics() instanceof List)
                {
                    for (Object customMetric : (List)invocation.getCustomMetrics())
                    {
                        sb.append(" CustomMetric [").append(customMetric).append("]");
                    }
                }
                sb.append("] ");
            }
            logger.info(sb.toString());
        }

    }

	/* (non-Javadoc)
	 * @see org.ikasan.spec.flow.FlowInvocationContextListener#snapEvent(org.ikasan.spec.flow.FlowElement, org.ikasan.spec.flow.FlowEvent, long)
	 */
	@Override
	public void snapEvent(FlowElement flowElement, FlowEvent event) 
	{
		// No implementation provided for this at them moment as events will fill log files.
	}
}
