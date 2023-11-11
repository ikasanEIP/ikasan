package org.ikasan.ootb.scheduler.agent.module.component.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.ikasan.ootb.scheduler.agent.module.component.converter.configuration.JobInitiationToContextualisedScheduledProcessEventConverterConfiguration;
import org.ikasan.ootb.scheduler.agent.rest.dto.ContextParameterInstanceDto;
import org.ikasan.ootb.scheduler.agent.rest.dto.DryRunParametersDto;
import org.ikasan.ootb.scheduler.agent.rest.dto.InternalEventDrivenJobInstanceDto;
import org.ikasan.ootb.scheduler.agent.rest.dto.SchedulerJobInitiationEventDto;
import org.ikasan.spec.component.transformation.TransformationException;
import org.ikasan.spec.scheduled.event.model.ContextualisedScheduledProcessEvent;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JobInitiationToContextualisedScheduledProcessEventConverterTest {

    @Test
    void test_exception_null_module_name_constructor() {
        assertThrows(IllegalArgumentException.class, () -> {
            new JobInitiationToContextualisedScheduledProcessEventConverter(null, "logFolder", "logParen");
        });
    }

    @Test
    void test_exception_null_log_folder_constructor() {
        assertThrows(IllegalArgumentException.class, () -> {
            new JobInitiationToContextualisedScheduledProcessEventConverter("moduleName", null, "logParen");
        });
    }

    @Test
    void test_exception_null_log_paren_constructor() {
        assertThrows(IllegalArgumentException.class, () -> {
            new JobInitiationToContextualisedScheduledProcessEventConverter("moduleName", "logFolder", null);
        });
    }

    @Test
    void test_convert_success() throws JsonProcessingException {
        SchedulerJobInitiationEventDto schedulerJobInitiationEventDto = new SchedulerJobInitiationEventDto();
        schedulerJobInitiationEventDto.setSkipped(true);
        schedulerJobInitiationEventDto.setDryRunParameters(new DryRunParametersDto());
        schedulerJobInitiationEventDto.setDryRun(true);
        schedulerJobInitiationEventDto.setAgentName("agentName");
        schedulerJobInitiationEventDto.setAgentUrl("agentUrl");
        schedulerJobInitiationEventDto.setChildContextNames(List.of("childContextId1", "childContextId2"));
        schedulerJobInitiationEventDto.setContextName("contextId");
        schedulerJobInitiationEventDto.setContextInstanceId("contextInstanceId");
        schedulerJobInitiationEventDto.setJobName("jobName");

        InternalEventDrivenJobInstanceDto internalEventDrivenJob = new InternalEventDrivenJobInstanceDto();
        internalEventDrivenJob.setAgentName("agentName");
        internalEventDrivenJob.setContextParameters(List.of(new ContextParameterInstanceDto(), new ContextParameterInstanceDto()));

        schedulerJobInitiationEventDto.setInternalEventDrivenJob(internalEventDrivenJob);

        ObjectMapper objectMapper = new ObjectMapper();

        JobInitiationToContextualisedScheduledProcessEventConverter converter
            = new JobInitiationToContextualisedScheduledProcessEventConverter("agentName", "logFolder", "/");
        JobInitiationToContextualisedScheduledProcessEventConverterConfiguration configuration
            = new JobInitiationToContextualisedScheduledProcessEventConverterConfiguration();
        converter.setConfiguration(configuration);


        ContextualisedScheduledProcessEvent result = converter.convert(objectMapper.writeValueAsString(schedulerJobInitiationEventDto));

        long currentTestTimeMillis = System.currentTimeMillis();

        //make sure the millis is in the last 500ms (If the test is taking more than this something is wrong)
        assertTrue(isWithinTheLast500Millis(result.getFireTime(), currentTestTimeMillis));

        assertEquals("agentName", result.getAgentName());
        assertEquals("jobName", result.getJobName());
        assertEquals("contextId", result.getContextName());
        assertEquals("contextInstanceId", result.getContextInstanceId());
        assertEquals(2, result.getChildContextNames().size());
        assertTrue(result.isJobStarting());
        assertFalse(result.isSuccessful());
        assertTrue(result.isSkipped());
        assertTrue(result.isDryRun());

        // file name is "logFolder/contextId-contextInstanceId-agentName-jobName-System.currentTimeMillis()-err.log"
        assertTrue(result.getResultError().startsWith("logFolder/contextId-contextInstanceId-agentName-jobName-"));
        assertTrue(result.getResultError().endsWith("-err.log"));
        String millis = result.getResultError().substring("logFolder/contextId-contextInstanceId-agentName-jobName-".length(), result.getResultError().length() - "-err.log".length());
        Long millisFromFileName = Long.valueOf(millis);
        //make sure the millis from the filename is in the last 500ms (If the test is taking more than this something is wrong)
        assertTrue(isWithinTheLast500Millis(millisFromFileName, currentTestTimeMillis));

        // file name is "logFolder/contextId-contextInstanceId-agentName-jobName-System.currentTimeMillis()-out.log"
        assertTrue(result.getResultOutput().startsWith("logFolder/contextId-contextInstanceId-agentName-jobName-"));
        assertTrue(result.getResultOutput().endsWith("-out.log"));
        millis = result.getResultOutput().substring("logFolder/contextId-contextInstanceId-agentName-jobName-".length(), result.getResultError().length() - "-err.log".length());
        millisFromFileName = Long.valueOf(millis);
        //make sure the millis from the filename is in the last 500ms (If the test is taking more than this something is wrong)
        assertTrue(isWithinTheLast500Millis(millisFromFileName, currentTestTimeMillis));

        assertEquals(2, result.getInternalEventDrivenJob().getContextParameters().size());
    }

    @Test
    void test_convert_success_with_hashed_logs() throws JsonProcessingException {
        SchedulerJobInitiationEventDto schedulerJobInitiationEventDto = new SchedulerJobInitiationEventDto();
        schedulerJobInitiationEventDto.setSkipped(true);
        schedulerJobInitiationEventDto.setDryRunParameters(new DryRunParametersDto());
        schedulerJobInitiationEventDto.setDryRun(true);
        schedulerJobInitiationEventDto.setAgentName("agentName");
        schedulerJobInitiationEventDto.setAgentUrl("agentUrl");
        schedulerJobInitiationEventDto.setChildContextNames(List.of("childContextId1", "childContextId2"));
        schedulerJobInitiationEventDto.setContextName("contextId");
        schedulerJobInitiationEventDto.setContextInstanceId("contextInstanceId");
        schedulerJobInitiationEventDto.setJobName("jobName");

        InternalEventDrivenJobInstanceDto internalEventDrivenJob = new InternalEventDrivenJobInstanceDto();
        internalEventDrivenJob.setAgentName("agentName");
        internalEventDrivenJob.setContextParameters(List.of(new ContextParameterInstanceDto(), new ContextParameterInstanceDto()));

        schedulerJobInitiationEventDto.setInternalEventDrivenJob(internalEventDrivenJob);

        ObjectMapper objectMapper = new ObjectMapper();

        JobInitiationToContextualisedScheduledProcessEventConverter converter
            = new JobInitiationToContextualisedScheduledProcessEventConverter("agentName", "logFolder", "/");
        JobInitiationToContextualisedScheduledProcessEventConverterConfiguration configuration
            = new JobInitiationToContextualisedScheduledProcessEventConverterConfiguration();
        configuration.setHashProcessLogFilenames(true);
        converter.setConfiguration(configuration);


        ContextualisedScheduledProcessEvent result = converter.convert(objectMapper.writeValueAsString(schedulerJobInitiationEventDto));

        long currentTestTimeMillis = System.currentTimeMillis();

        //make sure the millis is in the last 500ms (If the test is taking more than this something is wrong)
        assertTrue(isWithinTheLast500Millis(result.getFireTime(), currentTestTimeMillis));

        assertEquals("agentName", result.getAgentName());
        assertEquals("jobName", result.getJobName());
        assertEquals("contextId", result.getContextName());
        assertEquals("contextInstanceId", result.getContextInstanceId());
        assertEquals(2, result.getChildContextNames().size());
        assertTrue(result.isJobStarting());
        assertFalse(result.isSuccessful());
        assertTrue(result.isSkipped());
        assertTrue(result.isDryRun());

        // file name is "logFolder/contextId-contextInstanceId-agentName-jobName-System.currentTimeMillis()-err.log"
        assertFalse(result.getResultError().startsWith("logFolder/contextId-contextInstanceId-agentName-jobName-"));
        assertTrue(result.getResultError().endsWith("-err.log"));

        // file name is "logFolder/contextId-contextInstanceId-agentName-jobName-System.currentTimeMillis()-out.log"
        assertFalse(result.getResultOutput().startsWith("logFolder/contextId-contextInstanceId-agentName-jobName-"));
        assertTrue(result.getResultOutput().endsWith("-out.log"));

        assertEquals(2, result.getInternalEventDrivenJob().getContextParameters().size());
    }

    @Test
    void test_convert_exception() {
        assertThrows(TransformationException.class, () -> {
            JobInitiationToContextualisedScheduledProcessEventConverter converter
                = new JobInitiationToContextualisedScheduledProcessEventConverter("agentName", "logFolder", "/");

            converter.convert("BAD PAYLOAD");
        });
    }

    private boolean isWithinTheLast500Millis(long resultMillis, long testMillis) {
        return resultMillis > testMillis - 500 && resultMillis < testMillis + 500;
    }
}
