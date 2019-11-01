package org.ikasan.rest.module;

import org.apache.commons.io.IOUtils;
import org.hamcrest.core.IsInstanceOf;
import org.ikasan.filter.duplicate.model.DefaultFilterEntry;
import org.ikasan.filter.duplicate.service.ManagementFilterService;
import org.ikasan.model.ArrayListPagedSearchResult;
import org.ikasan.module.SimpleModule;
import org.ikasan.rest.module.model.TestFlow;
import org.ikasan.rest.module.model.TestFlowConfiguration;
import org.ikasan.rest.module.model.TestFlowElement;
import org.ikasan.rest.module.util.DateTimeConverter;
import org.ikasan.spec.component.endpoint.Consumer;
import org.ikasan.spec.configuration.Configuration;
import org.ikasan.spec.configuration.ConfigurationManagement;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.flow.FlowConfiguration;
import org.ikasan.spec.flow.FlowElement;
import org.ikasan.spec.hospital.service.HospitalService;
import org.ikasan.spec.metadata.ConfigurationMetaDataExtractor;
import org.ikasan.spec.metadata.ConfigurationMetaDataProvider;
import org.ikasan.spec.module.ModuleService;
import org.junit.Before;
import org.junit.Ignore;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Date;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = { ConfigurationApplication.class, MockedUserServiceTestConfig.class
//    TestConfiguration.class
})
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
    private ConfigurationManagement<ConfiguredResource, Configuration> configurationManagement;

    @MockBean
    private ConfigurationMetaDataExtractor<String> configurationMetaDataExtractor;

    @MockBean
    private ConfigurationMetaDataProvider<String> configurationMetaDataProvider;


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
        FlowConfiguration flowConfiguration = new TestFlowConfiguration(new TestFlowElement(new Object(),"test",null,new Object()));
        Flow flow = new TestFlow("test Flow", "testModule", "stopped",flowConfiguration);
        SimpleModule module = new SimpleModule("testModule", null, Arrays.asList(flow));
        Mockito
            .when(moduleService.getModules())
            .thenReturn(Arrays.asList(module));

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/rest/configuration/flows")
            .accept(MediaType.APPLICATION_JSON_VALUE);
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        assertEquals(200, result.getResponse().getStatus());

        Mockito
            .verify(moduleService).getModules();
        Mockito.verifyNoMoreInteractions(moduleService);

    }


    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    public void testFindSingleFlows() throws Exception
    {
        FlowConfiguration flowConfiguration = new TestFlowConfiguration(new TestFlowElement(new Object(),"test","desc",new Object()));
        Flow flow = new TestFlow("test Flow", "testModule", "stopped", flowConfiguration);
        SimpleModule module = new SimpleModule("testModule", null, Arrays.asList(flow));
        Mockito
            .when(moduleService.getModule("test-module"))
            .thenReturn(module);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/rest/configuration//test-module/test Flow/flow")
                                                              .accept(MediaType.APPLICATION_JSON_VALUE);
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        assertEquals(200, result.getResponse().getStatus());

        Mockito
            .verify(moduleService).getModule("test-module");
        Mockito.verifyNoMoreInteractions(moduleService);


    }

    @Ignore
    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    public void testPutSingleConfiguration() throws Exception
    {

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.put("/rest/configuration")
                                                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                                                    .content(loadDataFile("/data/componentConfig.json"))).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(HttpStatus.OK.value(), status);



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
