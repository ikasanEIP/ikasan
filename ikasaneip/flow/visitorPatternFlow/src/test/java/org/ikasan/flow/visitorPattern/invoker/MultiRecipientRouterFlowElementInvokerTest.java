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

import org.ikasan.flow.visitorPattern.InvalidFlowException;
import org.ikasan.spec.component.routing.Router;
import org.ikasan.spec.event.ReplicationFactory;
import org.ikasan.spec.flow.*;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Supports testing of the RouterFlowElementInvoker
 */
public class MultiRecipientRouterFlowElementInvokerTest
{
    /**
     * Mockery for mocking concrete classes
     */
    private Mockery mockery = new Mockery()
    {{
            setImposteriser(ClassImposteriser.INSTANCE);
    }};

    private FlowEventListener flowEventListener = mockery.mock(FlowEventListener.class, "flowEventListener");
    private FlowInvocationContext flowInvocationContext = mockery.mock(FlowInvocationContext.class, "flowInvocationContext");
    private FlowEvent flowEvent = mockery.mock(FlowEvent.class, "flowEvent");
    private FlowElement flowElement = mockery.mock(FlowElement.class, "flowElement");
    private FlowElementInvoker flowElementInvoker = mockery.mock(FlowElementInvoker.class, "flowElementInvoker");
    private Router router = mockery.mock(Router.class, "router");
    private ReplicationFactory replicationFactory = mockery.mock(ReplicationFactory.class, "replicationFactory");
    private Map payload = mockery.mock(Map.class, "payload");

