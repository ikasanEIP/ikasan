package org.ikasan.ootb.scheduler.agent.module.boot.recovery;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;

import org.ikasan.spec.dashboard.ContextInstanceRestService;

public class AgentInstanceRecoveryManager {
    private final ContextInstanceRestService contextInstanceRestService;
    private final long minutesToKeepRetrying;

    Executor executor = Executors.newSingleThreadExecutor();

    public AgentInstanceRecoveryManager(ContextInstanceRestService contextInstanceRestService, long minutesToKeepRetrying) {
        this.contextInstanceRestService = contextInstanceRestService;
        this.minutesToKeepRetrying = minutesToKeepRetrying;
    }

    @PostConstruct
    public void init() {
        executor.execute(new AgentRecoveryRunnable(this.contextInstanceRestService, this.minutesToKeepRetrying));
    }
}
