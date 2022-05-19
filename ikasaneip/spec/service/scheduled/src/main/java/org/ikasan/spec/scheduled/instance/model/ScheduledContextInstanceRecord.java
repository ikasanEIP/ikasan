package org.ikasan.spec.scheduled.instance.model;

public interface ScheduledContextInstanceRecord {

    String getId();

    String getContextName();

    void setContextName(String contextName);

    String getContextInstanceId();

    void setContextInstanceId(String contextInstanceId);

    ContextInstance getContextInstance();

    void setContextInstance(ContextInstance context);

    String getStatus();

    void setStatus(String status);

    long getTimestamp();

    void setTimestamp(long timestamp);

    long getModifiedTimestamp();

    void setModifiedTimestamp(long timestamp);

    String getModifiedBy();

    void setModifiedBy(String modifiedBy);
}
