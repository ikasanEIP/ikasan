package org.ikasan.spec.scheduled.joblock.service;

import org.ikasan.spec.scheduled.context.model.Context;
import org.ikasan.spec.scheduled.context.model.ContextTemplate;

public interface JobLockCacheInitialisationService {

    /**
     * Method to initialise the job lock cache for a Context.
     * @param context
     */
    void initialiseJobLockCache(Context context);

    /**
     * Remove the job locks from the job lock cache for a Context.
     * @param context
     */
    void removeJobLocksFromCache(Context context);
}
