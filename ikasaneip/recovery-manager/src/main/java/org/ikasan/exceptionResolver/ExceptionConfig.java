package org.ikasan.exceptionResolver;

import java.util.List;

public class ExceptionConfig
{
    private List<RetryConfig> retryConfigs;

    private List<ScheduledRetryConfig> scheduledRetryConfigs;

    private List<Class> excludedClasses;

    private List<Class> ignoredClasses;

    private List<Class> stopClasses;

    public List<RetryConfig> getRetryConfigs()
    {
        return retryConfigs;
    }

    public void setRetryConfigs(List<RetryConfig> retryConfigs)
    {
        this.retryConfigs = retryConfigs;
    }

    public List<ScheduledRetryConfig> getScheduledRetryConfigs()
    {
        return scheduledRetryConfigs;
    }

    public void setScheduledRetryConfigs(List<ScheduledRetryConfig> scheduledRetryConfigs)
    {
        this.scheduledRetryConfigs = scheduledRetryConfigs;
    }

    public List<Class> getExcludedClasses()
    {
        return excludedClasses;
    }

    public void setExcludedClasses(List<Class> excludedClasses)
    {
        this.excludedClasses = excludedClasses;
    }

    public List<Class> getIgnoredClasses()
    {
        return ignoredClasses;
    }

    public void setIgnoredClasses(List<Class> ignoredClasses)
    {
        this.ignoredClasses = ignoredClasses;
    }

    public List<Class> getStopClasses()
    {
        return stopClasses;
    }

    public void setStopClasses(List<Class> stopClasses)
    {
        this.stopClasses = stopClasses;
    }
}
