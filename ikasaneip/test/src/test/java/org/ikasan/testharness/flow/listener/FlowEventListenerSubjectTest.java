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

import org.ikasan.spec.event.ReplicationFactory;
import org.ikasan.spec.flow.FlowElement;
import org.ikasan.spec.flow.FlowEvent;
import org.ikasan.testharness.flow.FlowObserver;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.concurrent.Synchroniser;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

import java.util.List;

/**
 * Tests for the <code>FlowEventListenerSubject</code> class.
 *
 * @author Ikasan Development Team
 *
 */
public class FlowEventListenerSubjectTest
{
    private Mockery mockery = new Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
            setThreadingPolicy(new Synchroniser());
        }
    };
    
    /** mocked List<FlowObserver> */
    @SuppressWarnings("unchecked")
    private final List<FlowObserver> flowObservers = mockery.mock(List.class, "List<FlowObserver>");
    
    /** mocked FlowObserver */
    private final FlowObserver flowObserver = mockery.mock(FlowObserver.class, "FlowObserver");
    
    /** mocked FlowElement */
    private final FlowElement flowElement = mockery.mock(FlowElement.class, "FlowElement");
    
    /** mocked flowEvent */
    private final FlowEvent flowEvent = mockery.mock(FlowEvent.class, "FlowEvent");
    
    /** mocked replicationFactory */
    private final ReplicationFactory replicationFactory = mockery.mock(ReplicationFactory.class, "ReplicationFactory");
    
    /**
     * Sanity test the invocation of the before flow element to notify flow 
     * observers of a flow element invocation.
     */
    @Test
    public void test_beforeFlowElement() 
    {
        // expectations
        mockery.checking(new Expectations()
        {
            {
                // check each capture satisfies a flow expectation
                exactly(1).of(flowObserver).notify(flowElement);
            }
        });
        
        FlowEventListenerSubject flowEventListenerSubject = new FlowEventListenerSubject(replicationFactory);
        flowEventListenerSubject.addObserver(flowObserver);
        flowEventListenerSubject.beforeFlowElement("moduleName", "flowName", flowElement, flowEvent);
        mockery.assertIsSatisfied();
    }

    /**
     * Sanity test the invocation of the after flow element to notify flow 
     * observers of an event.
     */
    @Test
    @SuppressWarnings("unchecked")
    public void test_afterFlowElement()
    {
        // expectations
        mockery.checking(new Expectations()
        {
            {
                // check each capture satisfies a flow expectation
                exactly(1).of(flowObserver).notify(flowEvent);

                // ensure we have an independent copy of that event
                exactly(1).of(replicationFactory).replicate(flowEvent);
                will(returnValue(flowEvent));
            }
        });
        
        FlowEventListenerSubject flowEventListenerSubject = new FlowEventListenerSubject(replicationFactory);
        flowEventListenerSubject.addObserver(flowObserver);
        flowEventListenerSubject.afterFlowElement("moduleName", "flowName", flowElement, flowEvent);
        mockery.assertIsSatisfied();
    }

    /**
     * Sanity test the observer registration.
     */
    @Test
    @SuppressWarnings("unchecked")
    public void test_observerRegistrationOperations()
    {
        // expectations
        mockery.checking(new Expectations()
        {
            {
                exactly(1).of(flowObservers).add(flowObserver);
                exactly(1).of(flowObservers).remove(flowObserver);
                exactly(1).of(flowObservers).clear();
            }
        });
        
        FlowEventListenerSubject flowEventListenerSubject = new TestFlowEventListenerSubject(replicationFactory);
        flowEventListenerSubject.addObserver(flowObserver);
        flowEventListenerSubject.removeObserver(flowObserver);
        flowEventListenerSubject.removeAllObservers();
    }

    /**
     * Utility Test class to allow full access to the FlowEventListenerSubject
     * for testing purposes.
     * 
     * @author Ikasan Development Team
     *
     */
    private class TestFlowEventListenerSubject extends FlowEventListenerSubject
    {
        TestFlowEventListenerSubject(ReplicationFactory<FlowEvent> replicationFactory)
        {
            super(replicationFactory);
        }

        protected List<FlowObserver> initFlowObservers()
        {
            return flowObservers;
        }
    }
}    

