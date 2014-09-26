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

import org.ikasan.flow.event.FlowEventFactory;
import org.ikasan.spec.component.endpoint.Broker;
import org.ikasan.spec.flow.FlowEvent;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Supports testing of the VisitingFlowElementInvoker
 */
public class VisitingFlowElementInvokerTest
{
    /**
     * Mockery for mocking concrete classes
     */
    private Mockery mockery = new Mockery()
    {{
            setImposteriser(ClassImposteriser.INSTANCE);
    }};

    private Broker broker = mockery.mock(Broker.class, "broker");

    private static VisitingFlowElementInvoker visitingFlowElementInvoker;


    @BeforeClass
    public static void beforeClass()
    {
        visitingFlowElementInvoker = new VisitingFlowElementInvoker(null);
    }


    // IKASAN-706 Tests simple fix for Broker that returns a FlowEvent object
    @Test
    @SuppressWarnings("unchecked")
    public void test_noop_flowevent_broker()
    {
        final FlowEvent originalFlowEvent = new FlowEventFactory().newEvent("orig_id", new Object());

        Expectations expectations = new Expectations();
        expectations.one(broker).invoke(originalFlowEvent);
        expectations.will(Expectations.returnValue(originalFlowEvent));
        mockery.checking(expectations);

        visitingFlowElementInvoker.invoke("module", "flow", new DefaultFlowInvocationContext(),
                originalFlowEvent, new FlowElementImpl("component", broker));

        Assert.assertNotNull(originalFlowEvent);
        Assert.assertNotNull(originalFlowEvent.toString());
    }

    // IKASAN-706 Tests simple fix for Broker that returns a FlowEvent object
    @Test
    @SuppressWarnings("unchecked")
    public void test_modifying_floweventpayload_broker()
    {
        FlowEvent originalFlowEvent = new FlowEventFactory().newEvent("orig_id", new Object());
        FlowEvent brokerFlowEvent = new FlowEventFactory().newEvent("orig_id", "");

        Expectations expectations = new Expectations();
        expectations.one(broker).invoke(originalFlowEvent);
        expectations.will(Expectations.returnValue(brokerFlowEvent));
        mockery.checking(expectations);

        visitingFlowElementInvoker.invoke("module", "flow", new DefaultFlowInvocationContext(),
                originalFlowEvent, new FlowElementImpl("component", broker));

        Assert.assertTrue(originalFlowEvent.getPayload() instanceof String);
    }

}
