package org.ikasan.spec.scheduled.context.model;

import org.ikasan.spec.scheduled.job.model.SchedulerJob;

import java.io.Serializable;

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
}
