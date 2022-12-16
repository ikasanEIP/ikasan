package org.ikasan.ootb.scheduler.agent.module.component.filter;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.RandomStringUtils;
import org.ikasan.job.orchestration.model.context.ContextInstanceImpl;
import org.ikasan.ootb.scheduler.agent.module.component.filter.configuration.ContextInstanceFilterConfiguration;
import org.ikasan.ootb.scheduler.agent.rest.cache.ContextInstanceCache;
import org.ikasan.spec.scheduled.dryrun.DryRunModeService;
import org.ikasan.spec.scheduled.instance.model.ContextInstance;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ContextInstanceFilterTest {

    @Mock
    private DryRunModeService dryRunModeService;
    private ContextInstanceFilterConfiguration configuration;
    @Before
    public void setup() {
        String contextInstanceId = RandomStringUtils.randomAlphabetic(12);
        ContextInstance instance = new ContextInstanceImpl();
        instance.setName("ContextInstanceName1");
        instance.setId(contextInstanceId);

        ContextInstanceCache.instance().put(instance.getId(), instance);

        configuration = new ContextInstanceFilterConfiguration();
        configuration.addContextInstanceId(contextInstanceId);
    }

    @Test
    public void should_return_files_if_context_in_cache() {
        when(this.dryRunModeService.getDryRunMode()).thenReturn(false);

        ContextInstanceFilter filter = new ContextInstanceFilter(dryRunModeService, true);

        filter.setConfiguration(configuration);

        List<File> files = List.of(new File("."));

        List<File> results = (List<File>) filter.filter(files);

        assertEquals(files, results);
    }

    @Test(expected = ContextInstanceFilterException.class)
    public void should_throw_exception_if_context_not_in_cache() {
        configuration.setContextInstanceIds(new ArrayList<>());
        when(this.dryRunModeService.getDryRunMode()).thenReturn(false);

        ContextInstanceFilter filter = new ContextInstanceFilter(dryRunModeService, true);

        filter.setConfiguration(configuration);

        List<File> files = List.of(new File("."));

        filter.filter(files);
    }

    @Test
    public void should_return_files_if_context_not_in_cache_not_active() {
        ContextInstanceFilter filter = new ContextInstanceFilter(dryRunModeService, false);

        filter.setConfiguration(configuration);

        List<File> files = List.of(new File("."));

        List<File> results = (List<File>) filter.filter(files);

        assertEquals(files, results);
    }
}