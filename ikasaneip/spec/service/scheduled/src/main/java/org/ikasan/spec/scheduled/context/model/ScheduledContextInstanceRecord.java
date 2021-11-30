package org.ikasan.spec.scheduled.context.model;

public interface ScheduledContextInstanceRecord {

    String getId();

    String getContextName();

    String getContextInstance();

    String getStatus();

    void setStatus(String status);

    long getTimestamp();
}
