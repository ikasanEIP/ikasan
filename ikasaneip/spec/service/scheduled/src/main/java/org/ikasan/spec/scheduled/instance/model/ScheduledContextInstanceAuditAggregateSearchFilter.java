package org.ikasan.spec.scheduled.instance.model;

public class ScheduledContextInstanceAuditAggregateSearchFilter {
    private String contextName;
    private String contextInstanceId;
    private String scheduledProcessEventName;
    private String raisedInitiationEventName;

    public String getContextName() {
        return contextName;
    }

    public void setContextName(String contextName) {
        this.contextName = contextName;
    }

    public String getContextInstanceId() {
        return contextInstanceId;
    }

    public void setContextInstanceId(String contextInstanceId) {
        this.contextInstanceId = contextInstanceId;
    }

    public String getScheduledProcessEventName() {
        return scheduledProcessEventName;
    }

    public void setScheduledProcessEventName(String scheduledProcessEventName) {
        this.scheduledProcessEventName = scheduledProcessEventName;
    }

    public String getRaisedInitiationEventName() {
        return raisedInitiationEventName;
    }

    public void setRaisedInitiationEventName(String raisedInitiationEventName) {
        this.raisedInitiationEventName = raisedInitiationEventName;
    }
}
