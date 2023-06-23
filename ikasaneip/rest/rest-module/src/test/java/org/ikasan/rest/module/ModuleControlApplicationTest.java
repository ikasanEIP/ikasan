package org.ikasan.rest.module;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.core.IsInstanceOf;
import org.ikasan.module.ConfiguredModuleImpl;
import org.ikasan.module.SimpleModule;
import org.ikasan.rest.module.dto.ChangeFlowStartupModeDto;
import org.ikasan.rest.module.dto.ChangeFlowStateDto;
import org.ikasan.rest.module.dto.ModuleActivationDto;
import org.ikasan.rest.module.model.TestFlow;
import org.ikasan.rest.module.util.DateTimeConverter;
import org.ikasan.spec.dashboard.DashboardRestService;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.flow.FlowFactory;
import org.ikasan.spec.module.ModuleActivator;
import org.ikasan.spec.module.ModuleService;
import org.ikasan.spec.module.StartupControl;
import org.ikasan.spec.module.StartupType;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.internal.matchers.ThrowableCauseMatcher;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
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

import java.util.Arrays;
import java.util.List;

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
    protected ModuleService moduleService;

    @MockBean
    protected ModuleActivator moduleActivator;

    @MockBean
    protected FlowFactory flowFactory;

    @MockBean
    private DashboardRestService moduleMetadataDashboardRestService;

    @Autowired
    protected ModuleControlApplication moduleControlApplication;

    private DateTimeConverter dateTimeConverter = new DateTimeConverter();

    private ObjectMapper mapper = new ObjectMapper();

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
    public void getModuleFlowStatesLegacy() throws Exception
    {
        Flow flow = new TestFlow("test Flow", "testModule", "stopped");
        SimpleModule module = new SimpleModule("testModule", null, Arrays.asList(flow));
        Mockito
            .when(moduleService.getModule("testModule"))
            .thenReturn(module);
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/rest/moduleControl/flowStates/testModule")
            .accept(MediaType.APPLICATION_JSON_VALUE);
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        Mockito
            .verify(moduleService).getModule("testModule");
        Mockito.verifyNoMoreInteractions(moduleService);
        assertEquals(200, result.getResponse().getStatus());
        assertEquals("{\"testModule-test Flow\":\"stopped\"}", result.getResponse().getContentAsString());
    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    public void getModuleFlowStatesLegacyNull() throws Exception
    {
        Flow flow = new TestFlow("test Flow", "testModule", "stopped");
        Mockito
            .when(moduleService.getModule("testModule"))
            .thenReturn(null);
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/rest/moduleControl/flowStates/testModule")
            .accept(MediaType.APPLICATION_JSON_VALUE);
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        Mockito
            .verify(moduleService).getModule("testModule");
        Mockito.verifyNoMoreInteractions(moduleService);
        assertEquals(200, result.getResponse().getStatus());
        assertEquals("{}", result.getResponse().getContentAsString());
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
            "{\"errorMessage\":\"Module [testModule] Flow [nonExistingFlow] not found.\"}",
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

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    public void moduleActivate() throws Exception
    {
        ConfiguredModuleImpl module = new ConfiguredModuleImpl("testModule", flowFactory);
        Mockito
            .when(moduleService.getModules())
            .thenReturn(List.of(module));

        RequestBuilder requestBuilder = MockMvcRequestBuilders.put("/rest/moduleControl/activator")
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .content(createModuleActivationDto("activate"))
            .contentType(MediaType.APPLICATION_JSON_VALUE);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        Mockito
            .verify(moduleActivator).activate(module);
        Mockito.verifyNoMoreInteractions(moduleActivator);
        assertEquals(200, result.getResponse().getStatus());
    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    public void moduleDeactivate() throws Exception
    {
        ConfiguredModuleImpl module = new ConfiguredModuleImpl("testModule", flowFactory);
        Mockito
            .when(moduleService.getModules())
            .thenReturn(List.of(module));

        RequestBuilder requestBuilder = MockMvcRequestBuilders.put("/rest/moduleControl/activator")
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .content(createModuleActivationDto("deactivate"))
            .contentType(MediaType.APPLICATION_JSON_VALUE);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        Mockito
            .verify(moduleActivator).deactivate(module);
        Mockito.verifyNoMoreInteractions(moduleActivator);
        assertEquals(200, result.getResponse().getStatus());
    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    public void moduleActivatorBadAction() throws Exception
    {
        ConfiguredModuleImpl module = new ConfiguredModuleImpl("testModule", flowFactory);
        Mockito
            .when(moduleService.getModules())
            .thenReturn(List.of(module));

        RequestBuilder requestBuilder = MockMvcRequestBuilders.put("/rest/moduleControl/activator")
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .content(createModuleActivationDto("bad-action"))
            .contentType(MediaType.APPLICATION_JSON_VALUE);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        Mockito.verifyNoMoreInteractions(moduleActivator);
        assertEquals(HttpStatus.FORBIDDEN.value(), result.getResponse().getStatus());
    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    public void moduleActivatorNoModuleFound() throws Exception
    {
        Mockito
            .when(moduleService.getModules())
            .thenReturn(List.of());

        RequestBuilder requestBuilder = MockMvcRequestBuilders.put("/rest/moduleControl/activator")
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .content(createModuleActivationDto("activate"))
            .contentType(MediaType.APPLICATION_JSON_VALUE);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        Mockito.verifyNoMoreInteractions(moduleActivator);
        assertEquals(HttpStatus.FORBIDDEN.value(), result.getResponse().getStatus());
        assertEquals("\"Could not load module!\"", result.getResponse().getContentAsString());
    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    public void getModuleIsActivated() throws Exception
    {
        ConfiguredModuleImpl module = new ConfiguredModuleImpl("testModule", flowFactory);
        Mockito
            .when(moduleService.getModule("testModule"))
            .thenReturn(module);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/rest/moduleControl/isActivated/testModule")
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .contentType(MediaType.APPLICATION_JSON_VALUE);

        Mockito
            .when(moduleActivator.isActivated(module))
            .thenReturn(true);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        assertEquals(200, result.getResponse().getStatus());
        assertEquals("\"activated\"", result.getResponse().getContentAsString());
    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    public void getModuleIsNotActivated() throws Exception
    {
        ConfiguredModuleImpl module = new ConfiguredModuleImpl("testModule", flowFactory);
        Mockito
            .when(moduleService.getModule("testModule"))
            .thenReturn(module);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/rest/moduleControl/isActivated/testModule")
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .contentType(MediaType.APPLICATION_JSON_VALUE);

        Mockito
            .when(moduleActivator.isActivated(module))
            .thenReturn(false);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        assertEquals(200, result.getResponse().getStatus());
        assertEquals("\"deactivated\"", result.getResponse().getContentAsString());
    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    public void changeFlowStartupMode() throws Exception
    {
        RequestBuilder requestBuilder = MockMvcRequestBuilders.put("/rest/moduleControl/startupMode")
            .content(createChangeFlowStartupModeDto("automatic", "comment" ))
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .contentType(MediaType.APPLICATION_JSON_VALUE);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        Mockito
            .verify(moduleService).setStartupType(Mockito.eq("testModule"),Mockito.eq("testFlow"),Mockito.eq(
            StartupType.AUTOMATIC),Mockito.eq("comment"),Mockito.anyString());
        Mockito
            .verify(moduleService).getModule(Mockito.eq("testModule"));
        Mockito.verifyNoMoreInteractions(moduleService);
        assertEquals(200, result.getResponse().getStatus());

    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    public void changeFlowStartupMode_withInvalidStartupType() throws Exception
    {
        RequestBuilder requestBuilder = MockMvcRequestBuilders.put("/rest/moduleControl/startupMode")
            .content(createChangeFlowStartupModeDto("invalid", "comment" ))
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .contentType(MediaType.APPLICATION_JSON_VALUE);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        Mockito.verifyNoMoreInteractions(moduleService);
        assertEquals(400, result.getResponse().getStatus());

    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    public void changeFlowStartupMode_toDisabled_withoutComment() throws Exception
    {
        RequestBuilder requestBuilder = MockMvcRequestBuilders.put("/rest/moduleControl/startupMode")
            .content(createChangeFlowStartupModeDto("disabled", "" ))
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .contentType(MediaType.APPLICATION_JSON_VALUE);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        Mockito.verifyNoMoreInteractions(moduleService);
        assertEquals(400, result.getResponse().getStatus());

    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    public void getFlowStartupMode_nullStartupControl() throws Exception
    {
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/rest/moduleControl/startupMode/testModule/testFlow")
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .contentType(MediaType.APPLICATION_JSON_VALUE);

        Mockito
            .when(moduleService.getStartupControl(Mockito.eq("testModule"),Mockito.eq("testFlow")))
            .thenReturn(null);



        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        assertEquals(200, result.getResponse().getStatus());
        JSONAssert.assertEquals("JSON Result must equal!",
            "{\"moduleName\":\"testModule\",\"flowName\":\"testFlow\",\"startupType\":\"MANUAL\"}",
            result.getResponse().getContentAsString(),
            JSONCompareMode.LENIENT);
    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    public void getFlowStartupMode_manualStartupControl() throws Exception
    {
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/rest/moduleControl/startupMode/testModule/testFlow")
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .contentType(MediaType.APPLICATION_JSON_VALUE);

        Mockito
            .when(moduleService.getStartupControl(Mockito.eq("testModule"),Mockito.eq("testFlow")))
            .thenReturn(getStartupControl(StartupType.MANUAL, null));


        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        assertEquals(200, result.getResponse().getStatus());
        JSONAssert.assertEquals("JSON Result must equal!",
            "{\"moduleName\":\"testModule\",\"flowName\":\"testFlow\",\"startupType\":\"MANUAL\"}",
            result.getResponse().getContentAsString(),
            JSONCompareMode.LENIENT);
    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    public void getFlowStartupMode_automaticStartupControl() throws Exception
    {
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/rest/moduleControl/startupMode/testModule/testFlow")
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .contentType(MediaType.APPLICATION_JSON_VALUE);

        Mockito
            .when(moduleService.getStartupControl("testModule","testFlow"))
            .thenReturn(getStartupControl(StartupType.AUTOMATIC, null));

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        assertEquals(200, result.getResponse().getStatus());
        JSONAssert.assertEquals("JSON Result must equal!",
            "{\"moduleName\":\"testModule\",\"flowName\":\"testFlow\",\"startupType\":\"AUTOMATIC\"}",
            result.getResponse().getContentAsString(),
            JSONCompareMode.LENIENT);
    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    public void getFlowStartupMode_disabledStartupControl() throws Exception
    {
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/rest/moduleControl/startupMode/testModule/testFlow")
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .contentType(MediaType.APPLICATION_JSON_VALUE);

        Mockito
            .when(moduleService.getStartupControl(Mockito.eq("testModule"),Mockito.eq("testFlow")))
            .thenReturn(getStartupControl(StartupType.DISABLED, "disabled"));


        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        assertEquals(200, result.getResponse().getStatus());
        JSONAssert.assertEquals("JSON Result must equal!",
            "{\"moduleName\":\"testModule\",\"flowName\":\"testFlow\",\"startupType\":\"DISABLED\",\"comment\":\"disabled\"}",
            result.getResponse().getContentAsString(),
            JSONCompareMode.LENIENT);
    }

    private String createModuleActivationDto(String action) throws JsonProcessingException
    {
        ModuleActivationDto moduleActivationDto = new ModuleActivationDto();
        moduleActivationDto.setAction(action);
        return mapper.writeValueAsString(moduleActivationDto);
    }

    private String createChangeStateDto(String action) throws JsonProcessingException
    {
        ChangeFlowStateDto changeFlowStateDto = new ChangeFlowStateDto("testModule","testFlow",action);
        return mapper.writeValueAsString(changeFlowStateDto);
    }

    private String createChangeFlowStartupModeDto(String action,String comment) throws JsonProcessingException
    {
        ChangeFlowStartupModeDto dto = new ChangeFlowStartupModeDto("testModule","testFlow",action, comment);
        return mapper.writeValueAsString(dto);
    }

    private StartupControl getStartupControl(StartupType startupType, String comment){
        return  new StartupControl() {
            @Override
            public String getModuleName() {
                return null;
            }

            @Override
            public String getFlowName() {
                return null;
            }

            @Override
            public StartupType getStartupType() {
                return startupType;
            }

            @Override
            public void setStartupType(StartupType startupType) {

            }

            @Override
            public boolean isAutomatic() {
                return false;
            }

            @Override
            public boolean isManual() {
                return false;
            }

            @Override
            public boolean isDisabled() {
                return false;
            }

            @Override
            public String getComment() {
                return comment;
            }

            @Override
            public void setComment(String comment) {

            }
        };
    }
}
