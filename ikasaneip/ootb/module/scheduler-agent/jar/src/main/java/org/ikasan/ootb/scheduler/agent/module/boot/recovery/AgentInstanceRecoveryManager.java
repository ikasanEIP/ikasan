package org.ikasan.ootb.scheduler.agent.module.boot.recovery;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import jakarta.annotation.PostConstruct;
import org.ikasan.spec.dashboard.ContextInstanceRestService;
import org.ikasan.spec.module.ModuleService;
import org.ikasan.spec.scheduled.provision.ContextInstanceIdentifierProvisionService;

public class AgentInstanceRecoveryManager {
    private final ContextInstanceRestService contextInstanceRestService;
    private final ContextInstanceIdentifierProvisionService contextInstanceIdentifierProvisionService;
    private final long minutesToKeepRetrying;
    private final boolean agentRecoveryActive;
    private final String moduleName;
    private final ModuleService moduleService;

    Executor executor = Executors.newSingleThreadExecutor();

    public AgentInstanceRecoveryManager(ContextInstanceRestService contextInstanceRestService,
                                        ContextInstanceIdentifierProvisionService contextInstanceIdentifierProvisionService,
                                        long minutesToKeepRetrying,
                                        boolean agentRecoveryActive,
                                        String moduleName,
                                        ModuleService moduleService) {
        this.contextInstanceRestService = contextInstanceRestService;
        this.contextInstanceIdentifierProvisionService = contextInstanceIdentifierProvisionService;
        this.minutesToKeepRetrying = minutesToKeepRetrying;
        this.agentRecoveryActive = agentRecoveryActive;
        this.moduleName = moduleName;
        this.moduleService = moduleService;
    }

    @PostConstruct
    public void init() {
        if (agentRecoveryActive) {
            executor.execute(new AgentRecoveryRunnable(this.contextInstanceRestService, this.contextInstanceIdentifierProvisionService,
                this.minutesToKeepRetrying, this.moduleName, this.moduleService));
        }
    }
}
