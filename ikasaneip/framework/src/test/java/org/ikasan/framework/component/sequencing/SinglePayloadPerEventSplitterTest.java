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
