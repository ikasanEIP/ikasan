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

import java.util.LinkedHashMap;
import java.util.Map;

import org.ikasan.flow.configuration.FlowElementPersistentConfiguration;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.ikasan.spec.flow.FlowElement;
import org.ikasan.spec.flow.FlowElementConfiguration;
import org.ikasan.spec.flow.FlowElementInvoker;

/**
 * Simple implementation of <code>FlowElement</code>
 * 
 * @author Ikasan Development Team
 */
public class FlowElementImpl<COMPONENT> implements FlowElement<COMPONENT>, ConfiguredResource<FlowElementConfiguration>
{
    /** <code>FlowComponent</code> being wrapped and given flow context */
    private COMPONENT flowComponent;

    /** Flow context specific name for the wrapped component */
    private String componentName;

    /** <code>Map</code> of all flowComponent results to downstream <code>FlowElement</code>s */
    private Map<String, FlowElement> transitions;

    /** invoker for this flow element */
    private FlowElementInvoker<COMPONENT> flowElementInvoker;

    /**
     * Human readable description of this FlowElement
     */
    private String description;
    
    /** 
     * The flow element configuration.
     */
    private FlowElementConfiguration configuration = new FlowElementPersistentConfiguration();

    /**
     * The configured resource id.
     */
    private String configuredResourceId;

    /**
     * Constructor for when there are more than one subsequent <code>FlowElement</code>s
     * 
     * @param componentName The name of the component
     * @param flowComponent The FlowComponent
     * @param transitions A map of transitions
     */
    public FlowElementImpl(String componentName, COMPONENT flowComponent, FlowElementInvoker<COMPONENT> flowElementInvoker, Map<String, FlowElement> transitions)
    {
        this.componentName = componentName;
        this.flowComponent = flowComponent;
        this.flowElementInvoker = flowElementInvoker;
        this.transitions = transitions;
    }

    /**
     * Overloaded constructor for when there is at most one subsequent <code>FlowElement</code>
     * 
     * @param componentName The name of the component
     * @param flowComponent The FlowComponent
     * @param defaultTransition The default transition
     */
    public FlowElementImpl(String componentName, COMPONENT flowComponent, FlowElementInvoker<COMPONENT> flowElementInvoker, FlowElement defaultTransition)
    {
        this(componentName, flowComponent, flowElementInvoker, createTransitionMap(defaultTransition));
    }

    /**
     * Overloaded constructor for a <code>FlowElement</code> with no downstream
     * 
     * @param componentName The name of the component
     * @param flowComponent The FlowComponent
     */
    public FlowElementImpl(String componentName, COMPONENT flowComponent, FlowElementInvoker<COMPONENT> flowElementInvoker)
    {
        this(componentName, flowComponent, flowElementInvoker, (Map<String, FlowElement>) null);
    }

    /**
     * Creates the transition map when there is just the default transition
     * 
     * @param defaultTransition The default transition
     * @return Map<String, FlowElement> mapping "default" to the specified <code>FlowElement</code>
     */
    private static Map<String, FlowElement> createTransitionMap(FlowElement defaultTransition)
    {
        Map<String, FlowElement> defaultTransitions = new LinkedHashMap<>();
        defaultTransitions.put(DEFAULT_TRANSITION_NAME, defaultTransition);
        return defaultTransitions;
    }

    /*
     * (non-Javadoc)
     * 
     * @see flow.FlowElement#getFlowComponent()
     */
    public COMPONENT getFlowComponent()
    {
        return flowComponent;
    }

    /*
     * (non-Javadoc)
     * 
     * @see flow.FlowElement#getComponentName()
     */
    public String getComponentName()
    {
        return componentName;
    }

    /*
     * (non-Javadoc)
     * 
     * @see flow.FlowElement#getTransition(java.lang.String)
     */
    public FlowElement getTransition(String transitionName)
    {
        FlowElement result = null;
        if (transitions != null)
        {
            result = transitions.get(transitionName);
        }
        return result;
    }
    
    /* (non-Javadoc)
     * @see org.ikasan.framework.flow.FlowElement#getDescription()
     */
    public String getDescription()
    {
        return description;
    }

    /**
     * Setter for description
     * 
     * @param description
     */
    public void setDescription(String description)
    {
        this.description = description;
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder(getClass().getName() + " [");
        sb.append("name=");
        sb.append(componentName);
        sb.append(",");
        sb.append("flowComponent=");
        sb.append(flowComponent);
        sb.append(",");
        sb.append("transitions=");
        sb.append(transitions);
        sb.append("]");
        return sb.toString();
    }

    /**
     * Getter for the flow element invoker
     * @return
     */
    @Override
    public FlowElementInvoker<COMPONENT> getFlowElementInvoker()
    {
        return this.flowElementInvoker;
    }

    /**
     * Setter for the flow element invoker
     * @param flowElementInvoker
     */
    @Override
    public void setFlowElementInvoker(FlowElementInvoker<COMPONENT> flowElementInvoker)
    {
        this.flowElementInvoker = flowElementInvoker;
    }

    public Map<String, FlowElement> getTransitions()
    {
        Map<String, FlowElement> result = new LinkedHashMap<>();
        if (transitions != null)
        {
            result.putAll(transitions);
        }
        return result;
    }

	/* (non-Javadoc)
	 * @see org.ikasan.spec.configuration.Configured#getConfiguration()
	 */
	@Override
	public FlowElementConfiguration getConfiguration() 
	{
		return this.configuration;
	}

	/* (non-Javadoc)
	 * @see org.ikasan.spec.configuration.Configured#setConfiguration(java.lang.Object)
	 */
	@Override
	public void setConfiguration(FlowElementConfiguration configuration) 
	{
		this.configuration = configuration;
	}

	/* (non-Javadoc)
	 * @see org.ikasan.spec.configuration.ConfiguredResource#getConfiguredResourceId()
	 */
	@Override
	public String getConfiguredResourceId() 
	{
		return this.configuredResourceId;
	}

	/* (non-Javadoc)
	 * @see org.ikasan.spec.configuration.ConfiguredResource#setConfiguredResourceId(java.lang.String)
	 */
	@Override
	public void setConfiguredResourceId(String id) 
	{
		this.configuredResourceId = id;
	}
}
