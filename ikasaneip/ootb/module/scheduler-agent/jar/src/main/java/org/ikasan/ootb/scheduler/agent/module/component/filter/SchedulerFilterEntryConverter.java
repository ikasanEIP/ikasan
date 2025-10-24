package org.ikasan.ootb.scheduler.agent.module.component.filter;

import org.ikasan.filter.duplicate.model.DefaultFilterEntry;
import org.ikasan.filter.duplicate.model.FilterEntry;
import org.ikasan.filter.duplicate.model.FilterEntryConverter;
import org.ikasan.filter.duplicate.model.FilterEntryConverterException;
import org.ikasan.ootb.scheduler.agent.module.model.FileWatcherJobEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SchedulerFilterEntryConverter implements FilterEntryConverter<FileWatcherJobEvent> {

    /** logger */
    private static Logger logger = LoggerFactory.getLogger(SchedulerFilterEntryConverter.class);

    private String configuredResourceId;
    private int filterTimeToLive;

    public SchedulerFilterEntryConverter(String configuredResourceId, int filterTimeToLive) {
        this.configuredResourceId = configuredResourceId;
        if(this.configuredResourceId == null) {
            throw new IllegalArgumentException("configuredResourceId cannot be null!");
        }
        this.filterTimeToLive = filterTimeToLive;
    }

    @Override
    public FilterEntry convert(FileWatcherJobEvent fileWatcherJobEvent) throws FilterEntryConverterException {
        if(fileWatcherJobEvent.getCorrelatedFileList() == null || fileWatcherJobEvent.getCorrelatedFileList().getFileList() == null
            || fileWatcherJobEvent.getCorrelatedFileList().getFileList().isEmpty()) {
            throw new FilterEntryConverterException("Received a null or empty file list!");
        }

        if(fileWatcherJobEvent.getCorrelatedFileList().getFileList().size() > 1) {
            StringBuffer filenames = new StringBuffer();
            fileWatcherJobEvent.getCorrelatedFileList().getFileList().forEach(file -> filenames.append(file.getName()).append(" "));

            logger.info("Received multiple files {}. Expecting only one.", filenames.toString());
        }

        Integer criteria;

        criteria = (fileWatcherJobEvent.getJobName() + fileWatcherJobEvent.getCorrelatedFileList().getFileList().get(0).getName()
            + fileWatcherJobEvent.getCorrelationIdentifier()).hashCode();

        return new DefaultFilterEntry(criteria, configuredResourceId, filterTimeToLive);
    }
}
