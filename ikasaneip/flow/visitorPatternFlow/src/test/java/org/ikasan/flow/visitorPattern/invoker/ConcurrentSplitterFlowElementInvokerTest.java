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

import org.ikasan.flow.event.FlowEventFactory;
import org.ikasan.spec.component.splitting.Splitter;
import org.ikasan.spec.flow.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Supports testing of the SplitterFlowElementInvoker
 */
class ConcurrentSplitterFlowElementInvokerTest
{
    private FlowEventListener flowEventListener = Mockito.mock(FlowEventListener.class, "flowEventListener");

    private FlowInvocationContext flowInvocationContext = Mockito
        .mock(FlowInvocationContext.class, "flowInvocationContext");


    /**
     * the sub flow element, also used as the current splitter flow element
     */
    private FlowElement subFlowElement = Mockito.mock(FlowElement.class, "subFlowElement");

    /**
     * the main flow element, called after the concurrent subflows
     */
    private FlowElement mainFlowElement = Mockito.mock(FlowElement.class, "mainFlowElement");

    private FlowElementInvoker mainFlowElementInvoker = Mockito
        .mock(FlowElementInvoker.class, "mainFlowElementInvoker");

    private Splitter splitter = Mockito.mock(Splitter.class, "splitter");

    // this is to test the InvocationAware aspect
    interface SplitterInvocationAware extends Splitter, InvocationAware
    {
    }

    private SplitterInvocationAware splitterInvocationAware = Mockito
        .mock(SplitterInvocationAware.class, "splitterInvocationAware");

    private List<FlowEventListener> flowEventListeners = new ArrayList<FlowEventListener>();


    private ConcurrentSplitterFlowElementInvoker.SplitFlowElement asyncTask = Mockito
        .mock(ConcurrentSplitterFlowElementInvoker.SplitFlowElement.class, "mockAsyncTask");

    private ExecutorService executorService;

    @BeforeEach
    void setup(){
         executorService = Executors.newFixedThreadPool(1);
    }

    @AfterEach
    void shutdown(){
        executorService.shutdownNow();
    }

    @Test
    @SuppressWarnings("unchecked")
    void test_splitter_flowElementInvoker_one_payload_successful_thread_execution() throws Exception
    {
        String payload = "one";
        final List<String> payloads = new ArrayList<>();
        payloads.add(payload);
        FlowEvent flowEvent = new FlowEventFactory().newEvent("id", "id",payload);

        asyncTask._flowEvent = flowEvent;
        asyncTask._flowInvocationContext = flowInvocationContext;

        Mockito.spy(flowInvocationContext).addElementInvocation(Mockito.any(FlowElementInvocation.class));
        Mockito.spy(flowInvocationContext).setLastComponentName(null);

        Mockito.spy(flowEventListener).beforeFlowElement("moduleName", "flowName", subFlowElement, flowEvent);

        Mockito.when(subFlowElement.getFlowComponent()).thenReturn(splitter);

        Mockito.when(splitter.split(flowEvent)).thenThrow(new ClassCastException());


        Mockito.when(splitter.split(payload)).thenReturn(payloads);

        Mockito.when(subFlowElement.getTransition(FlowElement.SUBFLOW_TRANSITION_NAME)).thenReturn(subFlowElement);

        Mockito.when(asyncTask.call()).thenReturn(asyncTask);

        Mockito.when(subFlowElement.getTransition(FlowElement.DEFAULT_TRANSITION_NAME)).thenReturn(mainFlowElement);

        Mockito.when(mainFlowElement.getFlowElementInvoker()).thenReturn(mainFlowElementInvoker);
        Mockito.when(mainFlowElementInvoker
            .invoke(flowEventListeners, "moduleName", "flowName", flowInvocationContext, flowEvent, mainFlowElement))
               .thenReturn(null);

        Mockito.spy(flowEvent).setPayload(payload);
        Mockito.spy(flowEventListener).afterFlowElement("moduleName", "flowName", subFlowElement, flowEvent);
        Mockito.spy(flowEventListener)
               .afterFlowElement(Mockito.any(String.class), Mockito.any(String.class), Mockito.any(FlowElement.class),
                   Mockito.any(FlowEvent.class)
                                );

        Mockito.spy(flowInvocationContext).combine(flowInvocationContext);

        FlowElementInvoker flowElementInvoker = new StubbedConcurrentSplitterFlowElementInvoker(executorService, 1,
            null
        );
        flowEventListeners.add(flowEventListener);
        flowElementInvoker
            .invoke(flowEventListeners, "moduleName", "flowName", flowInvocationContext, flowEvent, subFlowElement);
        //mockery.assertIsSatisfied();
    }

