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
package org.ikasan.framework.component.endpoint;

import java.util.ArrayList;
import java.util.List;

import org.ikasan.common.Payload;
import org.ikasan.framework.component.Event;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Test class for WiretapEndpoint
 * 
 * @author Ikasan Development Team
 *
 */
public class EventWiretapEndpointTest {
	
    /**
     * Mockery for mocking concrete classes
     */
    private Mockery mockery = new Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };
    
    
	/**
	 * System Under Test
	 */
	@SuppressWarnings("deprecation")
    private EventWiretapEndpoint endpoint = new EventWiretapEndpoint();

	/**
	 * Test onEvent method
	 */
	@SuppressWarnings("deprecation")
    @Test
	public void testOnEvent() {
		final Event event = mockery.mock(Event.class);
		final List<Payload> payloads = new ArrayList<Payload>();
		
		final Payload payload1 = mockery.mock(Payload.class, "payload1");
		final Payload payload2 = mockery.mock(Payload.class, "payload2");
		
		payloads.add(payload1);
		payloads.add(payload2);
		
		
		mockery.checking(new Expectations()
        {
            {
                one(event).getPayloads();
                will(returnValue(payloads));
                
                one(payload1).getContent();will(returnValue("content".getBytes()));
                one(payload2).getContent();will(returnValue("content".getBytes()));
            }
        });
		
		endpoint.onEvent(event);
	}

}
