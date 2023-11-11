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

import org.ikasan.component.endpoint.consumer.api.spec.Endpoint;
import org.ikasan.spec.event.EventFactory;
import org.ikasan.spec.event.EventListener;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.imposters.ByteBuddyClassImposteriser;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutorService;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Test class for EventGeneratingConsumer.
 * 
 * @author Ikasan Development Team
 */
class EventGeneratingConsumerTest
{
    /**
     * Mockery for mocking concrete classes
     */
    private Mockery mockery = new Mockery()
    {{
            setImposteriser(ByteBuddyClassImposteriser.INSTANCE);
    }};

    private EventFactory flowEventFactory = mockery.mock(EventFactory.class);
    private EventListener eventListener = mockery.mock(EventListener.class);
    private Endpoint apiEventProvider = mockery.mock(Endpoint.class);
    private ExecutorService executorService = mockery.mock(ExecutorService.class);

    /**
     * Test failed constructor
     */
    @Test
    void test_failed_constructor_null_executorService()
    {
        assertThrows(IllegalArgumentException.class, () -> {
            new EventGeneratingConsumer(null, null);
        });
    }

    /**
     * Test failed constructor
     */
    @Test
    void test_failed_constructor_null_messageListener()
    {
        assertThrows(IllegalArgumentException.class, () -> {
            new EventGeneratingConsumer(executorService, null);
        });
    }

    /**
     * Test eventGenerator start
     */
    @Test
    void test_eventGenerator_start()
    {
        EventGeneratingConsumer eventGeneratingConsumer = new EventGeneratingConsumer(executorService, apiEventProvider);
        eventGeneratingConsumer.setEventFactory(flowEventFactory);
        eventGeneratingConsumer.setListener(eventListener);

        // expectations
        mockery.checking(new Expectations()
        {
            {
                // start
                one(executorService).submit(apiEventProvider);
            }
        });

        eventGeneratingConsumer.start();
        mockery.assertIsSatisfied();
    }
}
