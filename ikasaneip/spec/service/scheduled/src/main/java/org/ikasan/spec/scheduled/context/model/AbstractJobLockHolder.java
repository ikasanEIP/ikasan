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

    public final void addSchedulerJobs(String contextName, List<SchedulerJobLockParticipant> jobs) {
        if(!this.schedulerJobs.containsKey(contextName)) {
            this.schedulerJobs.put(contextName, new ArrayList<>());
        }
        this.schedulerJobs.get(contextName).addAll(jobs);
    }

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
