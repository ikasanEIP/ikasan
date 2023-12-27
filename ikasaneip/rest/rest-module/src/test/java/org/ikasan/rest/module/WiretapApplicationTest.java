package org.ikasan.rest.module;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.core.IsInstanceOf;
import org.ikasan.rest.module.dto.TriggerDto;
import org.ikasan.spec.flow.FlowEvent;
import org.ikasan.spec.module.ModuleService;
import org.ikasan.spec.search.PagedSearchResult;
import org.ikasan.spec.systemevent.SystemEventService;
import org.ikasan.spec.trigger.TriggerService;
import org.ikasan.spec.wiretap.WiretapEvent;
import org.ikasan.spec.wiretap.WiretapService;
import org.ikasan.trigger.model.TriggerImpl;
import org.ikasan.wiretap.listener.JobAwareFlowEventListener;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.internal.matchers.ThrowableCauseMatcher;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
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
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.Arrays;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = { WiretapApplication.class, MockedUserServiceTestConfig.class })
@EnableWebMvc
public class WiretapApplicationTest
{
    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    protected MockMvc mockMvc;

    @Autowired
    protected WebApplicationContext webApplicationContext;

    @MockBean
    protected JobAwareFlowEventListener jobAwareFlowEventListener;

    @MockBean
    protected ModuleService moduleService;

    @MockBean
    protected SystemEventService systemEventService;

    @MockBean
    protected WiretapService<FlowEvent, PagedSearchResult<WiretapEvent>, Long> wiretapService;

    private ObjectMapper mapper = new ObjectMapper();


    @Before
    public void setUp()
    {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    @WithMockUser(authorities = "readonly")
    public void createTriggerWithReadOnlyUser() throws Exception
    {
        exceptionRule.expect(new ThrowableCauseMatcher(new IsInstanceOf(AccessDeniedException.class)));
        RequestBuilder requestBuilder = MockMvcRequestBuilders.put("/rest/wiretap/trigger")
                                                              .content(createTriggerDto( ))
                                                              .accept(MediaType.APPLICATION_JSON_VALUE)
                                                              .contentType(MediaType.APPLICATION_JSON_VALUE);
        mockMvc.perform(requestBuilder).andReturn();
    }


    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    public void createTriggerPUT_when_returns_200() throws Exception
    {
        RequestBuilder requestBuilder = MockMvcRequestBuilders.put("/rest/wiretap/trigger")
                                                              .content(createTriggerDto())
                                                              .accept(MediaType.APPLICATION_JSON_VALUE)
                                                              .contentType(MediaType.APPLICATION_JSON_VALUE);


        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        assertEquals(201, result.getResponse().getStatus());
        Mockito.verify(jobAwareFlowEventListener).addDynamicTrigger(Mockito.any(TriggerImpl.class));

        Mockito.verify(systemEventService).logSystemEvent(
            Mockito.eq("testModule"),
            Mockito.startsWith("testModule-testFlow:org.ikasan.trigger.model.TriggerImpl[id=null,moduleName=testModule,flowName=testFlow,flowElementName=component,params={timeToLive=100},jobName=wiretap,relationship=AFTER]"),
            Mockito.eq("Create Wiretap"),
            Mockito.anyString());

        Mockito.verifyNoMoreInteractions(jobAwareFlowEventListener, systemEventService );

    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    public void createTriggerPUT_when_returns_401() throws Exception
    {
        Mockito.doThrow(new RuntimeException("issue persisting rigger"))
               .when(jobAwareFlowEventListener).addDynamicTrigger(Mockito.any(TriggerImpl.class));

        RequestBuilder requestBuilder = MockMvcRequestBuilders.put("/rest/wiretap/trigger")
                                                              .content(createTriggerDto())
                                                              .accept(MediaType.APPLICATION_JSON_VALUE)
                                                              .contentType(MediaType.APPLICATION_JSON_VALUE);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        assertEquals(403, result.getResponse().getStatus());
        assertEquals("""
            {"errorMessage":"An error has occurred trying to create a new trigger: \
            [issue persisting rigger] for request[TriggerDto[moduleName='testModule', flowName='testFlow', \
            flowElementName='component', relationship='after', jobType='wiretap', timeToLive='100']]"}\
            """, result.getResponse().getContentAsString());

    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    public void deleteTrigger_when_returns_200() throws Exception
    {

        Mockito.when(jobAwareFlowEventListener.getTrigger(1202l))
               .thenReturn(new TriggerImpl("testModule","testFlow","after","test"));

        RequestBuilder requestBuilder = MockMvcRequestBuilders.delete("/rest/wiretap/trigger/1202")
                                                              .accept(MediaType.APPLICATION_JSON_VALUE)
                                                              .contentType(MediaType.APPLICATION_JSON_VALUE);


        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        assertEquals(200, result.getResponse().getStatus());
        Mockito.verify(jobAwareFlowEventListener).getTrigger(1202l);
        Mockito.verify(jobAwareFlowEventListener).deleteDynamicTrigger(1202l);

        Mockito.verify(systemEventService).logSystemEvent(
            Mockito.eq("testModule"),
            Mockito.startsWith("testModule-testFlow:org.ikasan.trigger.model.TriggerImpl[id=null,moduleName=testModule,flowName=testFlow,flowElementName=null,params={},jobName=test,relationship=AFTER]"),
            Mockito.eq("Delete Wiretap"),
            Mockito.anyString());

        Mockito.verifyNoMoreInteractions(jobAwareFlowEventListener, systemEventService );


    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    public void delteTriggerwhen_returns_401() throws Exception
    {

        Mockito.doThrow(new RuntimeException("issue persisting rigger"))
               .when(jobAwareFlowEventListener).deleteDynamicTrigger(1202l);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.delete("/rest/wiretap/trigger/1202")
                                                              .accept(MediaType.APPLICATION_JSON_VALUE)
                                                              .contentType(MediaType.APPLICATION_JSON_VALUE);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        assertEquals(403, result.getResponse().getStatus());
        assertEquals("""
            {"errorMessage":"An error has occurred trying to delete trigger: [issue \
            persisting rigger] for request[1202]"}\
            """, result.getResponse().getContentAsString());

    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    public void getTriggers_when_returns_200() throws Exception
    {
        //String moduleName, String flowName, String relationshipDescription, String jobName, Map<String, String> params
        TriggerImpl t = new TriggerImpl("testModule","testFlow","AFTER",
            "tjob", new HashMap<>(){{put("timeToLive","200");}}
            );

        Mockito
            .when(jobAwareFlowEventListener.getTriggers())
            .thenReturn(Arrays.asList(t));

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/rest/wiretap/triggers")
                                                              .accept(MediaType.APPLICATION_JSON_VALUE)
                                                              .contentType(MediaType.APPLICATION_JSON_VALUE);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        assertEquals(200, result.getResponse().getStatus());
        assertEquals("""
            [{"moduleName":"testModule","flowName":"testFlow","relationship":"AFTER",\
            "jobType":"Wiretap","timeToLive":"200"}]\
            """, result.getResponse().getContentAsString());

    }

    private String createTriggerDto() throws JsonProcessingException
    {
        TriggerDto dto = new TriggerDto("testModule", "testFlow", "component", "after", "wiretap", "100" );
        return mapper.writeValueAsString(dto);
    }
}
