package org.ikasan.spec.scheduled.instance.model;

public interface ScheduledContextInstanceRecord {

    String getId();

    String getContextName();

    void setContextName(String contextName);

    ContextInstance getContextInstance();

    void setContextInstance(ContextInstance context);

    String getStatus();

    void setStatus(String status);

    long getTimestamp();

    void setTimestamp(long timestamp);
}
