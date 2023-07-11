package org.ikasan.rest.module;

import org.hamcrest.core.IsInstanceOf;
import org.ikasan.component.endpoint.quartz.consumer.ScheduledConsumer;
import org.ikasan.component.endpoint.quartz.consumer.ScheduledConsumerConfiguration;
import org.ikasan.module.SimpleModule;
import org.ikasan.rest.module.model.TestFlow;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.flow.FlowConfiguration;
import org.ikasan.spec.flow.FlowElement;
import org.ikasan.spec.module.ModuleContainer;
import org.ikasan.spec.serialiser.Serialiser;
import org.ikasan.spec.serialiser.SerialiserFactory;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.internal.matchers.ThrowableCauseMatcher;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.quartz.impl.JobDetailImpl;
import org.quartz.impl.matchers.GroupMatcher;
import org.quartz.impl.triggers.SimpleTriggerImpl;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = { SchedulerApplication.class, MockedUserServiceTestConfig.class })
public class SchedulerApplicationTest
{
    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    protected MockMvc mockMvc;

    @Autowired
    protected WebApplicationContext webApplicationContext;

    @MockBean
    protected Scheduler platformScheduler;

    @Autowired
    protected SchedulerApplication schedulerApplication;

    @MockBean
    protected ModuleContainer moduleContainer;

    @Mock
    protected FlowConfiguration flowConfiguration;

    @Mock
    protected SerialiserFactory serialiserFactory;

    @Mock
    protected Serialiser serialiser;

    @Mock
    protected ScheduledConsumer scheduledConsumer;

    @Mock
    protected ScheduledConsumerConfiguration scheduledConsumerConfiguration;

    @Mock
    protected FlowElement scheduledConsumerElement ;