    @Test(expected = IllegalArgumentException.class)
    @SuppressWarnings("unchecked")
    public void test_failed_constructor()
    {
        new MultiRecipientRouterFlowElementInvoker(null);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void test_router_flowElementInvoker_single_target()
    {
        final List<String> routes = new ArrayList<String>();
        routes.add("one");

        // expectations
        mockery.checking(new Expectations()
        {
            {
                exactly(1).of(flowElement).getComponentName();
                will(returnValue("componentName"));
                exactly(1).of(flowInvocationContext).addInvokedComponentName("componentName");
                exactly(1).of(flowEventListener).beforeFlowElement("moduleName", "flowName", flowElement, flowEvent);

                exactly(1).of(flowElement).getFlowComponent();
                will(returnValue(router));
                exactly(1).of(flowEvent).getPayload();
                will(returnValue(payload));
                exactly(1).of(router).route(payload);
                will(returnValue(routes));

                exactly(1).of(flowEventListener).afterFlowElement("moduleName", "flowName", flowElement, flowEvent);
                exactly(1).of(flowElement).getTransition("one");
                will(returnValue(flowElement));
            }
        });

        FlowElementInvoker flowElementInvoker = new MultiRecipientRouterFlowElementInvoker(replicationFactory);
        flowElementInvoker.invoke(flowEventListener, "moduleName", "flowName", flowInvocationContext, flowEvent, flowElement);

        mockery.assertIsSatisfied();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void test_router_flowElementInvoker_multiple_targets()
    {
        final List<String> routes = new ArrayList<String>();
        routes.add("one");
        routes.add("two");

        // expectations
        mockery.checking(new Expectations()
        {
            {
                exactly(1).of(flowElement).getComponentName();
                will(returnValue("componentName"));
                exactly(1).of(flowInvocationContext).addInvokedComponentName("componentName");
                exactly(1).of(flowEventListener).beforeFlowElement("moduleName", "flowName", flowElement, flowEvent);

                exactly(1).of(flowElement).getFlowComponent();
                will(returnValue(router));
                exactly(1).of(flowEvent).getPayload();
                will(returnValue(payload));
                exactly(1).of(router).route(payload);
                will(returnValue(routes));

                exactly(1).of(flowEventListener).afterFlowElement("moduleName", "flowName", flowElement, flowEvent);
                exactly(1).of(flowElement).getTransition("one");
                will(returnValue(flowElement));
                exactly(1).of(replicationFactory).replicate(flowEvent);
                will(returnValue(flowEvent));
                exactly(1).of(flowElement).getFlowElementInvoker();
                will(returnValue(flowElementInvoker));
                exactly(1).of(flowElementInvoker).invoke(flowEventListener, "moduleName", "flowName", flowInvocationContext, flowEvent, flowElement);
                will(returnValue(flowElement));
                exactly(1).of(flowElement).getFlowElementInvoker();
                will(returnValue(flowElementInvoker));
                exactly(1).of(flowElementInvoker).invoke(flowEventListener, "moduleName", "flowName", flowInvocationContext, flowEvent, flowElement);
                will(returnValue(null));

                exactly(1).of(flowElement).getTransition("two");
                will(returnValue(flowElement));
                exactly(1).of(replicationFactory).replicate(flowEvent);
                will(returnValue(flowEvent));
                exactly(1).of(flowElement).getFlowElementInvoker();
                will(returnValue(flowElementInvoker));
                exactly(1).of(flowElementInvoker).invoke(flowEventListener, "moduleName", "flowName", flowInvocationContext, flowEvent, flowElement);
                will(returnValue(flowElement));
                exactly(1).of(flowElement).getFlowElementInvoker();
                will(returnValue(flowElementInvoker));
                exactly(1).of(flowElementInvoker).invoke(flowEventListener, "moduleName", "flowName", flowInvocationContext, flowEvent, flowElement);
                will(returnValue(null));
            }
        });

        FlowElementInvoker flowElementInvoker = new MultiRecipientRouterFlowElementInvoker(replicationFactory);
        flowElementInvoker.invoke(flowEventListener, "moduleName", "flowName", flowInvocationContext, flowEvent, flowElement);

        mockery.assertIsSatisfied();
    }

    @Test(expected = InvalidFlowException.class)
    @SuppressWarnings("unchecked")
    public void test_router_flowElementInvoker_null_target()
    {
        // expectations
        mockery.checking(new Expectations()
        {
            {
                exactly(1).of(flowElement).getComponentName();
                will(returnValue("componentName"));
                exactly(1).of(flowInvocationContext).addInvokedComponentName("componentName");
                exactly(1).of(flowEventListener).beforeFlowElement("moduleName", "flowName", flowElement, flowEvent);

                exactly(1).of(flowElement).getFlowComponent();
                will(returnValue(router));
                exactly(1).of(flowEvent).getPayload();
                will(returnValue(payload));
                exactly(1).of(router).route(payload);
                will(returnValue(null));
                exactly(1).of(flowElement).getComponentName();
                will(returnValue("componentName"));
            }
        });

        FlowElementInvoker flowElementInvoker = new MultiRecipientRouterFlowElementInvoker(replicationFactory);
        flowElementInvoker.invoke(flowEventListener, "moduleName", "flowName", flowInvocationContext, flowEvent, flowElement);

        mockery.assertIsSatisfied();
    }

    @Test(expected = InvalidFlowException.class)
    @SuppressWarnings("unchecked")
    public void test_router_flowElementInvoker_no_target()
    {
        final List<String> routes = new ArrayList<String>();

        // expectations
        mockery.checking(new Expectations()
        {
            {
                exactly(1).of(flowElement).getComponentName();
                will(returnValue("componentName"));
                exactly(1).of(flowInvocationContext).addInvokedComponentName("componentName");
                exactly(1).of(flowEventListener).beforeFlowElement("moduleName", "flowName", flowElement, flowEvent);

                exactly(1).of(flowElement).getFlowComponent();
                will(returnValue(router));
                exactly(1).of(flowEvent).getPayload();
                will(returnValue(payload));
                exactly(1).of(router).route(payload);
                will(returnValue(routes));
                exactly(1).of(flowElement).getComponentName();
                will(returnValue("componentName"));
            }
        });

        FlowElementInvoker flowElementInvoker = new MultiRecipientRouterFlowElementInvoker(replicationFactory);
        flowElementInvoker.invoke(flowEventListener, "moduleName", "flowName", flowInvocationContext, flowEvent, flowElement);

        mockery.assertIsSatisfied();
    }

    @Test(expected = InvalidFlowException.class)
    @SuppressWarnings("unchecked")
    public void test_router_flowElementInvoker_no_next_flow_element_for_single_route()
    {
        final List<String> routes = new ArrayList<String>();
        routes.add("one");

        // expectations
        mockery.checking(new Expectations()
        {
            {
                exactly(1).of(flowElement).getComponentName();
                will(returnValue("componentName"));
                exactly(1).of(flowInvocationContext).addInvokedComponentName("componentName");
                exactly(1).of(flowEventListener).beforeFlowElement("moduleName", "flowName", flowElement, flowEvent);

                exactly(1).of(flowElement).getFlowComponent();
                will(returnValue(router));
                exactly(1).of(flowEvent).getPayload();
                will(returnValue(payload));
                exactly(1).of(router).route(payload);
                will(returnValue(routes));

                exactly(1).of(flowEventListener).afterFlowElement("moduleName", "flowName", flowElement, flowEvent);
                exactly(1).of(flowElement).getTransition("one");
                will(returnValue(null));
                exactly(1).of(flowElement).getComponentName();
                will(returnValue("componentName"));
            }
        });

        FlowElementInvoker flowElementInvoker = new MultiRecipientRouterFlowElementInvoker(replicationFactory);
        flowElementInvoker.invoke(flowEventListener, "moduleName", "flowName", flowInvocationContext, flowEvent, flowElement);

        mockery.assertIsSatisfied();
    }

    @Test(expected = InvalidFlowException.class)
    @SuppressWarnings("unchecked")
    public void test_router_flowElementInvoker_no_next_flow_multiple_targets()
    {
        final List<String> routes = new ArrayList<String>();
        routes.add("one");
        routes.add("two");

        // expectations
        mockery.checking(new Expectations()
        {
            {
                exactly(1).of(flowElement).getComponentName();
                will(returnValue("componentName"));
                exactly(1).of(flowInvocationContext).addInvokedComponentName("componentName");
                exactly(1).of(flowEventListener).beforeFlowElement("moduleName", "flowName", flowElement, flowEvent);

                exactly(1).of(flowElement).getFlowComponent();
                will(returnValue(router));
                exactly(1).of(flowEvent).getPayload();
                will(returnValue(payload));
                exactly(1).of(router).route(payload);
                will(returnValue(routes));

                exactly(1).of(flowEventListener).afterFlowElement("moduleName", "flowName", flowElement, flowEvent);
                exactly(1).of(flowElement).getTransition("one");
                will(returnValue(flowElement));
                exactly(1).of(replicationFactory).replicate(flowEvent);
                will(returnValue(flowEvent));
                exactly(1).of(flowElement).getFlowElementInvoker();
                will(returnValue(flowElementInvoker));
                exactly(1).of(flowElementInvoker).invoke(flowEventListener, "moduleName", "flowName", flowInvocationContext, flowEvent, flowElement);
                will(returnValue(null));

                exactly(1).of(flowElement).getTransition("two");
                will(returnValue(null));
                exactly(1).of(flowElement).getComponentName();
                will(returnValue("componentName"));
            }
        });

        FlowElementInvoker flowElementInvoker = new MultiRecipientRouterFlowElementInvoker(replicationFactory);
        flowElementInvoker.invoke(flowEventListener, "moduleName", "flowName", flowInvocationContext, flowEvent, flowElement);

        mockery.assertIsSatisfied();
    }
}
