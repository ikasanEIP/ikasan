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
import org.ikasan.flow.visitorPattern.InvalidFlowException;
import org.ikasan.spec.component.splitting.Splitter;
import org.ikasan.spec.component.splitting.SplitterException;
import org.ikasan.spec.configuration.Configured;
import org.ikasan.spec.event.EventFactory;
import org.ikasan.spec.flow.*;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.imposters.ByteBuddyClassImposteriser;
import org.jmock.lib.concurrent.Synchroniser;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Supports testing of the SplitterFlowElementInvoker
 */
public class SplitterFlowElementInvokerTest
{
    /**
     * Mockery for mocking concrete classes
     */
    private Mockery mockery = new Mockery()
    {{
            setImposteriser(ByteBuddyClassImposteriser.INSTANCE);
            setThreadingPolicy(new Synchroniser());
    }};

    private FlowEventListener flowEventListener = mockery.mock(FlowEventListener.class, "flowEventListener");
    private FlowInvocationContext flowInvocationContext = mockery.mock(FlowInvocationContext.class, "flowInvocationContext");
    private EventFactory eventFactory = mockery.mock(EventFactory.class, "flowEventFactory");
    private FlowEvent flowEvent = mockery.mock(FlowEvent.class, "flowEvent");
    private FlowElement flowElement = mockery.mock(FlowElement.class, "flowElement");
    private FlowElementInvoker flowElementInvoker = mockery.mock(FlowElementInvoker.class, "flowElementInvoker");
    private Splitter splitter = mockery.mock(Splitter.class, "splitter");
    private Object payload = mockery.mock(Object.class, "payload");

    private List<FlowEventListener> flowEventListeners = new ArrayList<FlowEventListener>();

    // this is to test the InvocationAware aspect
    interface SplitterInvocationAware extends Splitter, InvocationAware {}
    private SplitterInvocationAware splitterInvocationAware = mockery.mock(SplitterInvocationAware.class, "splitterInvocationAware");

