package org.ikasan.ootb.scheduler.agent.module.component.filter;

import org.ikasan.filter.duplicate.model.DefaultFilterEntry;
import org.ikasan.filter.duplicate.model.FilterEntry;
import org.ikasan.filter.duplicate.model.FilterEntryConverter;
import org.ikasan.filter.duplicate.model.FilterEntryConverterException;

import java.io.File;
import java.util.List;

public class SchedulerFilterEntryConverter implements FilterEntryConverter<List<File>> {

    private String configuredResourceId;
    private int filterTimeToLive;

    public SchedulerFilterEntryConverter(String configuredResourceId, int filterTimeToLive) {
        this.configuredResourceId = configuredResourceId;
        this.filterTimeToLive = filterTimeToLive;
    }

    @Override
    public FilterEntry convert(List<File> message) throws FilterEntryConverterException {
        Integer criteria = message.get(0).getName().hashCode();
        return new DefaultFilterEntry(criteria, configuredResourceId, filterTimeToLive);
    }
}
