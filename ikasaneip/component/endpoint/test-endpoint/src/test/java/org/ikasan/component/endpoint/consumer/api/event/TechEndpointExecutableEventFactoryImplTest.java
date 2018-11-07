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
package org.ikasan.component.endpoint.consumer.api.event;

import org.ikasan.spec.event.ExceptionListener;
import org.ikasan.spec.event.MessageListener;
import org.ikasan.component.endpoint.consumer.api.TechEndpointEventFactory;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for TechEndpointExecutableEventFactoryImpl.
 * 
 * @author Ikasan Development Team
 */
public class TechEndpointExecutableEventFactoryImplTest
{
    TechEndpointEventFactory<APIExecutableEvent> techEndpointEventFactory;

    /**
     * Mockery for mocking concrete classes
     */
    private Mockery mockery = new Mockery() {{
        setImposteriser(ClassImposteriser.INSTANCE);
    }};

    private MessageListener messageListener = mockery.mock(MessageListener.class);
    private ExceptionListener exceptionListener = mockery.mock(ExceptionListener.class);

    @Before
    public void setup()
    {
        techEndpointEventFactory = new TechEndpointExecutableEventFactoryImpl(messageListener, exceptionListener);
    }

    /**
     * Test
     */
    @Test
    public void test_techEndpointEventFactory_create_messageEvent_with_message()
    {
        APIExecutableEvent messageEvent = techEndpointEventFactory.getMessageEvent("message event");
        Assert.assertFalse(messageEvent.isExceptionEvent());
        Assert.assertTrue(messageEvent.isMessageEvent());
        Assert.assertFalse(messageEvent.isIntervalEvent());
        Assert.assertFalse(messageEvent.isRepeatEvent());
        Assert.assertTrue( ((APIMessageEvent)messageEvent).getLifeIdentifer().equals("message event") );
        Assert.assertTrue( messageEvent.getPayload().equals("message event") );

        // expectations
        mockery.checking(new Expectations() {
            {
                exactly(1).of(messageListener).onMessage(messageEvent);
            }
        });

        messageEvent.execute();
        this.mockery.assertIsSatisfied();
    }

    /**
     * Test
     */
    @Test
    public void test_techEndpointEventFactory_create_messageEvent_with_lifeId_and_message()
    {
        APIExecutableEvent messageEvent = techEndpointEventFactory.getMessageEvent("lifeId", "message event");
        Assert.assertFalse(messageEvent.isExceptionEvent());
        Assert.assertTrue(messageEvent.isMessageEvent());
        Assert.assertFalse(messageEvent.isIntervalEvent());
        Assert.assertFalse(messageEvent.isRepeatEvent());
        Assert.assertTrue( ((APIMessageEvent)messageEvent).getLifeIdentifer().equals("lifeId") );
        Assert.assertTrue( messageEvent.getPayload().equals("message event") );

        // expectations
        mockery.checking(new Expectations() {
            {
                exactly(1).of(messageListener).onMessage(messageEvent);
            }
        });

        messageEvent.execute();
        this.mockery.assertIsSatisfied();
    }

    /**
     * Test
     */
    @Test
    public void test_techEndpointEventFactory_create_exceptionEvent()
    {
        Exception exception = new Exception("bad juju");
        APIExecutableEvent exceptionEvent = techEndpointEventFactory.getExceptionEvent(exception);
        Assert.assertTrue(exceptionEvent.isExceptionEvent());
        Assert.assertFalse(exceptionEvent.isMessageEvent());
        Assert.assertFalse(exceptionEvent.isIntervalEvent());
        Assert.assertFalse(exceptionEvent.isRepeatEvent());
        Assert.assertTrue( exceptionEvent.getPayload().equals(exception) );

        // expectations
        mockery.checking(new Expectations() {
            {
                exactly(1).of(exceptionListener).onException(exception);
            }
        });

        exceptionEvent.execute();
        this.mockery.assertIsSatisfied();
    }

    /**
     * Test
     */
    @Test
    public void test_techEndpointEventFactory_create_intervalEvent()
    {
        APIExecutableEvent intervalEvent = techEndpointEventFactory.getIntervalEvent(10L);
        Assert.assertFalse(intervalEvent.isExceptionEvent());
        Assert.assertFalse(intervalEvent.isMessageEvent());
        Assert.assertTrue(intervalEvent.isIntervalEvent());
        Assert.assertFalse(intervalEvent.isRepeatEvent());
        Assert.assertTrue( intervalEvent.getPayload().equals(10L) );

        intervalEvent.execute();
        this.mockery.assertIsSatisfied();
    }

    /**
     * Test
     */
    @Test
    public void test_techEndpointEventFactory_create_repeatEvent()
    {
        APIExecutableEvent messageEvent = techEndpointEventFactory.getMessageEvent("message event");
        APIRepeatEvent repeatEvent = techEndpointEventFactory.getRepeatEvent(messageEvent, 2);
        Assert.assertFalse(repeatEvent.isExceptionEvent());
        Assert.assertFalse(repeatEvent.isMessageEvent());
        Assert.assertFalse(repeatEvent.isIntervalEvent());
        Assert.assertTrue(repeatEvent.isRepeatEvent());
        Assert.assertTrue( repeatEvent.getPayload().equals(messageEvent) );
        Assert.assertTrue( repeatEvent.getPayload().equals(messageEvent) );
        Assert.assertNull( repeatEvent.getPayload() );

        this.mockery.assertIsSatisfied();
    }

}
