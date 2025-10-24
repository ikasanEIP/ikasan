package org.ikasan.ootb.scheduler.agent.module.component.filter;

import org.ikasan.component.endpoint.filesystem.messageprovider.CorrelatedFileList;
import org.ikasan.filter.duplicate.IsDuplicateFilterRule;
import org.ikasan.ootb.scheduler.agent.module.component.filter.configuration.SchedulerFileFilterConfiguration;
import org.ikasan.ootb.scheduler.agent.module.model.FileWatcherJobEvent;
import org.ikasan.spec.scheduled.dryrun.DryRunModeService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.File;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SchedulerFileFilterTest {

    @Mock
    private IsDuplicateFilterRule isDuplicateFilterRule;

    @Test(expected = IllegalArgumentException.class)
    public void test_exception_constructor_null_filter_rule() {
        new SchedulerFileFilter(null);
    }

    @Test
    public void test_filter_accept_success_not_dry_run() {
        when(isDuplicateFilterRule.accept(any(Object.class))).thenReturn(true);

        SchedulerFileFilter filter = new SchedulerFileFilter(isDuplicateFilterRule);
        SchedulerFileFilterConfiguration configuration = new SchedulerFileFilterConfiguration();
        configuration.setJobName("jobName");
        filter.setConfiguration(configuration);
        List<File> files = List.of(new File("."));

        CorrelatedFileList correlatedFileList = new CorrelatedFileList(files
            , "correlationIdentifier");

        FileWatcherJobEvent fileWatcherJobEvent = new FileWatcherJobEvent();
        fileWatcherJobEvent.setCorrelatedFileList(correlatedFileList);

        FileWatcherJobEvent results = filter.filter(fileWatcherJobEvent);

        Assert.assertNotNull(results);
    }

    @Test
    public void test_filter_filter_success_not_dry_run() {
        when(isDuplicateFilterRule.accept(any(Object.class))).thenReturn(false);

        SchedulerFileFilter filter = new SchedulerFileFilter(isDuplicateFilterRule);
        SchedulerFileFilterConfiguration configuration = new SchedulerFileFilterConfiguration();
        configuration.setJobName("jobName");
        filter.setConfiguration(configuration);
        List<File> files = List.of(new File("."));

        CorrelatedFileList correlatedFileList = new CorrelatedFileList(files
            , "correlationIdentifier");

        FileWatcherJobEvent fileWatcherJobEvent = new FileWatcherJobEvent();
        fileWatcherJobEvent.setCorrelatedFileList(correlatedFileList);

        FileWatcherJobEvent results = filter.filter(fileWatcherJobEvent);

        Assert.assertNull(results);
    }

    @Test
    public void test_filter_accept_success_dry_run() {
        SchedulerFileFilter filter = new SchedulerFileFilter(isDuplicateFilterRule);
        SchedulerFileFilterConfiguration configuration = new SchedulerFileFilterConfiguration();
        configuration.setJobName("jobName");
        filter.setConfiguration(configuration);
        List<File> files = List.of(new File("."));

        CorrelatedFileList correlatedFileList = new CorrelatedFileList(files
            , "correlationIdentifier");

        FileWatcherJobEvent fileWatcherJobEvent = new FileWatcherJobEvent();
        fileWatcherJobEvent.setCorrelatedFileList(correlatedFileList);
        fileWatcherJobEvent.setDryRun(true);

        FileWatcherJobEvent results = filter.filter(fileWatcherJobEvent);

        Assert.assertNotNull(results);
    }

    @Test
    public void test_filter_accept_success_job_dry_run() {
        SchedulerFileFilter filter = new SchedulerFileFilter(isDuplicateFilterRule);
        SchedulerFileFilterConfiguration configuration = new SchedulerFileFilterConfiguration();
        configuration.setJobName("jobName");
        filter.setConfiguration(configuration);
        List<File> files = List.of(new File("."));

        CorrelatedFileList correlatedFileList = new CorrelatedFileList(files
            , "correlationIdentifier");

        FileWatcherJobEvent fileWatcherJobEvent = new FileWatcherJobEvent();
        fileWatcherJobEvent.setCorrelatedFileList(correlatedFileList);
        fileWatcherJobEvent.setDryRun(true);

        FileWatcherJobEvent results = filter.filter(fileWatcherJobEvent);

        Assert.assertNotNull(results);
    }
}
