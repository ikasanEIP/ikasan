package org.ikasan.spec.scheduled.context.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang3.SerializationUtils;
import org.ikasan.spec.scheduled.job.model.SchedulerJob;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public interface ContextTemplate extends Context<ContextTemplate, ContextParameter, SchedulerJob, JobLock>, Serializable {

    /**
     * Set context template to disabled.
     *
     * @param disabled
     */
    void setDisabled(boolean disabled);

    /**
     * Determine if the context template is disabled.
     *
     * @return
     */
    boolean isDisabled();


    /**
     * Retrieve all instances of SchedulerJob from the current ContextTemplate and its child Contexts.
     *
     * @return a list of SchedulerJob instances
     */
    @JsonIgnore
    default List<SchedulerJob> getAllSchedulerJobs() {
        ArrayList<SchedulerJob> results = new ArrayList<>();

        if(this.getScheduledJobs() != null && !this.getScheduledJobsMap().isEmpty()) {
            this.getScheduledJobs().forEach(job ->
            {
                SchedulerJob clone = SerializationUtils.clone(job);
                results.add(clone);
            });
        }

        if(this.getContexts() != null && !this.getContexts().isEmpty()) {
            this.getContexts().forEach(contextInstance -> results.addAll(contextInstance.getAllSchedulerJobs()));
        }

        return results;
    }

    /**
     * Retrieves all context names where a job with the given identifier resides.
     *
     * @param jobIdentifier the identifier of the job
     * @return a list of context names
     */
    @JsonIgnore
    default List<String> getAllContextNamesWhereJobResides(String jobIdentifier) {
        ArrayList<String> results = new ArrayList<>();

        if(this.getScheduledJobs() != null && !this.getScheduledJobsMap().isEmpty()) {
            if(this.getScheduledJobsMap().containsKey(jobIdentifier)) {
                results.add(this.getName());
            }
        }

        if(this.getContexts() != null && !this.getContexts().isEmpty()) {
            this.getContexts().forEach(contextInstance -> results
                .addAll(contextInstance.getAllContextNamesWhereJobResides(jobIdentifier)));
        }

        return results.stream().distinct().collect(Collectors.toList());
    }
}
