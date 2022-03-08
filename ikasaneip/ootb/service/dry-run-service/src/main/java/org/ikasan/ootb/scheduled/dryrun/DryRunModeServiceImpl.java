package org.ikasan.ootb.scheduled.dryrun;

import net.jodah.expiringmap.ExpirationPolicy;
import net.jodah.expiringmap.ExpiringMap;
import org.ikasan.ootb.scheduled.dryrun.configuration.DryRunConfiguredModuleConfiguration;
import org.ikasan.spec.configuration.ConfigurationService;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.module.Module;
import org.ikasan.spec.module.ModuleService;
import org.ikasan.spec.scheduled.dryrun.DryRunFileListJobParameter;
import org.ikasan.spec.scheduled.dryrun.DryRunModeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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
        jobNameFileMap = ExpiringMap.builder()
            .maxSize(maxMapSize)
            .expiration(expirationInMillis, TimeUnit.MILLISECONDS)
            .expirationPolicy(ExpirationPolicy.ACCESSED)
            .build();
    }

    @Override
    public void setDryRunMode(boolean dryRunMode) {
        ConfiguredResource<DryRunConfiguredModuleConfiguration> configureModule = getConfigureModule();
        configureModule.getConfiguration().setDryRunMode(dryRunMode);

        configurationService.update(configureModule);

        clearOutMapIfDryRunModeIsFalse(dryRunMode);
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
    public String getJobFileName(String jobFileName) {
        String fileName = null;
        if (jobNameFileMap.containsKey(jobFileName)) {
            fileName = jobNameFileMap.get(jobFileName);
            jobNameFileMap.remove(jobFileName);
        }
        return fileName;
    }

    private DryRunConfiguredModuleConfiguration getSchedulerAgentConfiguredModuleConfiguration() {
        return getConfigureModule().getConfiguration();
    }

    private ConfiguredResource<DryRunConfiguredModuleConfiguration> getConfigureModule() {
        Module<Flow> module = moduleService.getModule(moduleName);
        ConfiguredResource<DryRunConfiguredModuleConfiguration> configuredModule =
            (ConfiguredResource<DryRunConfiguredModuleConfiguration>) module;
        return configuredModule;
    }

    private void clearOutMapIfDryRunModeIsFalse(boolean dryRunMode) {
        if (!dryRunMode) {
            jobNameFileMap.clear();
        }
    }

}

