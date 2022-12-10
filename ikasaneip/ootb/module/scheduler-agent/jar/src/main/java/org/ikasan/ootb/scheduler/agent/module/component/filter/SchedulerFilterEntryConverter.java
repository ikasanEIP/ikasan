package org.ikasan.ootb.scheduler.agent.module.component.filter;

import org.ikasan.component.endpoint.filesystem.messageprovider.CorrelatedFileList;
import org.ikasan.filter.duplicate.model.DefaultFilterEntry;
import org.ikasan.filter.duplicate.model.FilterEntry;
import org.ikasan.filter.duplicate.model.FilterEntryConverter;
import org.ikasan.filter.duplicate.model.FilterEntryConverterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;

public class SchedulerFilterEntryConverter implements FilterEntryConverter<CorrelatedFileList> {

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
    public FilterEntry convert(CorrelatedFileList correlatedFileList) throws FilterEntryConverterException {
        if(correlatedFileList.getFileList() == null || correlatedFileList.getFileList().isEmpty()) {
            throw new FilterEntryConverterException("Received a null or empty file list!");
        }

        if(correlatedFileList.getFileList().size() > 1) {
            StringBuffer filenames = new StringBuffer();
            correlatedFileList.getFileList().forEach(file -> filenames.append(file.getName()).append(" "));

            logger.info("Received multiple files {}. Expecting only one.", filenames.toString());
        }

        Integer criteria = correlatedFileList.getFileList().get(0).getName().hashCode();
        return new DefaultFilterEntry(criteria, configuredResourceId, filterTimeToLive);
    }
}
