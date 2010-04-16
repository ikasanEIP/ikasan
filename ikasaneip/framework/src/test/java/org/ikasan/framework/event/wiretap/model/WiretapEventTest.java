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
package org.ikasan.framework.event.wiretap.model;

import static org.junit.Assert.*;
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
        assertNotNull(wiretapEvent.getUpdated());
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
