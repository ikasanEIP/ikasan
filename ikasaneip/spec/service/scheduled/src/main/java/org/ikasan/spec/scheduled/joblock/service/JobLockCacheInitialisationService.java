package org.ikasan.spec.scheduled.joblock.service;

import org.ikasan.spec.scheduled.context.model.Context;

public interface JobLockCacheInitialisationService {

    /**
     * Method to initialise the job lock cache for a Context. If isRefresh
     * flag is true, the underlying job lock cache will be built from the
     * context. This typically happens when a context is reset or created.
     *
     * @param context
     * @param isRefresh
     */
    void initialiseJobLockCache(Context context, boolean isRefresh);

    /**
     * Remove the job locks from the job lock cache for a Context.
     * @param context
     */
    void removeJobLocksFromCache(Context context);
}
