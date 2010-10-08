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
