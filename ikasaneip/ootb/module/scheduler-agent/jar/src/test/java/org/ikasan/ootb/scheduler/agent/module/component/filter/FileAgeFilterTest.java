package org.ikasan.ootb.scheduler.agent.module.component.filter;

import org.ikasan.component.endpoint.filesystem.messageprovider.CorrelatedFileList;
import org.ikasan.ootb.scheduler.agent.module.component.filter.configuration.FileAgeFilterConfiguration;
import org.ikasan.spec.component.filter.FilterException;
import org.ikasan.spec.scheduled.dryrun.DryRunModeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FileAgeFilterTest {

    @Mock
    File file;

    @Mock
    private DryRunModeService dryRunModeService;

    @Test
    void test_exception_empty_file_list() {
        assertThrows(FilterException.class, () -> {
            FileAgeFilterConfiguration configuration = new FileAgeFilterConfiguration();
            configuration.setFileAgeSeconds(30);

            FileAgeFilter fileAgeFilter = new FileAgeFilter(this.dryRunModeService);
            fileAgeFilter.setConfiguration(configuration);

            CorrelatedFileList correlatedFileList = new CorrelatedFileList(List.of()
            , "correlationIdentifier");

            fileAgeFilter.filter(correlatedFileList);
        });
    }

    @Test
    void test_filter_accept_success() {
        when(this.file.lastModified()).thenReturn(System.currentTimeMillis() - 50000);
        when(this.dryRunModeService.getDryRunMode()).thenReturn(false);
        when(dryRunModeService.isJobDryRun(any(String.class))).thenReturn(false);
        FileAgeFilterConfiguration configuration = new FileAgeFilterConfiguration();
        configuration.setFileAgeSeconds(30);
        configuration.setJobName("jobName");

        FileAgeFilter fileAgeFilter = new FileAgeFilter(this.dryRunModeService);
        fileAgeFilter.setConfiguration(configuration);

        CorrelatedFileList correlatedFileList = new CorrelatedFileList(List.of(file)
            , "correlationIdentifier");

        assertNotNull(fileAgeFilter.filter(correlatedFileList));
    }

    @Test
    void test_filter_accept_success_multiple_files() {
        when(file.lastModified()).thenReturn(System.currentTimeMillis() - 50000);
        when(dryRunModeService.isJobDryRun(any(String.class))).thenReturn(false);
        FileAgeFilterConfiguration configuration = new FileAgeFilterConfiguration();
        configuration.setFileAgeSeconds(30);
        configuration.setJobName("jobName");

        FileAgeFilter fileAgeFilter = new FileAgeFilter(this.dryRunModeService);
        fileAgeFilter.setConfiguration(configuration);

        CorrelatedFileList correlatedFileList = new CorrelatedFileList(List.of(file, file)
            , "correlationIdentifier");

        assertNotNull(fileAgeFilter.filter(correlatedFileList));
    }

    @Test
    void test_filter_filter_success() {
        when(file.lastModified()).thenReturn(System.currentTimeMillis());
        when(this.dryRunModeService.getDryRunMode()).thenReturn(false);
        when(dryRunModeService.isJobDryRun(any(String.class))).thenReturn(false);
        FileAgeFilterConfiguration configuration = new FileAgeFilterConfiguration();
        configuration.setFileAgeSeconds(30);
        configuration.setJobName("jobName");

        FileAgeFilter fileAgeFilter = new FileAgeFilter(this.dryRunModeService);
        fileAgeFilter.setConfiguration(configuration);

        CorrelatedFileList correlatedFileList = new CorrelatedFileList(List.of(file)
            , "correlationIdentifier");

        assertNull(fileAgeFilter.filter(correlatedFileList));
    }

    @Test
    void test_filter_filter_success_multiple_files() {
        when(file.lastModified()).thenReturn(System.currentTimeMillis());
        when(this.dryRunModeService.getDryRunMode()).thenReturn(false);
        when(dryRunModeService.isJobDryRun(any(String.class))).thenReturn(false);
        FileAgeFilterConfiguration configuration = new FileAgeFilterConfiguration();
        configuration.setFileAgeSeconds(30);
        configuration.setJobName("jobName");

        FileAgeFilter fileAgeFilter = new FileAgeFilter(this.dryRunModeService);
        fileAgeFilter.setConfiguration(configuration);

        CorrelatedFileList correlatedFileList = new CorrelatedFileList(List.of(file, file)
            , "correlationIdentifier");

        assertNull(fileAgeFilter.filter(correlatedFileList));
    }

    @Test
    void test_filter_dry_run_success() {
        when(this.dryRunModeService.getDryRunMode()).thenReturn(true);
        FileAgeFilterConfiguration configuration = new FileAgeFilterConfiguration();
        configuration.setFileAgeSeconds(30);
        configuration.setJobName("jobName");

        FileAgeFilter fileAgeFilter = new FileAgeFilter(this.dryRunModeService);
        fileAgeFilter.setConfiguration(configuration);

        CorrelatedFileList correlatedFileList = new CorrelatedFileList(List.of(file)
            , "correlationIdentifier");

        assertNotNull(fileAgeFilter.filter(correlatedFileList));
    }

    @Test
    void test_filter_job_dry_run_success() {
        when(this.dryRunModeService.getDryRunMode()).thenReturn(false);
        when(dryRunModeService.isJobDryRun(any(String.class))).thenReturn(true);
        FileAgeFilterConfiguration configuration = new FileAgeFilterConfiguration();
        configuration.setFileAgeSeconds(30);
        configuration.setJobName("jobName");

        FileAgeFilter fileAgeFilter = new FileAgeFilter(this.dryRunModeService);
        fileAgeFilter.setConfiguration(configuration);

        CorrelatedFileList correlatedFileList = new CorrelatedFileList(List.of(file)
            , "correlationIdentifier");

        assertNotNull(fileAgeFilter.filter(correlatedFileList));
    }
}
