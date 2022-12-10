package org.ikasan.ootb.scheduler.agent.module.component.filter;

import org.ikasan.component.endpoint.filesystem.messageprovider.CorrelatedFileList;
import org.ikasan.ootb.scheduler.agent.module.component.filter.configuration.FileAgeFilterConfiguration;
import org.ikasan.spec.component.filter.Filter;
import org.ikasan.spec.component.filter.FilterException;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.ikasan.spec.scheduled.dryrun.DryRunModeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;

public class FileAgeFilter implements Filter<CorrelatedFileList>, ConfiguredResource<FileAgeFilterConfiguration> {

    private static Logger logger = LoggerFactory.getLogger(FileAgeFilter.class);

    private DryRunModeService dryRunModeService;
    private FileAgeFilterConfiguration configuration;
    private String configurationId;

    public FileAgeFilter(DryRunModeService dryRunModeService) {
        this.dryRunModeService = dryRunModeService;
        if(this.dryRunModeService == null) {
            throw new IllegalArgumentException("dryRunModeService cannot be null!");
        }
    }

    @Override
    public CorrelatedFileList filter(CorrelatedFileList correlatedFileList) throws FilterException {
        if(correlatedFileList.getFileList() == null || correlatedFileList.getFileList().isEmpty()) {
            throw new FilterException("Received a null or empty file list!");
        }

        if(correlatedFileList.getFileList().size() > 1) {
            StringBuffer filenames = new StringBuffer();
            correlatedFileList.getFileList().forEach(file -> filenames.append(file.getName()).append(" "));

            logger.info("Received multiple files {}. Expecting only one.", filenames.toString());
        }
        if(this.dryRunModeService.getDryRunMode() || this.dryRunModeService.isJobDryRun(this.configuration.getJobName())) {
            return correlatedFileList;
        }
        else if(correlatedFileList.getFileList().get(0).lastModified() < System.currentTimeMillis() - (this.configuration.getFileAgeSeconds() * 1000)) {
            return correlatedFileList;
        }
        else {
            return null;
        }
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
    public FileAgeFilterConfiguration getConfiguration() {
        return this.configuration;
    }

    @Override
    public void setConfiguration(FileAgeFilterConfiguration configuration) {
        this.configuration = configuration;
    }
}
