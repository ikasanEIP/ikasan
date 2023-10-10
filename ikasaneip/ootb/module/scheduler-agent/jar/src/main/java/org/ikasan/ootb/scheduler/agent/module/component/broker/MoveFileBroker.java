package org.ikasan.ootb.scheduler.agent.module.component.broker;

import org.apache.commons.io.FileUtils;
import org.ikasan.component.endpoint.filesystem.messageprovider.CorrelatedFileList;
import org.ikasan.ootb.scheduler.agent.module.component.broker.configuration.MoveFileBrokerConfiguration;
import org.ikasan.spec.component.endpoint.Broker;
import org.ikasan.spec.component.endpoint.EndpointException;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.ikasan.spec.scheduled.dryrun.DryRunModeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MoveFileBroker implements Broker<CorrelatedFileList, CorrelatedFileList>, ConfiguredResource<MoveFileBrokerConfiguration> {

    private static Logger logger = LoggerFactory.getLogger(MoveFileBroker.class);

    private DryRunModeService dryRunModeService;
    private String configurationId;
    private MoveFileBrokerConfiguration configuration;

    public static SimpleDateFormat ARCHIVE_FILE_DATE_FORMATTER = new SimpleDateFormat("YYYYddMM_hhmmss");

    public MoveFileBroker(DryRunModeService dryRunModeService) {
        this.dryRunModeService = dryRunModeService;
        if(this.dryRunModeService == null) {
            throw new IllegalArgumentException("dryRunModeService cannot be null!");
        }
    }

    @Override
    public CorrelatedFileList invoke(CorrelatedFileList files) throws EndpointException {
        if(dryRunModeService.getDryRunMode() || dryRunModeService.isJobDryRun(configuration.getJobName()) || configuration.getMoveDirectory() == null) {
            return files;
        }

        try {
            for (File file : files.getFileList()) {
                if(!configuration.getMoveDirectory().isEmpty() && !configuration.getMoveDirectory().equals(".")) {
                    logger.info(String.format("Moving file[%s] to directory[%s]", file.getAbsolutePath(), configuration.getMoveDirectory()));

                    File archiveFile;
                    if(this.configuration.isRenameArchiveFile()) {
                        archiveFile = renameArchiveFile(file);
                    }
                    else {
                        archiveFile = new File(file.getAbsolutePath());
                    }
                    FileUtils.moveFileToDirectory(archiveFile, new File(configuration.getMoveDirectory()), true);
                }
            }
        }
        catch (Exception e) {
            throw new EndpointException(String.format("Error moving files to dir %s. %s", configuration.getMoveDirectory(), e.getMessage(), e));
        }

        return files;
    }

    private File renameArchiveFile(File file) {
        String filename;
        if(file.getName().contains(".")) {
            filename = file.getAbsolutePath() + "_" + ARCHIVE_FILE_DATE_FORMATTER.format(new Date());
        }
        else {
            filename = file.getAbsolutePath() + "_" + System.currentTimeMillis();
        }

        File archiveFile = new File(filename);
        file.renameTo(archiveFile);

        return archiveFile;
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
