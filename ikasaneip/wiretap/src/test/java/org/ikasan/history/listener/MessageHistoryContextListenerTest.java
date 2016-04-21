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
package org.ikasan.history.listener;

import org.ikasan.spec.flow.FinalAction;
import org.ikasan.spec.flow.FlowInvocationContext;
import org.ikasan.spec.flow.FlowInvocationContextListener;
import org.ikasan.spec.history.MessageHistoryService;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.concurrent.Synchroniser;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Test cases for MessageHistoryContextListener
 *
 * @author Ikasan Development Team
 */
public class MessageHistoryContextListenerTest<T>
{
    private final Mockery mockery = new Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
            setThreadingPolicy(new Synchroniser());
        }
    };

    @SuppressWarnings("unchecked")
    MessageHistoryService<FlowInvocationContext, T> messageHistoryService = mockery.mock(MessageHistoryService.class);

    FlowInvocationContext flowInvocationContext = mockery.mock(FlowInvocationContext.class);

    @Test(expected = IllegalArgumentException.class)
    public void test_constructor_null_service()
    {
        new MessageHistoryContextListener<>(null, "moduleName", "flowName");
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_constructor_null_moduleName()
    {
        new MessageHistoryContextListener<>(messageHistoryService, null, "flowName");
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_constructor_null_flowName()
    {
        new MessageHistoryContextListener<>(messageHistoryService, "moduleName", null);
    }

    @Test
    public void test_successful_endFlow_publish()
    {
        mockery.checking(new Expectations(){{
            exactly(2).of(flowInvocationContext).getFinalAction();
            will(returnValue(FinalAction.PUBLISH));
            oneOf(messageHistoryService).save(flowInvocationContext, "moduleName", "flowName");
        }});
        FlowInvocationContextListener listener = new MessageHistoryContextListener<>(messageHistoryService, "moduleName", "flowName");
        listener.endFlow(flowInvocationContext);
        mockery.assertIsSatisfied();
    }

    @Test
    public void test_successful_endFlow_null_action()
    {
        mockery.checking(new Expectations(){{
            oneOf(flowInvocationContext).getFinalAction();
            will(returnValue(null));
            oneOf(messageHistoryService).save(flowInvocationContext, "moduleName", "flowName");
        }});
        FlowInvocationContextListener listener = new MessageHistoryContextListener<>(messageHistoryService, "moduleName", "flowName");
        listener.endFlow(flowInvocationContext);
        mockery.assertIsSatisfied();
    }

    @Test
    public void test_successful_endFlow_withLogException()
    {
        mockery.checking(new Expectations(){{
            exactly(2).of(flowInvocationContext).getFinalAction();
            will(returnValue(FinalAction.PUBLISH));
            oneOf(messageHistoryService).save(flowInvocationContext, "moduleName", "flowName");
            will(throwException(new RuntimeException()));
        }});
        FlowInvocationContextListener listener = new MessageHistoryContextListener<>(messageHistoryService, "moduleName", "flowName");
        listener.endFlow(flowInvocationContext);
        mockery.assertIsSatisfied();
    }

    @Test(expected = RuntimeException.class)
    public void test_successful_endFlow_withRethrowException()
    {
        mockery.checking(new Expectations(){{
            exactly(2).of(flowInvocationContext).getFinalAction();
            will(returnValue(FinalAction.PUBLISH));
            oneOf(messageHistoryService).save(flowInvocationContext, "moduleName", "flowName");
            will(throwException(new RuntimeException()));
        }});
        MessageHistoryContextListener listener = new MessageHistoryContextListener<>(messageHistoryService, "moduleName", "flowName");
        listener.setRethrowServiceExceptions(true);
        listener.endFlow(flowInvocationContext);
        mockery.assertIsSatisfied();
    }
}
