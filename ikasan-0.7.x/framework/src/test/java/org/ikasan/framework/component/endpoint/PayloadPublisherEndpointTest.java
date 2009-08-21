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

import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import javax.resource.ResourceException;

import org.ikasan.common.Payload;
import org.ikasan.framework.component.Event;
import org.ikasan.framework.payload.service.PayloadPublisher;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.Sequence;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test class for PayloadPubliserEndpoint
 * 
 * @author Ikasan Development Team
 *
 */
public class PayloadPublisherEndpointTest {
	
    /**
     * Mockery for mocking concrete classes
     */
    private Mockery mockery = new Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };
	
    /** Mocked payload for testing */
	Payload payload1 = mockery.mock(Payload.class, "payload1");
	
	/** Mocked payload2 for testing */
	Payload payload2 = mockery.mock(Payload.class, "payload2");
	
	/** Mocked payload publisher for testing */
	PayloadPublisher payloadPublisher = mockery.mock(PayloadPublisher.class);
	
	/** Mocked event for testing */
	Event event = mockery.mock(Event.class);
	
	/**
	 * Test onEvent
	 * @throws ResourceException
	 * @throws EndpointException
	 */
	@Test
	public void testOnEvent() throws ResourceException, EndpointException {
		final List<Payload> payloads = new ArrayList<Payload>();
		payloads.add(payload1);
		payloads.add(payload2);
		
		PayloadPublisherEndpoint payloadPublisherEndpoint = new PayloadPublisherEndpoint(payloadPublisher);
		
		final Sequence sequence = mockery.sequence("invocationSequence");
		mockery.checking(new Expectations()
        {
            {
                one(event).getPayloads();
                inSequence(sequence);
                will(returnValue(payloads));
                
                one(payloadPublisher).publish(payload1);
                inSequence(sequence);
                
                one(payloadPublisher).publish(payload2);
                inSequence(sequence);

            }
        });
		
		payloadPublisherEndpoint.onEvent(event);
		
		mockery.assertIsSatisfied();
		
		
	}
	
	/**
	 * Test onEvent where a ResourceException is thrown and wrapped in a EndpointException
	 * @throws ResourceException
	 */
	@Test
	public void testOnEvent_throwsEndpointExceptionForResourceException() throws ResourceException {
		final List<Payload> payloads = new ArrayList<Payload>();
		final ResourceException resourceException = new ResourceException();
		
		payloads.add(payload1);
		payloads.add(payload2);
		
		PayloadPublisherEndpoint payloadPublisherEndpoint = new PayloadPublisherEndpoint(payloadPublisher);
		
		final Sequence sequence = mockery.sequence("invocationSequence");
		mockery.checking(new Expectations()
        {
            {
                one(event).getPayloads();
                inSequence(sequence);
                will(returnValue(payloads));
                
                one(payloadPublisher).publish(payload1);
                inSequence(sequence);
                will(throwException(resourceException));
            }
        });
		
		
		EndpointException thrownException = null;
		try {
			payloadPublisherEndpoint.onEvent(event);
			fail("exception should have been thrown");
		} catch (EndpointException e) {
			thrownException = e;
		}
		
		Assert.assertNotNull("EndpointException should have been thrown when underlying payloadPublisher throws a ResourceException", thrownException);
		Assert.assertEquals("thrown exception should wrap underlying ResourceException", resourceException, thrownException.getCause());
		
		mockery.assertIsSatisfied();
		
		
	}

}
