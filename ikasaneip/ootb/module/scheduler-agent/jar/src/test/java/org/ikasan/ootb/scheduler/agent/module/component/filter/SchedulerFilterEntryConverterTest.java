package org.ikasan.ootb.scheduler.agent.module.component.filter;

import org.ikasan.component.endpoint.filesystem.messageprovider.CorrelatedFileList;
import org.ikasan.filter.duplicate.model.FilterEntry;
import org.ikasan.filter.duplicate.model.FilterEntryConverterException;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SchedulerFilterEntryConverterTest {

    @Test
    void test_exception_constructor_null_configuration_id() {
        assertThrows(IllegalArgumentException.class, () -> {
            new SchedulerFilterEntryConverter(null, 30);
        });
    }

    @Test
    void text_convert_exception_empty_file_list() {
        assertThrows(FilterEntryConverterException.class, () -> {
            SchedulerFilterEntryConverter converter = new SchedulerFilterEntryConverter("configurationId", 30);

            List<File> files = List.of();

            CorrelatedFileList correlatedFileList = new CorrelatedFileList(files, "correlationIdentifier");
            converter.convert(correlatedFileList);
        });
    }

    @Test
    void text_convert_success() {
        SchedulerFilterEntryConverter converter = new SchedulerFilterEntryConverter("configurationId", 30);

        List<File> files = List.of(new File("."));

        CorrelatedFileList correlatedFileList = new CorrelatedFileList(files, "correlationIdentifier");
        FilterEntry entry = converter.convert(correlatedFileList);

        assertEquals((Integer) new File(".").getName().hashCode(), entry.getCriteria());
        assertEquals("configurationId", entry.getClientId());
    }

    @Test
    void text_convert_success_multiple_files() {
        SchedulerFilterEntryConverter converter = new SchedulerFilterEntryConverter("configurationId", 30);

        List<File> files = List.of(new File("."), new File("."));

        CorrelatedFileList correlatedFileList = new CorrelatedFileList(files, "correlationIdentifier");
        FilterEntry entry = converter.convert(correlatedFileList);

        assertEquals((Integer) new File(".").getName().hashCode(), entry.getCriteria());
        assertEquals("configurationId", entry.getClientId());
    }
}
