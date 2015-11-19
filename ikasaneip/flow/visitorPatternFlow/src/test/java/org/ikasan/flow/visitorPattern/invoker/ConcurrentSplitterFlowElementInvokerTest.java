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

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListenableFutureTask;
import org.ikasan.flow.visitorPattern.DefaultFlowInvocationContext;
import org.ikasan.flow.visitorPattern.InvalidFlowException;
import org.ikasan.spec.component.splitting.Splitter;
import org.ikasan.spec.component.splitting.SplitterException;
import org.ikasan.spec.flow.*;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.concurrent.Synchroniser;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
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
    private FlowElement flowElement = mockery.mock(FlowElement.class, "flowElement");
    private FlowElementInvoker flowElementInvoker = mockery.mock(FlowElementInvoker.class, "flowElementInvoker");
    private Splitter splitter = mockery.mock(Splitter.class, "splitter");
    private Object payload = mockery.mock(Object.class, "payload");
    private Callable asyncTask = mockery.mock(Callable.class, "mockAsyncTask");
    private ExecutorService executorService = mockery.mock(ExecutorService.class, "mockExecutorService");
    private ListenableFuture listenableFuture = mockery.mock(ListenableFuture.class, "mockListenableFuture");

    @Test
    @SuppressWarnings("unchecked")
    public void test_splitter_flowElementInvoker_one_payload_successful_thread_execution() throws Exception {
        final List payloads = new ArrayList();
        payloads.add(payload);

        final List<String> invokedComponents =  new ArrayList<String>();
        invokedComponents.add("componentName");

        // expectations
        mockery.checking(new Expectations() {
            {
                exactly(1).of(flowElement).getComponentName();
                will(returnValue("componentName"));
                exactly(1).of(flowInvocationContext).addInvokedComponentName("componentName");
                exactly(1).of(flowEventListener).beforeFlowElement("moduleName", "flowName", flowElement, flowEvent);

                exactly(1).of(flowElement).getFlowComponent();
                will(returnValue(splitter));
                exactly(1).of(splitter).split(flowEvent);
                will(throwException(new ClassCastException()));
                exactly(1).of(flowEvent).getPayload();
                will(returnValue(payload));
                exactly(1).of(splitter).split(payload);
                will(returnValue(payloads));

                exactly(1).of(flowElement).getTransition(FlowElement.DEFAULT_TRANSITION_NAME);
                will(returnValue(flowElement));
                exactly(1).of(flowEvent).setPayload(payload);
                exactly(1).of(flowEventListener).afterFlowElement("moduleName", "flowName", flowElement, flowEvent);

                exactly(1).of(flowInvocationContext).getInvokedComponents();
                will(returnValue(invokedComponents));

                exactly(1).of(executorService).execute(with(any(Runnable.class)));
            }
        });

        FlowElementInvoker flowElementInvoker = new StubbedConcurrentSplitterFlowElementInvoker(executorService, 1, null);
        flowElementInvoker.invoke(flowEventListener, "moduleName", "flowName", flowInvocationContext, flowEvent, flowElement);
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
         * Return mocked async task.
         *
         * @param nextFlowElementInRoute
         * @param flowEventListener
         * @param moduleName
         * @param flowName
         * @param flowInvocationContext
         * @param flowEvent
         * @return
         */
        protected Callable newAsyncTask(FlowElement nextFlowElementInRoute, FlowEventListener flowEventListener, String moduleName, String flowName, FlowInvocationContext flowInvocationContext, FlowEvent flowEvent)
        {
            return asyncTask;
        }

        protected boolean pendingCallback(List payloads)
        {
            return callbackCount < payloads.size() && callbackThrowableException == null;
        }
    }
}
