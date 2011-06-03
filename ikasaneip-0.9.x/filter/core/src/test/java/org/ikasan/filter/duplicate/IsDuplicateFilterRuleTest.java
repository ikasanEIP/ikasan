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
