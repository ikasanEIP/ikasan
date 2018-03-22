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

import org.ikasan.spec.configuration.Configured;
import org.ikasan.spec.event.MessageListener;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Test class for EventGeneratingConsumer.
 * 
 * @author Ikasan Development Team
 */
public class SimpleMessageGeneratorTest
{
    /**
     * Mockery for mocking concrete classes
     */
    private Mockery mockery = new Mockery()
    {{
            setImposteriser(ClassImposteriser.INSTANCE);
    }};

    private EventGeneratingConsumerConfiguration consumerConfiguration = mockery.mock(EventGeneratingConsumerConfiguration.class);
    private MessageListener messageListener = mockery.mock(MessageListener.class);

    /**
     * Test messageGenerator
     */
    @Test(expected = IllegalStateException.class)
    public void test_messageGenerator_null_messageListener()
    {
        MessageGenerator messageGenerator = new SimpleMessageGenerator();
        messageGenerator.run();
    }

    /**
     * Test messageGenerator
     */
    @Test
    public void test_messageGenerator_one_message()
    {
        MessageGenerator messageGenerator = new SimpleMessageGenerator();
        messageGenerator.setMessageListener(messageListener);
        ((Configured)messageGenerator).setConfiguration(consumerConfiguration);

        // expectations
        mockery.checking(new Expectations()
        {
            {
                // event limit of 1
                exactly(5).of(consumerConfiguration).getMaxEventLimit();
                will(returnValue(1));

                one(messageListener).onMessage("Message 1");
                exactly(2).of(consumerConfiguration).getEventGenerationInterval();
                will(returnValue(1L));
                exactly(1).of(consumerConfiguration).getEventsPerInterval();
                will(returnValue(1));
            }
        });

        ((SimpleMessageGenerator)messageGenerator).setRunning(true);
        ((SimpleMessageGenerator)messageGenerator).execute();
        mockery.assertIsSatisfied();
    }

    /**
     * Test messageGenerator
     */
    @Test
    public void test_messageGenerator_three_messages()
    {
        MessageGenerator messageGenerator = new SimpleMessageGenerator();
        messageGenerator.setMessageListener(messageListener);
        ((Configured)messageGenerator).setConfiguration(consumerConfiguration);

        // expectations
        mockery.checking(new Expectations()
        {
            {
                // event limit of 3
                exactly(9).of(consumerConfiguration).getMaxEventLimit();
                will(returnValue(3));

                one(messageListener).onMessage("Message 1");
                one(messageListener).onMessage("Message 2");
                one(messageListener).onMessage("Message 3");
                exactly(6).of(consumerConfiguration).getEventGenerationInterval();
                will(returnValue(1L));
                exactly(3).of(consumerConfiguration).getEventsPerInterval();
                will(returnValue(1));
            }
        });

        ((SimpleMessageGenerator)messageGenerator).setRunning(true);
        ((SimpleMessageGenerator)messageGenerator).execute();
        mockery.assertIsSatisfied();
    }


    /**
     * Test messageGenerator
     */
    @Test
    public void test_messageGenerator_four_messages_batching_of_two()
    {
        MessageGenerator messageGenerator = new SimpleMessageGenerator();
        messageGenerator.setMessageListener(messageListener);
        ((Configured)messageGenerator).setConfiguration(consumerConfiguration);

        // expectations
        mockery.checking(new Expectations()
        {
            {
                // event limit of 3
                exactly(11).of(consumerConfiguration).getMaxEventLimit();
                will(returnValue(4));

                one(messageListener).onMessage("Message 1");
                one(messageListener).onMessage("Message 2");
                one(messageListener).onMessage("Message 3");
                one(messageListener).onMessage("Message 4");
                exactly(6).of(consumerConfiguration).getEventGenerationInterval();
                will(returnValue(1L));
                exactly(4).of(consumerConfiguration).getEventsPerInterval();
                will(returnValue(2));
            }
        });

        ((SimpleMessageGenerator)messageGenerator).setRunning(true);
        ((SimpleMessageGenerator)messageGenerator).execute();
        mockery.assertIsSatisfied();
    }

}
