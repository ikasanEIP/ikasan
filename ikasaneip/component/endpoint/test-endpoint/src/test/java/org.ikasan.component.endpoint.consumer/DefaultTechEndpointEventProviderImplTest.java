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
package org.ikasan.component.endpoint.consumer;

import org.ikasan.component.endpoint.consumer.event.*;
import org.ikasan.spec.event.ExceptionListener;
import org.ikasan.spec.event.MessageListener;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for DefaultTechEndpointEventProviderImpl.
 * 
 * @author Ikasan Development Team
 */
public class DefaultTechEndpointEventProviderImplTest
{
    /**
     * Mockery for mocking concrete classes
     */
    private Mockery mockery = new Mockery() {{
        setImposteriser(ClassImposteriser.INSTANCE);
    }};

    private TechEndpointEventFactory techEndpointEventFactory = mockery.mock(TechEndpointEventFactory.class);
    private MessageListener messageListener = mockery.mock(MessageListener.class);
    private ExceptionListener exceptionListener = mockery.mock(ExceptionListener.class);

    TechEndpointEventFactory techEndpointExecutableEventFactory;

    @Before
    public void setup()
    {
        techEndpointExecutableEventFactory = new
                TechEndpointExecutableEventFactoryImpl(messageListener, exceptionListener);
    }

    /**
     * Test
     */
    @Test(expected = IllegalArgumentException.class)
    public void test_failed_constructor_null_techEndpointEventFactory()
    {
        new DefaultTechEndpointEventProviderImpl(null);
    }

    /**
     * Test
     */
    @Test(expected = IllegalArgumentException.class)
    public void test_failed_constructor_null_techEndpointEventFactory_with_eventProvider()
    {
        new DefaultTechEndpointEventProviderImpl(null, null);
    }

    /**
     * Test
     */
    @Test(expected = IllegalArgumentException.class)
    public void test_failed_constructor_with_null_eventProvider()
    {
        new DefaultTechEndpointEventProviderImpl(techEndpointEventFactory, null);
    }

    /**
     * Test
     */
    @Test
    public void test_constructor_with_eventProvider()
    {
        Exception exception = new Exception();

        TechEndpointEventProvider inTechEndpointEventProvider = TechEndpointEventProvider.with()
                .messageEvent("messageEvent")
                .messageEvent("lifeId", "messageEventWithLifeId")
                .exceptionEvent(exception)
                .interval(10L)
                .build();

        TechEndpointEventProvider<APIEvent> outTechEndpointEventProvider =
                new DefaultTechEndpointEventProviderImpl(techEndpointExecutableEventFactory, inTechEndpointEventProvider);

        APIEvent executableEvent = outTechEndpointEventProvider.consumeEvent();
        Assert.assertTrue(executableEvent.isMessageEvent());
        Assert.assertTrue(executableEvent instanceof APIExecutableEvent);

        executableEvent = outTechEndpointEventProvider.consumeEvent();
        Assert.assertTrue(executableEvent.isMessageEvent());
        Assert.assertTrue(executableEvent instanceof APIExecutableEvent);

        executableEvent = outTechEndpointEventProvider.consumeEvent();
        Assert.assertTrue(executableEvent.isExceptionEvent());
        Assert.assertTrue(executableEvent instanceof APIExecutableEvent);

        executableEvent = outTechEndpointEventProvider.consumeEvent();
        Assert.assertTrue(executableEvent.isIntervalEvent());
        Assert.assertTrue(executableEvent instanceof APIExecutableEvent);

        Assert.assertNull(outTechEndpointEventProvider.consumeEvent());
    }

