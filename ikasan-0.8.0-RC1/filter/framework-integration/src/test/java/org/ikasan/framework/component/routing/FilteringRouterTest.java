/*
 * $Id$
 * $URL$
 * 
 * =============================================================================
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
 * =============================================================================
 */

package org.ikasan.framework.component.routing;

import java.util.List;

import junit.framework.Assert;

import org.ikasan.filter.FilterException;
import org.ikasan.filter.FilterRule;
import org.ikasan.framework.component.Event;
import org.ikasan.framework.component.routing.Router;
import org.ikasan.framework.component.routing.RouterException;
import org.ikasan.framework.component.routing.FilteringRouter;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Test class for {@link FilteringRouter}
 * 
 * @author Ikasan Development Team
 *
 */
@SuppressWarnings("unchecked") //mocks and generics don't play nice with each other
public class FilteringRouterTest
{
    /** A {@link Mockery} for mocking classes and interfaces*/
    private Mockery mockery = new Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /** A mocked {@link Event}*/
    final private Event event = this.mockery.mock(Event.class, "mockEvent");

    /** A mocked {@link FilterRule} */
    final private FilterRule<Event> filterRule = this.mockery.mock(FilterRule.class, "mockRule");

    /** The {@link Router} implementation to be tested */
    private Router routerToTest = new FilteringRouter(this.filterRule);

    /**
     * Test case: filter rule rejects message, filter returns null. Router returns result:
     * {@link FilteringRouter#DISCARD_MESSAGE}
     * @throws RouterException
     * @throws FilterException 
     */
    @Test public void route_event_to_trash() throws RouterException, FilterException
    {
        this.mockery.checking(new Expectations()
        {
            {
                one(filterRule).accept(event);will(returnValue(false));
            }
        });
        List<String> result = this.routerToTest.onEvent(event);
        Assert.assertEquals(1, result.size());
        Assert.assertEquals(FilteringRouter.DISCARD_MESSAGE, result.get(0));
        this.mockery.assertIsSatisfied();
    }

    /**
     * Test case: filter rule accepts message, filter returns message. Router resturns result:
     * {@link FilteringRouter#PASS_MESSAGE_THRU}
     * @throws RouterException
     * @throws FilterException 
     */
    @Test public void route_event_to_next_element() throws RouterException, FilterException
    {
        this.mockery.checking(new Expectations()
        {
            {
                one(filterRule).accept(event);will(returnValue(true));
            }
        });
        List<String> result = this.routerToTest.onEvent(event);
        Assert.assertEquals(1, result.size());
        Assert.assertEquals(FilteringRouter.PASS_MESSAGE_THRU, result.get(0));
        this.mockery.assertIsSatisfied();
    }

    /**
     * Test case: failure of the filter component resulting in a
     * {@link FilterException} subsequently wrapped in a {@link RouterException}
     * as part of the Ikasan standard Router component interface contract.
     * @throws RouterException
     * @throws FilterException 
     */
    @Test(expected = RouterException.class) 
    public void failed_route_event_due_to_filterException() 
        throws RouterException, FilterException
    {
        this.mockery.checking(new Expectations()
        {
            {
                one(filterRule).accept(event);will(throwException(new FilterException("test exception")));
            }
        });
        this.routerToTest.onEvent(event);
        this.mockery.assertIsSatisfied();
    }
}
