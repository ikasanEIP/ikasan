package org.ikasan.spec.scheduled.general;

import org.quartz.Trigger;

import java.util.List;
import java.util.Optional;

public interface SchedulerService {

    /**
     * Get all triggers associated with a module.
     *
     * @param contextUrl
     * @return
     */
    Optional<List<Trigger>> getTriggers(String contextUrl);

    /**
     * Trigger a scheduled flow to run immediately.
     *
     * @param contextUrl
     * @param moduleName
     * @param flowName
     * @return
     */
    boolean triggerFlowNow(String contextUrl, String moduleName, String flowName);
}
