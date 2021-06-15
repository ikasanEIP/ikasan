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

public class MetricsRestServiceTest
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

    MetricsRestServiceImpl uut;

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
    public void get_metrics_time_period() throws IOException {
        stubFor(get(urlEqualTo("/rest/metrics/0/100000"))
            .withHeader(HttpHeaders.USER_AGENT, equalTo("user agent"))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
            .willReturn(aResponse()
                .withBody(loadDataFile("/data/metrics.json"))
                .withStatus(200)
            ));

        uut = new MetricsRestServiceImpl(environment, new HttpComponentsClientHttpRequestFactory()
            , "/rest/metrics/{startTime}/{endTime}");

        assertEquals(5, uut.getMetrics(0, 100000L).size());
    }

    @Test
    public void get_metrics_time_period_and_module_name() throws IOException {
        stubFor(get(urlEqualTo("/rest/metrics/moduleName/0/100000"))
            .withHeader(HttpHeaders.USER_AGENT, equalTo("user agent"))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
            .willReturn(aResponse()
                .withBody(loadDataFile("/data/metrics.json"))
                .withStatus(201)
            ));

        uut = new MetricsRestServiceImpl(environment, new HttpComponentsClientHttpRequestFactory()
            , "/rest/metrics/{moduleName}/{startTime}/{endTime}");

        assertEquals(5, uut.getMetrics("moduleName",0, 100000L).size());
    }

    @Test
    public void get_metrics_time_period_module_name_and_flow_name() throws IOException {
        stubFor(get(urlEqualTo("/rest/metrics/moduleName/flowName/0/100000"))
            .withHeader(HttpHeaders.USER_AGENT, equalTo("user agent"))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
            .willReturn(aResponse()
                .withBody(loadDataFile("/data/metrics.json"))
                .withStatus(201)
            ));

        uut = new MetricsRestServiceImpl(environment, new HttpComponentsClientHttpRequestFactory()
            , "/rest/metrics/{moduleName}/{flowName}/{startTime}/{endTime}");

        assertEquals(5, uut.getMetrics("moduleName","flowName",0, 100000L).size());
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
