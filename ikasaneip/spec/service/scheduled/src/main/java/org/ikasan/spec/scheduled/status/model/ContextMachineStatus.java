package org.ikasan.spec.scheduled.status.model;

import org.ikasan.spec.scheduled.instance.model.InstanceStatus;

public interface ContextMachineStatus {

    String getContextName();
    void setContextName(String contextName);
    String getContextInstanceId();
    void setContextInstanceId(String contextInstanceId);
    InstanceStatus getInstanceStatus();
    void setInstanceStatus(InstanceStatus instanceStatus);
}
