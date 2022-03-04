package org.ikasan.ootb.scheduler.agent.module.dryrun;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.ikasan.component.endpoint.filesystem.messageprovider.FileConsumerConfiguration;
import org.ikasan.component.endpoint.quartz.consumer.ScheduledConsumerConfiguration;
import org.ikasan.ootb.scheduler.agent.module.configuration.SchedulerAgentConfiguredModuleConfiguration;
import org.ikasan.spec.configuration.ConfigurationService;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.flow.FlowElement;
import org.ikasan.spec.module.Module;
import org.ikasan.spec.module.ModuleService;
import org.ikasan.spec.scheduled.dryrun.DryRunFileListJobParameter;
import org.ikasan.spec.scheduled.dryrun.DryRunModeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import net.jodah.expiringmap.ExpirationPolicy;
import net.jodah.expiringmap.ExpiringMap;

public class DryRunModeServiceImpl implements DryRunModeService<DryRunFileListJobParameter> {

    @Value("${dry.run.map.size:1000}")
    private int maxMapSize;

    @Value("${dry.run.expiration.millis:600000}")
    private long expirationInMillis;

    @Value("${module.name}")
    private String moduleName;

    @Autowired
    private ModuleService moduleService;

    @Autowired
    private ConfigurationService configurationService;

    private Map<String, String> jobNameFileMap;

    @PostConstruct
    public void init() {
        // todo init from database
        jobNameFileMap = ExpiringMap.builder()
            .maxSize(maxMapSize)
            .expiration(expirationInMillis, TimeUnit.MILLISECONDS)
            .expirationPolicy(ExpirationPolicy.ACCESSED)
            .build();
    }

    @Override
    public void setDryRunMode(boolean dryRunMode) {
        ConfiguredResource<SchedulerAgentConfiguredModuleConfiguration> configureModule = getConfigureModule();
        configureModule.getConfiguration().setDryRunMode(dryRunMode);

        configurationService.update(configureModule);

        //clear out the map when we turn dry run mode off
        if (!dryRunMode) {
            jobNameFileMap.clear();
        }
    }

    @Override
    public boolean getDryRunMode() {
        return getSchedulerAgentConfiguredModuleConfiguration().isDryRunMode();
    }

    @Override
    public void addDryRunFileList(List<DryRunFileListJobParameter> dryRunFileList) {
        dryRunFileList.forEach(j -> jobNameFileMap.put(j.getJobName(), j.getFileName()));
    }

    @Override
    public String getDryRunFileName() {
        Map<String, String> flowDefinitions = getSchedulerAgentConfiguredModuleConfiguration().getFlowDefinitions();
        Module<Flow> module = moduleService.getModule(moduleName);
        for (String key : flowDefinitions.keySet()) {
            Flow flow = module.getFlow(key);
            FlowElement<?> file_consumer = flow.getFlowElement("File Consumer");
            if (file_consumer != null) {
                ConfiguredResource<ScheduledConsumerConfiguration> consumer =
                    (ConfiguredResource<ScheduledConsumerConfiguration>) file_consumer.getFlowComponent();
                FileConsumerConfiguration fcc = (FileConsumerConfiguration) consumer.getConfiguration();
                if (jobNameIsInMap(fcc)) {
                    // TODO remove from map
                    return jobNameFileMap.get(fcc.getJobName());
                }
            }
        }
        return null;
    }

    private boolean jobNameIsInMap(FileConsumerConfiguration fc) {
        return fc != null && jobNameFileMap.containsKey(fc.getJobName());
    }

    private SchedulerAgentConfiguredModuleConfiguration getSchedulerAgentConfiguredModuleConfiguration() {
        return getConfigureModule().getConfiguration();
    }

    private ConfiguredResource<SchedulerAgentConfiguredModuleConfiguration> getConfigureModule() {
        Module<Flow> module = moduleService.getModule(moduleName);
        ConfiguredResource<SchedulerAgentConfiguredModuleConfiguration> configuredModule =
            (ConfiguredResource<SchedulerAgentConfiguredModuleConfiguration>) module;
        return configuredModule;
    }
}

