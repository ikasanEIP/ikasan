 /* 
 * $Id: EventSourceSystemRouterTest.java 16808 2009-04-27 07:28:17Z mitcje $
 * $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/framework/src/test/java/org/ikasan/framework/component/routing/EventSourceSystemRouterTest.java $
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.ikasan.framework.component.Event;
import org.ikasan.framework.component.routing.RouterException;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Assert;
import org.junit.Test;


/**
 * @author Ikasan Development Team
 *
 */
public class EventSourceSystemRouterTest extends TestCase
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
    final Event event = classMockery.mock(Event.class);

    /**
     * tests the case when there is no match to a configured value
     * 
     * @throws RouterException
     */
    @Test
    public void testEvaluate_withUnmatchedEventSrcSystemReturnsDefaultWhenConfiguredToDoSo() throws RouterException
    {
        classMockery.checking(new Expectations()
        {
            {
                one(event).getSrcSystem();
                will(returnValue("unknown"));
                
                allowing(event).idToString();will(returnValue("idString"));
            }
        });
        
        EventSourceSystemRouter eventSourceSystemRouter = new EventSourceSystemRouter(null, true);
        final List<String> result = eventSourceSystemRouter.onEvent(event);
        Assert.assertEquals("Result should be of size 1", 1, result.size());
        Assert.assertEquals("evaluator should return its default when it cannot match the event name", EventSourceSystemRouter.DEFAULT_RESULT, result.get(0)); 
    }
    
    /**
     * tests the case when there is no match to a configured value, and not configured to default, will throw exception
     */
    @Test
    public void testEvaluate_withUnmatchedEventNameWhenNotConfiguredToReturnDefault()
    {
        classMockery.checking(new Expectations()
        {
            {
                one(event).getSrcSystem();
                will(returnValue("unknown"));
                
                allowing(event).idToString();will(returnValue("idString"));
            }
        });
        
        EventSourceSystemRouter eventSourceSystemRouter = new EventSourceSystemRouter(null, false);
        RouterException routerException = null;
        try{
            eventSourceSystemRouter.onEvent(event);
            Assert.fail("Exception should have been thrown for unmatched source system");
        } catch(RouterException re){
            routerException=re;
        }
        Assert.assertNotNull("Exception should have been thrown", routerException);
        Assert.assertTrue("Exception should be UnroutableEventException", (routerException instanceof UnroutableEventException));
    }
    
    /**
     * @throws RouterException 
     */
    public void testEvaluate_withMatchedEventSourceSystemReturnsMatchedResult() throws RouterException
    {
        final Map<String, List<String>> targetsToSrcSystems = new HashMap<String, List<String>>();
        
        //knownEventSourceSystem should route to targetA, and targetB but not targetC
        final String knownEventSourceSystem = "knownEventSourceSystem";
        final String targetA = "targetA";
        final String targetB = "targetB";
        final String targetC = "targetC";
        
        List<String> targetASourceSystems = new ArrayList<String>();
        targetASourceSystems.add(knownEventSourceSystem);
        List<String> targetBSourceSystems = new ArrayList<String>();
        targetBSourceSystems.add(knownEventSourceSystem);
        List<String> targetCSourceSystems = new ArrayList<String>();
        
        targetsToSrcSystems.put(targetA, targetASourceSystems);
        targetsToSrcSystems.put(targetB, targetBSourceSystems);
        targetsToSrcSystems.put(targetC, targetCSourceSystems);
        
        classMockery.checking(new Expectations()
        {
            {
                one(event).getSrcSystem();
                will(returnValue(knownEventSourceSystem));
                
                allowing(event).idToString();will(returnValue("idString"));
            }
        });
        
        EventSourceSystemRouter eventNameEvaluator = new EventSourceSystemRouter(targetsToSrcSystems, true);
        final List<String> routingTargets = eventNameEvaluator.onEvent(event);
        assertTrue("result should include targetA", routingTargets.contains(targetA));
        assertTrue("result should include targetB", routingTargets.contains(targetB));
        assertFalse("result should not include targetC", routingTargets.contains(targetC));

    }
}
