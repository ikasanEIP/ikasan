/*
 * $Id$
 * $URL$
 * 
 * ====================================================================
 * Ikasan Enterprise Integration Platform
 * Copyright (c) 2003-2010 Mizuho International plc. and individual contributors as indicated
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
