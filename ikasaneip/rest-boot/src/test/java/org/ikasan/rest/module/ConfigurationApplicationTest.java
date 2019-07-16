package org.ikasan.rest.module;

import org.ikasan.spec.configuration.Configuration;
import org.ikasan.spec.configuration.ConfigurationManagement;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.metadata.ConfigurationMetaDataExtractor;
import org.ikasan.spec.module.Module;
import org.ikasan.spec.module.ModuleService;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = { ConfigurationApplicationConfiguration.class,ConfigurationApplication.class})
@WebMvcTest(value = ConfigurationApplication.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ConfigurationApplicationTest
{


    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ConfigurationManagement<ConfiguredResource, Configuration> configurationManagement;
    @Autowired
    private ConfigurationMetaDataExtractor<String> configurationMetaDataExtractor;
    @Autowired
    private ModuleService moduleService;

    @Autowired
    private Mockery mockery;

    @Test
    public void test_get_flow_by_name() throws Exception
    {
        Module module = mockery.mock(Module.class);
        Flow flow = mockery.mock(Flow.class);
        String expectedJson = "{ expected: value}";
        mockery.checking(new Expectations()
        {{
            oneOf(moduleService).getModule("my-module-name");
            will(returnValue(module));
            oneOf(module).getFlow("my flow");
            will(returnValue(flow));
            oneOf(configurationMetaDataExtractor).getFlowConfiguration(flow);
            will(returnValue(expectedJson));

        }});

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get(
            "/rest/configuration/my-module-name/my flow/flow").accept(
            MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        JSONAssert.assertEquals(expectedJson, result.getResponse()
            .getContentAsString(), false);
    }

    @Test
    public void test_get_flows() throws Exception
    {
        Module module = mockery.mock(Module.class);
        String expectedJson = "{ expected: value}";
        mockery.checking(new Expectations()
        {{
            oneOf(moduleService).getModules();
            will(returnValue(Arrays.asList(module)));
            oneOf(configurationMetaDataExtractor).getFlowsConfiguration(module);
            will(returnValue(expectedJson));

        }});

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get(
            "/rest/configuration/flows").accept(
            MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        JSONAssert.assertEquals(expectedJson, result.getResponse()
            .getContentAsString(), false);
    }
    @Test
    public void test_get_components_by_flow() throws Exception
    {
        Module module = mockery.mock(Module.class);
        Flow flow = mockery.mock(Flow.class);
        String expectedJson = "{ expected: value}";
        mockery.checking(new Expectations()
        {{
            oneOf(moduleService).getModule("my-module-name");
            will(returnValue(module));
            oneOf(module).getFlow("my flow");
            will(returnValue(flow));
            oneOf(configurationMetaDataExtractor).getComponentsConfiguration(flow);
            will(returnValue(expectedJson));

        }});

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get(
            "/rest/configuration/my-module-name/my flow/components").accept(
            MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        JSONAssert.assertEquals(expectedJson, result.getResponse()
            .getContentAsString(), false);
    }

    @Test
    public void test_get_components() throws Exception
    {
        Module module = mockery.mock(Module.class);
        String expectedJson = "{ expected: value}";
        mockery.checking(new Expectations()
        {{
            oneOf(moduleService).getModules();
            will(returnValue(Arrays.asList(module)));
            oneOf(configurationMetaDataExtractor).getComponentsConfiguration(module);
            will(returnValue(expectedJson));

        }});

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get(
            "/rest/configuration/components").accept(
            MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        JSONAssert.assertEquals(expectedJson, result.getResponse()
            .getContentAsString(), false);
    }

    @Test
    public void test_get_invokers_by_flow() throws Exception
    {
        Module module = mockery.mock(Module.class);
        Flow flow = mockery.mock(Flow.class);
        String expectedJson = "{ expected: value}";
        mockery.checking(new Expectations()
        {{
            oneOf(moduleService).getModule("my-module-name");
            will(returnValue(module));
            oneOf(module).getFlow("my flow");
            will(returnValue(flow));
            oneOf(configurationMetaDataExtractor).getInvokersConfiguration(flow);
            will(returnValue(expectedJson));

        }});

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get(
            "/rest/configuration/my-module-name/my flow/invokers").accept(
            MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        JSONAssert.assertEquals(expectedJson, result.getResponse()
            .getContentAsString(), false);
    }

    @Test
    public void test_get_invokers() throws Exception
    {
        Module module = mockery.mock(Module.class);
        String expectedJson = "{ expected: value}";
        mockery.checking(new Expectations()
        {{
            oneOf(moduleService).getModules();
            will(returnValue(Arrays.asList(module)));
            oneOf(configurationMetaDataExtractor).getInvokersConfiguration(module);
            will(returnValue(expectedJson));

        }});

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get(
            "/rest/configuration/invokers").accept(
            MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        JSONAssert.assertEquals(expectedJson, result.getResponse()
            .getContentAsString(), false);
    }


}
