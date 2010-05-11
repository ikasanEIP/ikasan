package org.ikasan.filter;

import org.ikasan.filter.FilterRule;
import org.ikasan.filter.MessageFilter;
import org.ikasan.filter.DefaultMessageFilter;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Assert;
import org.junit.Test;

public class DefaultMessageFilterTest
{
    private Mockery mockery = new Mockery();
    private final FilterRule filterRule = this.mockery.mock(FilterRule.class, "filterRule");
    private MessageFilter filterToTest = new DefaultMessageFilter(this.filterRule);

    @Test public void filter_discards_message()
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

    @Test public void filter_pass_message_thru()
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
}
