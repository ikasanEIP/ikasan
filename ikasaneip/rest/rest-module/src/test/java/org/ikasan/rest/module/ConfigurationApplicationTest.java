package org.ikasan.rest.module;

import org.apache.commons.io.IOUtils;
import org.hamcrest.core.IsInstanceOf;
import org.ikasan.configurationService.metadata.ConfigurationMetaDataImpl;
import org.ikasan.configurationService.model.DefaultConfiguration;
import org.ikasan.module.ConfiguredModuleConfiguration;
import org.ikasan.module.ConfiguredModuleImpl;
import org.ikasan.module.SimpleModule;
import org.ikasan.rest.module.model.TestFlow;
import org.ikasan.rest.module.model.TestFlowConfiguration;
import org.ikasan.rest.module.model.TestFlowElement;
import org.ikasan.spec.configuration.Configuration;
import org.ikasan.spec.configuration.ConfigurationManagement;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.flow.FlowConfiguration;
import org.ikasan.spec.flow.FlowFactory;
import org.ikasan.spec.metadata.ConfigurationMetaData;
import org.ikasan.spec.metadata.ConfigurationMetaDataExtractor;
import org.ikasan.spec.module.ModuleService;
import org.ikasan.spec.systemevent.SystemEventService;
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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = { ConfigurationApplication.class, MockedUserServiceTestConfig.class })
@EnableWebMvc
public class ConfigurationApplicationTest
{
    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    protected MockMvc mockMvc;

    @Autowired
    protected WebApplicationContext webApplicationContext;

    @Autowired
    protected ConfigurationApplication configurationApplication;

    @MockBean
    protected ModuleService moduleService;

    @MockBean
    protected SystemEventService systemEventService;

    @MockBean
    protected FlowFactory flowFactory;

    @MockBean
    protected ConfigurationManagement<ConfiguredResource, Configuration> configurationManagement;

    @MockBean
    private ConfigurationMetaDataExtractor<ConfigurationMetaData> configurationMetaDataExtractor;

