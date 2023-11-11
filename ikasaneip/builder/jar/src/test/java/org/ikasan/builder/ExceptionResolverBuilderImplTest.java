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
package org.ikasan.builder;

import org.ikasan.exceptionResolver.ExceptionResolver;
import org.ikasan.exceptionResolver.action.*;
import org.ikasan.flow.visitorPattern.FlowElementImpl;
import org.ikasan.spec.flow.FlowElement;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * This test class supports the <code>ExceptionResolverBuilderImpl</code> class.
 *
 * @author Ikasan Development Team
 */
class ExceptionResolverBuilderImplTest
{
    /**
     * Test.
     */
    @Test
    void test_successful_exceptionResolver_all_actions()
    {
        ExceptionResolverBuilder exceptionResolverBuilder = new ExceptionResolverBuilderImpl();
        exceptionResolverBuilder.addExceptionToAction(ExceptionOne.class, OnException.ignoreException());
        exceptionResolverBuilder.addExceptionToAction(ExceptionTwo.class, OnException.stop());
        exceptionResolverBuilder.addExceptionToAction(ExceptionThree.class, OnException.excludeEvent());
        exceptionResolverBuilder.addExceptionToAction(ExceptionFour.class, OnException.retryIndefinitely());
        exceptionResolverBuilder.addExceptionToAction(ExceptionFourDelayOverride.class, OnException.retryIndefinitely(1L));
        exceptionResolverBuilder.addExceptionToAction(ExceptionFive.class, OnException.retry(1,1));
        exceptionResolverBuilder.addExceptionToAction(ExceptionSix.class, OnException.scheduledCronRetry("0 * 14 * * ?", 1));
        ExceptionResolver exceptionResolver = exceptionResolverBuilder.build();

        ExceptionAction action = exceptionResolver.resolve(null, new ExceptionOne());
        assertEquals(action, IgnoreAction.instance(), "ExceptionAction should be Ignore");

        action = exceptionResolver.resolve(null, new ExceptionTwo());
        assertEquals(action, StopAction.instance(), "ExceptionAction should be Stop");

        action = exceptionResolver.resolve(null, new ExceptionThree());
        assertEquals(action, ExcludeEventAction.instance(), "ExceptionAction should be Exclude");

        RetryAction actualRetryAction = exceptionResolver.resolve(null, new ExceptionFour());
        RetryAction expectedRetryAction = new RetryAction();
        assertEquals(actualRetryAction.getDelay(), expectedRetryAction.getDelay(), "ExceptionAction should be Retry with default delay");
        assertEquals(actualRetryAction.getMaxRetries(), expectedRetryAction.getMaxRetries(), "ExceptionAction should be Retry with indefinite maxRetries");

        RetryAction actualRetryActionDelayOverride = exceptionResolver.resolve(null, new ExceptionFourDelayOverride());
        RetryAction expectedRetryActionDelayOverride = new RetryAction();
        expectedRetryActionDelayOverride.setDelay(1l);
        assertEquals(actualRetryActionDelayOverride.getDelay(), expectedRetryActionDelayOverride.getDelay(), "ExceptionAction should be Retry with override delay");
        assertEquals(actualRetryActionDelayOverride.getMaxRetries(), expectedRetryActionDelayOverride.getMaxRetries(), "ExceptionAction should be Retry with indefinite maxRetries");

        actualRetryAction = exceptionResolver.resolve(null, new ExceptionFive());
        expectedRetryAction = new RetryAction(1, 1);
        assertEquals(actualRetryAction.getDelay(), expectedRetryAction.getDelay(), "ExceptionAction should be Retry with delay 1");
        assertEquals(actualRetryAction.getMaxRetries(), expectedRetryAction.getMaxRetries(), "ExceptionAction should be Retry with maxRetries 1");

        ScheduledRetryAction actualScheduledRetryAction = exceptionResolver.resolve(null, new ExceptionSix());
        ScheduledRetryAction expectedScheduldeRetryAction = new ScheduledRetryAction("0 * 14 * * ?", 1);
        assertEquals("0 * 14 * * ?", actualScheduledRetryAction.getCronExpression(), "ExceptionAction should be ScheduledRetry with cron '0 * 14 * * ?'");
        assertEquals(expectedScheduldeRetryAction.getMaxRetries(), expectedRetryAction.getMaxRetries(), "ExceptionAction should be ScheduledRetry with maxRetries 1");
    }

    /**
     * Test.
     */
    @Test
    void test_successful_component_specific_exceptionResolver()
    {
        ExceptionResolverBuilder exceptionResolverBuilder = new ExceptionResolverBuilderImpl();
        exceptionResolverBuilder.addExceptionToAction("componentOne", ExceptionOne.class, OnException.ignoreException());
        exceptionResolverBuilder.addExceptionToAction("componentTwo", ExceptionOne.class, OnException.excludeEvent());
        exceptionResolverBuilder.addExceptionToAction(ExceptionOne.class, OnException.stop());
        ExceptionResolver exceptionResolver = exceptionResolverBuilder.build();

        // specific action for componentTwo
        ExceptionAction action = exceptionResolver.resolve("componentTwo", new ExceptionOne());
        assertEquals(action, ExcludeEventAction.instance(), "ExceptionAction should be Exclude");

        // action for undefined component
        action = exceptionResolver.resolve(null, new ExceptionOne());
        assertEquals(action, StopAction.instance(), "ExceptionAction should be Stop");

        // specific action for componentOne
        action = exceptionResolver.resolve("componentOne", new ExceptionOne());
        assertEquals(action, IgnoreAction.instance(), "ExceptionAction should be Ignore");
    }

    /**
     * Test.
     */
    @Test
    void test_successful_component_specific_exceptionResolver_for_flowElement()
    {
        FlowElement flowElement1 = new FlowElementImpl("componentOne", null, null);
        FlowElement flowElement2 = new FlowElementImpl("componentTwo", null, null);

        ExceptionResolverBuilder exceptionResolverBuilder = new ExceptionResolverBuilderImpl();
        exceptionResolverBuilder.addExceptionToAction(flowElement1, ExceptionOne.class, OnException.ignoreException());
        exceptionResolverBuilder.addExceptionToAction(flowElement2, ExceptionOne.class, OnException.excludeEvent());
        exceptionResolverBuilder.addExceptionToAction(ExceptionOne.class, OnException.stop());
        ExceptionResolver exceptionResolver = exceptionResolverBuilder.build();

        // specific action for componentTwo
        ExceptionAction action = exceptionResolver.resolve("componentTwo", new ExceptionOne());
        assertEquals(action, ExcludeEventAction.instance(), "ExceptionAction should be Exclude");

        // action for undefined component
        action = exceptionResolver.resolve(null, new ExceptionOne());
        assertEquals(action, StopAction.instance(), "ExceptionAction should be Stop");

        // specific action for componentOne
        action = exceptionResolver.resolve("componentOne", new ExceptionOne());
        assertEquals(action, IgnoreAction.instance(), "ExceptionAction should be Ignore");
    }

    class ExceptionOne extends Exception {}

    class ExceptionTwo extends Exception {}

    class ExceptionThree extends Exception {}

    class ExceptionFour extends Exception {}

    class ExceptionFourDelayOverride extends Exception {}

    class ExceptionFive extends Exception {}

    class ExceptionSix extends Exception {}
}