    /**
     * Test
     */
    @Test
    public void test_constructor_with_eventProvider_with_repeat()
    {
        Exception exception = new Exception();

        TechEndpointEventProvider inTechEndpointEventProvider = TechEndpointEventProvider.with()
                .messageEvent("messageEvent")
                .messageEvent("lifeId", "messageEventWithLifeId")
                .repeat(1)
                .exceptionEvent(exception)
                .interval(10L)
                .build();

        TechEndpointEventProvider<APIEvent> outTechEndpointEventProvider =
                new DefaultTechEndpointEventProviderImpl(techEndpointExecutableEventFactory, inTechEndpointEventProvider);

        APIEvent executableEvent = outTechEndpointEventProvider.consumeEvent();
        Assert.assertTrue(executableEvent.isMessageEvent());
        Assert.assertTrue(executableEvent instanceof APIExecutableEvent);

        executableEvent = outTechEndpointEventProvider.consumeEvent();
        Assert.assertTrue(executableEvent.isMessageEvent());
        Assert.assertTrue(executableEvent instanceof APIExecutableEvent);

        executableEvent = outTechEndpointEventProvider.consumeEvent();
        Assert.assertTrue(executableEvent.isExceptionEvent());
        Assert.assertTrue(executableEvent instanceof APIExecutableEvent);

        executableEvent = outTechEndpointEventProvider.consumeEvent();
        Assert.assertTrue(executableEvent.isIntervalEvent());
        Assert.assertTrue(executableEvent instanceof APIExecutableEvent);

        Assert.assertNull(outTechEndpointEventProvider.consumeEvent());
    }

    /**
     * Test
     */
    @Test
    public void test_send_with_getEvent()
    {
        Exception giveMeAnA = new Exception("Give me an A");
        TechEndpointEventProvider<APIEvent> techEndpointEventProvider = TechEndpointEventProvider.with()
                .messageEvent("Give me an I")
                .messageEvent("lifeId", "Give me an K")
                .exceptionEvent(giveMeAnA)
                .interval(10)
                .build();

        APIEvent apiEvent = techEndpointEventProvider.consumeEvent();
        Assert.assertTrue(apiEvent.isMessageEvent());
        Assert.assertTrue("Give me an I".equals(((APIMessageEvent)apiEvent).getLifeIdentifer()));
        Assert.assertTrue("Give me an I".equals(((APIMessageEvent)apiEvent).getPayload()));

        apiEvent = techEndpointEventProvider.consumeEvent();
        Assert.assertTrue(apiEvent.isMessageEvent());
        Assert.assertTrue("lifeId".equals(((APIMessageEvent)apiEvent).getLifeIdentifer()));
        Assert.assertTrue("Give me an K".equals(((APIMessageEvent)apiEvent).getPayload()));

        apiEvent = techEndpointEventProvider.consumeEvent();
        Assert.assertTrue(apiEvent.isExceptionEvent());
        Assert.assertEquals(giveMeAnA, ((APIExceptionEvent)apiEvent).getPayload());

        apiEvent = techEndpointEventProvider.consumeEvent();
        long interval = ((APIIntervalEvent)apiEvent).getPayload();
        Assert.assertTrue(10l == interval);

        Assert.assertNull(techEndpointEventProvider.consumeEvent());
    }

    /**
     * Test
     */
    @Test
    public void test_send_with_repeat()
    {
        Exception giveMeAnA = new Exception("Give me an A");
        TechEndpointEventProvider<APIEvent> techEndpointEventProvider = TechEndpointEventProvider.with()
                .messageEvent("Give me an I").repeat(1)
                .messageEvent("lifeId", "Give me an K").repeat(2)
                .exceptionEvent(giveMeAnA).repeat(3)
                .interval(10).repeat(4)
                .build();

        // repeat 1
        APIEvent apiEvent = techEndpointEventProvider.consumeEvent();
        Assert.assertTrue(apiEvent.isMessageEvent());
        Assert.assertTrue("Give me an I".equals(((APIMessageEvent)apiEvent).getLifeIdentifer()));
        Assert.assertTrue("Give me an I".equals(((APIMessageEvent)apiEvent).getPayload()));

        // repeat 2
        apiEvent = techEndpointEventProvider.consumeEvent();
        Assert.assertTrue(apiEvent.isMessageEvent());
        Assert.assertTrue("lifeId".equals(((APIMessageEvent)apiEvent).getLifeIdentifer()));
        Assert.assertTrue("Give me an K".equals(((APIMessageEvent)apiEvent).getPayload()));
        apiEvent = techEndpointEventProvider.consumeEvent();
        Assert.assertTrue(apiEvent.isMessageEvent());
        Assert.assertTrue("lifeId".equals(((APIMessageEvent)apiEvent).getLifeIdentifer()));
        Assert.assertTrue("Give me an K".equals(((APIMessageEvent)apiEvent).getPayload()));

        // repeat 3
        apiEvent = techEndpointEventProvider.consumeEvent();
        Assert.assertTrue(apiEvent.isExceptionEvent());
        Assert.assertEquals(giveMeAnA, ((APIExceptionEvent)apiEvent).getPayload());
        apiEvent = techEndpointEventProvider.consumeEvent();
        Assert.assertTrue(apiEvent.isExceptionEvent());
        Assert.assertEquals(giveMeAnA, ((APIExceptionEvent)apiEvent).getPayload());
        apiEvent = techEndpointEventProvider.consumeEvent();
        Assert.assertTrue(apiEvent.isExceptionEvent());
        Assert.assertEquals(giveMeAnA, ((APIExceptionEvent)apiEvent).getPayload());

        // repeat 4
        apiEvent = techEndpointEventProvider.consumeEvent();
        long interval = ((APIIntervalEvent)apiEvent).getPayload();
        Assert.assertTrue(10l == interval);
        apiEvent = techEndpointEventProvider.consumeEvent();
        interval = ((APIIntervalEvent)apiEvent).getPayload();
        Assert.assertTrue(10l == interval);
        apiEvent = techEndpointEventProvider.consumeEvent();
        interval = ((APIIntervalEvent)apiEvent).getPayload();
        Assert.assertTrue(10l == interval);
        apiEvent = techEndpointEventProvider.consumeEvent();
        interval = ((APIIntervalEvent)apiEvent).getPayload();
        Assert.assertTrue(10l == interval);

        Assert.assertNull(techEndpointEventProvider.consumeEvent());
    }

