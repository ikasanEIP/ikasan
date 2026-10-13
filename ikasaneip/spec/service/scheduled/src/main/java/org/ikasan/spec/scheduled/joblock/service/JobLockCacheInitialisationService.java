package org.ikasan.spec.scheduled.joblock.service;

import org.ikasan.spec.scheduled.context.model.Context;

public interface JobLockCacheInitialisationService {

    /**
     * Initializes the job lock cache with the provided configuration.
     *
     * @param context              The context in which the job lock cache is being initialized.
     *                              This typically contains environment-specific details.
     * @param isRefresh            A flag indicating whether the cache should be refreshed.
     *                              If true, the cache will be updated to reflect the latest state.
     * @param clearPreviousLocks   A flag indicating whether to clear existing locks in the cache
     *                              before initialization. If true, any previously stored locks will be removed.
     */
    void initialiseJobLockCache(Context context, boolean isRefresh, boolean clearPreviousLocks);

    /**
     * Removes all job locks from the cache.
     *
     * @param context The context in which the job locks are being removed. Typically contains
     *                environmental or session-specific information required for the operation.
     */
    void removeJobLocksFromCache(Context context);
}
