package org.ikasan.rest.module;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.core.IsInstanceOf;
import org.ikasan.module.SimpleModule;
import org.ikasan.rest.module.dto.ChangeFlowStateDto;
import org.ikasan.rest.module.model.TestFlow;
import org.ikasan.rest.module.util.DateTimeConverter;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.module.ModuleService;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.internal.matchers.ThrowableCauseMatcher;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
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
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = { ModuleControlApplication.class, MockedUserServiceTestConfig.class })
@EnableWebMvc
public class ModuleControlApplicationTest
{
    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    protected MockMvc mockMvc;

    @Autowired
    protected WebApplicationContext webApplicationContext;

    @MockBean
    protected ModuleService moduleServiceAuto;

    @Mock
    protected ModuleService moduleService;

    @Autowired
    protected ModuleControlApplication moduleControlApplication;

    private DateTimeConverter dateTimeConverter = new DateTimeConverter();

    private ObjectMapper mapper = new ObjectMapper();

    @Before
    public void setUp()
    {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        moduleControlApplication.setModuleService(moduleService);
    }

    @Test
    @WithMockUser(authorities = "readonly")
    public void searchWithReadOnlyUser() throws Exception
    {
        exceptionRule.expect(new ThrowableCauseMatcher(new IsInstanceOf(AccessDeniedException.class)));
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/rest/moduleControl/testModule")
            .accept(MediaType.APPLICATION_JSON_VALUE);
        mockMvc.perform(requestBuilder).andReturn();
    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    public void getModuleFlowStates() throws Exception
    {
        Flow flow = new TestFlow("test Flow", "testModule", "stopped");
        SimpleModule module = new SimpleModule("testModule", null, Arrays.asList(flow));
        Mockito
            .when(moduleService.getModule("testModule"))
            .thenReturn(module);
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/rest/moduleControl/testModule")
            .accept(MediaType.APPLICATION_JSON_VALUE);
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        Mockito
            .verify(moduleService).getModule("testModule");
        Mockito.verifyNoMoreInteractions(moduleService);
        assertEquals(200, result.getResponse().getStatus());
        JSONAssert.assertEquals("JSON Result must equal!",
            "{\"flows\":[{\"name\":\"test Flow\",\"state\":\"stopped\"}],\"name\":\"testModule\"}",
            result.getResponse().getContentAsString(),
            JSONCompareMode.LENIENT);
    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    public void getFlowState() throws Exception
    {
        Flow flow = new TestFlow("test Flow", "testModule", "stopped");
        SimpleModule module = new SimpleModule("testModule", null, Arrays.asList(flow));
        Mockito
            .when(moduleService.getModule("testModule"))
            .thenReturn(module);
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/rest/moduleControl/testModule/test Flow")
            .accept(MediaType.APPLICATION_JSON_VALUE);
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        Mockito
            .verify(moduleService).getModule("testModule");
        Mockito.verifyNoMoreInteractions(moduleService);
        assertEquals(200, result.getResponse().getStatus());
        JSONAssert.assertEquals("JSON Result must equal!",
            "{\"name\":\"test Flow\",\"state\":\"stopped\"}",
            result.getResponse().getContentAsString(),
            JSONCompareMode.LENIENT);
    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    public void getFlowStateWhenFlowDoesntExist() throws Exception
    {
        Flow flow = new TestFlow("test Flow", "testModule", "stopped");
        SimpleModule module = new SimpleModule("testModule", null, Arrays.asList(flow));
        Mockito
            .when(moduleService.getModule("testModule"))
            .thenReturn(module);
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/rest/moduleControl/testModule/nonExistingFlow")
            .accept(MediaType.APPLICATION_JSON_VALUE);
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        Mockito
            .verify(moduleService).getModule("testModule");
        Mockito.verifyNoMoreInteractions(moduleService);
        assertEquals(404, result.getResponse().getStatus());
        JSONAssert.assertEquals("JSON Result must equal!",
            "{\"errorCode\":null,\"errorMessage\":\"Module [testModule] Flow [nonExistingFlow] not found.\"}",
            result.getResponse().getContentAsString(),
            JSONCompareMode.LENIENT);
    }


    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    public void moduleControl() throws Exception
    {
        RequestBuilder requestBuilder = MockMvcRequestBuilders.put("/rest/moduleControl")
            .content(createChangeStateDto("start"))
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .contentType(MediaType.APPLICATION_JSON_VALUE);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        Mockito
            .verify(moduleService).startFlow(Mockito.eq("testModule"),Mockito.eq("testFlow"),Mockito.anyString());
        Mockito.verifyNoMoreInteractions(moduleService);
        assertEquals(200, result.getResponse().getStatus());

    }

    private String createChangeStateDto(String action) throws JsonProcessingException
    {

        ChangeFlowStateDto changeFlowStateDto = new ChangeFlowStateDto("testModule","testFlow",action);
        return mapper.writeValueAsString(changeFlowStateDto);
    }
}
