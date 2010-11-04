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
