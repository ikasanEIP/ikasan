package org.ikasan.ootb.scheduler.agent.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.ikasan.bigqueue.IBigQueue;
import org.ikasan.ootb.scheduler.agent.rest.dto.ContextParameterInstanceDto;
import org.ikasan.ootb.scheduler.agent.rest.dto.ContextualisedScheduledProcessEventDto;
import org.ikasan.ootb.scheduler.agent.rest.dto.InternalEventDrivenJobInstanceDto;
import org.ikasan.ootb.scheduler.agent.rest.dto.SchedulerJobInitiationEventDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.ikasan.ootb.scheduler.agent.rest.cache.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;

@SpringBootTest(classes = {SchedulerJobInitiationEventApplication.class, MockedUserServiceTestConfigWithConverter.class})
@EnableWebMvc
class SchedulerJobInitiationEventApplicationTest
{

    protected MockMvc mockMvc;

    @Autowired
    protected WebApplicationContext webApplicationContext;

    @MockBean
    protected IBigQueue inboundQueue;

    private ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setUp()
    {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    void test_job_initiation_event_put() throws Exception
    {
        RequestBuilder requestBuilder = MockMvcRequestBuilders.put("/rest/schedulerJobInitiation")
                                                              .content(createSchedulerJobInitiationEventDto("TEST"))
                                                              .accept(MediaType.APPLICATION_JSON)
                                                              .contentType(MediaType.APPLICATION_JSON);

        Mockito.doNothing().when(this.inboundQueue).enqueue(any(byte[].class));

        InboundJobQueueCache.instance().put("agentName-TEST_contextName-inbound-queue", this.inboundQueue);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        Mockito.verify(this.inboundQueue, times(1)).enqueue(any(byte[].class));

        assertEquals(200, result.getResponse().getStatus());
    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    void test_job_initiation_event_put_exception() throws Exception
    {
        RequestBuilder requestBuilder = MockMvcRequestBuilders.put("/rest/schedulerJobInitiation")
            .content(createSchedulerJobInitiationEventDto("TEST"))
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .contentType(MediaType.APPLICATION_JSON_VALUE);

        Mockito.doThrow(new RuntimeException("test exception")).when(this.inboundQueue).enqueue(any(byte[].class));

        InboundJobQueueCache.instance().put("agentName-TEST_contextName-inbound-queue", this.inboundQueue);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        Mockito.verify(this.inboundQueue, times(1)).enqueue(any(byte[].class));

        assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());
    }


    private String createSchedulerJobInitiationEventDto(String event) throws JsonProcessingException
    {
        SchedulerJobInitiationEventDto dto = new SchedulerJobInitiationEventDto();
        dto.setAgentName("agentName");
        dto.setContextName("contextName");
        dto.setJobName(event);

        InternalEventDrivenJobInstanceDto internalEventDrivenJobInstanceDto = new InternalEventDrivenJobInstanceDto();
        ContextParameterInstanceDto contextParameterInstanceDto = new ContextParameterInstanceDto();
        contextParameterInstanceDto.setName("name");
        contextParameterInstanceDto.setValue("value");
        internalEventDrivenJobInstanceDto.setContextParameters(List.of(contextParameterInstanceDto));
        ContextualisedScheduledProcessEventDto contextualisedScheduledProcessEventDto = new ContextualisedScheduledProcessEventDto();
        contextualisedScheduledProcessEventDto.setInternalEventDrivenJob(new InternalEventDrivenJobInstanceDto());
        internalEventDrivenJobInstanceDto.setScheduledProcessEvent(contextualisedScheduledProcessEventDto);


        dto.setInternalEventDrivenJob(internalEventDrivenJobInstanceDto);

        return mapper.writeValueAsString(dto);
    }
}
