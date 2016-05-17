package org.ikasan.component.endpoint.util;

import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

public class JodaFixedTimeRule implements TestRule
{
    private DateTime fixedTime;

    public JodaFixedTimeRule(long fixedTimeMillis)
    {
        this.fixedTime = new DateTime(fixedTimeMillis);
    }

    public JodaFixedTimeRule()
    {
        this(DateTime.now().getMillis());
    }

    @Override
    public Statement apply(final Statement base, Description description)
    {
        return new Statement()
        {
            @Override
            public void evaluate() throws Throwable
            {
                DateTimeUtils.setCurrentMillisFixed(fixedTime.getMillis());
                base.evaluate();
                DateTimeUtils.setCurrentMillisSystem();
            }
        };
    }

    public DateTime getFixedTime()
    {
        return fixedTime;
    }

    public void setFixedTime(DateTime newFixedTime){
        fixedTime = newFixedTime;
    }
}
