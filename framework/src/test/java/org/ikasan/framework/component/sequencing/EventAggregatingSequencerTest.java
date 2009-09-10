/*
 * $Id$
 * $URL$
 * 
 * ====================================================================
 * Ikasan Enterprise Integration Platform
 * Copyright (c) 2003-2008 Mizuho International plc. and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the 
 * Free Software Foundation Europe e.V. Talstrasse 110, 40217 Dusseldorf, Germany 
 * or see the FSF site: http://www.fsfeurope.org/.
 * ====================================================================
 */
package org.ikasan.framework.component.sequencing;

import static org.junit.Assert.*;

import java.util.List;

import javax.resource.ResourceException;

import org.ikasan.framework.component.Event;
import org.ikasan.framework.event.service.EventAggregator;
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
        assertTrue(eventAggregatingSequencer.onEvent(event) == null);
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
        List<Event> events = eventAggregatingSequencer.onEvent(event);
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
