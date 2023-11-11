package org.ikasan.ootb.scheduler.agent.module.component.filter;

import static org.ikasan.component.endpoint.quartz.consumer.CorrelatingScheduledConsumer.EMPTY_CORRELATION_ID;
import static org.ikasan.ootb.scheduler.agent.module.component.filter.ContextInstanceFilter.CORRELATION_ID;
import static org.junit.jupiter.api.Assertions.*;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.impl.JobExecutionContextImpl;

@ExtendWith(MockitoExtension.class)
class ContextInstanceFilterTest {

    @Mock
    private DryRunModeService dryRunModeService;
    /** Mock jobExecutionContext **/
    @Mock
    private  JobExecutionContextImpl jobExecutionContextImpl;
    @Mock
    private  JobDataMap jobDataMap;

    @Mock
    private JobDetail jobDetail;

    private ContextInstanceFilterConfiguration configuration;
    private static final String contextInstanceId = RandomStringUtils.randomAlphabetic(12);

    @BeforeEach
    void setup() {
        ContextInstance instance = new ContextInstanceImpl();
        instance.setName("ContextInstanceName1");
        instance.setId(contextInstanceId);
        ContextInstanceCache.instance().put(instance.getId(), instance);

        configuration = new ContextInstanceFilterConfiguration();
    }

    @Test
    void should_return_files_if_context_in_cache() {
        when(this.dryRunModeService.getDryRunMode()).thenReturn(false);

        ContextInstanceFilter filter = new ContextInstanceFilter(dryRunModeService, true);

        filter.setConfiguration(configuration);

        List<File> files = List.of(new File("."));

        List<File> results = (List<File>) filter.filter(files);

        assertEquals(files, results);
    }

    @Test
    void should_throw_exception_if_context_not_in_cache() {
        assertThrows(ContextInstanceFilterException.class, () -> {
            when(this.dryRunModeService.getDryRunMode()).thenReturn(false);
            when(jobExecutionContextImpl.getMergedJobDataMap()).thenReturn(jobDataMap);
            when(jobDataMap.get(CORRELATION_ID)).thenReturn("someValueNotCorrelationId");

            ContextInstanceFilter filter = new ContextInstanceFilter(dryRunModeService, true);
            filter.filter(jobExecutionContextImpl);
        });
    }

    @Test
    void should_pass_though_event_if_correlationId_in_cache() {
        when(this.dryRunModeService.getDryRunMode()).thenReturn(false);
        when(jobExecutionContextImpl.getMergedJobDataMap()).thenReturn(jobDataMap);
        when(jobDataMap.get(CORRELATION_ID)).thenReturn(contextInstanceId);

        ContextInstanceFilter filter = new ContextInstanceFilter(dryRunModeService, true);
        assertNotNull(filter.filter(jobExecutionContextImpl));
    }

    @Test
    void should_filter_event_if_correlationId_null() {
        when(this.dryRunModeService.getDryRunMode()).thenReturn(false);
        when(jobExecutionContextImpl.getMergedJobDataMap()).thenReturn(jobDataMap);
        when(jobDataMap.get(CORRELATION_ID)).thenReturn(null);
        when(jobExecutionContextImpl.getJobDetail()).thenReturn(jobDetail);

        ContextInstanceFilter filter = new ContextInstanceFilter(dryRunModeService, true);
        assertNull(filter.filter(jobExecutionContextImpl));
    }

    @Test
    void should_filter_event_if_correlationId_empty_string() {
        when(this.dryRunModeService.getDryRunMode()).thenReturn(false);
        when(jobExecutionContextImpl.getMergedJobDataMap()).thenReturn(jobDataMap);
        when(jobDataMap.get(CORRELATION_ID)).thenReturn("");
        when(jobExecutionContextImpl.getJobDetail()).thenReturn(jobDetail);

        ContextInstanceFilter filter = new ContextInstanceFilter(dryRunModeService, true);
        assertNull(filter.filter(jobExecutionContextImpl));
    }

    @Test
    void should_filter_event_if_correlationId_empty_correlation_id_marker() {
        when(this.dryRunModeService.getDryRunMode()).thenReturn(false);
        when(jobExecutionContextImpl.getMergedJobDataMap()).thenReturn(jobDataMap);
        when(jobDataMap.get(CORRELATION_ID)).thenReturn(EMPTY_CORRELATION_ID);
        when(jobExecutionContextImpl.getJobDetail()).thenReturn(jobDetail);

        ContextInstanceFilter filter = new ContextInstanceFilter(dryRunModeService, true);
        assertNull(filter.filter(jobExecutionContextImpl));
    }


    @Test
    void should_return_files_if_context_not_in_cache_not_active() {
        ContextInstanceFilter filter = new ContextInstanceFilter(dryRunModeService, false);

        filter.setConfiguration(configuration);

        List<File> files = List.of(new File("."));

        List<File> results = (List<File>) filter.filter(files);

        assertEquals(files, results);
    }
}