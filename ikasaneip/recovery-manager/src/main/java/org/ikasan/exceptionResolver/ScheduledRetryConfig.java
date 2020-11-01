package org.ikasan.exceptionResolver;

public class ScheduledRetryConfig
{
    private Class className;

    private String cronExpression;

    private int maxRetries;

    public Class getClassName()
    {
        return className;
    }

    public void setClassName(Class className)
    {
        this.className = className;
    }

    public String getCronExpression()
    {
        return cronExpression;
    }

    public void setCronExpression(String cronExpression)
    {
        this.cronExpression = cronExpression;
    }

    public int getMaxRetries()
    {
        return maxRetries;
    }

    public void setMaxRetries(int maxRetries)
    {
        this.maxRetries = maxRetries;
    }
}
