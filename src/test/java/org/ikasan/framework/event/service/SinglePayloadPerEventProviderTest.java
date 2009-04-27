/*
 * $Id: SinglePayloadPerEventProviderTest.java 16808 2009-04-27 07:28:17Z mitcje $
 * $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/framework/src/test/java/org/ikasan/framework/event/service/SinglePayloadPerEventProviderTest.java $
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
package org.ikasan.framework.event.service;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import javax.resource.ResourceException;

import junit.framework.JUnit4TestAdapter;

import org.ikasan.common.Payload;

import org.ikasan.framework.component.Event;
import org.ikasan.framework.event.service.EventProvider;
import org.ikasan.framework.payload.service.PayloadProvider;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * This test class supports the <code>SinglePayloadPerEventProvider</code>
 * class.
 * 
 * @author Ikasan Development Team
 */
public class SinglePayloadPerEventProviderTest
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

    /** Mock payload provider */
    final PayloadProvider payloadProvider = classMockery.mock(PayloadProvider.class);

    /** Mock payload */
    final Payload payload = classMockery.mock(Payload.class);

    /** Module name */
    String moduleName = "moduleName";

    /** Component Name */
    String componentName = "componentName";

    /**
     * Setup runs before each test
     */
    @Before
    public void setUp()
    {
        // nothing to do here
    }

    /**
     * Test failed constructor due to 'null' payloadProvider.
     */
    @Test(expected = IllegalArgumentException.class)
    public void test_failed_constructorDueToNullPayloadProvider()
    {
        PayloadProvider aPayloadProvider = null;
        new SinglePayloadPerEventProvider(aPayloadProvider, moduleName, componentName);
    }

    /**
     * Test execution of the SinglePayloadPerEventProvider based on returning no
     * payloads and hence no 'null' Events.
     * 
     * @throws ResourceException
     */
    @Test
    public void test_successful_nullEventReturnAsNoPayloads() throws ResourceException
    {
        EventProvider eventProvider = new SinglePayloadPerEventProvider(this.payloadProvider, moduleName, componentName);
        // 
        // set expectations
        classMockery.checking(new Expectations()
        {
            {
                // return 'null' payloads
                one(payloadProvider).getNextRelatedPayloads();
                will(returnValue(null));
            }
        });
        assertTrue(eventProvider.getEvents() == null);
    }

    /**
     * Test execution of the SinglePayloadPerEventProvider based on returning
     * zero payload. Different to 'null' as this has a valid payloads list.
     * 
     * @throws ResourceException
     */
    @Test
    public void test_successful_NoEventReturnedBasedNoPayload() throws ResourceException
    {
        test_successful_EventPerPaylaod(0);
    }

    /**
     * Test execution of the SinglePayloadPerEventProvider based on returning
     * one payload, hence, one Event.
     * 
     * @throws ResourceException
     */
    @Test
    public void test_successful_OneEventReturnedBasedOnOnePayload() throws ResourceException
    {
        test_successful_EventPerPaylaod(1);
    }

    /**
     * Test execution of the SinglePayloadPerEventProvider based on returning
     * five payloads, hence, five Events.
     * 
     * @throws ResourceException
     */
    @Test
    public void test_successful_FiveEventsReturnedBasedOnFivePayloads() throws ResourceException
    {
        test_successful_EventPerPaylaod(5);
    }

    /**
     * Centralise the event per payload test to allow tests with differing
     * numbers of payloads.
     * 
     * @param numOfPayloads
     * @throws ResourceException
     */
    private void test_successful_EventPerPaylaod(int numOfPayloads) throws ResourceException
    {
        EventProvider eventProvider = new SinglePayloadPerEventProvider(this.payloadProvider, moduleName, componentName);
        final List<Payload> payloads = new ArrayList<Payload>();
        for (int x = 0; x < numOfPayloads; x++)
        {
            payloads.add(payload);
        }
        // 
        // set expectations
        classMockery.checking(new Expectations()
        {
            {
                // return payloads
                one(payloadProvider).getNextRelatedPayloads();
                will(returnValue(payloads));
                // ignore payload operations - this is not the test focus
                ignoring(payload);
            }
        });
        List<Event> events = eventProvider.getEvents();
        if (numOfPayloads > 0)
        {
            assertTrue(events.size() == numOfPayloads);
        }
        else
        {
            assertTrue(events == null);
        }
        for (int x = 0; x < numOfPayloads; x++)
        {
            if (events != null)
            {
                Event event = events.get(x);
                assertTrue(event.getComponentGroupName().equals(moduleName));
                assertTrue(event.getComponentName().equals(componentName));
                assertTrue(event.getPayloads().size() == 1);
            }
            else
            {
                fail("Events was null");
            }
        }
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

    /**
     * The suite is this class
     * 
     * @return JUnit Test class
     */
    public static junit.framework.Test suite()
    {
        return new JUnit4TestAdapter(SinglePayloadPerEventProviderTest.class);
    }
}
