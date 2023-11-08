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
package org.ikasan.builder.component.endpoint;

import org.ikasan.builder.AopProxyProvider;
import org.ikasan.component.endpoint.consumer.EventGeneratingConsumer;
import org.ikasan.spec.component.endpoint.Consumer;
import org.ikasan.spec.event.ExceptionListener;
import org.ikasan.spec.event.MessageListener;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.imposters.ByteBuddyClassImposteriser;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * This test class supports the <code>EventGeneratingConsumerBuilderImpl</code> class.
 *
 * @author Ikasan Development Team
 */
public class EventGeneratingConsumerBuilderTest
{
    /**
     * Mockery for mocking concrete classes
     */
    private Mockery mockery = new Mockery() {
        {
            setImposteriser(ByteBuddyClassImposteriser.INSTANCE);
        }
    };

    @Rule
    public ExpectedException thrown= ExpectedException.none();

    /**
     * Mocks
     */
    final AopProxyProvider aopProxyProvider = mockery.mock(AopProxyProvider.class, "mockAopProxyProvider");
    final MessageListener messageListener = mockery.mock(MyMessageListener.class, "mockMessageListener");

    /**
     * Test successful builder creation.
     */
    @Test
    public void eventGeneratingConsumer_build()
    {
        EventGeneratingConsumerBuilder eventGeneratingConsumerBuilder = new EventGeneratingConsumerBuilderImpl(aopProxyProvider);

        // expectations
        mockery.checking(new Expectations()
        {
            {
                // set event factory
                oneOf(aopProxyProvider).applyPointcut(with("eventConsumer"), with(any(EventGeneratingConsumer.class)));
                will(returnValue(messageListener));
            }
        });

        Consumer eventGeneratingConsumer = eventGeneratingConsumerBuilder.build();
        eventGeneratingConsumer.start();

        try
        {
            Thread.sleep(1000);
        }
        catch(InterruptedException e)
        {

        }

        eventGeneratingConsumer.stop();
        mockery.assertIsSatisfied();
    }


    class MyMessageListener implements MessageListener, ExceptionListener
    {
        @Override
        public void onException(Object throwableException)
        {

        }

        @Override
        public void onMessage(Object o)
        {

        }
    }
}
