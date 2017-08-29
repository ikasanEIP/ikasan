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
import org.ikasan.spec.component.endpoint.Producer;
import org.ikasan.spec.flow.*;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.concurrent.Synchroniser;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

import java.util.Map;

/**
 * Supports testing of the ProducerFlowElementInvoker
 */
public class ProducerFlowElementInvokerTest
{
    /**
     * Mockery for mocking concrete classes
     */
    private Mockery mockery = new Mockery()
    {{
            setImposteriser(ClassImposteriser.INSTANCE);
            setThreadingPolicy(new Synchroniser());
    }};

    private FlowEventListener flowEventListener = mockery.mock(FlowEventListener.class, "flowEventListener");
    private FlowInvocationContext flowInvocationContext = mockery.mock(FlowInvocationContext.class, "flowInvocationContext");
    private FlowEvent flowEvent = mockery.mock(FlowEvent.class, "flowEvent");
    private FlowElement flowElement = mockery.mock(FlowElement.class, "flowElement");
    private Producer producer = mockery.mock(Producer.class, "producer");
    private Map payload = mockery.mock(Map.class, "payload");

    // this is to test the InvocationAware aspect
    interface ProducerInvocationAware extends Producer, InvocationAware {}
    private ProducerInvocationAware producerInvocationAware = mockery.mock(ProducerInvocationAware.class, "producerInvocationAware");

