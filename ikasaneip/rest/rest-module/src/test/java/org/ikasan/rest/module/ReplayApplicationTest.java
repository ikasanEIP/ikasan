package org.ikasan.rest.module;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.core.IsInstanceOf;
import org.ikasan.module.SimpleModule;
import org.ikasan.rest.module.dto.ReplayRequestDto;
import org.ikasan.rest.module.model.TestFlow;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.flow.FlowConfiguration;
import org.ikasan.spec.module.ModuleContainer;
import org.ikasan.spec.resubmission.ResubmissionService;
import org.ikasan.spec.serialiser.Serialiser;
import org.ikasan.spec.serialiser.SerialiserFactory;
import org.ikasan.spec.systemevent.SystemEventService;
import org.junit.internal.matchers.ThrowableCauseMatcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = {ReplayApplication.class, MockedUserServiceTestConfig.class})
@EnableWebMvc
class ReplayApplicationTest
{

    protected MockMvc mockMvc;

    @Autowired
    protected WebApplicationContext webApplicationContext;

    @MockBean
    protected ModuleContainer moduleContainer;

    @MockBean
    protected SystemEventService systemEventService;

    @Mock
    protected FlowConfiguration flowConfiguration;

    @Mock
    protected ResubmissionService resubmissionService;

    @Mock
    protected SerialiserFactory serialiserFactory;

    @Mock
    protected Serialiser serialiser;

    @Autowired
    protected ReplayApplication replayApplication;

    private ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setUp()
    {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

    }

