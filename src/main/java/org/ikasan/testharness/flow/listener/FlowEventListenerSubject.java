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
package org.ikasan.testharness.flow.listener;

import java.util.ArrayList;
import java.util.List;

import org.ikasan.framework.component.Event;
import org.ikasan.framework.flow.FlowElement;
import org.ikasan.framework.flow.event.listener.FlowEventListener;
import org.ikasan.testharness.flow.FlowObserver;
import org.ikasan.testharness.flow.FlowSubject;


/**
 * Captures all component and event activity in this flow by implementing 
 * the Ikasan Framework FlowEventListener. 
 * 
 * The FlowSubject implementation states that this class will be observable
 * by one or more registered observers.
 *
 * @author Ikasan Development Team 
 *
 */
public class FlowEventListenerSubject
    implements FlowEventListener, FlowSubject
{
    /** observer to be notified of flow component invocations */
    private List<FlowObserver> flowObservers;

    /**
     * Constructor
     */
    public FlowEventListenerSubject()
    {
        this.flowObservers = initFlowObservers();
    }
    
    /**
     * Utility method for initialising the flow observers list implemented 
     * for ease of testing.
     * @return
     */
    protected List<FlowObserver> initFlowObservers()
    {
        return new ArrayList<FlowObserver>();
    }
    
    /**
     * Unsupported as we don't care about afterFlow operations in this implementation.
     * @param String moduleName
     * @param String flowName
     * @param Event event
     */
    public void afterFlow(String moduleName, String flowName, Event event)
    {
        throw new UnsupportedOperationException("afterFlow not supported in this implementation");
    }

    /**
     * Unsupported as we don't care about beforeFlow operations in this implementation.
     * @param String moduleName
     * @param String flowName
     * @param Event event
     */
    public void beforeFlow(String moduleName, String flowName, Event event)
    {
        throw new UnsupportedOperationException("beforeFlow not supported in this implementation");
    }

    /**
     * Does nothing in this implementation as we are only interested in the
     * afterFlowElement operations for event capture.
     * @param String moduleName
     * @param String flowName
     * @param FlowElement flowElement
     * @param Event event
     */
    public void beforeFlowElement(String moduleName, String flowName,
            FlowElement flowElement, Event event)
    {
        // notification of the component we are about to invoke
        for(FlowObserver flowObserver:this.flowObservers)
        {
            flowObserver.notify(flowElement);
        }
    }
    
    /**
     * Captures the details of the flow component and event in flight.
     * @param String moduleName
     * @param String flowName
     * @param FlowElement flowElement
     * @param Event event
     */
    public void afterFlowElement(String moduleName, String flowName,
            FlowElement flowElement, Event event)
    {
        // notification of the resulting event from the invoked component
        for(FlowObserver flowObserver:this.flowObservers)
        {
            try
            {
                Event eventSnapshot = event.spawn();
                flowObserver.notify(eventSnapshot);
            }
            catch (CloneNotSupportedException e)
            {
                flowObserver.notify("Failed to capture event at runtime due to [" 
                        + e.getMessage() + "]");
            }
        }
    }

    /**
     * Add the specified observer
     * @param FlowObserver
     */
    public void addObserver(FlowObserver flowObserver)
    {
        this.flowObservers.add(flowObserver);
        
    }

    /**
     * Remove the specified observer
     * @param FlowObserver
     */
    public void removeObserver(FlowObserver flowObserver)
    {
        this.flowObservers.remove(flowObserver);
        
    }

    /**
     * Remove all observers
     */
    public void removeAllObservers()
    {
        this.flowObservers.clear();
    }

}
