package org.ikasan.spec.scheduled.instance.model;

public interface ScheduledContextInstanceAuditRecord {

    String getId();

    String getContextName();

    void setContextName(String contextName);

    String getContextInstanceId();

    ScheduledContextInstanceAudit getScheduledContextInstanceAudit();

    void setScheduledContextInstanceAudit(ScheduledContextInstanceAudit scheduledContextInstanceAudit);

    long getTimestamp();
}
