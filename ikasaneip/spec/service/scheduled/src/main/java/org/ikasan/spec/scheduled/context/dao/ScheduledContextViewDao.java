package org.ikasan.spec.scheduled.context.dao;

import org.ikasan.spec.scheduled.context.model.ScheduledContextViewRecord;

public interface ScheduledContextViewDao {

    String SCHEDULED_CONTEXT_VIEW_TYPE = "scheduledContextView";
    long DO_NOT_EXPIRE = -1L;

    /**
     * Save a ScheduledContextViewRecord
     *
     * @param scheduledContextViewRecord
     */
    void save(ScheduledContextViewRecord scheduledContextViewRecord);

    /**
     * Get a context view.
     *
     * @return
     */
    ScheduledContextViewRecord getContextView(String parentContextName, String contextName);

}
