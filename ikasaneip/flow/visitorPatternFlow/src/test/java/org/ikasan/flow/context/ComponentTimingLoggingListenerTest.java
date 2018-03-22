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
package org.ikasan.flow.context;

import org.ikasan.spec.flow.FlowElement;
import org.ikasan.spec.flow.FlowElementInvocation;
import org.ikasan.spec.flow.FlowInvocationContext;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.concurrent.Synchroniser;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Test cases for the <code>ComponentTimingLoggerListener</code>
 *
 * @author Ikasan Development Team
 */
public class ComponentTimingLoggingListenerTest
{
    /**
     * Mockery for mocking concrete classes
     */
    private Mockery mockery = new Mockery()
    {{
            setImposteriser(ClassImposteriser.INSTANCE);
            setThreadingPolicy(new Synchroniser());
    }};

    protected Logger logger = mockery.mock(Logger.class);

    protected FlowInvocationContext flowInvocationContext = mockery.mock(FlowInvocationContext.class);
    protected FlowElementInvocation flowElementInvocation = mockery.mock(FlowElementInvocation.class);
    protected FlowElement flowElement = mockery.mock(FlowElement.class);

    @Test
    public void test_log()
    {
        final List<FlowElementInvocation> invocations = new ArrayList<>();
        invocations.add(flowElementInvocation);

        ComponentTimingLoggingListener listener = new ComponentTimingLoggingListener();
        ComponentTimingLoggingListener.logger = logger;

        mockery.checking(new Expectations()
        {
            {
                exactly(3).of(flowInvocationContext).getElementInvocations();
                will(returnValue(invocations));

                exactly(1).of(flowInvocationContext).getFlowStartTimeMillis();
                will(returnValue(123456789000L));
                exactly(1).of(flowInvocationContext).getFlowEndTimeMillis();
                will(returnValue(123456790000L));

                exactly(1).of(flowElementInvocation).getBeforeIdentifier();
                will(returnValue("id"));
                exactly(1).of(flowElementInvocation).getFlowElement();
                will(returnValue(flowElement));
                exactly(1).of(flowElementInvocation).getStartTimeMillis();
                will(returnValue(123456789000L));
                exactly(1).of(flowElementInvocation).getEndTimeMillis();
                will(returnValue(123456790000L));
                exactly(1).of(flowElement).getComponentName();
                will(returnValue("componentName"));

                exactly(1).of(flowElementInvocation).getCustomMetrics();
                will(returnValue(null));

                exactly(1).of(logger).info("Flow Invocation: ID [id] Start [123456789000] End [123456790000] [Element [componentName] Time [1000ms]] ");

            }
        });

        listener.endFlow(flowInvocationContext);

        mockery.assertIsSatisfied();
    }
}
