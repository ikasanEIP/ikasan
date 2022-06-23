package org.ikasan.ootb.scheduler.agent.module.component.broker;

import org.apache.commons.io.FileUtils;
import org.ikasan.ootb.scheduler.agent.module.component.broker.configuration.MoveFileBrokerConfiguration;
import org.ikasan.spec.component.endpoint.Broker;
import org.ikasan.spec.component.endpoint.EndpointException;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.ikasan.spec.scheduled.dryrun.DryRunModeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;

public class MoveFileBroker implements Broker<List<File>, List<File>>, ConfiguredResource<MoveFileBrokerConfiguration> {

    private static Logger logger = LoggerFactory.getLogger(MoveFileBroker.class);

    private DryRunModeService dryRunModeService;
    private String configurationId;
    private MoveFileBrokerConfiguration configuration;

    public MoveFileBroker(DryRunModeService dryRunModeService) {
        this.dryRunModeService = dryRunModeService;
        if(this.dryRunModeService == null) {
            throw new IllegalArgumentException("dryRunModeService cannot be null!");
        }
    }

    @Override
    public List<File> invoke(List<File> files) throws EndpointException {
        if(dryRunModeService.getDryRunMode() || dryRunModeService.isJobDryRun(configuration.getJobName()) || configuration.getMoveDirectory() == null) {
            return files;
        }

        try {
            for (File file : files) {
                logger.info(String.format("Moving file[%s] to directory[%s]", file.getAbsolutePath(), configuration.getMoveDirectory()));
                FileUtils.moveFileToDirectory(file, new File(configuration.getMoveDirectory()), true);
            }
        }
        catch (Exception e) {
            throw new EndpointException(String.format("Error moving files to dir %s.", configuration.getMoveDirectory(), e));
        }

        return files;
    }

    @Override
    public String getConfiguredResourceId() {
        return this.configurationId;
    }

    @Override
    public void setConfiguredResourceId(String configurationId) {
        this.configurationId = configurationId;
    }

    @Override
    public MoveFileBrokerConfiguration getConfiguration() {
        return this.configuration;
    }

    @Override
    public void setConfiguration(MoveFileBrokerConfiguration configuration) {
        this.configuration = configuration;
    }
}
