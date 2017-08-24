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

import org.ikasan.flow.configuration.FlowElementPersistentConfiguration;
import org.ikasan.spec.component.splitting.Splitter;
import org.ikasan.spec.flow.*;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.concurrent.Synchroniser;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Supports testing of the SplitterFlowElementInvoker
 */
public class ConcurrentSplitterFlowElementInvokerTest
{
    private final Synchroniser synchroniser = new Synchroniser();

    /**
     * Mockery for mocking concrete classes
     */
    private Mockery mockery = new Mockery()
    {{
            setImposteriser(ClassImposteriser.INSTANCE);
            setThreadingPolicy(synchroniser);
        }};

    private FlowEventListener flowEventListener = mockery.mock(FlowEventListener.class, "flowEventListener");
    private FlowInvocationContext flowInvocationContext = mockery.mock(FlowInvocationContext.class, "flowInvocationContext");
    private FlowEvent flowEvent = mockery.mock(FlowEvent.class, "flowEvent");
    /** the sub flow element, also used as the current splitter flow element */
    private FlowElement subFlowElement = mockery.mock(FlowElement.class, "subFlowElement");

    /** the main flow element, called after the concurrent subflows */
    private FlowElement mainFlowElement = mockery.mock(FlowElement.class, "mainFlowElement");
    private FlowElementInvoker mainFlowElementInvoker = mockery.mock(FlowElementInvoker.class, "mainFlowElementInvoker");

    private Splitter splitter = mockery.mock(Splitter.class, "splitter");

    // this is to test the InvocationAware aspect
    interface SplitterInvocationAware extends Splitter, InvocationAware {}
    private SplitterInvocationAware splitterInvocationAware = mockery.mock(SplitterInvocationAware.class, "splitterInvocationAware");


    private Object payload = mockery.mock(Object.class, "payload");
    private ConcurrentSplitterFlowElementInvoker.SplitFlowElement asyncTask = mockery.mock(ConcurrentSplitterFlowElementInvoker.SplitFlowElement.class, "mockAsyncTask");
    private ExecutorService executorService = Executors.newFixedThreadPool(1);

    @Test
    @SuppressWarnings("unchecked")
    public void test_splitter_flowElementInvoker_one_payload_successful_thread_execution() throws Exception {
        final List payloads = new ArrayList();
        payloads.add(payload);

        asyncTask._flowEvent = flowEvent;
        asyncTask._flowInvocationContext = flowInvocationContext;

        // expectations
        mockery.checking(new Expectations() {
            {
                exactly(2).of(flowEvent).getIdentifier();
                will(returnValue(payload));
                exactly(2).of(flowEvent).getRelatedIdentifier();
                will(returnValue(payload));
                exactly(1).of(flowInvocationContext).addElementInvocation(with(any(FlowElementInvocation.class)));
                exactly(1).of(flowInvocationContext).setLastComponentName(null);

                exactly(1).of(flowEventListener).beforeFlowElement("moduleName", "flowName", subFlowElement, flowEvent);

                exactly(1).of(subFlowElement).getFlowComponent();
                will(returnValue(splitter));

                exactly(2).of(subFlowElement).getConfiguration();
                will(returnValue(new FlowElementPersistentConfiguration()));

                exactly(1).of(splitter).split(flowEvent);
                will(throwException(new ClassCastException()));
                exactly(2).of(flowEvent).getPayload();
                will(returnValue(payload));
                exactly(1).of(flowEvent).getIdentifier();
                will(returnValue("id"));

                exactly(1).of(splitter).split(payload);
                will(returnValue(payloads));

                exactly(1).of(subFlowElement).getTransition(FlowElement.SUBFLOW_TRANSITION_NAME);
                will(returnValue(subFlowElement));

                exactly(1).of(asyncTask).call();
                will(returnValue(asyncTask));

                exactly(1).of(subFlowElement).getTransition(FlowElement.DEFAULT_TRANSITION_NAME);
                will(returnValue(mainFlowElement));

                exactly(1).of(mainFlowElement).getFlowElementInvoker();
                will(returnValue(mainFlowElementInvoker));
                exactly(1).of(mainFlowElementInvoker).invoke(flowEventListener, "moduleName", "flowName", flowInvocationContext, flowEvent, mainFlowElement);
                will(returnValue(null));

                exactly(1).of(flowEvent).setPayload(payload);
                exactly(1).of(flowEventListener).afterFlowElement("moduleName", "flowName", subFlowElement, flowEvent);
                exactly(2).of(mainFlowElement).getConfiguration();
                will(returnValue(new FlowElementPersistentConfiguration()));
                exactly(1).of(flowEventListener).afterFlowElement(with(any(String.class)), with(any(String.class)), with(any(FlowElement.class)), with(any(FlowEvent.class)));

                exactly(1).of(flowInvocationContext).combine(flowInvocationContext);

            }
        });

        FlowElementInvoker flowElementInvoker = new StubbedConcurrentSplitterFlowElementInvoker(executorService, 1, null);
        flowElementInvoker.invoke(flowEventListener, "moduleName", "flowName", flowInvocationContext, flowEvent, subFlowElement);
        mockery.assertIsSatisfied();
    }


