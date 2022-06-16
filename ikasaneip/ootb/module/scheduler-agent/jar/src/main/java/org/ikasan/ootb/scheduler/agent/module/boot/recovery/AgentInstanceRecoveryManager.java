package org.ikasan.ootb.scheduler.agent.module.boot.recovery;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;

import org.ikasan.spec.dashboard.ContextInstanceRestService;

public class AgentInstanceRecoveryManager {
    private final ContextInstanceRestService contextInstanceRestService;
    private final long minutesToKeepRetrying;
    private boolean agentRecoveryActive;

    Executor executor = Executors.newSingleThreadExecutor();

    public AgentInstanceRecoveryManager(ContextInstanceRestService contextInstanceRestService, long minutesToKeepRetrying, boolean agentRecoveryActive) {
        this.contextInstanceRestService = contextInstanceRestService;
        this.minutesToKeepRetrying = minutesToKeepRetrying;
        this.agentRecoveryActive = agentRecoveryActive;
    }

    @PostConstruct
    public void init() {
        if (agentRecoveryActive) {
            executor.execute(new AgentRecoveryRunnable(this.contextInstanceRestService, this.minutesToKeepRetrying));
        }
    }
}
