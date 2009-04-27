/*
 * $Id: ContainsPayloadRouterTest.java 16808 2009-04-27 07:28:17Z mitcje $
 * $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/framework/src/test/java/org/ikasan/framework/component/routing/ContainsPayloadRouterTest.java $
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
