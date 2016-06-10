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
package org.ikasan.spec.flow;

import java.util.Map;

import org.ikasan.spec.configuration.ConfiguredResource;

/**
 * FlowElement represents a particular unique usage of an operational component within a flow.
 * <code>FlowElement</code>s wrap components, providing them with a context specific name. They also
 * define any transitions from this point in flow, this is a mapping of the various potential results that a
 * component may return to subsequent (downstream) <code>FlowElement<code>s
 * 
 * @author Ikasan Development Team
 */
public interface FlowElement<COMPONENT> extends ConfiguredResource<FlowElementConfiguration>
{
    /** Name of the default transition for components that have a unique result */
    public static final String DEFAULT_TRANSITION_NAME = "default";

    /** Name of the subFlow transition for components that have a unique result */
    public static final String SUBFLOW_TRANSITION_NAME = "subFlow";

    /**
     * Accessor for the wrapped component.
     * 
     * @return COMPONENT
     */
    public abstract COMPONENT getFlowComponent();

    /**
     * Accessor for the componentName. This is the unique identifier for a <code>FlowElement</code>
     * 
     * @return componentName
     */
    public abstract String getComponentName();

    /**
     * Retrieves the subsequent FlowElement (if any) representing the next node in the flow
     * 
     * @param transitionName - this value should be a member of the set of possible results returnable from the wrapped
     *            component
     * @return FlowElement representing the next node in the flow
     */
    public abstract FlowElement getTransition(String transitionName);

    /**
     * Retrieves a Map of all this FlowElement's transitions
     * 
     * @return a Map of all this FlowElement's transitions
     */
    public Map<String, FlowElement> getTransitions();
    
    /**
     * Returns a human readable description of this FlowElement
     * 
     * @return String description
     */
    public String getDescription();

    /**
     * Getter for the flow element invoker.
     * @return flowElementInvoker
     */
    public FlowElementInvoker getFlowElementInvoker();

    /**
     * Setter for the flow element invoker.
     * @param flowElementInvoker
     */
    public void setFlowElementInvoker(FlowElementInvoker<COMPONENT> flowElementInvoker);
}
