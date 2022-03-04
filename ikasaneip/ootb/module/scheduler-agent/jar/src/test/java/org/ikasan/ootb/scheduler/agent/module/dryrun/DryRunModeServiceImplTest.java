package org.ikasan.ootb.scheduler.agent.module.dryrun;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Map;

import org.ikasan.module.ConfiguredModuleImpl;
import org.ikasan.ootb.scheduler.agent.module.configuration.SchedulerAgentConfiguredModuleConfiguration;
import org.ikasan.ootb.scheduler.agent.rest.dto.DryRunFileListJobParameterDto;
import org.ikasan.spec.configuration.ConfigurationService;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.ikasan.spec.module.ModuleService;
import org.ikasan.spec.scheduled.dryrun.DryRunFileListJobParameter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

@RunWith(MockitoJUnitRunner.class)
public class DryRunModeServiceImplTest {

    @Mock
    private ModuleService moduleService;

    @Mock
    private ConfiguredModuleImpl module;

    @Mock
    private SchedulerAgentConfiguredModuleConfiguration configureModule;

    @Mock
    private ConfigurationService configurationService;

    @InjectMocks
    private DryRunModeServiceImpl service;

    @Before
    public void setUp() {
        ReflectionTestUtils.setField(service, "maxMapSize", 1);
        ReflectionTestUtils.setField(service, "expirationInMillis", 250);
        ReflectionTestUtils.setField(service, "moduleName", "scheduler-agent");
        service.init();
    }

    @Test
    public void shouldExpireEntriesInMap() throws InterruptedException {
        DryRunFileListJobParameter job = createFileListJob("Job Name 1", "/some/bogus/file/bogus.txt");

        service.addDryRunFileList(List.of(job));
        Map<String, String> jobNameFileMap = (Map<String, String>) ReflectionTestUtils.getField(service, "jobNameFileMap");

        assertEquals(1, jobNameFileMap.size());
        assertEquals("/some/bogus/file/bogus.txt", jobNameFileMap.get("Job Name 1"));

        Thread.sleep(300);

        assertEquals(0, jobNameFileMap.size());
    }

    @Test
    public void shouldNotAllowMoreThanMaxEntries() throws InterruptedException {
        DryRunFileListJobParameter job1 = createFileListJob("Job Name 1", "/some/bogus/file/bogus1.txt");
        DryRunFileListJobParameter job2 = createFileListJob("Job Name 2", "/some/bogus/file/bogus2.txt");

        service.addDryRunFileList(List.of(job1, job2));
        Map<String, String> jobNameFileMap = (Map<String, String>) ReflectionTestUtils.getField(service, "jobNameFileMap");

        assertEquals(jobNameFileMap.size(), 1);
        assertEquals("/some/bogus/file/bogus2.txt", jobNameFileMap.get("Job Name 2"));

        Thread.sleep(300);

        assertEquals(0, jobNameFileMap.size());

        //allows more entries
        service.addDryRunFileList(List.of(job1));
        assertEquals(1, jobNameFileMap.size());
        assertEquals("/some/bogus/file/bogus1.txt", jobNameFileMap.get("Job Name 1"));
    }

    @Test
    public void shouldEmptyMapWhenSetDryRunToFalse() {
        when(moduleService.getModule("scheduler-agent")).thenReturn(module);
        when(module.getConfiguration()).thenReturn(configureModule);

        DryRunFileListJobParameter job1 = createFileListJob("Job Name 1", "/some/bogus/file/bogus1.txt");
        service.addDryRunFileList(List.of(job1));

        Map<String, String> jobNameFileMap = (Map<String, String>) ReflectionTestUtils.getField(service, "jobNameFileMap");
        assertEquals(1, jobNameFileMap.size());
        assertEquals("/some/bogus/file/bogus1.txt", jobNameFileMap.get("Job Name 1"));

        service.setDryRunMode(false);

        assertEquals(0, jobNameFileMap.size());

        verify(moduleService).getModule("scheduler-agent");
        verify(module).getConfiguration();
        verify(configureModule).setDryRunMode(false);
        verify(configurationService).update(any(ConfiguredResource.class));
    }

    @Test
    public void shouldNotEmptyMapWhenSetDryRunToTrue() {
        when(moduleService.getModule("scheduler-agent")).thenReturn(module);
        when(module.getConfiguration()).thenReturn(configureModule);

        DryRunFileListJobParameter job1 = createFileListJob("Job Name 1", "/some/bogus/file/bogus1.txt");
        service.addDryRunFileList(List.of(job1));

        Map<String, String> jobNameFileMap = (Map<String, String>) ReflectionTestUtils.getField(service, "jobNameFileMap");
        assertEquals(1, jobNameFileMap.size());
        assertEquals("/some/bogus/file/bogus1.txt", jobNameFileMap.get("Job Name 1"));

        service.setDryRunMode(true);

        assertEquals(1, jobNameFileMap.size());
        assertEquals("/some/bogus/file/bogus1.txt", jobNameFileMap.get("Job Name 1"));
    }

    @Test
    public void shouldSetAndSaveDryRunMode() throws Exception {
        when(moduleService.getModule("scheduler-agent")).thenReturn(module);
        when(module.getConfiguration()).thenReturn(configureModule);
        doNothing().when(configureModule).setDryRunMode(true);

        service.setDryRunMode(true);

        verify(moduleService).getModule("scheduler-agent");
        verify(module).getConfiguration();
        verify(configureModule).setDryRunMode(true);
        verify(configurationService).update(any(ConfiguredResource.class));
    }

    @Test
    public void shouldReturnDryRunMode() {
        when(moduleService.getModule("scheduler-agent")).thenReturn(module);
        when(module.getConfiguration()).thenReturn(configureModule);
        when(configureModule.isDryRunMode()).thenReturn(true);

        assertTrue(service.getDryRunMode());
    }

    @Test
    public void shouldReturnFileNameIfJobNameInMapAndRemoveFromMap() {
        DryRunFileListJobParameter job1 = createFileListJob("Job Name 1", "/some/bogus/file/bogus1.txt");

        service.addDryRunFileList(List.of(job1));

        assertEquals("/some/bogus/file/bogus1.txt", service.getJobFileName("Job Name 1"));

        Map<String, String> jobNameFileMap = (Map<String, String>) ReflectionTestUtils.getField(service, "jobNameFileMap");

        assertEquals(0, jobNameFileMap.size());

        assertNull(service.getJobFileName("Job Name 1"));
    }

    @Test
    public void shouldReturnNullIfJobNameNotInMap() {
        assertNull(service.getJobFileName("any"));
    }

    private DryRunFileListJobParameterDto createFileListJob(String jobName, String fileName) {
        DryRunFileListJobParameterDto dto = new DryRunFileListJobParameterDto();
        dto.setJobName(jobName);
        dto.setFileName(fileName);
        return dto;
    }
}