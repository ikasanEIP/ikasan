package org.ikasan.spec.scheduled.context.model;

import org.ikasan.spec.scheduled.job.model.SchedulerJob;
import org.ikasan.spec.scheduled.job.model.SchedulerJobLockParticipant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AbstractJobLockHolder {

    protected final Map<String, List<SchedulerJobLockParticipant>> schedulerJobs = new ConcurrentHashMap<>();

    /**
     * Add scheduler jobs to the map of scheduler jobs.
     * If the context name does not already exist in the map, a new entry is created.
     *
     * @param contextName The name of the context
     * @param jobs        The list of SchedulerJobLockParticipant jobs to be added
     */
    public final void addSchedulerJobs(String contextName, List<SchedulerJobLockParticipant> jobs) {
        if(!this.schedulerJobs.containsKey(contextName)) {
            this.schedulerJobs.put(contextName, new ArrayList<>());
        }
        this.schedulerJobs.get(contextName).addAll(jobs);
    }

    /**
     * Removes all scheduler jobs for a given context from the map of scheduler jobs.
     *
     * @param context The context for which to remove scheduler jobs
     */
    public final synchronized void removeSchedulerJobsForContext(Context context) {
        Map<String, List<SchedulerJobLockParticipant>> newJobLockMap = new HashMap<>();
        this.schedulerJobs.entrySet().forEach(entry -> {
            entry.getValue().forEach(job -> {
                if(!job.getContextName().equals(context.getName())) {
                    if(!newJobLockMap.containsKey(entry.getKey())) {
                        newJobLockMap.put(entry.getKey(), new ArrayList<>());
                    }
                    newJobLockMap.get(entry.getKey()).add(job);
                }
            });
        });

        this.schedulerJobs.clear();
        this.schedulerJobs.putAll(newJobLockMap);
    }
}
