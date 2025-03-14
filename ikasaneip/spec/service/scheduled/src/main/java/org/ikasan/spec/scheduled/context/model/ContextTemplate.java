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
     * Set whether to delay agent synchronisation until the next instance.
     *
     * @param delayAgentSynchronisationUntilNextInstance true to delay agent synchronisation until the next instance, false otherwise
     */
    void setDelayAgentSynchronisationUntilNextInstance(boolean delayAgentSynchronisationUntilNextInstance);

    /**
     * Checks whether the agent synchronization should be delayed until the next instance.
     *
     * @return true if agent synchronization should be delayed until the next instance, false otherwise
     */
    boolean isDelayAgentSynchronisationUntilNextInstance();

    /**
     * Set whether agent synchronization is required or not for the current context template.
     *
     * @param requiresAgentSynchronisation true if agent synchronization is required, false otherwise
     */
    void setRequiresAgentSynchronisation(boolean requiresAgentSynchronisation);


    /**
     * Indicates whether the agent synchronization is required or not.
     * This method is used to check if agent synchronization is needed for the current context template.
     *
     * @return true if agent synchronization is required, false otherwise
     */
    boolean isRequiresAgentSynchronisation();

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
