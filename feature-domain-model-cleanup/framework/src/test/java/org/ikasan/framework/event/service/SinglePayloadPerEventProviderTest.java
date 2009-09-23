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
package org.ikasan.framework.event.service;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import javax.resource.ResourceException;

import junit.framework.JUnit4TestAdapter;

import org.ikasan.common.Payload;
import org.ikasan.framework.component.Event;
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
//                assertTrue(event.getComponentGroupName().equals(moduleName));
//                assertTrue(event.getComponentName().equals(componentName));
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
