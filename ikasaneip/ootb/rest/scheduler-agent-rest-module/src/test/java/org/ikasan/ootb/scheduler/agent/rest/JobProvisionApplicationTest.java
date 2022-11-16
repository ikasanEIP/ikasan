package org.ikasan.ootb.scheduler.agent.rest;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.hamcrest.core.IsInstanceOf;
import org.ikasan.job.orchestration.model.context.*;
import org.ikasan.job.orchestration.model.job.*;
import org.ikasan.spec.scheduled.context.model.*;
import org.ikasan.spec.scheduled.job.model.InternalEventDrivenJob;
import org.ikasan.spec.scheduled.job.model.SchedulerJob;
import org.ikasan.spec.scheduled.job.model.SchedulerJobWrapper;
import org.ikasan.spec.scheduled.provision.JobProvisionService;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.internal.matchers.ThrowableCauseMatcher;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verifyNoInteractions;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = { JobProvisionApplication.class, MockedUserServiceTestConfig.class })
@EnableWebMvc
public class JobProvisionApplicationTest {
    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    protected MockMvc mockMvc;

    @Autowired
    protected WebApplicationContext webApplicationContext;

    @MockBean
    private JobProvisionService jobProvisionService;

    private ObjectMapper mapper = new ObjectMapper();

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        PolymorphicTypeValidator ptv = BasicPolymorphicTypeValidator.builder()
            .allowIfSubType("org.ikasan.spec.scheduled.job.model")
            .allowIfSubType("org.ikasan.job.orchestration.model.job")
            .allowIfSubType("org.ikasan.job.orchestration.model.context")
            .allowIfSubType("java.util.ArrayList")
            .allowIfSubType("java.util.HashMap")
            .build();

        final var simpleModule = new SimpleModule()
            .addAbstractTypeMapping(And.class, AndImpl.class)
            .addAbstractTypeMapping(Or.class, OrImpl.class)
            .addAbstractTypeMapping(Not.class, NotImpl.class)
            .addAbstractTypeMapping(ContextTemplate.class, ContextTemplateImpl.class)
            .addAbstractTypeMapping(Context.class, ContextImpl.class)
            .addAbstractTypeMapping(ContextParameter.class, ContextParameterImpl.class)
            .addAbstractTypeMapping(SchedulerJob.class, SchedulerJobImpl.class)
            .addAbstractTypeMapping(JobDependency.class, JobDependencyImpl.class)
            .addAbstractTypeMapping(ContextDependency.class, ContextDependencyImpl.class)
            .addAbstractTypeMapping(LogicalGrouping.class, LogicalGroupingImpl.class)
            .addAbstractTypeMapping(LogicalOperator.class, LogicalOperatorImpl.class)
            .addAbstractTypeMapping(InternalEventDrivenJob.class, InternalEventDrivenJobImpl.class)
            .addAbstractTypeMapping(List.class, ArrayList.class)
            .addAbstractTypeMapping(Map.class, HashMap.class);

        mapper.registerModule(simpleModule);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.activateDefaultTyping(ptv, ObjectMapper.DefaultTyping.NON_FINAL);
    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    public void test_exception_job_provision_jobs_put() throws Exception {
        doThrow(new RuntimeException("error provisioning jobs!"))
            .when(jobProvisionService).provisionJobs(anyList(), anyString());

        String payload = createSchedulerJobWrapper();

        RequestBuilder requestBuilder = MockMvcRequestBuilders.put("/rest/jobProvision")
            .content(payload)
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());
    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    public void test_success_job_provision_jobs_put() throws Exception {
        String payload = createSchedulerJobWrapper();

        RequestBuilder requestBuilder = MockMvcRequestBuilders.put("/rest/jobProvision")
            .content(payload)
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
    }

    @Test
    @WithMockUser(authorities = "readonly")
    public void test_success_job_initiation_event_put_read_only() throws Exception {
        exceptionRule.expect(new ThrowableCauseMatcher(new IsInstanceOf(AccessDeniedException.class)));
        String payload = createSchedulerJobWrapper();

        RequestBuilder requestBuilder = MockMvcRequestBuilders.put("/rest/jobProvision")
            .content(payload)
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder).andReturn();
    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    public void test_success_job_remove_delete() throws Exception {
        String payload = "contextName";

        RequestBuilder requestBuilder = MockMvcRequestBuilders.delete("/rest/jobProvision/remove")
            .content(payload)
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
    }

    @Test
    @WithMockUser(authorities = "readonly")
    public void test_execption_job_remove_delete_read_only() throws Exception {
        exceptionRule.expect(new ThrowableCauseMatcher(new IsInstanceOf(AccessDeniedException.class)));
        String payload = "contextName";

        RequestBuilder requestBuilder = MockMvcRequestBuilders.delete("/rest/jobProvision/remove")
            .content(payload)
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder).andReturn();
    }

    private String createSchedulerJobWrapper() throws JsonProcessingException {
        SchedulerJobWrapper wrapper = new SchedulerJobWrapperImpl();

        List<String> childIds = new ArrayList<>();
        childIds.add("childId");

        InternalEventDrivenJobImpl internalEventDrivenJob = new InternalEventDrivenJobImpl();
        internalEventDrivenJob.setAgentName("agentName");
        internalEventDrivenJob.setJobName("jobName");
        internalEventDrivenJob.setContextName("contextId");
        internalEventDrivenJob.setChildContextNames(childIds);

        FileEventDrivenJobImpl fileEventDrivenJob = new FileEventDrivenJobImpl();
        fileEventDrivenJob.setAgentName("agentName");
        fileEventDrivenJob.setJobName("jobName");
        fileEventDrivenJob.setContextName("contextId");
        fileEventDrivenJob.setChildContextNames(childIds);

        QuartzScheduleDrivenJobImpl quartzScheduleDrivenJob = new QuartzScheduleDrivenJobImpl();
        quartzScheduleDrivenJob.setAgentName("agentName");
        quartzScheduleDrivenJob.setJobName("jobName");
        quartzScheduleDrivenJob.setContextName("contextId");
        quartzScheduleDrivenJob.setChildContextNames(childIds);
        ArrayList<SchedulerJob> jobs = new ArrayList<>();
        jobs.add(internalEventDrivenJob);
        jobs.add(fileEventDrivenJob);
        jobs.add(quartzScheduleDrivenJob);
        wrapper.setJobs(jobs);

        return this.mapper.writerWithDefaultPrettyPrinter().writeValueAsString(wrapper);
    }
}
