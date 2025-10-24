package org.ikasan.ootb.scheduler.agent.module.component.broker;

import org.apache.commons.io.FileUtils;
import org.ikasan.ootb.scheduler.agent.module.model.FileWatcherJobEvent;
import org.ikasan.spec.component.endpoint.Broker;
import org.ikasan.spec.component.endpoint.EndpointException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MoveFileBroker implements Broker<FileWatcherJobEvent, FileWatcherJobEvent> {

    private static Logger logger = LoggerFactory.getLogger(MoveFileBroker.class);
    public static SimpleDateFormat ARCHIVE_FILE_DATE_FORMATTER = new SimpleDateFormat("YYYYddMM_hhmmss");


    /**
     * Constructs a MoveFileBroker with the provided DryRunModeService.
     */
    public MoveFileBroker() {
    }

    @Override
    public FileWatcherJobEvent invoke(FileWatcherJobEvent fileWatcherJobEvent) throws EndpointException {
        if(fileWatcherJobEvent.isDryRun()) {
            return fileWatcherJobEvent;
        }

        if(fileWatcherJobEvent.getCorrelatedFileList() != null && fileWatcherJobEvent.getCorrelatedFileList().getFileList() != null) {
            try {
                for (File file : fileWatcherJobEvent.getCorrelatedFileList().getFileList()) {
                    if (fileWatcherJobEvent.getMoveDirectory() != null && !fileWatcherJobEvent.getMoveDirectory().isEmpty()
                        && !fileWatcherJobEvent.getMoveDirectory().equals(".")) {
                        logger.info(String.format("Moving file[%s] to directory[%s]", file.getAbsolutePath(), fileWatcherJobEvent.getMoveDirectory()));
                        File destDir = new File(fileWatcherJobEvent.getMoveDirectory());

                        if (file.getParentFile().equals(destDir)) {
                            logger.info(String.format("Not moving file[%s] to directory[%s], as the source and destination directories are the same!"
                                , file.getAbsolutePath(), fileWatcherJobEvent.getMoveDirectory()));
                        } else {
                            File destFile = new File(destDir, file.getName());
                            if (destFile.exists()) {
                                this.renameArchiveFile(destFile);
                            }
                            FileUtils.moveFileToDirectory(new File(file.getAbsolutePath()), destDir, true);
                        }
                    }
                }
            } catch (Exception e) {
                throw new EndpointException(String.format("Error moving fileWatcherJobEvent to dir %s. %s", fileWatcherJobEvent.getMoveDirectory(), e.getMessage(), e));
            }
        }

        return fileWatcherJobEvent;
    }

    /**
     * Renames the given file by appending a timestamp to its name.
     *
     * @param file the file to be renamed
     * @return the newly renamed File object
     */
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
}
