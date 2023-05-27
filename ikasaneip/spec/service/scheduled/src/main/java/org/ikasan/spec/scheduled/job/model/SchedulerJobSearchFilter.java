package org.ikasan.spec.scheduled.job.model;

import java.util.List;

public interface SchedulerJobSearchFilter {

    public String getJobNameFilter();

    public void setJobNameFilter(String jobNameFilter);

    public List<String> getNotJobNameInFilter();

    public void setNotJobNameInFilter(List<String> notJobNameInFilter);

    public String getJobTypeFilter();

    public void setJobTypeFilter(String jobTypeFilter);

    public String getContextSearchFilter();

    public void setContextSearchFilter(String contextSearchFilter);

    public List<String> getTobTypes();

    public boolean isHeld();

    public void setHeld(boolean held);

    public boolean isSkipped();

    public void setSkipped(boolean skipped);

    void setStatus(String status);

    void setTargetResidingContextOnly(Boolean targetResidingContextOnly);

    Boolean isTargetResidingContextOnly();

    void setParticipatesInLock(Boolean participatesInLock);

    Boolean isParticipatesInLock();
}
