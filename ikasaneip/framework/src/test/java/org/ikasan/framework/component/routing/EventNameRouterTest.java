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
package org.ikasan.framework.component.routing;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ikasan.framework.component.Event;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit test class for EventNameRouter
 * 
 * @author Ikasan Development Team
 *
 */
public class EventNameRouterTest
{

    
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
     * Mocked Event
     */
    final Event event = this.classMockery.mock(Event.class);

    /**
     * tests the case when there is no match to a configured value
     * 
     * @throws RouterException
     */
    @SuppressWarnings("unqualified-field-access")//cannot qualify field from with an anonymous class
    @Test
    public void testEvaluate_withUnmatchedEventNameReturnsDefaultWhenConfiguredToDoSo() throws RouterException
    {
        this.classMockery.checking(new Expectations()
        {
            {
                one(event).getName();
                will(returnValue("unknown"));
                
                allowing(event).idToString();will(returnValue("idString"));
            }
        });
        
        EventNameRouter eventNameRouter = new EventNameRouter(null, true);
        final List<String> result = eventNameRouter.onEvent(event);
        Assert.assertEquals("Result should be of size 1", 1, result.size());
        Assert.assertEquals("evaluator should return its default when it cannot match the event name", Router.DEFAULT_RESULT, result.get(0)); 
    }

    /**
     * tests the case when there is no match to a configured value, and not configured to default, will throw exception
     */
    @Test
    @SuppressWarnings("unqualified-field-access")//cannot qualify field from with an anonymous class
    public void testEvaluate_withUnmatchedEventNameWhenNotConfiguredToReturnDefault()
    {
        this.classMockery.checking(new Expectations()
        {
            {
                one(event).getName(); will(returnValue("unknown"));
                allowing(event).idToString(); will(returnValue("idString"));
            }
        });

        EventNameRouter eventNameRouter = new EventNameRouter(null, false);
        RouterException routerException = null;
        try
        {
            eventNameRouter.onEvent(this.event);
            Assert.fail("Exception should have been thrown for unmatched event name");
        }
        catch(RouterException re)
        {
            routerException=re;
        }
        Assert.assertNotNull("Exception should have been thrown", routerException);
        Assert.assertTrue("Exception should be UnroutableEventException", (routerException instanceof UnroutableEventException));
    }
    

    /**
     * tests the case where there is a match to a configured value
     * 
     * @throws RouterException
     */
    @Test
    @SuppressWarnings("unqualified-field-access")//cannot qualify field from with an anonymous class
    public void testEvaluate_withMatchedEventNameReturnsMatchedResult() throws RouterException
    {
        final Map<String, String> eventNamesToResults = new HashMap<String, String>();
        final String knownEventName = "knownEventName";
        final String knownEventResult = "knownEventResult";
        
        eventNamesToResults.put(knownEventName, knownEventResult);
        
        this.classMockery.checking(new Expectations()
        {
            {
                one(event).getName();
                will(returnValue(knownEventName));
                
                allowing(event).idToString();will(returnValue("idString"));
            }
        });
        
        EventNameRouter eventNameRouter = new EventNameRouter(eventNamesToResults, false);
        final List<String> result = eventNameRouter.onEvent(this.event);
        Assert.assertEquals("Result should be of size 1", 1, result.size());
        Assert.assertEquals("evaluator should return its configured result for a matched event name", knownEventResult, result.get(0));
    }
}
