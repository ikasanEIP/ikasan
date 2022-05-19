package org.ikasan.spec.scheduled.job.model;

import java.util.List;

public interface SchedulerJobSearchFilter {

    public String getJobNameFilter();

    public void setJobNameFilter(String jobNameFilter);

    public String getJobTypeFilter();

    public void setJobTypeFilter(String jobTypeFilter);

    public String getContextSearchFilter();

    public void setContextSearchFilter(String contextSearchFilter);

    public List<String> getTobTypes();
}
