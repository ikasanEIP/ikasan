package org.ikasan.spec.scheduled.context.model;

import org.ikasan.spec.scheduled.job.model.SchedulerJob;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface Context<CONTEXT extends Context, CONTEXT_PARAM, JOB extends SchedulerJob, JOB_LOCK extends JobLock> extends Serializable {

    String getName();

    void setName(String name);

    String getDescription();

    void setDescription(String description);

    String getTimezone();

    void setTimezone(String timezone);

    List<String> cronExpressions = new ArrayList<>();
    Map<String,String> dateTimeRanges = new HashMap<>();

    List<String> getBlackoutWindowCronExpressions();

    void setBlackoutWindowCronExpressions(List<String> blackoutWindowCronExpressions);

    Map<Long, Long> getBlackoutWindowDateTimeRanges();

    void setBlackoutWindowDateTimeRanges(Map<Long, Long> blackoutWindowDateTimeRanges);

    List<CONTEXT_PARAM> getContextParameters();

    void setContextParameters(List<CONTEXT_PARAM> contextParameters) ;

    List<JOB> getScheduledJobs();

    void setScheduledJobs(List<JOB> scheduledJobs) ;

    List<JobDependency> getJobDependencies();

    void setJobDependencies(List<JobDependency> jobDependencies) ;

    List<CONTEXT> getContexts();

    void setContexts(List<CONTEXT> contexts) ;

    List<ContextDependency> getContextDependencies();

    void setContextDependencies(List<ContextDependency> contextDependencies);

    Map<String, JOB> getScheduledJobsMap();

    Map<String, CONTEXT> getContextsMap();

    String getTimeWindowStart();

    void setTimeWindowStart(String timeWindowStart);

    String getTimeWindowEnd();

    void setTimeWindowEnd(String timeWindowEnd);

    void setJobLocks(List<JOB_LOCK> jobLocks);

    List<JOB_LOCK> getJobLocks();

    Map<String, JOB_LOCK> getJobLocksMap();

    List<JOB_LOCK> getAllNestedJobLocks();
}
