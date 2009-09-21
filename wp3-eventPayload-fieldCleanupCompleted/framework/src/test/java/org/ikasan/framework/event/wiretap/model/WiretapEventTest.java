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
package org.ikasan.framework.event.wiretap.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Date;

import junit.framework.JUnit4TestAdapter;

import org.ikasan.framework.component.Event;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * This test class supports the <code>WiretapEvent</code> class.
 * 
 * @author Ikasan Development Team
 */
public class WiretapEventTest
{
    /**
     * Mockery for mocking concrete classes
     */
    private Mockery mockery = new Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /** mock event */
    final Event event = this.mockery.mock(Event.class);

    /**
     * Setup runs before each test
     */
    @Before
    public void setUp()
    {
        // nothing
    }

    /**
     * Test successful wiretap save with an event containing a single payload.
     */
    @Test
    public void test_successfulWiretapEndpoint_oneEvent_onePayload()
    {
        String moduleName = "moduleName";
        String flowName = "flowName";
        String componentName = "componentName";
        String eventId = "eventId";
        String payloadId = "payloadId";
        String payloadContent = "payloadContent";
        Date expiry  = new Date(System.currentTimeMillis()+1000);
        
        // create the class to be tested
        WiretapEvent wiretapEvent = new WiretapEvent(moduleName, flowName, componentName, eventId, payloadId, new String(payloadContent.getBytes()), expiry);
        
        assertEquals(wiretapEvent.getModuleName(), moduleName);
        assertEquals(wiretapEvent.getFlowName(), flowName);
        assertEquals(wiretapEvent.getComponentName(), componentName);
        assertEquals(wiretapEvent.getEventId(), eventId);
        assertEquals(wiretapEvent.getPayloadId(), payloadId);
        assertEquals(new String(wiretapEvent.getPayloadContent()), payloadContent);

        // TODO - not sure how else to test dynamic date.time creation
        assertNotNull(wiretapEvent.getCreated());
    }
    
    /**
     * The suite is this class
     * 
     * @return JUnit Test class
     */
    public static junit.framework.Test suite()
    {
        return new JUnit4TestAdapter(WiretapEventTest.class);
    }
}
