package org.ikasan.ootb.scheduler.agent.module.component.filter;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.List;

import org.apache.commons.lang3.RandomStringUtils;
import org.ikasan.job.orchestration.model.context.ContextInstanceImpl;
import org.ikasan.ootb.scheduler.agent.module.component.filter.configuration.ContextInstanceFilterConfiguration;
import org.ikasan.ootb.scheduler.agent.rest.cache.ContextInstanceCache;
import org.ikasan.spec.scheduled.dryrun.DryRunModeService;
import org.ikasan.spec.scheduled.instance.model.ContextInstance;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ContextInstanceFilterTest {

    @Mock
    private DryRunModeService dryRunModeService;

    @Test
    public void should_return_files_if_context_in_cache() {
        String contextName = RandomStringUtils.randomAlphabetic(12);
        ContextInstance instance = new ContextInstanceImpl();
        instance.setName(contextName);

        ContextInstanceCache.instance().put(instance);

        ContextInstanceFilterConfiguration configuration = new ContextInstanceFilterConfiguration();
        configuration.setContextName(contextName);

        when(this.dryRunModeService.getDryRunMode()).thenReturn(false);

        ContextInstanceFilter filter = new ContextInstanceFilter(dryRunModeService);

        filter.setConfiguration(configuration);

        List<File> files = List.of(new File("."));

        List<File> results = (List<File>) filter.filter(files);

        assertEquals(files, results);
    }

    @Test(expected = ContextInstanceFilterException.class)
    public void should_throw_exception_if_context_not_in_cache() {
        String contextName = RandomStringUtils.randomAlphabetic(12);

        ContextInstanceFilterConfiguration configuration = new ContextInstanceFilterConfiguration();
        configuration.setContextName(contextName);

        when(this.dryRunModeService.getDryRunMode()).thenReturn(false);

        ContextInstanceFilter filter = new ContextInstanceFilter(dryRunModeService);

        filter.setConfiguration(configuration);

        List<File> files = List.of(new File("."));

        filter.filter(files);
    }

}