package org.ikasan.spec.scheduled.status.model;

import org.ikasan.spec.scheduled.instance.model.InstanceStatus;

import java.util.List;

public interface ContextJobInstanceStatus {
    String getContextName();
    void setContextName(String contextName);
    String getContextInstanceId();
    void setContextInstanceId(String contextInstanceId);
    InstanceStatus getInstanceStatus();
    void setInstanceStatus(InstanceStatus instanceStatus);
    List<ContextJobInstanceDetailsStatus> getJobDetails();
    void setJobDetails(List<ContextJobInstanceDetailsStatus> jobDetails);
}
