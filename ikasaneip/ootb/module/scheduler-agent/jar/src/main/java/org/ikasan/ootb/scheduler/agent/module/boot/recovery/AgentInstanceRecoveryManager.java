package org.ikasan.ootb.scheduler.agent.module.boot.recovery;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;

import org.ikasan.spec.dashboard.ContextInstanceRestService;
import org.ikasan.spec.module.ModuleService;

public class AgentInstanceRecoveryManager {
    private final ContextInstanceRestService contextInstanceRestService;
    private final long minutesToKeepRetrying;
    private boolean agentRecoveryActive;
    private String moduleName;
    private ModuleService moduleService;

    Executor executor = Executors.newSingleThreadExecutor();

    public AgentInstanceRecoveryManager(ContextInstanceRestService contextInstanceRestService,
                                        long minutesToKeepRetrying,
                                        boolean agentRecoveryActive,
                                        String moduleName,
                                        ModuleService moduleService) {
        this.contextInstanceRestService = contextInstanceRestService;
        this.minutesToKeepRetrying = minutesToKeepRetrying;
        this.agentRecoveryActive = agentRecoveryActive;
        this.moduleName = moduleName;
        this.moduleService = moduleService;
    }

    @PostConstruct
    public void init() {
        if (agentRecoveryActive) {
            executor.execute(new AgentRecoveryRunnable(this.contextInstanceRestService, this.minutesToKeepRetrying, this.moduleName, this.moduleService));
        }
    }
}