    /**
     * Test
     */
    @Test
    public void test_send_with_getEvent_rollback()
    {
        Exception giveMeAnA = new Exception("Give me an A");
        TechEndpointEventProvider<APIEvent> techEndpointEventProvider = TechEndpointEventProvider.with()
                .messageEvent("Give me an I")
                .messageEvent("lifeId", "Give me an K")
                .exceptionEvent(giveMeAnA)
                .interval(10)
                .build();

        APIEvent apiEvent = techEndpointEventProvider.consumeEvent();
        Assert.assertTrue(apiEvent.isMessageEvent());
        Assert.assertTrue("Give me an I".equals(((APIMessageEvent)apiEvent).getLifeIdentifer()));
        Assert.assertTrue("Give me an I".equals(((APIMessageEvent)apiEvent).getPayload()));

        // rollback on last event which was a message
        techEndpointEventProvider.rollback();
        apiEvent = techEndpointEventProvider.consumeEvent();
        Assert.assertTrue(apiEvent.isMessageEvent());
        Assert.assertTrue("Give me an I".equals(((APIMessageEvent)apiEvent).getLifeIdentifer()));
        Assert.assertTrue("Give me an I".equals(((APIMessageEvent)apiEvent).getPayload()));

        apiEvent = techEndpointEventProvider.consumeEvent();
        Assert.assertTrue(apiEvent.isMessageEvent());
        Assert.assertTrue("lifeId".equals(((APIMessageEvent)apiEvent).getLifeIdentifer()));
        Assert.assertTrue("Give me an K".equals(((APIMessageEvent)apiEvent).getPayload()));

        apiEvent = techEndpointEventProvider.consumeEvent();
        Assert.assertTrue(apiEvent.isExceptionEvent());
        Assert.assertEquals(giveMeAnA, ((APIExceptionEvent)apiEvent).getPayload());

        // rollback on last event should have no effect as it was an exception
        techEndpointEventProvider.rollback();

        apiEvent = techEndpointEventProvider.consumeEvent();
        long interval = ((APIIntervalEvent)apiEvent).getPayload();
        Assert.assertTrue(10l == interval);

        // rollback on last event should have no effect as it was an interval
        techEndpointEventProvider.rollback();
        Assert.assertNull(techEndpointEventProvider.consumeEvent());
    }

