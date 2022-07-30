package org.ikasan.spec.scheduled.context.dao;

import org.ikasan.spec.scheduled.context.model.ScheduledContextViewRecord;

public interface ScheduledContextViewDao {

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
