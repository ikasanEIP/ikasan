package org.ikasan.rest.client;

import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.apache.commons.io.IOUtils;
import org.ikasan.configurationService.metadata.JsonConfigurationMetaDataProvider;
import org.ikasan.spec.metadata.ConfigurationMetaData;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.core.env.Environment;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestConfiguration.class })
public class ConfigurationRestServiceImplTest
{
    public static final String CONFIGURATION_METADATA_JSON = "/data/configuration.json";

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(WireMockConfiguration.options().dynamicPort());

    private ConfigurationRestServiceImpl uut;

    private String contexBaseUrl;

    @Resource
    private JsonConfigurationMetaDataProvider jsonConfigurationMetaDataProvider;

    @Before
    public void setup()
    {
        contexBaseUrl = "http://localhost:" + wireMockRule.port();
        Environment environment = new StandardEnvironment();
        uut = new ConfigurationRestServiceImpl(environment, jsonConfigurationMetaDataProvider);
    }

    @Test
    public void getComponents_returns_200() throws IOException
    {

        stubFor(get(urlEqualTo(ConfigurationRestServiceImpl.COMPONENTS_CONFIGURATION_URL))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
            .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON.toString())).willReturn(
                aResponse().withBody(loadDataFile(CONFIGURATION_METADATA_JSON))
                           .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString())
                           .withStatus(200)));
        List<ConfigurationMetaData> result = uut.getComponents(contexBaseUrl);
        assertEquals(2, result.size());
        assertEquals("consumerConfiguredResourceId", result.get(0).getConfigurationId());
        assertEquals("producerConfiguredResourceId", result.get(1).getConfigurationId());

    }

    @Test
    public void getComponents_returns_400() throws IOException
    {

        stubFor(get(urlEqualTo(ConfigurationRestServiceImpl.COMPONENTS_CONFIGURATION_URL))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
            .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON.toString())).willReturn(
                aResponse().withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString())
                           .withStatus(400)));
        List<ConfigurationMetaData> result = uut.getComponents(contexBaseUrl);
        assertEquals(0, result.size());

    }

    @Test
    public void getInvokers_returns_200() throws IOException
    {

        stubFor(get(urlEqualTo(ConfigurationRestServiceImpl.INVOKERS_CONFIGURATION_URL))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
            .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON.toString())).willReturn(
                aResponse().withBody(loadDataFile(CONFIGURATION_METADATA_JSON))
                           .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString())
                           .withStatus(200)));
        List<ConfigurationMetaData> result = uut.getInvokers(contexBaseUrl);
        assertEquals(2, result.size());
        assertEquals("consumerConfiguredResourceId", result.get(0).getConfigurationId());
        assertEquals("producerConfiguredResourceId", result.get(1).getConfigurationId());

    }

    @Test
    public void getInvokers_returns_400() throws IOException
    {

        stubFor(get(urlEqualTo(ConfigurationRestServiceImpl.INVOKERS_CONFIGURATION_URL))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
            .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON.toString())).willReturn(
                aResponse()
                           .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString())
                           .withStatus(400)));
        List<ConfigurationMetaData> result = uut.getInvokers(contexBaseUrl);
        assertEquals(0, result.size());

    }

    @Test
    public void getFlowComponents_returns_200() throws IOException
    {

        stubFor(get(urlEqualTo("/rest/configuration/test%20Module/testFlow/components"))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
            .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON.toString())).willReturn(
                aResponse().withBody(loadDataFile(CONFIGURATION_METADATA_JSON))
                           .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString())
                           .withStatus(200)));
        List<ConfigurationMetaData> result = uut.getFlowComponents(contexBaseUrl,"test Module","testFlow");
        assertEquals(2, result.size());
        assertEquals("consumerConfiguredResourceId", result.get(0).getConfigurationId());
        assertEquals("producerConfiguredResourceId", result.get(1).getConfigurationId());

    }

    @Test
    public void getFlowComponents_returns_400() throws IOException
    {

        stubFor(get(urlEqualTo("/rest/configuration/test%20Module/testFlow/components"))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
            .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON.toString())).willReturn(
                aResponse()
                           .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString())
                           .withStatus(400)));
        List<ConfigurationMetaData> result = uut.getFlowComponents(contexBaseUrl,"test Module","testFlow");
        assertEquals(0, result.size());

    }

    @Test
    public void getFlowInvokers_returns_200() throws IOException
    {

        stubFor(get(urlEqualTo("/rest/configuration/test%20Module/testFlow/invokers"))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
            .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON.toString())).willReturn(
                aResponse().withBody(loadDataFile(CONFIGURATION_METADATA_JSON))
                           .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString())
                           .withStatus(200)));
        List<ConfigurationMetaData> result = uut.getFlowInvokers(contexBaseUrl,"test Module","testFlow");
        assertEquals(2, result.size());
        assertEquals("consumerConfiguredResourceId", result.get(0).getConfigurationId());
        assertEquals("producerConfiguredResourceId", result.get(1).getConfigurationId());

    }

    @Test
    public void getFlowConfiguration_returns_200() throws IOException
    {

        stubFor(get(urlEqualTo("/rest/configuration/test%20Module/testFlow/flow"))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
            .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON.toString())).willReturn(
                aResponse().withBody(loadDataFile(CONFIGURATION_METADATA_JSON))
                           .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString())
                           .withStatus(200)));
        List<ConfigurationMetaData> result = uut.getFlowConfigurations(contexBaseUrl,"test Module","testFlow");
        assertEquals(2, result.size());
        assertEquals("consumerConfiguredResourceId", result.get(0).getConfigurationId());
        assertEquals("producerConfiguredResourceId", result.get(1).getConfigurationId());

    }


    protected InputStream loadDataFileStream(String fileName) throws IOException
    {
        return getClass().getResourceAsStream(fileName);
    }

    protected String loadDataFile(String fileName) throws IOException
    {
        String contentToSend = IOUtils.toString(loadDataFileStream(fileName), "UTF-8");

        return contentToSend;
    }
}
