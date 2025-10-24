package org.ikasan.ootb.scheduler.agent.module.component.filter;

import org.ikasan.component.endpoint.filesystem.messageprovider.CorrelatedFileList;
import org.ikasan.ootb.scheduler.agent.module.component.filter.configuration.FileAgeFilterConfiguration;
import org.ikasan.ootb.scheduler.agent.module.model.FileWatcherJobEvent;
import org.ikasan.spec.component.filter.FilterException;
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
public class FileAgeFilterTest {

    @Mock
    File file;

    @Test(expected = FilterException.class)
    public void test_exception_empty_file_list() {
        FileAgeFilter fileAgeFilter = new FileAgeFilter();

        CorrelatedFileList correlatedFileList = new CorrelatedFileList(List.of()
            , "correlationIdentifier");

        FileWatcherJobEvent event = new FileWatcherJobEvent();
        event.setCorrelatedFileList(correlatedFileList);
        event.setJobName("jobName");
        event.setMinFileAgeSeconds(30);
        fileAgeFilter.filter(event);
    }

    @Test
    public void test_filter_accept_success() {
        when(this.file.lastModified()).thenReturn(System.currentTimeMillis() - 50000);

        FileAgeFilter fileAgeFilter = new FileAgeFilter();

        CorrelatedFileList correlatedFileList = new CorrelatedFileList(List.of(file)
            , "correlationIdentifier");

        FileWatcherJobEvent event = new FileWatcherJobEvent();
        event.setCorrelatedFileList(correlatedFileList);
        event.setJobName("jobName");
        event.setMinFileAgeSeconds(30);

        Assert.assertNotNull(fileAgeFilter.filter(event));
    }

    @Test
    public void test_filter_accept_success_multiple_files() {
        when(file.lastModified()).thenReturn(System.currentTimeMillis() - 50000);
        FileAgeFilter fileAgeFilter = new FileAgeFilter();

        CorrelatedFileList correlatedFileList = new CorrelatedFileList(List.of(file, file)
            , "correlationIdentifier");

        FileWatcherJobEvent event = new FileWatcherJobEvent();
        event.setCorrelatedFileList(correlatedFileList);
        event.setJobName("jobName");
        event.setMinFileAgeSeconds(30);

        Assert.assertNotNull(fileAgeFilter.filter(event));
    }

    @Test
    public void test_filter_filter_success() {
        when(file.lastModified()).thenReturn(System.currentTimeMillis());
        FileAgeFilter fileAgeFilter = new FileAgeFilter();

        CorrelatedFileList correlatedFileList = new CorrelatedFileList(List.of(file)
            , "correlationIdentifier");

        FileWatcherJobEvent event = new FileWatcherJobEvent();
        event.setCorrelatedFileList(correlatedFileList);
        event.setJobName("jobName");
        event.setMinFileAgeSeconds(30);

        Assert.assertNull(fileAgeFilter.filter(event));
    }

    @Test
    public void test_filter_filter_success_multiple_files() {
        when(file.lastModified()).thenReturn(System.currentTimeMillis());
        FileAgeFilter fileAgeFilter = new FileAgeFilter();

        CorrelatedFileList correlatedFileList = new CorrelatedFileList(List.of(file, file)
            , "correlationIdentifier");

        FileWatcherJobEvent event = new FileWatcherJobEvent();
        event.setCorrelatedFileList(correlatedFileList);
        event.setJobName("jobName");
        event.setMinFileAgeSeconds(30);

        Assert.assertNull(fileAgeFilter.filter(event));
    }

    @Test
    public void test_filter_dry_run_success() {
        FileAgeFilter fileAgeFilter = new FileAgeFilter();

        CorrelatedFileList correlatedFileList = new CorrelatedFileList(List.of(file)
            , "correlationIdentifier");

        FileWatcherJobEvent event = new FileWatcherJobEvent();
        event.setCorrelatedFileList(correlatedFileList);
        event.setJobName("jobName");
        event.setMinFileAgeSeconds(30);
        event.setDryRun(true);

        Assert.assertNotNull(fileAgeFilter.filter(event));
    }

    @Test
    public void test_filter_job_dry_run_success() {
        FileAgeFilter fileAgeFilter = new FileAgeFilter();

        CorrelatedFileList correlatedFileList = new CorrelatedFileList(List.of(file)
            , "correlationIdentifier");

        FileWatcherJobEvent event = new FileWatcherJobEvent();
        event.setCorrelatedFileList(correlatedFileList);
        event.setJobName("jobName");
        event.setMinFileAgeSeconds(30);
        event.setDryRun(true);

        Assert.assertNotNull(fileAgeFilter.filter(event));
    }
}
