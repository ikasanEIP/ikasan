package org.ikasan.ootb.scheduler.agent.module.component.filter;

import org.ikasan.ootb.scheduler.agent.module.model.FileWatcherJobEvent;
import org.ikasan.spec.component.filter.Filter;
import org.ikasan.spec.component.filter.FilterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileAgeFilter implements Filter<FileWatcherJobEvent> {

    private static Logger logger = LoggerFactory.getLogger(FileAgeFilter.class);

    @Override
    public FileWatcherJobEvent filter(FileWatcherJobEvent fileWatcherJobEvent) throws FilterException {
        if(fileWatcherJobEvent.isDryRun()) {
            return fileWatcherJobEvent;
        }

        if(fileWatcherJobEvent.getCorrelatedFileList() == null || fileWatcherJobEvent.getCorrelatedFileList().getFileList() == null
            || fileWatcherJobEvent.getCorrelatedFileList().getFileList().isEmpty()) {
            throw new FilterException("Received a null or empty file list!");
        }

        if(fileWatcherJobEvent.getCorrelatedFileList().getFileList().size() > 1) {
            StringBuffer filenames = new StringBuffer();
            fileWatcherJobEvent.getCorrelatedFileList().getFileList().forEach(file -> filenames.append(file.getName()).append(" "));

            logger.info("Received multiple files {}. Expecting only one.", filenames);
        }

        if(fileWatcherJobEvent.getCorrelatedFileList().getFileList().get(0).lastModified() < System.currentTimeMillis() - (fileWatcherJobEvent.getMinFileAgeSeconds() * 1000)) {
            return fileWatcherJobEvent;
        }
        else {
            return null;
        }
    }
}
