package org.ikasan.spec.scheduled.status.model;

import java.util.List;

public interface ContextJobInstanceStatusWrapper {

    List<ContextJobInstanceStatus> getJobPlans();
    void setJobPlans(List<ContextJobInstanceStatus> jobPlans);
}
