package org.ikasan.spec.scheduled.status.model;

import org.ikasan.spec.scheduled.instance.model.InstanceStatus;

import java.util.Set;

public interface ContextJobInstanceDetailsStatus {

    String getJobName();
    void setJobName(String jobName);
    Set<String> getChildContextName();
    void setChildContextName(Set<String> childContextName);
    InstanceStatus getInstanceStatus();
    void setInstanceStatus(InstanceStatus instanceStatus);
    boolean isTargetResidingContextOnly();
    void setTargetResidingContextOnly(boolean targetResidingContextOnly);
    long getStartTime();
    void setStartTime(long startTime);
    long getEndTime();
    void setEndTime(long endTime);

    /**
     * Check if the job already exist if targetResidingContextOnly = true.
     * helper method to add into the childContextName.
     *
     * Not required to implement for POJO for model.
     * @param jobName name of job
     * @return true if already existing, false if not
     */
    boolean checkExist(String jobName);
}