    @Test
    @SuppressWarnings("unchecked")
    void test_splitter_flowElementInvoker_one_payload_successful_thread_execution_invocation_aware()
        throws Exception
    {
        String payload = "one";
        final List<String> payloads = new ArrayList<>();
        payloads.add(payload);
        FlowEvent flowEvent = new FlowEventFactory().newEvent("id", "id",payload);
        asyncTask._flowEvent = flowEvent;
        asyncTask._flowInvocationContext = flowInvocationContext;


        Mockito.spy(flowInvocationContext).addElementInvocation(Mockito.any(FlowElementInvocation.class));
        Mockito.spy(flowInvocationContext).setLastComponentName(null);

        Mockito.spy(flowEventListener).beforeFlowElement("moduleName", "flowName", subFlowElement, flowEvent);

        Mockito.when(subFlowElement.getFlowComponent()).thenReturn(splitterInvocationAware);

        Mockito.spy(splitterInvocationAware).setFlowElementInvocation(Mockito.any(FlowElementInvocation.class));

        Mockito.when(splitterInvocationAware.split(flowEvent)).thenThrow(new ClassCastException());


        Mockito.when(splitterInvocationAware.split(payload)).thenReturn(payloads);

        Mockito.spy(splitterInvocationAware).unsetFlowElementInvocation(Mockito.any(FlowElementInvocation.class));

        Mockito.when(subFlowElement.getTransition(FlowElement.SUBFLOW_TRANSITION_NAME)).thenReturn(subFlowElement);

        Mockito.when(asyncTask.call()).thenReturn(asyncTask);

        Mockito.when(subFlowElement.getTransition(FlowElement.DEFAULT_TRANSITION_NAME)).thenReturn(mainFlowElement);

        Mockito.when(mainFlowElement.getFlowElementInvoker()).thenReturn(mainFlowElementInvoker);
        Mockito.when(mainFlowElementInvoker
            .invoke(flowEventListeners, "moduleName", "flowName", flowInvocationContext, flowEvent, mainFlowElement))
               .thenReturn(null);

        Mockito.spy(flowEvent).setPayload(payload);
        Mockito.spy(flowEventListener).afterFlowElement("moduleName", "flowName", subFlowElement, flowEvent);
        Mockito.spy(flowEventListener)
               .afterFlowElement(Mockito.any(String.class), Mockito.any(String.class), Mockito.any(FlowElement.class),
                   Mockito.any(FlowEvent.class)
                                );

        Mockito.spy(flowInvocationContext).combine(flowInvocationContext);

        FlowElementInvoker flowElementInvoker = new StubbedConcurrentSplitterFlowElementInvoker(executorService, 1,
            null
        );
        flowEventListeners.add(flowEventListener);
        flowElementInvoker
            .invoke(flowEventListeners, "moduleName", "flowName", flowInvocationContext, flowEvent, subFlowElement);

    }

    class StubbedConcurrentSplitterFlowElementInvoker extends ConcurrentSplitterFlowElementInvoker
    {
        int callbackCount;

        Throwable callbackThrowableException;

        public StubbedConcurrentSplitterFlowElementInvoker(ExecutorService executorService, int callbackCount,
                                                           Throwable callbackThrowableException)
        {
            super(executorService);
            this.callbackCount = callbackCount;
            this.callbackThrowableException = callbackThrowableException;
        }

        /**
         * Mock the async task.
         */
        protected SplitFlowElement newAsyncTask(FlowElement nextFlowElementInRoute,
                                                List<FlowEventListener> flowEventListeners, String moduleName,
                                                String flowName, FlowInvocationContext flowInvocationContext,
                                                FlowEvent flowEvent)
        {
            return asyncTask;
        }
    }
}