package org.ikasan.ootb.scheduler.agent.module.component.filter;

import org.ikasan.component.endpoint.filesystem.messageprovider.CorrelatedFileList;
import org.ikasan.filter.duplicate.model.FilterEntry;
import org.ikasan.filter.duplicate.model.FilterEntryConverterException;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.util.List;

public class SchedulerFilterEntryConverterTest {

    @Test(expected = IllegalArgumentException.class)
    public void test_exception_constructor_null_configuration_id() {
        new SchedulerFilterEntryConverter(null, 30);
    }

    @Test(expected = FilterEntryConverterException.class)
    public void text_convert_exception_empty_file_list() {
        SchedulerFilterEntryConverter converter = new SchedulerFilterEntryConverter("configurationId", 30);

        List<File> files = List.of();

        CorrelatedFileList correlatedFileList = new CorrelatedFileList(files, "correlationIdentifier");
        converter.convert(correlatedFileList);
    }

    @Test
    public void text_convert_success() {
        SchedulerFilterEntryConverter converter = new SchedulerFilterEntryConverter("configurationId", 30);

        List<File> files = List.of(new File("."));

        CorrelatedFileList correlatedFileList = new CorrelatedFileList(files, "correlationIdentifier");
        FilterEntry entry = converter.convert(correlatedFileList);

        Assert.assertEquals((Integer) new File(".correlationIdentifier").getName().hashCode(), entry.getCriteria());
        Assert.assertEquals("configurationId", entry.getClientId());
    }

    @Test
    public void text_convert_success_multiple_files() {
        SchedulerFilterEntryConverter converter = new SchedulerFilterEntryConverter("configurationId", 30);

        List<File> files = List.of(new File("."), new File("."));

        CorrelatedFileList correlatedFileList = new CorrelatedFileList(files, "correlationIdentifier");
        FilterEntry entry = converter.convert(correlatedFileList);

        Assert.assertEquals((Integer) new File(".correlationIdentifier").getName().hashCode(), entry.getCriteria());
        Assert.assertEquals("configurationId", entry.getClientId());
    }
}
