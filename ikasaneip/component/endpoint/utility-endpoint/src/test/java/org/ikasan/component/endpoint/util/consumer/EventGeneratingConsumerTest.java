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
package org.ikasan.component.endpoint.util.consumer;

import org.ikasan.spec.event.EventFactory;
import org.ikasan.spec.event.EventListener;
import org.ikasan.spec.flow.FlowEvent;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Test class for EventGeneratingConsumer.
 * 
 * @author Ikasan Development Team
 */
public class EventGeneratingConsumerTest
{
    /**
     * Mockery for mocking concrete classes
     */
    private Mockery mockery = new Mockery()
    {{
            setImposteriser(ClassImposteriser.INSTANCE);
    }};

    private EventGeneratingConsumerConfiguration consumerConfiguration = mockery.mock(EventGeneratingConsumerConfiguration.class);
    private EventFactory flowEventFactory = mockery.mock(EventFactory.class);
    private EventListener eventListener = mockery.mock(EventListener.class);
    private FlowEvent flowEvent = mockery.mock(FlowEvent.class);

    /**
     * Test eventGenerator with a limit of 1 event
     */
    @Test
    public void test_eventGenerator_one_event()
    {
        EventGeneratingConsumer eventGeneratingConsumer = new EventGeneratingConsumer();
        eventGeneratingConsumer.setEventFactory(flowEventFactory);
        eventGeneratingConsumer.setListener(eventListener);
        eventGeneratingConsumer.setConfiguration(consumerConfiguration);

        EventGeneratingConsumer.EventGenerator eventGenerator = eventGeneratingConsumer.new EventGenerator();

        // expectations
        mockery.checking(new Expectations()
        {
            {
                // event limit of 1
                exactly(7).of(consumerConfiguration).getEventLimit();
                will(returnValue(1));

                // new flowEvent
                one(consumerConfiguration).getIdentifier();
                will(returnValue("identifier"));
                one(consumerConfiguration).getPayload();
                will(returnValue("payload"));

                one(flowEventFactory).newEvent("identifier", "payload");
                will(returnValue(flowEvent));

                one(eventListener).invoke(flowEvent);
            }
        });

        eventGenerator.run();
        mockery.assertIsSatisfied();
    }

    /**
     * Test eventGenerator with a limit of 1 event and interval of 5ms
     */
    @Test
    public void test_eventGenerator_one_event_batch_size_one()
    {
        EventGeneratingConsumer eventGeneratingConsumer = new EventGeneratingConsumer();
        eventGeneratingConsumer.setEventFactory(flowEventFactory);
        eventGeneratingConsumer.setListener(eventListener);
        eventGeneratingConsumer.setConfiguration(consumerConfiguration);

        EventGeneratingConsumer.EventGenerator eventGenerator = eventGeneratingConsumer.new EventGenerator();

        // expectations
        mockery.checking(new Expectations()
        {
            {
                // event limit of 1
                exactly(10).of(consumerConfiguration).getEventLimit();
                will(returnValue(2));

                // new flowEvent
                exactly(2).of(consumerConfiguration).getIdentifier();
                will(returnValue("identifier"));
                exactly(2).of(consumerConfiguration).getPayload();
                will(returnValue("payload"));

                exactly(2).of(flowEventFactory).newEvent("identifier", "payload");
                will(returnValue(flowEvent));

                exactly(2).of(eventListener).invoke(flowEvent);

                exactly(2).of(consumerConfiguration).getEventGenerationInterval();
                will(returnValue(1L));

                exactly(1).of(consumerConfiguration).getBatchsize();
                will(returnValue(1));
            }
        });

        eventGenerator.run();
        mockery.assertIsSatisfied();
    }
}
