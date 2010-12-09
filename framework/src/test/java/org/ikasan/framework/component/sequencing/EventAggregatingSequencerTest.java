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
package org.ikasan.framework.component.sequencing;

import static org.junit.Assert.assertTrue;

import java.util.List;

import javax.resource.ResourceException;

import org.ikasan.framework.component.Event;
import org.ikasan.framework.event.service.EventAggregator;
import org.ikasan.spec.sequencing.SequencerException;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * This test class supports the <code>EventAggregatingSequencer</code>
 * class.
 * 
 * @author Ikasan Development Team
 */
public class EventAggregatingSequencerTest
{
    /**
     * Mockery for mocking concrete classes
     */
    private Mockery classMockery = new Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /** Mock event aggregator */
    final EventAggregator eventAggregator = classMockery.mock(EventAggregator.class);

    /** Mock event */
    final Event event = classMockery.mock(Event.class);
    
    /**
     * Name of the module doing the aggregating - required if aggregator creates a new event
     */
    private String moduleName = "moduleName";
    
    /**
     * Name of the component doing the aggregating - required if aggregator creates a new event
     */   
    private String componentName = "componentName";

    /**
     * Setup runs before each test
     */
    @Before
    public void setUp()
    {
        // nothing to do here
    }

    /**
     * Test failed constructor due to 'null' eventAggregator.
     */
    @Test(expected = IllegalArgumentException.class)
    public void test_failed_constructorDueToNullEventAggregator()
    {
        new EventAggregatingSequencer(null);
    }

    /**
     * Test execution of the EventAggregatingSequencer based on returning 
     * 'null' event.
     * 
     * @throws ResourceException
     * @throws SequencerException 
     */
    @Test
    public void test_successful_aggregationReturingNullEvent() 
        throws ResourceException, SequencerException
    {
        // 
        // set expectations
        classMockery.checking(new Expectations()
        {
            {
                one(eventAggregator).aggregate(event);
                will(returnValue(null));
            }
        });

        EventAggregatingSequencer eventAggregatingSequencer = new EventAggregatingSequencer(eventAggregator);
        assertTrue(eventAggregatingSequencer.onEvent(event, moduleName, componentName) == null);
    }

    /**
     * Test execution of the EventAggregatingSequencer based on returning 
     * an event.
     * 
     * @throws ResourceException
     * @throws SequencerException 
     */
    @Test
    public void test_successful_aggregationReturingEvent() 
        throws ResourceException, SequencerException
    {
        // 
        // set expectations
        classMockery.checking(new Expectations()
        {
            {
                one(eventAggregator).aggregate(event);
                will(returnValue(event));
            }
        });

        EventAggregatingSequencer eventAggregatingSequencer = new EventAggregatingSequencer(eventAggregator);
        List<Event> events = eventAggregatingSequencer.onEvent(event, moduleName, componentName);
        assertTrue(events.size() == 1);
    }

    /**
     * Teardown after each test
     */
    @After
    public void tearDown()
    {
        // check all expectations were satisfied
        classMockery.assertIsSatisfied();
    }
}
