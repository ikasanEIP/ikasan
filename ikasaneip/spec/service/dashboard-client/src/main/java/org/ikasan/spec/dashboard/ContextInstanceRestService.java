package org.ikasan.spec.dashboard;

import org.ikasan.spec.scheduled.job.model.FileEventDrivenJob;

import java.util.Map;

public interface ContextInstanceRestService<T> {

    /**
     * Retrieves all instances that the dashboard thinks the agent should handle for a given agent name.
     *
     * @param agentName The name of the agent to retrieve instances for.
     * @return A map of instance names to their corresponding objects.
     */
    Map<String, T> getAllInstancesDashboardThinksAgentShouldHandle(String agentName);

    /**
     * Retrieves a FileEventDrivenJob based on the provided job name and context name.
     *
     * @param jobName The name of the job to retrieve.
     * @param contextName The name of the context associated with the job.
     * @return A FileEventDrivenJob object matching the provided job name and context name, or null if not found.
     */
    FileEventDrivenJob getFileEventJob(String jobName, String contextName);
}
