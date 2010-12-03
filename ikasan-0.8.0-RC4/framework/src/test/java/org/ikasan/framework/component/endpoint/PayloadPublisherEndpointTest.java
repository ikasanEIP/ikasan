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
