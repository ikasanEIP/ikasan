/* 
 * $Id: 
 * $URL: 
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

import org.ikasan.spec.flow.FlowElementInvocation;
import org.ikasan.spec.flow.FlowInvocationContext;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * This class acts as a transfer object holding flow invocation time data relevant only
 * to a single invocation of an Event down a Flow. 
 *
 * Unlike the FlowEvent object, the same FlowInvocation object will be present immediately prior
 * to the invocation of any component in a flow. The FlowEvents of course may be split, aggregated, etc.
 *
 * It remains to be seen if we will need to transport any other data in this object, of if at some
 * later stage, the FlowComponents themselves will need access to this information
 *
 * @author Ikasan Development Team
 *
 */
public class DefaultFlowInvocationContext implements FlowInvocationContext
{
    /** a LinkedList of all the invocations for the current event in this Flow */
    private LinkedList<FlowElementInvocation> invocations = new LinkedList<>();

    /** the epoch time the flow was started by this event */
    private long flowStartTimeMillis;

    /** the epoch time the flow completed */
    private long flowEndTimeMillis;

    /** this is the last invoked component only in the case the invoker is ignoring context invocations */
    private String lastComponentName;

    /**
     * Accessor for the name of the last component invoked
     *
     * @return name of the last component invoked, or null if none exists yet
     */
    public String getLastComponentName()
    {
        if (this.lastComponentName != null)
        {
            return this.lastComponentName;
        }
        String componentName = null;
        if (!invocations.isEmpty())
        {
            componentName = invocations.getLast().getFlowElement().getComponentName();
        }

        return componentName;
    }

    @Override
    public void setLastComponentName(String componentName)
    {
        this.lastComponentName = componentName;
    }

    @Override
    public void addInvocation(FlowElementInvocation flowElementInvocation) {
        invocations.add(flowElementInvocation);
    }

    @Override
    public List<FlowElementInvocation> getInvocations() {
        return invocations;
    }

    @Override
    public void startFlow() {
        this.flowStartTimeMillis = System.currentTimeMillis();
    }

    @Override
    public void endFlow() {
        this.flowEndTimeMillis = System.currentTimeMillis();
    }

    @Override
    public long getFlowStartTimeMillis() {
        return flowStartTimeMillis;
    }

    @Override
    public long getFlowEndTimeMillis() {
        return flowEndTimeMillis;
    }

    @Override
	public void combine(FlowInvocationContext flowInvocationContext)
	{
        invocations.addAll(flowInvocationContext.getInvocations());
	}

}
