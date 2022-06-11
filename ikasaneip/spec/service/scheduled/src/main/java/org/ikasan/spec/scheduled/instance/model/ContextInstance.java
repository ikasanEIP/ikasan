package org.ikasan.spec.scheduled.instance.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang3.SerializationUtils;
import org.ikasan.spec.scheduled.context.model.Context;
import org.ikasan.spec.scheduled.event.model.SchedulerJobInitiationEvent;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface ContextInstance extends Context<ContextInstance, ContextParameterInstance, SchedulerJobInstance, JobLockInstance>, StatefulEntity, Serializable {

    /**
     * Get the id of the ContextInstance
     *
     * @return
     */
    String getId();

    /**
     * Set the id of the ContextInstance
     *
     * @param id
     */
    void setId(String id);

    /**
     * Get the created date time of the ContextInstance in milli since epoch.
     *
     * @return
     */
    long getCreatedDateTime();

    /**
     * Set the created date time of the ContextInstance in milli since epoch.
     *
     * @param createdDateTime
     */
    void setCreatedDateTime(long createdDateTime);

    /**
     * Get the updated date time of the ContextInstance in milli since epoch.
     *
     * @return
     */
    long getUpdatedDateTime();

    /**
     * GSet the updated date time of the ContextInstance in milli since epoch.
     *
     * @param updatedDateTime
     */
    void setUpdatedDateTime(long updatedDateTime);

    /**
     * Get the start date time of the ContextInstance in milli since epoch.
     *
     * @return
     */
    long getStartTime();

    /**
     * Set the start date time of the ContextInstance in milli since epoch.
     *
     * @param startTime
     */
    void setStartTime(long startTime);

    /**
     * Get the end date time of the ContextInstance in milli since epoch.
     *
     * @return
     */
    long getEndTime();

    /**
     * Set the end date time of the ContextInstance in milli since epoch.
     *
     * @param endTime
     */
    void setEndTime(long endTime);

    /**
     * Get the timezone that the ContextInstance ran.
     *
     * @return
     */
    String getTimezone();

    /**
     * Set the timezone that the ContextInstance ran.
     *
     * @param timezone
     */
    void setTimezone(String timezone);

    /**
     * Get all held jobs associated with the context instance.
     *
     * @return
     */
    Map<String, SchedulerJobInitiationEvent> getHeldJobs();

    /**
     * Set all held jobs associated with the context instance.
     *
     * @param heldJobs
     */
    void setHeldJobs(Map<String, SchedulerJobInitiationEvent> heldJobs);

    /**
     * Recursively get all SchedulerJobInstances from the ContextInstance and all nested contexts.
     *
     * @return
     */
    @JsonIgnore
    default List<SchedulerJobInstance> getAllSchedulerJobInstances() {
        ArrayList<SchedulerJobInstance> results = new ArrayList<>();

        if(this.getScheduledJobs() != null && !this.getScheduledJobsMap().isEmpty()) {
            this.getScheduledJobs().forEach(job ->
            {
                SchedulerJobInstance clone = SerializationUtils.clone(job);
                clone.setChildContextName(this.getName());
                results.add(clone);
            });
        }

        if(this.getContexts() != null && !this.getContexts().isEmpty()) {
            this.getContexts().forEach(contextInstance -> results.addAll(contextInstance.getAllSchedulerJobInstances()));
        }

        return results;
    }
}