    @Before
    public void setUp()
    {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    @WithMockUser(authorities = "readonly")
    public void searchWithReadOnlyUser() throws Exception
    {
        exceptionRule.expect(new ThrowableCauseMatcher(new IsInstanceOf(AccessDeniedException.class)));
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/rest/scheduler/")
            .accept(MediaType.APPLICATION_JSON_VALUE);
        mockMvc.perform(requestBuilder).andReturn();
    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    public void search() throws Exception
    {
        Trigger trigger = new SimpleTriggerImpl();
        ((SimpleTriggerImpl) trigger).setGroup("Group");
        ((SimpleTriggerImpl) trigger).setJobName("Name");
        TriggerKey triggerKey = new TriggerKey("key");
        Set<TriggerKey> set = new HashSet();
        set.add(triggerKey);

        Mockito
            .when(platformScheduler.isShutdown())
            .thenReturn(false);

        Mockito
            .when(platformScheduler.getTriggerGroupNames())
            .thenReturn(new ArrayList<>(Arrays.asList("group")));

        Mockito
            .when(platformScheduler.getTriggerKeys(Mockito.any(GroupMatcher.class)))
            .thenReturn(set);

        Mockito
            .when(platformScheduler.getTrigger(triggerKey))
            .thenReturn(trigger);


        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/rest/scheduler/")
            .accept(MediaType.APPLICATION_JSON_VALUE);
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();


        Mockito
            .verify(platformScheduler).isShutdown();
        Mockito
            .verify(platformScheduler).getTriggerGroupNames();


        assertEquals(200, result.getResponse().getStatus());

        JSONAssert.assertEquals("JSON Result must equal!",
            "[{\"group\":\"Group\",\"jobName\":\"Name\",\"jobGroup\":\"DEFAULT\",\"jobDataMap\":{},"
                + "\"misfireInstruction\":0,\"priority\":5,\"repeatCount\":0,\"repeatInterval\":0,"
                + "\"timesTriggered\":0,\"scheduleBuilder\":{},\"triggerBuilder\":{},\"jobKey\":{\"name\":\"Name\","
                + "\"group\":\"DEFAULT\"},\"fullName\":\"Group.null\",\"fullJobName\":\"DEFAULT.Name\"}]",
            result.getResponse().getContentAsString(),
            JSONCompareMode.LENIENT);

    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    public void getWhenSchedulerShutdown() throws Exception
    {

        Mockito
            .when(platformScheduler.isShutdown())
            .thenReturn(true);


        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/rest/scheduler/")
                                                              .accept(MediaType.APPLICATION_JSON_VALUE);
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();


        Mockito
            .verify(platformScheduler).isShutdown();


        assertEquals(400, result.getResponse().getStatus());

        JSONAssert.assertEquals("JSON Result must equal!",
            "{\"errorMessage\":\"Scheduler was shutdown\"}",
            result.getResponse().getContentAsString(),
            JSONCompareMode.LENIENT);

    }


    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    public void triggerNowWhenModuleNotFound() throws Exception
    {

        Mockito
            .when(platformScheduler.isShutdown())
            .thenReturn(false);

        Mockito
            .when(moduleContainer.getModule("test-module"))
            .thenReturn(null);


        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/rest/scheduler/test-module/Scheduled Flow/395f7f9b-c8a9-4c2a-be3a-92e3b95bd929]")
                                                              .accept(MediaType.APPLICATION_JSON_VALUE);
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();


        Mockito
            .verify(platformScheduler).isShutdown();

        Mockito
            .verify(moduleContainer).getModule("test-module");


        assertEquals(400, result.getResponse().getStatus());
        assertEquals("{\"errorMessage\":\"Could not get module from module container using name:  [test-module]\"}", result.getResponse().getContentAsString());


    }


    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    public void triggerNowWhenFlowNotFound() throws Exception
    {
        Flow flow = new TestFlow("testFlow", "testModule", "running", flowConfiguration, serialiserFactory);
        SimpleModule module = new SimpleModule("testModule", null, Arrays.asList(flow));


        Mockito
            .when(platformScheduler.isShutdown())
            .thenReturn(false);

        Mockito
            .when(moduleContainer.getModule("testModule"))
            .thenReturn(module);


        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/rest/scheduler/testModule/Scheduled Flow/395f7f9b-c8a9-4c2a-be3a-92e3b95bd929]")
                                                              .accept(MediaType.APPLICATION_JSON_VALUE);
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();


        Mockito
            .verify(platformScheduler).isShutdown();

        Mockito
            .verify(moduleContainer).getModule("testModule");


        assertEquals(400, result.getResponse().getStatus());
        assertEquals("{\"errorMessage\":\"Could not get flow from module container using name:  [Scheduled Flow]\"}", result.getResponse().getContentAsString());


    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    public void triggerNow() throws Exception
    {

        Flow flow = new TestFlow("testFlow", "testModule", "running", flowConfiguration, serialiserFactory);
        SimpleModule module = new SimpleModule("testModule", null, Arrays.asList(flow));

        JobDetailImpl jobDetail = new JobDetailImpl();
        jobDetail.setName("testName");
        jobDetail.setGroup("testGroup");
        Mockito
            .when(platformScheduler.isShutdown())
            .thenReturn(false);

        Mockito
            .when(moduleContainer.getModule("testModule"))
            .thenReturn(module);

        Mockito
            .when(flowConfiguration.getConsumerFlowElement())
            .thenReturn(scheduledConsumerElement);

        Mockito
            .when(scheduledConsumerElement.getFlowComponent())
            .thenReturn(scheduledConsumer);

        Mockito
            .when(scheduledConsumer.getConfiguration())
            .thenReturn(scheduledConsumerConfiguration);

        Mockito
            .when(scheduledConsumerConfiguration.getJobName())
            .thenReturn("jobName");

        Mockito
            .when(scheduledConsumerConfiguration.getJobGroupName())
            .thenReturn("jobGroup");

        Mockito
            .when(scheduledConsumerConfiguration.getDescription())
            .thenReturn("jobDescription");

        Mockito
            .when(scheduledConsumer.getJobDetail())
            .thenReturn(jobDetail);


        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/rest/scheduler/testModule/testFlow/395f7f9b-c8a9-4c2a-be3a-92e3b95bd929]")
                                                              .accept(MediaType.APPLICATION_JSON_VALUE);
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();


        Mockito
            .verify(platformScheduler).isShutdown();

        Mockito
            .verify(moduleContainer).getModule("testModule");

        Mockito
            .verify(flowConfiguration).getConsumerFlowElement();

        Mockito
            .verify(scheduledConsumerElement,Mockito.times(2)).getFlowComponent();

        Mockito
            .verify(scheduledConsumer).getJobDetail();

        Mockito
            .verify(scheduledConsumer).scheduleAsEagerTrigger(Mockito.any(Trigger.class),Mockito.eq(0));


        assertEquals(200, result.getResponse().getStatus());
    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    public void triggerNow_null_job_name_etc() throws Exception
    {

        Flow flow = new TestFlow("testFlow", "testModule", "running", flowConfiguration, serialiserFactory);
        SimpleModule module = new SimpleModule("testModule", null, Arrays.asList(flow));

        JobDetailImpl jobDetail = new JobDetailImpl();
        jobDetail.setName("testName");
        jobDetail.setGroup("testGroup");
        Mockito
            .when(platformScheduler.isShutdown())
            .thenReturn(false);

        Mockito
            .when(moduleContainer.getModule("testModule"))
            .thenReturn(module);

        Mockito
            .when(flowConfiguration.getConsumerFlowElement())
            .thenReturn(scheduledConsumerElement);

        Mockito
            .when(scheduledConsumerElement.getFlowComponent())
            .thenReturn(scheduledConsumer);

        Mockito
            .when(scheduledConsumer.getConfiguration())
            .thenReturn(scheduledConsumerConfiguration);

        Mockito
            .when(scheduledConsumerConfiguration.getJobName())
            .thenReturn(null);

        Mockito
            .when(scheduledConsumerConfiguration.getJobGroupName())
            .thenReturn(null);

        Mockito
            .when(scheduledConsumerConfiguration.getDescription())
            .thenReturn(null);

        Mockito
            .when(scheduledConsumer.getJobDetail())
            .thenReturn(jobDetail);


        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/rest/scheduler/testModule/testFlow/395f7f9b-c8a9-4c2a-be3a-92e3b95bd929]")
            .accept(MediaType.APPLICATION_JSON_VALUE);
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();


        Mockito
            .verify(platformScheduler).isShutdown();

        Mockito
            .verify(moduleContainer).getModule("testModule");

        Mockito
            .verify(flowConfiguration).getConsumerFlowElement();

        Mockito
            .verify(scheduledConsumerElement,Mockito.times(2)).getFlowComponent();

        Mockito
            .verify(scheduledConsumer).getJobDetail();

        Mockito
            .verify(scheduledConsumer).scheduleAsEagerTrigger(Mockito.any(Trigger.class),Mockito.eq(0));


        assertEquals(200, result.getResponse().getStatus());
    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    public void triggerNow_empty_job_name_etc() throws Exception
    {

        Flow flow = new TestFlow("testFlow", "testModule", "running", flowConfiguration, serialiserFactory);
        SimpleModule module = new SimpleModule("testModule", null, Arrays.asList(flow));

        JobDetailImpl jobDetail = new JobDetailImpl();
        jobDetail.setName("testName");
        jobDetail.setGroup("testGroup");
        Mockito
            .when(platformScheduler.isShutdown())
            .thenReturn(false);

        Mockito
            .when(moduleContainer.getModule("testModule"))
            .thenReturn(module);

        Mockito
            .when(flowConfiguration.getConsumerFlowElement())
            .thenReturn(scheduledConsumerElement);

        Mockito
            .when(scheduledConsumerElement.getFlowComponent())
            .thenReturn(scheduledConsumer);

        Mockito
            .when(scheduledConsumer.getConfiguration())
            .thenReturn(scheduledConsumerConfiguration);

        Mockito
            .when(scheduledConsumerConfiguration.getJobName())
            .thenReturn("");

        Mockito
            .when(scheduledConsumerConfiguration.getJobGroupName())
            .thenReturn("");

        Mockito
            .when(scheduledConsumerConfiguration.getDescription())
            .thenReturn("");

        Mockito
            .when(scheduledConsumer.getJobDetail())
            .thenReturn(jobDetail);


        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/rest/scheduler/testModule/testFlow/395f7f9b-c8a9-4c2a-be3a-92e3b95bd929]")
            .accept(MediaType.APPLICATION_JSON_VALUE);
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();


        Mockito
            .verify(platformScheduler).isShutdown();

        Mockito
            .verify(moduleContainer).getModule("testModule");

        Mockito
            .verify(flowConfiguration).getConsumerFlowElement();

        Mockito
            .verify(scheduledConsumerElement,Mockito.times(2)).getFlowComponent();

        Mockito
            .verify(scheduledConsumer).getJobDetail();

        Mockito
            .verify(scheduledConsumer).scheduleAsEagerTrigger(Mockito.any(Trigger.class),Mockito.eq(0));


        assertEquals(200, result.getResponse().getStatus());
    }
}