    @Test
    @SuppressWarnings("unchecked")
    public void test_producer_flowElementInvoker_payloadOnly()
    {
        // expectations
        mockery.checking(new Expectations()
        {
            {
                // first execution
                exactly(2).of(flowEvent).getIdentifier();
                will(returnValue(payload));
                exactly(2).of(flowEvent).getRelatedIdentifier();
                will(returnValue(payload));
                exactly(1).of(flowInvocationContext).addElementInvocation(with(any(FlowElementInvocation.class)));
                exactly(1).of(flowInvocationContext).setLastComponentName(null);
                exactly(1).of(flowInvocationContext).setFinalAction(FinalAction.PUBLISH);
                exactly(1).of(flowEventListener).beforeFlowElement("moduleName", "flowName", flowElement, flowEvent);

                exactly(1).of(flowElement).getFlowComponent();
                will(returnValue(producer));

                exactly(2).of(flowElement).getConfiguration();
                will(returnValue(new FlowElementPersistentConfiguration()));

                exactly(1).of(producer).invoke(flowEvent);
                will(throwException(new ClassCastException()));
                exactly(1).of(flowEvent).getPayload();
                will(returnValue(payload));
                exactly(1).of(producer).invoke(payload);

                exactly(1).of(flowEventListener).afterFlowElement("moduleName", "flowName", flowElement, flowEvent);

                // second execution
                exactly(2).of(flowEvent).getIdentifier();
                will(returnValue(payload));
                exactly(2).of(flowEvent).getRelatedIdentifier();
                will(returnValue(payload));
                exactly(1).of(flowInvocationContext).addElementInvocation(with(any(FlowElementInvocation.class)));
                exactly(1).of(flowInvocationContext).setLastComponentName(null);
                exactly(1).of(flowInvocationContext).setFinalAction(FinalAction.PUBLISH);
                exactly(1).of(flowEventListener).beforeFlowElement("moduleName", "flowName", flowElement, flowEvent);

                exactly(1).of(flowElement).getFlowComponent();
                will(returnValue(producer));

                exactly(2).of(flowElement).getConfiguration();
                will(returnValue(new FlowElementPersistentConfiguration()));

                exactly(1).of(flowEvent).getPayload();
                will(returnValue(payload));
                exactly(1).of(producer).invoke(payload);

                exactly(1).of(flowEventListener).afterFlowElement("moduleName", "flowName", flowElement, flowEvent);
            }
        });

        FlowElementInvoker flowElementInvoker = new ProducerFlowElementInvoker();
        flowElementInvoker.invoke(flowEventListener, "moduleName", "flowName", flowInvocationContext, flowEvent, flowElement);
        flowElementInvoker.invoke(flowEventListener, "moduleName", "flowName", flowInvocationContext, flowEvent, flowElement);

        mockery.assertIsSatisfied();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void test_producer_flowElementInvoker_payloadOnly_invocation_aware()
    {
        // expectations
        mockery.checking(new Expectations()
        {
            {
                // first execution
                exactly(2).of(flowEvent).getIdentifier();
                will(returnValue(payload));
                exactly(2).of(flowEvent).getRelatedIdentifier();
                will(returnValue(payload));
                exactly(1).of(flowInvocationContext).addElementInvocation(with(any(FlowElementInvocation.class)));
                exactly(1).of(flowInvocationContext).setLastComponentName(null);
                exactly(1).of(flowInvocationContext).setFinalAction(FinalAction.PUBLISH);
                exactly(1).of(flowEventListener).beforeFlowElement("moduleName", "flowName", flowElement, flowEvent);

                exactly(1).of(flowElement).getFlowComponent();
                will(returnValue(producerInvocationAware));

                exactly(2).of(flowElement).getConfiguration();
                will(returnValue(new FlowElementPersistentConfiguration()));

                exactly(1).of(producerInvocationAware).setFlowElementInvocation(with(any(FlowElementInvocation.class)));
                exactly(1).of(producerInvocationAware).invoke(flowEvent);
                will(throwException(new ClassCastException()));
                exactly(1).of(flowEvent).getPayload();
                will(returnValue(payload));
                exactly(1).of(producerInvocationAware).invoke(payload);
                exactly(1).of(producerInvocationAware).unsetFlowElementInvocation(with(any(FlowElementInvocation.class)));

                exactly(1).of(flowEventListener).afterFlowElement("moduleName", "flowName", flowElement, flowEvent);

                // second execution
                exactly(2).of(flowEvent).getIdentifier();
                will(returnValue(payload));
                exactly(2).of(flowEvent).getRelatedIdentifier();
                will(returnValue(payload));
                exactly(1).of(flowInvocationContext).addElementInvocation(with(any(FlowElementInvocation.class)));
                exactly(1).of(flowInvocationContext).setLastComponentName(null);
                exactly(1).of(flowInvocationContext).setFinalAction(FinalAction.PUBLISH);
                exactly(1).of(flowEventListener).beforeFlowElement("moduleName", "flowName", flowElement, flowEvent);

                exactly(1).of(flowElement).getFlowComponent();
                will(returnValue(producer));

                exactly(2).of(flowElement).getConfiguration();
                will(returnValue(new FlowElementPersistentConfiguration()));

                exactly(1).of(flowEvent).getPayload();
                will(returnValue(payload));
                exactly(1).of(producer).invoke(payload);

                exactly(1).of(flowEventListener).afterFlowElement("moduleName", "flowName", flowElement, flowEvent);
            }
        });

        FlowElementInvoker flowElementInvoker = new ProducerFlowElementInvoker();
        flowElementInvoker.invoke(flowEventListener, "moduleName", "flowName", flowInvocationContext, flowEvent, flowElement);
        flowElementInvoker.invoke(flowEventListener, "moduleName", "flowName", flowInvocationContext, flowEvent, flowElement);

        mockery.assertIsSatisfied();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void test_producer_flowElementInvoker_flowEvent()
    {
        // expectations
        mockery.checking(new Expectations()
        {
            {
                // first execution
                exactly(2).of(flowEvent).getIdentifier();
                will(returnValue(payload));
                exactly(2).of(flowEvent).getRelatedIdentifier();
                will(returnValue(payload));
                exactly(1).of(flowInvocationContext).addElementInvocation(with(any(FlowElementInvocation.class)));
                exactly(1).of(flowInvocationContext).setLastComponentName(null);
                exactly(1).of(flowInvocationContext).setFinalAction(FinalAction.PUBLISH);
                exactly(1).of(flowEventListener).beforeFlowElement("moduleName", "flowName", flowElement, flowEvent);

                exactly(1).of(flowElement).getFlowComponent();
                will(returnValue(producer));

                exactly(2).of(flowElement).getConfiguration();
                will(returnValue(new FlowElementPersistentConfiguration()));

                exactly(1).of(producer).invoke(flowEvent);

                exactly(1).of(flowEventListener).afterFlowElement("moduleName", "flowName", flowElement, flowEvent);

                // second execution
                exactly(2).of(flowEvent).getIdentifier();
                will(returnValue(payload));
                exactly(2).of(flowEvent).getRelatedIdentifier();
                will(returnValue(payload));
                exactly(1).of(flowInvocationContext).addElementInvocation(with(any(FlowElementInvocation.class)));
                exactly(1).of(flowInvocationContext).setLastComponentName(null);
                exactly(1).of(flowInvocationContext).setFinalAction(FinalAction.PUBLISH);

                exactly(1).of(flowEventListener).beforeFlowElement("moduleName", "flowName", flowElement, flowEvent);

                exactly(1).of(flowElement).getFlowComponent();
                will(returnValue(producer));

                exactly(2).of(flowElement).getConfiguration();
                will(returnValue(new FlowElementPersistentConfiguration()));

                exactly(1).of(producer).invoke(flowEvent);

                exactly(1).of(flowEventListener).afterFlowElement("moduleName", "flowName", flowElement, flowEvent);
           }
        });

        FlowElementInvoker flowElementInvoker = new ProducerFlowElementInvoker();
        flowElementInvoker.invoke(flowEventListener, "moduleName", "flowName", flowInvocationContext, flowEvent, flowElement);
        flowElementInvoker.invoke(flowEventListener, "moduleName", "flowName", flowInvocationContext, flowEvent, flowElement);

        mockery.assertIsSatisfied();
    }
}
