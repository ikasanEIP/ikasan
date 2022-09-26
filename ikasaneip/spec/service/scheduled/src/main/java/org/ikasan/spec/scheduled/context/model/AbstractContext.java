package org.ikasan.spec.scheduled.context.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.ikasan.spec.scheduled.job.model.SchedulerJob;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class AbstractContext<CONTEXT extends Context, JOB extends SchedulerJob, JOB_LOCK extends JobLock> implements Serializable {

    protected List<CONTEXT> contexts = new ArrayList<>();
    protected List<JOB> scheduledJobs = new ArrayList<>() ;
    protected List<JOB_LOCK> jobLocks = new ArrayList<>();

    @JsonIgnore
    protected Map<String, JOB> scheduledJobsMap = new HashMap<>();
    @JsonIgnore
    protected Map<String, CONTEXT> contextsMap = new HashMap<>();
    @JsonIgnore
    protected Map<String, JOB_LOCK> jobLocksMap = new HashMap<>();

    /**
     * Method to get the context name.
     *
     * @return
     */
    public abstract String getName();

    /**
     * Get the scheduler jobs associated with the context.
     *
     * @return
     */
    public final List<JOB> getScheduledJobs() {
        return scheduledJobs;
    }

    /**
     * Get the scheduled job map.
     *
     * @return
     */
    public final Map<String, JOB> getScheduledJobsMap() {
        return scheduledJobsMap;
    }

    /**
     * Get the job lock map.
     *
     * @return
     */
    public final Map<String, JOB_LOCK> getJobLocksMap() {
        return jobLocksMap;
    }

    /**
     * Set the scheduler jobs associated with the context.
     *
     * @param scheduledJobs
     */
    public final void setScheduledJobs(List<JOB> scheduledJobs) {
        this.scheduledJobs = scheduledJobs;
        if(scheduledJobs != null) {
            this.scheduledJobsMap = this.scheduledJobs.stream()
                .collect(Collectors.toMap(item -> item.getIdentifier() , item -> item, (a1, a2) -> a1));
        }
    }

    /**
     * Get all nested contexts.
     *
     * @return
     */
    public final List<CONTEXT> getContexts() {
        return contexts;
    }

    /**
     * Set the nested contexts.
     *
     * @param contexts
     */
    public final void setContexts(List<CONTEXT> contexts) {
        this.contexts = contexts;
        if(this.contexts != null) {
            this.contextsMap = this.contexts.stream()
                .collect(Collectors.toMap(item -> item.getName(), item -> item, (a1, a2) -> a1));
        }
    }

    /**
     * Set the jobs locks for the context.
     *
     * @param jobLocks
     */
    public final void setJobLocks(List<JOB_LOCK> jobLocks) {
        this.jobLocks = jobLocks;

        if(this.jobLocks != null) {
            this.jobLocksMap = this.jobLocks.stream()
                .collect(Collectors.toMap(JobLock::getName, item -> item));
        }
    }

    /**
     * Get the job locks associated with the context.
     *
     * @return
     */
    public final List<JOB_LOCK> getJobLocks() {
        return this.jobLocks;
    }


    /**
     * Get jobs locks assocaited with this and all nested contexts.
     *
     * @return
     */
    @JsonIgnore
    public final List<JOB_LOCK> getAllNestedJobLocks() {
        List<JOB_LOCK> jobLocks = this._getAllNestedJobLocks();
        jobLocks.forEach(jobLock -> {
            if(jobLock.getJobs() != null) {
                jobLock.getJobs()
                    .values().forEach(jobs -> jobs
                        .forEach(job -> job.setContextName(this.getName())));
            }
        });
        return jobLocks;
    }

    /**
     * Internal helper method.
     *
     * @return
     */
    private List<JOB_LOCK> _getAllNestedJobLocks() {
        List<JOB_LOCK> jobLocks = new ArrayList<>();
        if (this.getJobLocks() != null) {
            jobLocks.addAll(this.getJobLocks());
        }
        if (this.getContexts() != null) {
            this.getContexts().forEach(c -> jobLocks.addAll(c.getAllNestedJobLocks()));
        }
        return jobLocks;
    }
}
