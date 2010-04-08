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

import java.util.ArrayList;
import java.util.List;

import javax.resource.ResourceException;

import org.ikasan.common.Payload;
import org.ikasan.framework.component.Event;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * This test class supports the <code>SinglePayloadPerEventSplitter</code>
 * class.
 * 
 * @author Ikasan Development Team
 */
public class SinglePayloadPerEventSplitterTest
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

    /** Mock event */
    final Event event = classMockery.mock(Event.class);

    /** Mock event */
    final Event spawnedEvent = classMockery.mock(Event.class, "incomingEvent");

    /** Mock payload */
    final List<Payload> spawnedEventPayloads = classMockery.mock(List.class, "spawnedEventPayloads");

    /** Mock payload */
    final Payload payload = classMockery.mock(Payload.class, "spawnedEvent");

    /**
     * Setup runs before each test
     */
    @Before
    public void setUp()
    {
        // nothing to do here
    }

    /**
     * Test execution of the SinglePayloadPerEventSplitter with an incoming event 
     * containing a single payload.
     * 
     * @throws ResourceException
     * @throws SequencerException 
     */
    @Test
    public void test_successful_eventSplitOnOnePayload() 
        throws ResourceException, SequencerException
    {
        final List<Payload> payloads = new ArrayList<Payload>();
        payloads.add(payload);
        
        // 
        // set expectations
        classMockery.checking(new Expectations()
        {
            {
                // get the event's payloads
                one(event).getPayloads();
                will(returnValue(payloads));

                // only one payload so just return the incoming event in the list
            }
        });

        SinglePayloadPerEventSplitter singlePayloadPerEventSplitter = new SinglePayloadPerEventSplitter();
        List<Event> events = singlePayloadPerEventSplitter.onEvent(event);
        assertTrue(events.size() == 1);
    }

    /**
     * Test execution of the SinglePayloadPerEventSplitter with an incoming event 
     * containing a multiple payloads.
     * 
     * @throws ResourceException
     * @throws SequencerException 
     * @throws CloneNotSupportedException 
     */
    @Test
    public void test_successful_eventSplitOnMultiplePayloads() 
        throws ResourceException, SequencerException, CloneNotSupportedException
    {
        final List<Payload> payloads = new ArrayList<Payload>();
        payloads.add(payload);
        payloads.add(payload);
        payloads.add(payload);
        
        // 
        // set expectations
        classMockery.checking(new Expectations()
        {
            {
                // get the event's payloads
                one(event).getPayloads();
                will(returnValue(payloads));

                // spawn one event for each payload
                exactly(3).of(event).spawn();
                will(returnValue(spawnedEvent));

                // clear each new event's existing payloads
                exactly(3).of(spawnedEvent).getPayloads();
                will(returnValue(spawnedEventPayloads));
                exactly(3).of(spawnedEventPayloads).clear();

                // add the new event's payload
                exactly(3).of(spawnedEvent).setPayload(payload);
            }
        });

        SinglePayloadPerEventSplitter singlePayloadPerEventSplitter = new SinglePayloadPerEventSplitter();
        List<Event> events = singlePayloadPerEventSplitter.onEvent(event);
        assertTrue(events.size() == 3);
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