    @Test
    @WithMockUser(authorities = "readonly")
    void searchWithReadOnlyUser() throws Exception
    {
        exceptionRule.expect(new ThrowableCauseMatcher(new IsInstanceOf(AccessDeniedException.class)));
        RequestBuilder requestBuilder = MockMvcRequestBuilders.put("/rest/replay")
                                                              .content(createReplayRequestDto("TEST"))
                                                              .accept(MediaType.APPLICATION_JSON_VALUE)
                                                              .contentType(MediaType.APPLICATION_JSON_VALUE);
        mockMvc.perform(requestBuilder).andReturn();
    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    void replayPUT() throws Exception
    {
        Object deserialisedObject = new Object();

        Flow flow = new TestFlow("testFlow", "testModule", "running", flowConfiguration, serialiserFactory);
        SimpleModule module = new SimpleModule("testModule", null, Arrays.asList(flow));

        RequestBuilder requestBuilder = MockMvcRequestBuilders.put("/rest/replay")
                                                              .content(createReplayRequestDto("TEST"))
                                                              .accept(MediaType.APPLICATION_JSON_VALUE)
                                                              .contentType(MediaType.APPLICATION_JSON_VALUE);

        Mockito.when(moduleContainer.getModule(Mockito.eq("testModule"))).thenReturn(module);

        Mockito.when(flowConfiguration.getResubmissionService()).thenReturn(resubmissionService);
        Mockito.when(serialiserFactory.getDefaultSerialiser()).thenReturn(serialiser);
        Mockito.when(serialiser.deserialise(Mockito.any())).thenReturn(deserialisedObject);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        Mockito.verify(moduleContainer).getModule(Mockito.eq("testModule"));
        Mockito.verify(flowConfiguration).getResubmissionService();
        Mockito.verify(serialiserFactory).getDefaultSerialiser();
        Mockito.verify(serialiser).deserialise(Mockito.any());

        Mockito.verify(resubmissionService).onResubmission(deserialisedObject);

        Mockito.verify(systemEventService).logSystemEvent(
            Mockito.eq("testModule"),
            Mockito.startsWith("testModule-testFlow:"),
            Mockito.eq("Replaying Event"),
            Mockito.anyString()
                                                         );

        Mockito.verifyNoMoreInteractions(moduleContainer, flowConfiguration, resubmissionService, serialiserFactory,
            serialiser, systemEventService );

        assertEquals(200, result.getResponse().getStatus());

    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    void replayPUT_when_module_notFound() throws Exception
    {

        Flow flow = new TestFlow("testFlow", "testModule", "running", flowConfiguration, serialiserFactory);
        SimpleModule module = new SimpleModule("testModule", null, Arrays.asList(flow));

        RequestBuilder requestBuilder = MockMvcRequestBuilders.put("/rest/replay")
                                                              .content(createReplayRequestDto("TEST"))
                                                              .accept(MediaType.APPLICATION_JSON_VALUE)
                                                              .contentType(MediaType.APPLICATION_JSON_VALUE);

        Mockito.when(moduleContainer.getModule(Mockito.eq("testModule"))).thenReturn(null);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        Mockito.verify(moduleContainer).getModule(Mockito.eq("testModule"));

        Mockito.verifyNoMoreInteractions(moduleContainer, flowConfiguration, resubmissionService, serialiserFactory,
            serialiser, systemEventService);

        assertEquals(400, result.getResponse().getStatus());
        assertEquals("""
            {"errorMessage":"Could not get module from module container using name:  \
            [testModule]"}\
            """, result.getResponse().getContentAsString());

    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    void replayPUT_flow_not_found() throws Exception
    {

        Flow flow = new TestFlow("Invalid", "testModule", "running", flowConfiguration, serialiserFactory);
        SimpleModule module = new SimpleModule("testModule", null, Arrays.asList(flow));

        RequestBuilder requestBuilder = MockMvcRequestBuilders.put("/rest/replay")
                                                              .content(createReplayRequestDto("TEST"))
                                                              .accept(MediaType.APPLICATION_JSON_VALUE)
                                                              .contentType(MediaType.APPLICATION_JSON_VALUE);

        Mockito.when(moduleContainer.getModule(Mockito.eq("testModule"))).thenReturn(module);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        Mockito.verify(moduleContainer).getModule(Mockito.eq("testModule"));

        Mockito.verifyNoMoreInteractions(moduleContainer, flowConfiguration, resubmissionService, systemEventService);

        assertEquals(400, result.getResponse().getStatus());
        assertEquals("""
            {"errorMessage":"Could not get flow from module container using name:  \
            [testFlow]"}\
            """, result.getResponse().getContentAsString());

    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    void replayPUT_when_flow_stopped() throws Exception
    {

        Flow flow = new TestFlow("testFlow", "testModule", "stopped", flowConfiguration, serialiserFactory);
        SimpleModule module = new SimpleModule("testModule", null, Arrays.asList(flow));

        RequestBuilder requestBuilder = MockMvcRequestBuilders.put("/rest/replay")
                                                              .content(createReplayRequestDto("TEST"))
                                                              .accept(MediaType.APPLICATION_JSON_VALUE)
                                                              .contentType(MediaType.APPLICATION_JSON_VALUE);

        Mockito.when(moduleContainer.getModule(Mockito.eq("testModule"))).thenReturn(module);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        Mockito.verify(moduleContainer).getModule(Mockito.eq("testModule"));

        Mockito.verifyNoMoreInteractions(moduleContainer, flowConfiguration, resubmissionService, serialiserFactory,
            serialiser, systemEventService);

        assertEquals(400, result.getResponse().getStatus());
        assertEquals("""
                {"errorMessage":"Events cannot be replayed when the flow that is being \
                replayed to is in a stopped state.  Module[testModule] Flow[testFlow]"}\
                """,
            result.getResponse().getContentAsString()
                    );

    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    void replayPUT_when_replay_service_is_null() throws Exception
    {
        Flow flow = new TestFlow("testFlow", "testModule", "running", flowConfiguration, serialiserFactory);
        SimpleModule module = new SimpleModule("testModule", null, Arrays.asList(flow));

        RequestBuilder requestBuilder = MockMvcRequestBuilders.put("/rest/replay")
                                                              .content(createReplayRequestDto("TEST"))
                                                              .accept(MediaType.APPLICATION_JSON_VALUE)
                                                              .contentType(MediaType.APPLICATION_JSON_VALUE);

        Mockito.when(moduleContainer.getModule(Mockito.eq("testModule"))).thenReturn(module);

        Mockito.when(flowConfiguration.getResubmissionService()).thenReturn(null);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        Mockito.verify(moduleContainer).getModule(Mockito.eq("testModule"));
        Mockito.verify(flowConfiguration).getResubmissionService();

        Mockito.verifyNoMoreInteractions(moduleContainer, flowConfiguration, resubmissionService, serialiserFactory,
            serialiser, systemEventService );

        assertEquals(400, result.getResponse().getStatus());
        assertEquals("""
                {"errorMessage":"The resubmission service on the flow you are \
                resubmitting to is null. This is most likely due to the resubmission service not being set on the \
                flow factory for the flow you are resubmitting to."}\
                """,
            result.getResponse().getContentAsString()
                    );

    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    void replayPUT_when_resubmission_throws_exception() throws Exception
    {
        Object deserialisedObject = new Object();

        Flow flow = new TestFlow("testFlow", "testModule", "running", flowConfiguration, serialiserFactory);
        SimpleModule module = new SimpleModule("testModule", null, Arrays.asList(flow));

        RequestBuilder requestBuilder = MockMvcRequestBuilders.put("/rest/replay")
                                                              .content(createReplayRequestDto("TEST"))
                                                              .accept(MediaType.APPLICATION_JSON_VALUE)
                                                              .contentType(MediaType.APPLICATION_JSON_VALUE);

        Mockito.when(moduleContainer.getModule(Mockito.eq("testModule"))).thenReturn(module);

        Mockito.when(flowConfiguration.getResubmissionService()).thenReturn(resubmissionService);
        Mockito.when(serialiserFactory.getDefaultSerialiser()).thenReturn(serialiser);
        Mockito.when(serialiser.deserialise(Mockito.any())).thenReturn(deserialisedObject);
        Mockito.doThrow((new RuntimeException("Error"))).when(resubmissionService).onResubmission(deserialisedObject);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        Mockito.verify(moduleContainer).getModule(Mockito.eq("testModule"));
        Mockito.verify(flowConfiguration).getResubmissionService();
        Mockito.verify(serialiserFactory).getDefaultSerialiser();
        Mockito.verify(serialiser).deserialise(Mockito.any());

        Mockito.verify(resubmissionService).onResubmission(deserialisedObject);

        Mockito.verifyNoMoreInteractions(moduleContainer, flowConfiguration, resubmissionService, serialiserFactory,
            serialiser, systemEventService  );

        assertEquals(404, result.getResponse().getStatus());

    }
    private String createReplayRequestDto(String event) throws JsonProcessingException
    {
        ReplayRequestDto dto = new ReplayRequestDto("testModule", "testFlow", event.getBytes());
        return mapper.writeValueAsString(dto);
    }
}