    @Before
    public void setUp()
    {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    @WithMockUser(authorities = "readonly")
    public void findConfigurationsWithReadOnlyUser() throws Exception
    {
        exceptionRule.expect(new ThrowableCauseMatcher(new IsInstanceOf(AccessDeniedException.class)));
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/rest/configuration/flows")
                                                              .accept(MediaType.APPLICATION_JSON_VALUE);
        mockMvc.perform(requestBuilder).andReturn();
    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    public void testFindFlows() throws Exception
    {
        FlowConfiguration flowConfiguration = new TestFlowConfiguration(
            new TestFlowElement(new Object(), "test", null, new Object()));
        Flow flow = new TestFlow("test Flow", "testModule", "stopped", flowConfiguration);
        SimpleModule module = new SimpleModule("testModule", null, Arrays.asList(flow));
        Mockito.when(moduleService.getModules()).thenReturn(Arrays.asList(module));

        ConfigurationMetaData configurationMetaData = new ConfigurationMetaDataImpl("test_id", "test descript",
            "TestClass", new ArrayList<>()
        );

        Mockito.when(configurationMetaDataExtractor.getFlowsConfiguration(module))
               .thenReturn(Arrays.asList(configurationMetaData));

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/rest/configuration/flows")
                                                              .accept(MediaType.APPLICATION_JSON_VALUE);
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        assertEquals(200, result.getResponse().getStatus());
        JSONAssert.assertEquals("JSON Result must equal!",
            """
            [{"configurationId":"test_id","description":"test descript",\
            "implementingClass":"TestClass",\
            "parameters":[]}]\
            """,
            result.getResponse().getContentAsString(), JSONCompareMode.LENIENT
                               );

        Mockito.verify(moduleService).getModules();
        Mockito.verify(configurationMetaDataExtractor).getFlowsConfiguration(module);
        Mockito.verifyNoMoreInteractions(moduleService, configurationMetaDataExtractor);

    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    public void testFindModule() throws Exception
    {
        ConfiguredModuleImpl module = new ConfiguredModuleImpl("testModule", flowFactory);
        ConfiguredModuleConfiguration configuredModuleConfiguration = new ConfiguredModuleConfiguration();
        module.setConfiguration(configuredModuleConfiguration);
        module.setConfiguredResourceId("id");

        Mockito.when(moduleService.getModules()).thenReturn(Arrays.asList(module));

        ConfigurationMetaData configurationMetaData = new ConfigurationMetaDataImpl("test_id", "test descript",
            "TestClass", new ArrayList<>()
        );

        Mockito.when(configurationMetaDataExtractor.getConfiguration(module))
            .thenReturn(configurationMetaData);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/rest/configuration/module")
            .accept(MediaType.APPLICATION_JSON_VALUE);
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        assertEquals(200, result.getResponse().getStatus());
        JSONAssert.assertEquals("JSON Result must equal!",
            """
            {"configurationId":"test_id","description":"test descript",\
            "implementingClass":"TestClass",\
            "parameters":[]}\
            """,
            result.getResponse().getContentAsString(), JSONCompareMode.LENIENT
        );

        Mockito.verify(moduleService).getModules();
        Mockito.verify(configurationMetaDataExtractor).getConfiguration(module);
        Mockito.verifyNoMoreInteractions(moduleService, configurationMetaDataExtractor);

    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    public void testFindSingleFlows() throws Exception
    {
        FlowConfiguration flowConfiguration = new TestFlowConfiguration(
            new TestFlowElement(new Object(), "test", "desc", new Object()));
        Flow flow = new TestFlow("test Flow", "testModule", "stopped", flowConfiguration);
        SimpleModule module = new SimpleModule("testModule", null, Arrays.asList(flow));
        Mockito.when(moduleService.getModule("test-module")).thenReturn(module);

        ConfigurationMetaData configurationMetaData = new ConfigurationMetaDataImpl("test_id", "test descript",
            "TestClass", new ArrayList<>()
        );

        Mockito.when(configurationMetaDataExtractor.getFlowConfiguration(flow)).thenReturn(configurationMetaData);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/rest/configuration/test-module/test Flow/flow")
                                                              .accept(MediaType.APPLICATION_JSON_VALUE);
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        assertEquals(200, result.getResponse().getStatus());
        JSONAssert.assertEquals("JSON Result must equal!",
            """
            {"configurationId":"test_id","description":"test descript",\
            "implementingClass":"TestClass",\
            "parameters":[]}\
            """,
            result.getResponse().getContentAsString(), JSONCompareMode.LENIENT
                               );

        Mockito.verify(moduleService).getModule("test-module");
        Mockito.verifyNoMoreInteractions(moduleService);

    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    public void testPutSingleConfiguration() throws Exception
    {
        MvcResult mvcResult = mockMvc.perform(
            MockMvcRequestBuilders.put("/rest/configuration").contentType(MediaType.APPLICATION_JSON_VALUE)
                                  .content(loadDataFile("/data/componentConfig.json"))).andReturn();

        Mockito.when(configurationManagement.getConfiguration("consumerConfiguredResourceId"))
               .thenReturn(null);


        int status = mvcResult.getResponse().getStatus();
        assertEquals(HttpStatus.OK.value(), status);

        Mockito.verify(configurationManagement).saveConfiguration(Mockito.any(Configuration.class));
        Mockito.verify(configurationManagement).getConfiguration("consumerConfiguredResourceId");
        Mockito.verify(systemEventService).logSystemEvent(
            Mockito.eq("consumerConfiguredResourceId"),
            Mockito.anyString(),
            Mockito.anyString()
            );
        Mockito.verifyNoMoreInteractions(configurationManagement,systemEventService);
    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    public void testDeleteConfiguration() throws Exception
    {
        Configuration configuration = new DefaultConfiguration("testConfigId");
        Mockito.when(configurationManagement.getConfiguration("testConfigId")).thenReturn(configuration);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.delete("/rest/configuration/testConfigId")
                                                                    .contentType(MediaType.APPLICATION_JSON_VALUE))
                                     .andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(HttpStatus.OK.value(), status);

        Mockito.verify(configurationManagement).getConfiguration("testConfigId");
        Mockito.verify(configurationManagement).deleteConfiguration(configuration);
        Mockito.verify(systemEventService).logSystemEvent(
            Mockito.eq("testConfigId"),
            Mockito.anyString(),
            Mockito.anyString()
                                                         );
        Mockito.verifyNoMoreInteractions(configurationManagement,systemEventService);
    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    public void testDeleteConfigurationWhenConfigurationIdNotFound() throws Exception
    {
        Mockito.when(configurationManagement.getConfiguration("testConfigId")).thenReturn(null);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.delete("/rest/configuration/testConfigId")
                                                                    .contentType(MediaType.APPLICATION_JSON_VALUE))
                                     .andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(HttpStatus.NOT_FOUND.value(), status);

        Mockito.verify(configurationManagement).getConfiguration("testConfigId");
        Mockito.verifyNoMoreInteractions(configurationManagement);

    }

    protected String loadDataFile(String fileName) throws IOException
    {
        String contentToSend = IOUtils.toString(loadDataFileStream(fileName), "UTF-8");

        return contentToSend;
    }

    protected InputStream loadDataFileStream(String fileName) throws IOException
    {
        return getClass().getResourceAsStream(fileName);
    }

}
