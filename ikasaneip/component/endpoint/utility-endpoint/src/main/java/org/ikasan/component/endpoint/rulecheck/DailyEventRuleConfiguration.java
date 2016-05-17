package org.ikasan.component.endpoint.rulecheck;

public class DailyEventRuleConfiguration extends RuleCheckConfiguration
{
    private volatile String lastDateProcessed;

    public String getLastDateProcessed()
    {
        return lastDateProcessed;
    }

    public void setLastDateProcessed(String lastDateProcessed)
    {
        this.lastDateProcessed = lastDateProcessed;
    }
}
