package org.ikasan.filter.duplicate;

import org.ikasan.filter.FilterRule;
import org.ikasan.filter.duplicate.IsDuplicateFilterRule;
import org.ikasan.filter.duplicate.service.DuplicatesFilterService;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Assert;
import org.junit.Test;

public class IsDuplicateFilterRuleTest
{
    private Mockery mockery = new Mockery();
    private final DuplicatesFilterService service = this.mockery.mock(DuplicatesFilterService.class, "service");
    private FilterRule filterRuleToTest = new IsDuplicateFilterRule(this.service);

    private final String message = "somemessage";

    @Test public void rule_rejects_message()
    {
        this.mockery.checking(new Expectations()
        {
            {
                one(service).isDuplicate(message);will(returnValue(true));
            }
        });
        boolean result= this.filterRuleToTest.accept(message);
        Assert.assertFalse(result);
        this.mockery.assertIsSatisfied();
    }

    @Test public void rule_accepts_message()
    {
        final String message = "somemessage";
        this.mockery.checking(new Expectations()
        {
            {
                one(service).isDuplicate(message);will(returnValue(false));
                one(service).persistMessage(message);
            }
        });
        boolean result= this.filterRuleToTest.accept(message);
        Assert.assertTrue(result);
        this.mockery.assertIsSatisfied();
    }
}
