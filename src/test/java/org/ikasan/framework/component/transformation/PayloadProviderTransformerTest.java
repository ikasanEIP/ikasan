/*
 * $Id: PayloadProviderTransformerTest.java 16808 2009-04-27 07:28:17Z mitcje $
 * $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/framework/src/test/java/org/ikasan/framework/component/transformation/PayloadProviderTransformerTest.java $
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
package org.ikasan.framework.component.transformation;

import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import javax.resource.ResourceException;

import org.ikasan.common.Payload;
import org.ikasan.framework.component.Event;
import org.ikasan.framework.payload.service.PayloadProvider;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.Sequence;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test class for <code>PayloadProviderTransformer</code>
 * 
 * @author Ikasan Development Team
 *
 */
public class PayloadProviderTransformerTest {

    /**
     * Mockery for mocking concrete classes
     */
    private Mockery mockery = new Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };
	
    /** Test payload */
	Payload payload1 = mockery.mock(Payload.class, "payload1");
	
	/** Test payload2 */
	Payload payload2 = mockery.mock(Payload.class, "payload2");
	
	/** Test payload provider */
	PayloadProvider payloadProvider = mockery.mock(PayloadProvider.class);
	
	/** Mocked event */
	Event event = mockery.mock(Event.class);
	
	/**
	 * Test the happy path
	 * 
	 * @throws ResourceException
	 * @throws TransformationException
	 */
	@Test
	public void testOnEvent() throws ResourceException, TransformationException {
		final List<Payload> payloads = new ArrayList<Payload>();
		payloads.add(payload1);
		payloads.add(payload2);
		
		PayloadProviderTransformer payloadProviderTransformer = new PayloadProviderTransformer(payloadProvider);
		
		final Sequence sequence = mockery.sequence("invocationSequence");
		mockery.checking(new Expectations()
        {
            {
                one(payloadProvider).getNextRelatedPayloads();
                inSequence(sequence);
                will(returnValue(payloads));
                
                one(event).setPayload(payload1);
                inSequence(sequence);
                
                one(event).setPayload(payload2);
                inSequence(sequence);

            }
        });
		
		payloadProviderTransformer.onEvent(event);
		
		mockery.assertIsSatisfied();
	}

	/**
	 * tests that ResourceExceptions get rethrown as TransformerExceptions
	 * 
	 * @throws ResourceException
	 */
	@Test
	public void testOnEvent_throwsTransformerExceptionForResourceException() throws ResourceException {

		final ResourceException resourceException = new ResourceException();

		PayloadProviderTransformer payloadProviderTransformer = new PayloadProviderTransformer(payloadProvider);
		
		final Sequence sequence = mockery.sequence("invocationSequence");
		mockery.checking(new Expectations()
        {
            {
                one(payloadProvider).getNextRelatedPayloads();
                inSequence(sequence);
                will(throwException(resourceException));
            }
        });
		
		
		TransformationException thrownException = null;
		try {
			payloadProviderTransformer.onEvent(event);
			fail("exception should have been thrown");
		} catch (TransformationException e) {
			thrownException = e;
		}
		
		Assert.assertNotNull("TransformationException should have been thrown when underlying payloadProvider throws a ResourceException", thrownException);
		Assert.assertEquals("thrown exception should wrap underlying ResourceException", resourceException, thrownException.getCause());
		
		mockery.assertIsSatisfied();
		
		
	}
}
