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
package org.ikasan.filter;

import org.ikasan.filter.FilterRule;
import org.ikasan.filter.MessageFilter;
import org.ikasan.filter.DefaultMessageFilter;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test class for {@link DefaultMessageFilter}
 * 
 * @author Ikasan Development Team
 *
 */
@SuppressWarnings("unchecked") //Mocking doesn't play nice with generics. Warning can be safely ignored
public class DefaultMessageFilterTest
{
    /** A {@link Mockery} for mocking interfaces */
    private Mockery mockery = new Mockery();

    /** A mocked {@link FilterRule} */
    private final FilterRule<String> filterRule = this.mockery.mock(FilterRule.class, "filterRule");

    /** The {@link MessageFilter} implementation to test*/
    private MessageFilter<String> filterToTest = new DefaultMessageFilter<String>(this.filterRule);

    /**
     * Test case: if filtering rule returns false (message was not accepted), filter
     * must return null.
     * @throws FilterException 
     */
    @Test public void filter_discards_message() throws FilterException
    {
        final String messageToFilter = "somemessage";
        this.mockery.checking(new Expectations()
        {
            {
                one(filterRule).accept(messageToFilter);will(returnValue(false));
            }
        });
        String filteredMessage = this.filterToTest.filter(messageToFilter);
        Assert.assertNull(filteredMessage);
        this.mockery.assertIsSatisfied();
    }

    /**
     * Test case: if filtering rule returns true (message was accepted), filter
     * must return the message. 
     * @throws FilterException 
     */
    @Test public void filter_pass_message_thru() throws FilterException
    {
        final String messageToFilter = "somemessage";
        this.mockery.checking(new Expectations()
        {
            {
                one(filterRule).accept(messageToFilter);will(returnValue(true));
            }
        });
        String filteredMessage = this.filterToTest.filter(messageToFilter);
        Assert.assertNotNull(filteredMessage);
        this.mockery.assertIsSatisfied();
    }

    /**
     * Test case: filtering rule failure due to a thrown FilterException. 
     * @throws FilterException 
     */
    @Test(expected = FilterException.class)
    public void failed_filter_due_to_filterException() throws FilterException
    {
        final String messageToFilter = "somemessage";
        this.mockery.checking(new Expectations()
        {
            {
                one(filterRule).accept(messageToFilter);will(throwException(new FilterException("test exception")));
            }
        });
        this.filterToTest.filter(messageToFilter);
        this.mockery.assertIsSatisfied();
    }
}
