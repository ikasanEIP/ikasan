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
