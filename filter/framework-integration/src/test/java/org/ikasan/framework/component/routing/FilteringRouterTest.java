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

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.ikasan.common.Payload;
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
 * @author Summer
 *
 */
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

    /** The {@link #event}'s list of payloads */
    final private List<Payload> payloads = new ArrayList<Payload>();

    /** A mocked {@link Payload}*/
    final private Payload payload = this.mockery.mock(Payload.class, "mockPayload");

    /** A mocked {@link FilterRule} */
    final private FilterRule filterRule = this.mockery.mock(FilterRule.class, "mockRule");

    /** The {@link Router} implementation to be tested */
    private Router routerToTest = new FilteringRouter(this.filterRule);

    /**
     * Test case: filter rule rejects message, filter returns null. Router returns result:
     * {@link FilteringRouter#DISCARD_MESSAGE}
     * @throws RouterException
     */
    @Test public void route_event_to_trash() throws RouterException
    {
        this.payloads.add(payload);
        final String data = "somemessage";
        this.mockery.checking(new Expectations()
        {
            {
                one(event).getPayloads();will(returnValue(payloads));
                one(payload).getContent();will(returnValue(data.getBytes()));
                one(filterRule).accept(data);will(returnValue(false));
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
     */
    @Test public void route_event_to_next_element() throws RouterException
    {
        this.payloads.add(payload);
        final String data = "somemessage";
        this.mockery.checking(new Expectations()
        {
            {
                one(event).getPayloads();will(returnValue(payloads));
                one(payload).getContent();will(returnValue(data.getBytes()));
                one(filterRule).accept(data);will(returnValue(true));
            }
        });
        List<String> result = this.routerToTest.onEvent(event);
        Assert.assertEquals(1, result.size());
        Assert.assertEquals(FilteringRouter.PASS_MESSAGE_THRU, result.get(0));
        this.mockery.assertIsSatisfied();
    }
}
