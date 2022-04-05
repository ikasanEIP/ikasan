package org.ikasan.ootb.scheduler.agent.module.component.filter;

import org.ikasan.filter.duplicate.IsDuplicateFilterRule;
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

    @Mock
    private DryRunModeService dryRunModeService;

    @Test(expected = IllegalArgumentException.class)
    public void test_exception_constructor_null_filter_rule() {
        new SchedulerFileFilter(null, dryRunModeService);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_exception_constructor_null_dry_run_mode_service() {
        new SchedulerFileFilter(isDuplicateFilterRule, null);
    }

    @Test
    public void test_filter_accept_success_not_dry_run() {
        when(dryRunModeService.getDryRunMode()).thenReturn(false);
        when(isDuplicateFilterRule.accept(any(Object.class))).thenReturn(true);

        SchedulerFileFilter filter = new SchedulerFileFilter(isDuplicateFilterRule, dryRunModeService);
        List<File> files = List.of(new File("."));

        List<File> results = filter.filter(files);

        Assert.assertNotNull(results);
    }

    @Test
    public void test_filter_filter_success_not_dry_run() {
        when(dryRunModeService.getDryRunMode()).thenReturn(false);
        when(isDuplicateFilterRule.accept(any(Object.class))).thenReturn(false);

        SchedulerFileFilter filter = new SchedulerFileFilter(isDuplicateFilterRule, dryRunModeService);
        List<File> files = List.of(new File("."));

        List<File> results = filter.filter(files);

        Assert.assertNull(results);
    }

    @Test
    public void test_filter_accept_success_dry_run() {
        when(dryRunModeService.getDryRunMode()).thenReturn(true);

        SchedulerFileFilter filter = new SchedulerFileFilter(isDuplicateFilterRule, dryRunModeService);
        List<File> files = List.of(new File("."));

        List<File> results = filter.filter(files);

        Assert.assertNotNull(results);
    }
}