    @Test
    @SuppressWarnings("unchecked")
    public void test_splitter_flowElementInvoker_no_split()
    {
        final List payloads = new ArrayList();
        payloads.add(payload);

        long timestamp = System.currentTimeMillis();

        // expectations
        mockery.checking(new Expectations()
        {
            {
                exactly(3).of(flowEvent).getIdentifier();
                will(returnValue(payload));
                exactly(3).of(flowEvent).getRelatedIdentifier();
                will(returnValue(payload));
                exactly(1).of(flowEvent).getTimestamp();
                will(returnValue(timestamp));
                exactly(1).of(flowInvocationContext).addElementInvocation(with(any(FlowElementInvocation.class)));
                exactly(1).of(flowInvocationContext).setLastComponentName(null);
                exactly(1).of(flowEventListener).beforeFlowElement("moduleName", "flowName", flowElement, flowEvent);

                exactly(1).of(flowElement).getFlowComponent();
                will(returnValue(splitter));
                exactly(1).of(flowEvent).getPayload();
                will(returnValue(payload));
                exactly(1).of(splitter).split(flowEvent);
                will(throwException(new ClassCastException()));
                exactly(1).of(splitter).split(payload);
                will(returnValue(payloads));

                exactly(1).of(flowEventListener).afterFlowElement("moduleName", "flowName", flowElement, flowEvent);
                exactly(1).of(flowElement).getTransition(FlowElement.DEFAULT_TRANSITION_NAME);
                will(returnValue(flowElement));

                exactly(1).of(eventFactory).newEvent(payload, payload, timestamp, payload);
                will(returnValue(flowEvent));

                exactly(1).of(flowElement).getFlowElementInvoker();
                will(returnValue(flowElementInvoker));
                exactly(1).of(flowElementInvoker).invoke(flowEventListeners, "moduleName", "flowName", flowInvocationContext, flowEvent, flowElement);
                will(returnValue(null));
            }
        });

        FlowElementInvoker flowElementInvoker = new SplitterFlowElementInvoker(eventFactory);
        flowEventListeners.add(flowEventListener);
        flowElementInvoker.invoke(flowEventListeners, "moduleName", "flowName", flowInvocationContext, flowEvent, flowElement);

        mockery.assertIsSatisfied();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void test_splitter_flowElementInvoker_no_split_real_flowEvent_with_string_payload()
    {
        final List payloads = new ArrayList();
        payloads.add("one");
        FlowEventFactory flowEventFactory = new FlowEventFactory();
        FlowEvent realFlowEvent = flowEventFactory.newEvent("identifier", "relatedIdentifier", payloads);

        // expectations
        mockery.checking(new Expectations()
        {
            {
                exactly(1).of(flowInvocationContext).addElementInvocation(with(any(FlowElementInvocation.class)));
                exactly(1).of(flowInvocationContext).setLastComponentName(null);

                exactly(1).of(flowElement).getFlowComponent();
                will(returnValue(splitter));
                exactly(1).of(splitter).split(realFlowEvent);
                will(throwException(new ClassCastException()));
                exactly(1).of(splitter).split(payloads);
                will(returnValue(payloads));

                exactly(1).of(flowElement).getTransition(FlowElement.DEFAULT_TRANSITION_NAME);
                will(returnValue(flowElement));

                exactly(1).of(flowElement).getFlowElementInvoker();
                will(returnValue(flowElementInvoker));
                exactly(1).of(flowElementInvoker).invoke(null, "moduleName", "flowName", flowInvocationContext, realFlowEvent, flowElement);
                will(returnValue(null));
            }
        });

        FlowElementInvoker flowElementInvoker = new SplitterFlowElementInvoker(flowEventFactory);
        flowEventListeners.add(flowEventListener);
        flowElementInvoker.invoke(null, "moduleName", "flowName", flowInvocationContext, realFlowEvent, flowElement);

        mockery.assertIsSatisfied();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void test_splitter_flowElementInvoker_no_split_real_flowEvent_with_flowEvent_payload()
    {
        FlowEventFactory flowEventFactory = new FlowEventFactory();
        FlowEvent payloadFlowEvent = flowEventFactory.newEvent("identifier", "relatedIdentifier", "one");
        final List payloads = new ArrayList();
        payloads.add(payloadFlowEvent);
        FlowEvent realFlowEvent = flowEventFactory.newEvent("identifier", "relatedIdentifier", payloads);

        // expectations
        mockery.checking(new Expectations()
        {
            {
                exactly(1).of(flowInvocationContext).addElementInvocation(with(any(FlowElementInvocation.class)));
                exactly(1).of(flowInvocationContext).setLastComponentName(null);

                exactly(1).of(flowElement).getFlowComponent();
                will(returnValue(splitter));
                exactly(1).of(splitter).split(realFlowEvent);
                will(throwException(new ClassCastException()));
                exactly(1).of(splitter).split(payloads);
                will(returnValue(payloads));

                exactly(1).of(flowElement).getTransition(FlowElement.DEFAULT_TRANSITION_NAME);
                will(returnValue(flowElement));

                exactly(1).of(flowElement).getFlowElementInvoker();
                will(returnValue(flowElementInvoker));
                exactly(1).of(flowElementInvoker).invoke(null, "moduleName", "flowName", flowInvocationContext, realFlowEvent, flowElement);
                will(returnValue(null));
            }
        });

        FlowElementInvoker flowElementInvoker = new SplitterFlowElementInvoker(flowEventFactory);
        flowEventListeners.add(flowEventListener);
        flowElementInvoker.invoke(null, "moduleName", "flowName", flowInvocationContext, realFlowEvent, flowElement);

        mockery.assertIsSatisfied();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void test_splitter_flowElementInvoker_split_to_individualFlowEvents()
    {
        final List payloads = new ArrayList();
        payloads.add(flowEvent);
        payloads.add(flowEvent);
        payloads.add(flowEvent);

        // expectations
        mockery.checking(new Expectations()
        {
            {
                exactly(2).of(flowEvent).getIdentifier();
                will(returnValue(payload));
                exactly(2).of(flowEvent).getRelatedIdentifier();
                will(returnValue(payload));
                exactly(1).of(flowInvocationContext).addElementInvocation(with(any(FlowElementInvocation.class)));
                exactly(1).of(flowInvocationContext).setLastComponentName(null);
                exactly(1).of(flowEventListener).beforeFlowElement("moduleName", "flowName", flowElement, flowEvent);

                exactly(1).of(flowElement).getFlowComponent();
                will(returnValue(splitter));
                exactly(1).of(flowEvent).getPayload();
                will(returnValue(payload));
                exactly(1).of(splitter).split(flowEvent);
                will(throwException(new ClassCastException()));
                exactly(1).of(splitter).split(payload);
                will(returnValue(payloads));

                exactly(1).of(flowElement).getTransition(FlowElement.DEFAULT_TRANSITION_NAME);
                will(returnValue(flowElement));

                exactly(1).of(flowEventListener).afterFlowElement("moduleName", "flowName", flowElement, flowEvent);
                exactly(1).of(flowEventListener).afterFlowElement("moduleName", "flowName", flowElement, flowEvent);
                exactly(1).of(flowEventListener).afterFlowElement("moduleName", "flowName", flowElement, flowEvent);
                exactly(3).of(flowElement).getFlowElementInvoker();
                will(returnValue(flowElementInvoker));
                exactly(3).of(flowElementInvoker).invoke(flowEventListeners, "moduleName", "flowName", flowInvocationContext, flowEvent, flowElement);
                will(returnValue(null));            }
        });

        FlowElementInvoker flowElementInvoker = new SplitterFlowElementInvoker(eventFactory);
        flowEventListeners.add(flowEventListener);
        flowElementInvoker.invoke(flowEventListeners, "moduleName", "flowName", flowInvocationContext, flowEvent, flowElement);

        mockery.assertIsSatisfied();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void test_splitter_flowElementInvoker_split_to_listOfEvents()
    {
        final List payloads = new ArrayList();
        payloads.add(flowEvent);
        payloads.add(payload);
        payloads.add(flowEvent);

        final List flowEvents = new ArrayList();
        flowEvents.add(flowEvent);
        flowEvents.add(flowEvent);
        flowEvents.add(flowEvent);

        long timestamp = System.currentTimeMillis();

        // expectations
        mockery.checking(new Expectations()
        {
            {
                // first call
                exactly(2).of(flowEvent).getIdentifier();
                will(returnValue(payload));
                exactly(2).of(flowEvent).getRelatedIdentifier();
                will(returnValue(payload));
                exactly(1).of(flowEvent).getTimestamp();
                will(returnValue(timestamp));
                exactly(1).of(flowInvocationContext).addElementInvocation(with(any(FlowElementInvocation.class)));
                exactly(1).of(flowInvocationContext).setLastComponentName(null);
                exactly(1).of(flowEventListener).beforeFlowElement("moduleName", "flowName", flowElement, flowEvent);

                exactly(1).of(flowElement).getFlowComponent();
                will(returnValue(splitter));
                exactly(1).of(splitter).split(flowEvent);
                will(returnValue(payloads));

                exactly(1).of(flowElement).getTransition(FlowElement.DEFAULT_TRANSITION_NAME);
                will(returnValue(flowElement));

                // first payload is already a flowEvent so nothing to create

                // second payload needs a flowEvent created
                exactly(1).of(flowEvent).getIdentifier();
                will(returnValue("identifier"));
                exactly(1).of(flowEvent).getRelatedIdentifier();
                will(returnValue("relatedIdentifier"));
                exactly(1).of(flowEvent).getTimestamp();
                will(returnValue(timestamp));
                exactly(1).of(eventFactory).newEvent("identifier", "relatedIdentifier", timestamp, payload);
                will(returnValue(flowEvent));

                // third payload is already a flowEvent so nothing to create

                // create new flowEvent for publication downstream
                exactly(1).of(eventFactory).newEvent("identifier", "relatedIdentifier", timestamp, flowEvents);
                will(returnValue(flowEvent));

                exactly(1).of(flowEventListener).afterFlowElement("moduleName", "flowName", flowElement, flowEvent);
                exactly(1).of(flowElement).getFlowElementInvoker();
                will(returnValue(flowElementInvoker));
                exactly(1).of(flowElementInvoker).invoke(flowEventListeners, "moduleName", "flowName", flowInvocationContext, flowEvent, flowElement);
                will(returnValue(null));
            }
        });

        FlowElementInvoker flowElementInvoker = new SplitterFlowElementInvoker(eventFactory);
        SplitterInvokerConfiguration configuration = new SplitterInvokerConfiguration();
        configuration.setSplitEventToListOfEvents();
        ((Configured)flowElementInvoker).setConfiguration(configuration);

        flowEventListeners.add(flowEventListener);
        flowElementInvoker.invoke(flowEventListeners, "moduleName", "flowName", flowInvocationContext, flowEvent, flowElement);
        mockery.assertIsSatisfied();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void test_splitter_flowElementInvoker_split_to_listOfPayloads()
    {
        final List payloads = new ArrayList();
        payloads.add(flowEvent);
        payloads.add(payload);
        payloads.add(flowEvent);

        long timestamp = System.currentTimeMillis();

        // expectations
        mockery.checking(new Expectations()
        {
            {
                // first call
                exactly(2).of(flowEvent).getIdentifier();
                will(returnValue(payload));
                exactly(2).of(flowEvent).getRelatedIdentifier();
                will(returnValue(payload));
                exactly(1).of(flowEvent).getTimestamp();
                will(returnValue(timestamp));
                exactly(1).of(flowInvocationContext).addElementInvocation(with(any(FlowElementInvocation.class)));
                exactly(1).of(flowInvocationContext).setLastComponentName(null);
                exactly(1).of(flowEventListener).beforeFlowElement("moduleName", "flowName", flowElement, flowEvent);

                exactly(1).of(flowElement).getFlowComponent();
                will(returnValue(splitter));
                exactly(1).of(splitter).split(flowEvent);
                will(returnValue(payloads));

                exactly(1).of(flowElement).getTransition(FlowElement.DEFAULT_TRANSITION_NAME);
                will(returnValue(flowElement));

                // create new flowEvent for publication downstream
                exactly(1).of(flowEvent).getIdentifier();
                will(returnValue("identifier"));
                exactly(1).of(flowEvent).getRelatedIdentifier();
                will(returnValue("relatedIdentifier"));
                exactly(1).of(eventFactory).newEvent("identifier", "relatedIdentifier", timestamp, payloads);
                will(returnValue(flowEvent));

                exactly(1).of(flowEventListener).afterFlowElement("moduleName", "flowName", flowElement, flowEvent);
                exactly(1).of(flowElement).getFlowElementInvoker();
                will(returnValue(flowElementInvoker));
                exactly(1).of(flowElementInvoker).invoke(flowEventListeners, "moduleName", "flowName", flowInvocationContext, flowEvent, flowElement);
                will(returnValue(null));
            }
        });

        FlowElementInvoker flowElementInvoker = new SplitterFlowElementInvoker(eventFactory);
        SplitterInvokerConfiguration configuration = new SplitterInvokerConfiguration();
        configuration.setSplitEventToListOfPayloads();
        ((Configured)flowElementInvoker).setConfiguration(configuration);

        flowEventListeners.add(flowEventListener);
        flowElementInvoker.invoke(flowEventListeners, "moduleName", "flowName", flowInvocationContext, flowEvent, flowElement);
        mockery.assertIsSatisfied();
    }

    @Test(expected = SplitterException.class)
    @SuppressWarnings("unchecked")
    public void test_splitter_flowElementInvoker_no_payload()
    {
        final List payloads = new ArrayList();

        // expectations
        mockery.checking(new Expectations()
        {
            {
                exactly(2).of(flowEvent).getIdentifier();
                will(returnValue(payload));
                exactly(2).of(flowEvent).getRelatedIdentifier();
                will(returnValue(payload));
                exactly(1).of(flowInvocationContext).addElementInvocation(with(any(FlowElementInvocation.class)));
                exactly(1).of(flowInvocationContext).setLastComponentName(null);
                exactly(1).of(flowEventListener).beforeFlowElement("moduleName", "flowName", flowElement, flowEvent);

                exactly(1).of(flowElement).getFlowComponent();
                will(returnValue(splitter));

                exactly(1).of(flowEvent).getPayload();
                will(returnValue(payload));
                exactly(1).of(splitter).split(flowEvent);
                will(throwException(new ClassCastException()));
                exactly(1).of(splitter).split(payload);
                will(returnValue(payloads));

                exactly(1).of(flowElement).getTransition(FlowElement.DEFAULT_TRANSITION_NAME);
                will(returnValue(flowElement));

                exactly(1).of(flowElement).getComponentName();
                will(returnValue("componentName"));
            }
        });

        FlowElementInvoker flowElementInvoker = new SplitterFlowElementInvoker(eventFactory);
        flowEventListeners.add(flowEventListener);
        flowElementInvoker.invoke(flowEventListeners, "moduleName", "flowName", flowInvocationContext, flowEvent, flowElement);

        mockery.assertIsSatisfied();
    }

    @Test(expected = SplitterException.class)
    @SuppressWarnings("unchecked")
    public void test_splitter_flowElementInvoker_null_payload()
    {
        // expectations
        mockery.checking(new Expectations()
        {
            {
                exactly(2).of(flowEvent).getIdentifier();
                will(returnValue(payload));
                exactly(2).of(flowEvent).getRelatedIdentifier();
                will(returnValue(payload));
                exactly(1).of(flowInvocationContext).addElementInvocation(with(any(FlowElementInvocation.class)));
                exactly(1).of(flowInvocationContext).setLastComponentName(null);
                exactly(1).of(flowEventListener).beforeFlowElement("moduleName", "flowName", flowElement, flowEvent);

                exactly(1).of(flowElement).getFlowComponent();
                will(returnValue(splitter));

                exactly(1).of(flowEvent).getPayload();
                will(returnValue(payload));
                exactly(1).of(splitter).split(flowEvent);
                will(throwException(new ClassCastException()));
                exactly(1).of(splitter).split(payload);
                will(returnValue(null));

                exactly(1).of(flowElement).getTransition(FlowElement.DEFAULT_TRANSITION_NAME);
                will(returnValue(flowElement));

                exactly(1).of(flowElement).getComponentName();
                will(returnValue("componentName"));
            }
        });

        FlowElementInvoker flowElementInvoker = new SplitterFlowElementInvoker(eventFactory);
        flowEventListeners.add(flowEventListener);
        flowElementInvoker.invoke(flowEventListeners, "moduleName", "flowName", flowInvocationContext, flowEvent, flowElement);

        mockery.assertIsSatisfied();
    }

    @Test(expected = InvalidFlowException.class)
    @SuppressWarnings("unchecked")
    public void test_splitter_flowElementInvoker_null_flowElement()
    {
        // expectations
        mockery.checking(new Expectations()
        {
            {
                exactly(2).of(flowEvent).getIdentifier();
                will(returnValue(payload));
                exactly(2).of(flowEvent).getRelatedIdentifier();
                will(returnValue(payload));
                exactly(1).of(flowInvocationContext).addElementInvocation(with(any(FlowElementInvocation.class)));
                exactly(1).of(flowInvocationContext).setLastComponentName(null);
                exactly(1).of(flowEventListener).beforeFlowElement("moduleName", "flowName", flowElement, flowEvent);

                exactly(1).of(flowElement).getFlowComponent();
                will(returnValue(splitter));

                exactly(1).of(flowEvent).getPayload();
                will(returnValue(payload));
                exactly(1).of(splitter).split(flowEvent);
                will(throwException(new ClassCastException()));
                exactly(1).of(splitter).split(payload);
                will(returnValue(null));

                exactly(1).of(flowElement).getTransition(FlowElement.DEFAULT_TRANSITION_NAME);
                will(returnValue(null));
                exactly(1).of(flowElement).getComponentName();
                will(returnValue("componentName"));
            }
        });

        FlowElementInvoker flowElementInvoker = new SplitterFlowElementInvoker(eventFactory);
        flowEventListeners.add(flowEventListener);
        flowElementInvoker.invoke(flowEventListeners, "moduleName", "flowName", flowInvocationContext, flowEvent, flowElement);

        mockery.assertIsSatisfied();
    }
}
