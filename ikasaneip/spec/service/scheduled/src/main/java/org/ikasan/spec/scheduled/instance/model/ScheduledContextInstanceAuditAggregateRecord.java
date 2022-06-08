package org.ikasan.spec.scheduled.instance.model;

public interface ScheduledContextInstanceAuditAggregateRecord {

    String getId();

    String getContextName();

    void setContextName(String contextName);

    String getContextInstanceId();

    void setContextInstanceId(String contextInstanceId);

    String getScheduledProcessEventName();

    void setScheduledProcessEventName(String scheduledProcessEventName);

    String getRaisedEvents();

    ScheduledContextInstanceAuditAggregate getScheduledContextInstanceAuditAggregate();

    void setScheduledContextInstanceAuditAggregate(ScheduledContextInstanceAuditAggregate scheduledContextInstanceAudit);

    long getTimestamp();
}
