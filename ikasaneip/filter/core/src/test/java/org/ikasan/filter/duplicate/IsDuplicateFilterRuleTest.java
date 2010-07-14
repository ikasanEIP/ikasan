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
package org.ikasan.filter.duplicate;

import org.ikasan.filter.FilterException;
import org.ikasan.filter.FilterRule;
import org.ikasan.filter.duplicate.IsDuplicateFilterRule;
import org.ikasan.filter.duplicate.model.FilterEntry;
import org.ikasan.filter.duplicate.model.FilterEntryConverter;
import org.ikasan.filter.duplicate.model.FilterEntryConverterException;
import org.ikasan.filter.duplicate.service.DuplicateFilterService;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test class for {@link IsDuplicateFilterRule}
 * 
 * @author Ikasan Development Team
 *
 */
@SuppressWarnings("unchecked") //Mocking doesn't play nice with generics. Warning can be safely ignored
public class IsDuplicateFilterRuleTest
{
    /** A {@link Mockery} for mocking interfaces*/
    private Mockery mockery = new Mockery();

    /** A mocked {@link DuplicateFilterService}*/
    private final DuplicateFilterService service = this.mockery.mock(DuplicateFilterService.class, "service");

    /** A mocked {@link FilterEntryConverter} with {@link String} parameter*/
    private final FilterEntryConverter<String> converter = this.mockery.mock(FilterEntryConverter.class, "converter");

    /** A mocked {@link FilterEntry}*/
    private final FilterEntry entry = this.mockery.mock(FilterEntry.class, "entry");

    /** {@link FilterRule} implementation to test*/
    private FilterRule<String> filterRuleToTest = new IsDuplicateFilterRule<String>(this.service, this.converter);

    /** A dummy message to pass to the filtering algorithm */
    private final String message = "somemessage";

    /**
     * Test case:The message is found to be a duplicate; filtering rule will
     * not accept message and return false.
     * @throws FilterException 
     */
    @Test public void rule_rejects_message() throws FilterException
    {
        this.mockery.checking(new Expectations()
        {
            {
                one(converter).convert(message);will(returnValue(entry));
                one(service).isDuplicate(entry);will(returnValue(true));
            }
        });
        boolean result= this.filterRuleToTest.accept(message);
        Assert.assertFalse(result);
        this.mockery.assertIsSatisfied();
    }

    /**
     * Test case: the message is not found in persistence; filtering rule will
     * accept message and return true
     * @throws FilterException 
     */
    @Test public void rule_accepts_message() throws FilterException
    {
        final String message = "somemessage";
        this.mockery.checking(new Expectations()
        {
            {
                one(converter).convert(message);will(returnValue(entry));
                one(service).isDuplicate(entry);will(returnValue(false));
                one(service).persistMessage(entry);
            }
        });
        boolean result= this.filterRuleToTest.accept(message);
        Assert.assertTrue(result);
        this.mockery.assertIsSatisfied();
    }

    /**
     * Test case: the filter fails due to a {@link FilterEntryConverterException}
     * @throws FilterException 
     */
    @Test(expected = FilterEntryConverterException.class)
    public void failed_rule_due_to_filterEntryConverterException() throws FilterException
    {
        final String message = "somemessage";
        this.mockery.checking(new Expectations()
        {
            {
                one(converter).convert(message);will(throwException(new FilterEntryConverterException("test exception")));
            }
        });
        this.filterRuleToTest.accept(message);
        this.mockery.assertIsSatisfied();
    }
}
