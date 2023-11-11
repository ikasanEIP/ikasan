package org.ikasan.ootb.scheduler.agent.module.component.filter;

import org.ikasan.component.endpoint.filesystem.messageprovider.CorrelatedFileList;
import org.ikasan.filter.duplicate.IsDuplicateFilterRule;
import org.ikasan.ootb.scheduler.agent.module.component.filter.configuration.SchedulerFileFilterConfiguration;
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
class SchedulerFileFilterTest {

    @Mock
    private IsDuplicateFilterRule isDuplicateFilterRule;

    @Mock
    private DryRunModeService dryRunModeService;

    @Test
    void test_exception_constructor_null_filter_rule() {
        assertThrows(IllegalArgumentException.class, () -> {
            new SchedulerFileFilter(null, dryRunModeService);
        });
    }

    @Test
    void test_exception_constructor_null_dry_run_mode_service() {
        assertThrows(IllegalArgumentException.class, () -> {
            new SchedulerFileFilter(isDuplicateFilterRule, null);
        });
    }

    @Test
    void test_filter_accept_success_not_dry_run() {
        when(dryRunModeService.getDryRunMode()).thenReturn(false);
        when(dryRunModeService.isJobDryRun(any(String.class))).thenReturn(false);
        when(isDuplicateFilterRule.accept(any(Object.class))).thenReturn(true);

        SchedulerFileFilter filter = new SchedulerFileFilter(isDuplicateFilterRule, dryRunModeService);
        SchedulerFileFilterConfiguration configuration = new SchedulerFileFilterConfiguration();
        configuration.setJobName("jobName");
        filter.setConfiguration(configuration);
        List<File> files = List.of(new File("."));

        CorrelatedFileList correlatedFileList = new CorrelatedFileList(files
            , "correlationIdentifier");

        CorrelatedFileList results = filter.filter(correlatedFileList);

        assertNotNull(results);
    }

    @Test
    void test_filter_filter_success_not_dry_run() {
        when(dryRunModeService.getDryRunMode()).thenReturn(false);
        when(dryRunModeService.isJobDryRun(any(String.class))).thenReturn(false);
        when(isDuplicateFilterRule.accept(any(Object.class))).thenReturn(false);

        SchedulerFileFilter filter = new SchedulerFileFilter(isDuplicateFilterRule, dryRunModeService);
        SchedulerFileFilterConfiguration configuration = new SchedulerFileFilterConfiguration();
        configuration.setJobName("jobName");
        filter.setConfiguration(configuration);
        List<File> files = List.of(new File("."));

        CorrelatedFileList correlatedFileList = new CorrelatedFileList(files
            , "correlationIdentifier");

        CorrelatedFileList results = filter.filter(correlatedFileList);

        assertNull(results);
    }

    @Test
    void test_filter_accept_success_dry_run() {
        when(dryRunModeService.getDryRunMode()).thenReturn(true);

        SchedulerFileFilter filter = new SchedulerFileFilter(isDuplicateFilterRule, dryRunModeService);
        SchedulerFileFilterConfiguration configuration = new SchedulerFileFilterConfiguration();
        configuration.setJobName("jobName");
        filter.setConfiguration(configuration);
        List<File> files = List.of(new File("."));

        CorrelatedFileList correlatedFileList = new CorrelatedFileList(files
            , "correlationIdentifier");

        CorrelatedFileList results = filter.filter(correlatedFileList);

        assertNotNull(results);
    }

    @Test
    void test_filter_accept_success_job_dry_run() {
        when(dryRunModeService.getDryRunMode()).thenReturn(false);
        when(dryRunModeService.isJobDryRun(any(String.class))).thenReturn(true);

        SchedulerFileFilter filter = new SchedulerFileFilter(isDuplicateFilterRule, dryRunModeService);
        SchedulerFileFilterConfiguration configuration = new SchedulerFileFilterConfiguration();
        configuration.setJobName("jobName");
        filter.setConfiguration(configuration);
        List<File> files = List.of(new File("."));

        CorrelatedFileList correlatedFileList = new CorrelatedFileList(files
            , "correlationIdentifier");

        CorrelatedFileList results = filter.filter(correlatedFileList);

        assertNotNull(results);
    }
}
