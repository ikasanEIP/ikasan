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
package org.ikasan.testharness.flow.comparator.model;

import java.util.ArrayList;
import java.util.List;

import org.ikasan.common.Payload;
import org.ikasan.framework.component.Event;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Tests for the <code>EventComparator</code> class.
 *
 * @author Ikasan Development Team
 *
 */
public class EventComparatorTest
{
    Mockery mockery = new Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };
    
    /** mocked actual event */
    final Event actualEvent = mockery.mock(Event.class, "Mock Actual Event");
    
    /** mocked expected event */
    final Event expectedEvent = mockery.mock(Event.class, "Mock expected Event");
    
    /** mocked actual payload */
    final Payload actualPayload = mockery.mock(Payload.class, "Mock Actual Payload");
    
    /** mocked expected payload */
    final Payload expectedPayload = mockery.mock(Payload.class, "Mock expected Payload");
    
    /**
     * Sanity test the default EventComparator for an expected and actual 
     * event that are deemed equal.
     */
    @Test
    public void test_successfulEventComparator() 
    {
        final List<Payload> expectedPayloads = new ArrayList<Payload>();
        expectedPayloads.add(expectedPayload);
        expectedPayloads.add(expectedPayload);

        final List<Payload> actualPayloads = new ArrayList<Payload>();
        actualPayloads.add(actualPayload);
        actualPayloads.add(actualPayload);

        // expectations
        mockery.checking(new Expectations()
        {
            {
                // compare event name
                exactly(2).of(expectedEvent).getName();
                will(returnValue("name"));
                exactly(2).of(actualEvent).getName();
                will(returnValue("name"));

                // compare event srcSystem
                exactly(2).of(expectedEvent).getSrcSystem();
                will(returnValue("srcSystem"));
                exactly(2).of(actualEvent).getSrcSystem();
                will(returnValue("srcSystem"));

                // compare event payloads
                exactly(3).of(expectedEvent).getPayloads();
                will(returnValue(expectedPayloads));
                exactly(3).of(actualEvent).getPayloads();
                will(returnValue(actualPayloads));

                exactly(4).of(expectedPayload).getContent();
                will(returnValue("content is the same".getBytes()));
                exactly(4).of(actualPayload).getContent();
                will(returnValue("content is the same".getBytes()));
            }
        });

        EventComparator eventComparator = new EventComparator();
        eventComparator.compare(expectedEvent, actualEvent);
        
        mockery.assertIsSatisfied();
    }

    /**
     * Sanity test the default EventComparator for an expected and actual 
     * event but with different event names.
     */
    @Test(expected = junit.framework.ComparisonFailure.class)
    public void test_failedEventComparatorDueToDifferentEventNames() 
    {
        final List<Payload> expectedPayloads = new ArrayList<Payload>();
        expectedPayloads.add(expectedPayload);
        expectedPayloads.add(expectedPayload);

        final List<Payload> actualPayloads = new ArrayList<Payload>();
        actualPayloads.add(actualPayload);
        actualPayloads.add(actualPayload);

        // expectations
        mockery.checking(new Expectations()
        {
            {
                // compare event name
                exactly(2).of(expectedEvent).getName();
                will(returnValue("name"));
                exactly(2).of(actualEvent).getName();
                will(returnValue("named something else"));
            }
        });

        EventComparator eventComparator = new EventComparator();
        eventComparator.compare(expectedEvent, actualEvent);
        
        mockery.assertIsSatisfied();
    }

    /**
     * Sanity test the default EventComparator for an expected and actual 
     * event but with different event srcSystems.
     */
    @Test(expected = junit.framework.ComparisonFailure.class)
    public void test_failedEventComparatorDueToDifferentEventSrcSystem() 
    {
        final List<Payload> expectedPayloads = new ArrayList<Payload>();
        expectedPayloads.add(expectedPayload);
        expectedPayloads.add(expectedPayload);

        final List<Payload> actualPayloads = new ArrayList<Payload>();
        actualPayloads.add(actualPayload);
        actualPayloads.add(actualPayload);

        // expectations
        mockery.checking(new Expectations()
        {
            {
                // compare event name
                exactly(2).of(expectedEvent).getName();
                will(returnValue("name"));
                exactly(2).of(actualEvent).getName();
                will(returnValue("name"));

                // compare event srcSystem
                exactly(2).of(expectedEvent).getSrcSystem();
                will(returnValue("srcSystem"));
                exactly(2).of(actualEvent).getSrcSystem();
                will(returnValue("different srcSystem"));
            }
        });

        EventComparator eventComparator = new EventComparator();
        eventComparator.compare(expectedEvent, actualEvent);
        
        mockery.assertIsSatisfied();
    }

    /**
     * Sanity test the default EventComparator for an expected and actual 
     * event but with different event payload numbers.
     */
    @Test(expected = junit.framework.AssertionFailedError.class)
    public void test_failedEventComparatorDueToDifferentEventPayloadNumbers() 
    {
        final List<Payload> expectedPayloads = new ArrayList<Payload>();
        expectedPayloads.add(expectedPayload);
        expectedPayloads.add(expectedPayload);

        final List<Payload> actualPayloads = new ArrayList<Payload>();
        actualPayloads.add(actualPayload);

        // expectations
        mockery.checking(new Expectations()
        {
            {
                // compare event name
                exactly(2).of(expectedEvent).getName();
                will(returnValue("name"));
                exactly(2).of(actualEvent).getName();
                will(returnValue("name"));

                // compare event srcSystem
                exactly(2).of(expectedEvent).getSrcSystem();
                will(returnValue("srcSystem"));
                exactly(2).of(actualEvent).getSrcSystem();
                will(returnValue("srcSystem"));

                // compare event payloads
                exactly(3).of(expectedEvent).getPayloads();
                will(returnValue(expectedPayloads));
                exactly(3).of(actualEvent).getPayloads();
                will(returnValue(actualPayloads));
            }
        });

        EventComparator eventComparator = new EventComparator();
        eventComparator.compare(expectedEvent, actualEvent);
        
        mockery.assertIsSatisfied();
    }

    /**
     * Sanity test the default EventComparator for an expected and actual 
     * event but with different event payload content.
     */
    @Test(expected = junit.framework.AssertionFailedError.class)
    public void test_failedEventComparatorDueToDifferentEventPayloadContent() 
    {
        final List<Payload> expectedPayloads = new ArrayList<Payload>();
        expectedPayloads.add(expectedPayload);
        expectedPayloads.add(expectedPayload);

        final List<Payload> actualPayloads = new ArrayList<Payload>();
        actualPayloads.add(actualPayload);
        actualPayloads.add(actualPayload);

        // expectations
        mockery.checking(new Expectations()
        {
            {
                // compare event name
                exactly(2).of(expectedEvent).getName();
                will(returnValue("name"));
                exactly(2).of(actualEvent).getName();
                will(returnValue("name"));

                // compare event srcSystem
                exactly(2).of(expectedEvent).getSrcSystem();
                will(returnValue("srcSystem"));
                exactly(2).of(actualEvent).getSrcSystem();
                will(returnValue("srcSystem"));

                // compare event payloads
                exactly(3).of(expectedEvent).getPayloads();
                will(returnValue(expectedPayloads));
                exactly(3).of(actualEvent).getPayloads();
                will(returnValue(actualPayloads));

                exactly(4).of(expectedPayload).getContent();
                will(returnValue("content is the same".getBytes()));
                exactly(3).of(actualPayload).getContent();
                will(returnValue("content is the same".getBytes()));
                exactly(1).of(actualPayload).getContent();
                will(returnValue("content is not the same".getBytes()));
            }
        });

        EventComparator eventComparator = new EventComparator();
        eventComparator.compare(expectedEvent, actualEvent);
        
        mockery.assertIsSatisfied();
    }
    
}    

