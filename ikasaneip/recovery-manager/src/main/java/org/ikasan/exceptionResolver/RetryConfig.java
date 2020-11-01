package org.ikasan.exceptionResolver;

public class RetryConfig
{
    private Class className;

    private long delayInMillis;

    private int maxRetries;

    public Class getClassName()
    {
        return className;
    }

    public void setClassName(Class className)
    {
        this.className = className;
    }

    public long getDelayInMillis()
    {
        return delayInMillis;
    }

    public void setDelayInMillis(long delayInMillis)
    {
        this.delayInMillis = delayInMillis;
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
