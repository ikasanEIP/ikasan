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
