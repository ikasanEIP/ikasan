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
package org.ikasan.framework.component.routing;

import java.util.ArrayList;
import java.util.List;

import org.ikasan.common.Payload;
import org.ikasan.framework.component.Event;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Assert;
import org.junit.Test;

/**
 * Unit tests for ContainsPayloadRouter
 * 
 * @author Ikasan Development Team
 */
@SuppressWarnings("deprecation")
public class ContainsPayloadRouterTest
{
    
    /**
     * Constructor
     */
    public ContainsPayloadRouterTest()
    {
       payloads.add(payload);
    }

    /**
     * Mockery for mocking concrete classes
     */
    private Mockery classMockery = new Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };
    
    /**
     * Mockery for mocking interfaces
     */
    private Mockery mockery = new Mockery();
    
    /**
     * Class to test
     */
    private Router containsPayloadRouter = new ContainsPayloadRouter();
    
    /**
     * Event to evaluate
     */
    final Event event = classMockery.mock(Event.class);
    
    /**
     * List of mock Payloads
     */
    final List<Payload> payloads = new ArrayList<Payload>();
    
    /**
     * Mocked Payload
     */
    private final Payload payload = mockery.mock(Payload.class);
    
    /**
     * Test the case where the Event contains payloads
     * @throws RouterException 
     */
    @Test
    public void testEvaluate_withPayloadedEventReturnsTrueString() throws RouterException
    {
        classMockery.checking(new Expectations()
        {
            {
                one(event).getPayloads();
                will(returnValue(payloads));
            }
        });
        List<String> result = containsPayloadRouter.onEvent(event);
        Assert.assertEquals("Result should be of size 1", 1, result.size());
        Assert.assertEquals("Should return String value of true when Event contains payloads", Boolean.TRUE.toString(), result.get(0));
    }
    
    /**
     * Test the case where the Event contains zero payloads
     * @throws RouterException 
     */
    @Test
    public void testEvaluate_withZeroPayloadEventReturnsFalseString() throws RouterException
    {
        classMockery.checking(new Expectations()
        {
            {
                one(event).getPayloads();
                will(returnValue(new ArrayList<Payload>()));
            }
        });
        List<String> result = containsPayloadRouter.onEvent(event);
        Assert.assertEquals("Result should be of size 1", 1, result.size());
        Assert.assertEquals("Should return String value of false when Event does not contain payloads", Boolean.FALSE.toString(), result.get(0));

    }
    
    /**
     * Test the case where Event.getPayloads returns null
     * @throws RouterException 
     */
    @Test
    public void testEvaluate_withNullPayloadsEventReturnsFalseString() throws RouterException
    {
        classMockery.checking(new Expectations()
        {
            {
                one(event).getPayloads();
                will(returnValue(null));
            }
        });
        List<String> result = containsPayloadRouter.onEvent(event);
        Assert.assertEquals("Result should be of size 1", 1, result.size());
        Assert.assertEquals("Should return String value of false when Event does not contain payloads", Boolean.FALSE.toString(), result.get(0));

    }
}
