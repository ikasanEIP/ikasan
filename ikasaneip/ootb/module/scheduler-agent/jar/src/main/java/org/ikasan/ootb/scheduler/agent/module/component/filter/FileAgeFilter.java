package org.ikasan.ootb.scheduler.agent.module.component.filter;

import org.ikasan.ootb.scheduler.agent.module.component.filter.configuration.FileAgeFilterConfiguration;
import org.ikasan.spec.component.filter.Filter;
import org.ikasan.spec.component.filter.FilterException;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.ikasan.spec.scheduled.dryrun.DryRunModeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;

public class FileAgeFilter implements Filter<List<File>>, ConfiguredResource<FileAgeFilterConfiguration> {

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
    public List<File> filter(List<File> files) throws FilterException {
        if(files == null || files.isEmpty()) {
            throw new FilterException("Received a null or empty file list!");
        }

        if(files.size() > 1) {
            StringBuffer filenames = new StringBuffer();
            files.forEach(file -> filenames.append(file.getName()).append(" "));

            logger.info("Received multiple files {}. Expecting only one.", filenames.toString());
        }
        if(dryRunModeService.getDryRunMode()) {
            return files;
        }
        else if(files.get(0).lastModified() < System.currentTimeMillis() - (this.configuration.getFileAgeSeconds() * 1000)) {
            return files;
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
    public void setConfiguredResourceId(String id) {
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
