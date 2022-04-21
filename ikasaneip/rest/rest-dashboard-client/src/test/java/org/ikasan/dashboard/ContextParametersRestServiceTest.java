package org.ikasan.dashboard;

import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.apache.commons.io.IOUtils;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.concurrent.Synchroniser;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

import java.io.IOException;
import java.io.InputStream;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.Assert.assertEquals;

public class ContextParametersRestServiceTest
{
    private Mockery mockery = new Mockery()
    {{
        setImposteriser(ClassImposteriser.INSTANCE);
        setThreadingPolicy(new Synchroniser());
    }};

    Environment environment = mockery.mock(Environment.class);

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(
        WireMockConfiguration.options().dynamicPort()); // No-args constructor defaults to port 8080

    ContextParametersRestServiceImpl uut;

    @Before
    public void setup()
    {
        String dashboardBaseUrl = "http://localhost:" + wireMockRule.port();
        mockery.checking(new Expectations()
        {{
            atLeast(2).of(environment).getProperty(MetricsRestServiceImpl.DASHBOARD_BASE_URL_PROPERTY);
            will(returnValue(dashboardBaseUrl));
            oneOf(environment).getProperty(MetricsRestServiceImpl.DASHBOARD_USERNAME_PROPERTY);
            will(returnValue(null));
            oneOf(environment).getProperty(MetricsRestServiceImpl.DASHBOARD_PASSWORD_PROPERTY);
            will(returnValue(null));
            oneOf(environment).getProperty(MetricsRestServiceImpl.DASHBOARD_REST_USERAGENT);
            will(returnValue("user agent"));
        }});
    }

    @Test
    public void get_all() throws IOException {
        stubFor(get(urlEqualTo("/rest/jobContext/getAll"))
            .withHeader(HttpHeaders.USER_AGENT, equalTo("user agent"))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
            .willReturn(aResponse()
                .withBody(loadDataFile("/data/job-context-parameters-all.json"))
                .withStatus(200)
            ));

        uut = new ContextParametersRestServiceImpl(environment,
                                                   new HttpComponentsClientHttpRequestFactory(),
                                              "/rest/jobContext/getAll");

        assertEquals(2, uut.getAll().size());
    }

    @Test
    public void get_by_contextName() throws IOException {
        stubFor(get(urlEqualTo("/rest/jobContext/getByContextName?contextName=context-instance-1"))
            .withHeader(HttpHeaders.USER_AGENT, equalTo("user agent"))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
            .willReturn(aResponse()
                .withBody(loadDataFile("/data/job-context-parameters-1.json"))
                .withStatus(201)
            ));

        uut = new ContextParametersRestServiceImpl(environment,
                                                   new HttpComponentsClientHttpRequestFactory(),
                                              "/rest/jobContext/getByContextName?contextName=context-instance-1");

        assertEquals(1, uut.getByContextName("context-instance-1").size());
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