    /**
     * Test
     */
    @Test
    public void test_send_with_getEvent_with_repeast_with_rollback()
    {
        Exception giveMeAnA = new Exception("Give me an A");
        TechEndpointEventProvider<APIEvent> techEndpointEventProvider = TechEndpointEventProvider.with()
                .messageEvent("Give me an I")
                .messageEvent("lifeId", "Give me an K").repeat(2)
                .exceptionEvent(giveMeAnA)
                .interval(10)
                .build();

        APIEvent apiEvent = techEndpointEventProvider.consumeEvent();
        Assert.assertTrue(apiEvent.isMessageEvent());
        Assert.assertTrue("Give me an I".equals(((APIMessageEvent)apiEvent).getLifeIdentifer()));
        Assert.assertTrue("Give me an I".equals(((APIMessageEvent)apiEvent).getPayload()));

        // rollback on last event which was a message
        techEndpointEventProvider.rollback();
        apiEvent = techEndpointEventProvider.consumeEvent();
        Assert.assertTrue(apiEvent.isMessageEvent());
        Assert.assertTrue("Give me an I".equals(((APIMessageEvent)apiEvent).getLifeIdentifer()));
        Assert.assertTrue("Give me an I".equals(((APIMessageEvent)apiEvent).getPayload()));

        apiEvent = techEndpointEventProvider.consumeEvent();
        Assert.assertTrue(apiEvent.isMessageEvent());
        Assert.assertTrue("lifeId".equals(((APIMessageEvent)apiEvent).getLifeIdentifer()));
        Assert.assertTrue("Give me an K".equals(((APIMessageEvent)apiEvent).getPayload()));

        // rollback on last event which was a message
        techEndpointEventProvider.rollback();

        apiEvent = techEndpointEventProvider.consumeEvent();
        Assert.assertTrue(apiEvent.isMessageEvent());
        Assert.assertTrue("lifeId".equals(((APIMessageEvent)apiEvent).getLifeIdentifer()));
        Assert.assertTrue("Give me an K".equals(((APIMessageEvent)apiEvent).getPayload()));

        apiEvent = techEndpointEventProvider.consumeEvent();
        Assert.assertTrue(apiEvent.isMessageEvent());
        Assert.assertTrue("lifeId".equals(((APIMessageEvent)apiEvent).getLifeIdentifer()));
        Assert.assertTrue("Give me an K".equals(((APIMessageEvent)apiEvent).getPayload()));

        apiEvent = techEndpointEventProvider.consumeEvent();
        Assert.assertTrue(apiEvent.isExceptionEvent());
        Assert.assertEquals(giveMeAnA, ((APIExceptionEvent)apiEvent).getPayload());

        // rollback on last event should have no effect as it was an exception
        techEndpointEventProvider.rollback();

        apiEvent = techEndpointEventProvider.consumeEvent();
        long interval = ((APIIntervalEvent)apiEvent).getPayload();
        Assert.assertTrue(10l == interval);

        // rollback on last event should have no effect as it was an interval
        techEndpointEventProvider.rollback();
        Assert.assertNull(techEndpointEventProvider.consumeEvent());
    }

    /**
     * Test
     */
    @Test
    public void test_new_provider_from_existing_provider()
    {
        Exception giveMeAnA = new Exception("Give me an A");
        TechEndpointEventProvider<APIEvent> techEndpointEventProvider = TechEndpointEventProvider.with()
                .messageEvent("Give me an I")
                .messageEvent("lifeId", "Give me an K")
                .exceptionEvent(giveMeAnA)
                .interval(10)
                .build();


        APIEvent apiEvent = techEndpointEventProvider.consumeEvent();
        Assert.assertTrue(apiEvent.isMessageEvent());
        Assert.assertTrue("Give me an I".equals(((APIMessageEvent)apiEvent).getLifeIdentifer()));
        Assert.assertTrue("Give me an I".equals(((APIMessageEvent)apiEvent).getPayload()));

        apiEvent = techEndpointEventProvider.consumeEvent();
        Assert.assertTrue(apiEvent.isMessageEvent());
        Assert.assertTrue("lifeId".equals(((APIMessageEvent)apiEvent).getLifeIdentifer()));
        Assert.assertTrue("Give me an K".equals(((APIMessageEvent)apiEvent).getPayload()));

        apiEvent = techEndpointEventProvider.consumeEvent();
        Assert.assertTrue(apiEvent.isExceptionEvent());
        Assert.assertEquals(giveMeAnA, ((APIExceptionEvent)apiEvent).getPayload());

        apiEvent = techEndpointEventProvider.consumeEvent();
        long interval = ((APIIntervalEvent)apiEvent).getPayload();
        Assert.assertTrue(10l == interval);

        Assert.assertNull(techEndpointEventProvider.consumeEvent());
    }

}
