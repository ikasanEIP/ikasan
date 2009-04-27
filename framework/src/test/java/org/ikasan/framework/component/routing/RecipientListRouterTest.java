 /* 
 * $Id: RecipientListRouterTest.java 16808 2009-04-27 07:28:17Z mitcje $
 * $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/framework/src/test/java/org/ikasan/framework/component/routing/RecipientListRouterTest.java $
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

import junit.framework.Assert;

import org.ikasan.framework.component.Event;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Test class for <code>RecipientList</code>
 * 
 * @author Ikasan Development Team
 * 
 */
public class RecipientListRouterTest
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
     * mocked Event
     */
    private Event event = classMockery.mock(Event.class);

    /**
     * Tests that we indeed return the list with which we are configured
     */
    @Test
    public void testOnEvent()
    {
        List<String> recipientList = new ArrayList<String>();
        recipientList.add("recipient1");
        recipientList.add("recipient2");
        RecipientListRouter recipientListRouter = new RecipientListRouter(recipientList);
        List<String> result = recipientListRouter.onEvent(event);
        Assert.assertEquals("RecipientListRouter should simply return an equivallent List of Strings to that with which it is configured", recipientList,
            result);
    }

    /**
     * Tests that null recipient lists fail on creation
     */
    @Test(expected = IllegalArgumentException.class)
    public void testOnEvent_withNullRecipientList()
    {
        new RecipientListRouter(null);
    }
}