    @Test
    @SuppressWarnings("unchecked")
    public void test_splitter_flowElementInvoker_one_payload_successful_thread_execution_invocation_aware() throws Exception {
        final List payloads = new ArrayList();
        payloads.add(payload);

        asyncTask._flowEvent = flowEvent;
        asyncTask._flowInvocationContext = flowInvocationContext;

        // expectations
        mockery.checking(new Expectations() {
            {
                exactly(2).of(flowEvent).getIdentifier();
                will(returnValue(payload));
                exactly(2).of(flowEvent).getRelatedIdentifier();
                will(returnValue(payload));
                exactly(1).of(flowInvocationContext).addElementInvocation(with(any(FlowElementInvocation.class)));
                exactly(1).of(flowInvocationContext).setLastComponentName(null);

                exactly(1).of(flowEventListener).beforeFlowElement("moduleName", "flowName", subFlowElement, flowEvent);

                exactly(1).of(subFlowElement).getFlowComponent();
                will(returnValue(splitterInvocationAware));

                exactly(1).of(splitterInvocationAware).setFlowElementInvocation(with(any(FlowElementInvocation.class)));

                exactly(2).of(subFlowElement).getConfiguration();
                will(returnValue(new FlowElementPersistentConfiguration()));

                exactly(1).of(splitterInvocationAware).split(flowEvent);
                will(throwException(new ClassCastException()));
                exactly(2).of(flowEvent).getPayload();
                will(returnValue(payload));
                exactly(1).of(flowEvent).getIdentifier();
                will(returnValue("id"));

                exactly(1).of(splitterInvocationAware).split(payload);
                will(returnValue(payloads));

                exactly(1).of(splitterInvocationAware).unsetFlowElementInvocation(with(any(FlowElementInvocation.class)));


                exactly(1).of(subFlowElement).getTransition(FlowElement.SUBFLOW_TRANSITION_NAME);
                will(returnValue(subFlowElement));

                exactly(1).of(asyncTask).call();
                will(returnValue(asyncTask));

                exactly(1).of(subFlowElement).getTransition(FlowElement.DEFAULT_TRANSITION_NAME);
                will(returnValue(mainFlowElement));

                exactly(1).of(mainFlowElement).getFlowElementInvoker();
                will(returnValue(mainFlowElementInvoker));
                exactly(1).of(mainFlowElementInvoker).invoke(flowEventListener, "moduleName", "flowName", flowInvocationContext, flowEvent, mainFlowElement);
                will(returnValue(null));

                exactly(1).of(flowEvent).setPayload(payload);
                exactly(1).of(flowEventListener).afterFlowElement("moduleName", "flowName", subFlowElement, flowEvent);
                exactly(2).of(mainFlowElement).getConfiguration();
                will(returnValue(new FlowElementPersistentConfiguration()));
                exactly(1).of(flowEventListener).afterFlowElement(with(any(String.class)), with(any(String.class)), with(any(FlowElement.class)), with(any(FlowEvent.class)));

                exactly(1).of(flowInvocationContext).combine(flowInvocationContext);

            }
        });

        FlowElementInvoker flowElementInvoker = new StubbedConcurrentSplitterFlowElementInvoker(executorService, 1, null);
        flowElementInvoker.invoke(flowEventListener, "moduleName", "flowName", flowInvocationContext, flowEvent, subFlowElement);
        mockery.assertIsSatisfied();
    }

    class StubbedConcurrentSplitterFlowElementInvoker extends ConcurrentSplitterFlowElementInvoker
    {
        int callbackCount;
        Throwable callbackThrowableException;

        public StubbedConcurrentSplitterFlowElementInvoker(ExecutorService executorService, int callbackCount, Throwable callbackThrowableException)
        {
            super(executorService);
            this.callbackCount = callbackCount;
            this.callbackThrowableException = callbackThrowableException;
        }

        /**
         * Mock the async task.
         */
        protected SplitFlowElement newAsyncTask(FlowElement nextFlowElementInRoute, FlowEventListener flowEventListener, String moduleName, String flowName, FlowInvocationContext flowInvocationContext, FlowEvent flowEvent)
        {
            return asyncTask;
        }

        protected boolean pendingCallback(List payloads)
        {
            return callbackCount < payloads.size() && callbackThrowableException == null;
        }
    }
}