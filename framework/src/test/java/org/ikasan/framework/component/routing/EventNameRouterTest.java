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
