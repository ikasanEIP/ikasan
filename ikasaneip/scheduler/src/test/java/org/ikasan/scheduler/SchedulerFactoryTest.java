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
package org.ikasan.scheduler;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.imposters.ByteBuddyClassImposteriser;
import org.junit.jupiter.api.Test;
import org.quartz.ListenerManager;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.TriggerListener;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * This test class supports the <code>SchedulerFactory</code> class.
 * 
 * @author Ikasan Development Team
 */
class SchedulerFactoryTest
{
    /**
     * Mockery for mocking concrete classes
     */
    private Mockery mockery = new Mockery()
    {
        {
            setImposteriser(ByteBuddyClassImposteriser.INSTANCE);
        }
    };

    /** Mock scheduler */
    final Scheduler scheduler = mockery.mock(Scheduler.class, "mockScheduler");

    /** Mock listener manager */
    final ListenerManager listenerManager = mockery.mock(ListenerManager.class, "mockListenerManager");

    /**
     * Test successful retrieval of the scheduler instance.
     * @throws SchedulerException 
     */
    @Test
    void test_getScheduler() throws SchedulerException
    {

        // expectations
        mockery.checking(new Expectations()
        {
            {
                exactly(1).of(scheduler).setJobFactory(with(any(ScheduledJobFactory.class)));
                exactly(1).of(scheduler).getListenerManager();
                will(returnValue(listenerManager));
                exactly(1).of(listenerManager).addTriggerListener(with(any(TriggerListener.class)));
                exactly(1).of(scheduler).start();
            }
        });

        SchedulerFactory schedulerFactory = new StubbedSchedulerFactory();
        assertNotNull( schedulerFactory.getScheduler() );
        mockery.assertIsSatisfied();
    }

    /**
     * Test failed scheduler factory creation.
     * @throws SchedulerException 
     */
    @Test
    void test_failed_schedulerFactory_creation() throws SchedulerException
    {
        assertThrows(RuntimeException.class, () -> {
            // expectations
            mockery.checking(new Expectations()
            {
                {
                    exactly(1).of(scheduler).setJobFactory(with(any(ScheduledJobFactory.class)));
                    will(throwException(new SchedulerException()));
                }
            });

            new StubbedSchedulerFactory();
            mockery.assertIsSatisfied();
        });
    }
    
    /**
     * Test class extends SchedulerFactory to provide a mock scheduler instance.
     * @author Jeff Mitchell
     *
     */
    private class StubbedSchedulerFactory extends SchedulerFactory
    {
        @Override
        protected Scheduler newScheduler()
        {
            return scheduler;
        }
    }
}
