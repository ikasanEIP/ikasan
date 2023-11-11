package org.ikasan.dashboard;

import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import org.apache.commons.io.IOUtils;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.imposters.ByteBuddyClassImposteriser;
import org.jmock.lib.concurrent.Synchroniser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

import java.io.IOException;
import java.io.InputStream;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MetricsRestServiceTest
{
    private Mockery mockery = new Mockery()
    {{
        setImposteriser(ByteBuddyClassImposteriser.INSTANCE);
        setThreadingPolicy(new Synchroniser());
    }};

    Environment environment = mockery.mock(Environment.class);

    @RegisterExtension
    public WireMockExtension wireMockRule = WireMockExtension.newInstance().options(WireMockConfiguration.options().dynamicPort()).build(); // No-args constructor defaults to port 8080

    MetricsRestServiceImpl uut;

    @BeforeEach
    void setup()
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
    void get_metrics_time_period() throws IOException {
        stubFor(get(urlEqualTo("/rest/metrics/0/100000"))
            .withHeader(HttpHeaders.USER_AGENT, equalTo("user agent"))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
            .willReturn(aResponse()
                .withBody(loadDataFile("/data/metrics.json"))
                .withStatus(200)
            ));

        uut = new MetricsRestServiceImpl(environment, new HttpComponentsClientHttpRequestFactory());

        assertEquals(5, uut.getMetrics(0, 100000L).size());
    }

    @Test
    void get_metrics_time_period_expected() throws IOException {
        assertThrows(RuntimeException.class, () -> {
            stubFor(get(urlEqualTo("/rest/metrics/0/100000"))
                .withHeader(HttpHeaders.USER_AGENT, equalTo("user agent"))
                .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
                .willReturn(aResponse()
                    .withBody("bad payload")
                    .withStatus(200)
                ));

            uut = new MetricsRestServiceImpl(environment, new HttpComponentsClientHttpRequestFactory());

            uut.getMetrics(0, 100000L);
        });
    }

    @Test
    void get_metrics_time_period_returns_401_followed_by_authentication_and_successful_get() throws IOException {
        stubFor(get(urlEqualTo("/rest/metrics/0/100000"))
            .withHeader(HttpHeaders.USER_AGENT, equalTo("user agent"))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
            .willReturn(aResponse()
                .withBody(loadDataFile("/data/metrics.json"))
                .withStatus(401)
            ));

        stubFor(post(urlEqualTo("/authenticate"))
            .withHeader(HttpHeaders.USER_AGENT, equalTo("testModule"))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
            .withRequestBody(containing("{\"username\":\"admin\",\"password\":\"admin\"}"))
            .willReturn(aResponse().withBody("{\"token\":\"msamsmsamsmas\"}")
                .withStatus(200)
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString())
            ));

        stubFor(get(urlEqualTo("/rest/metrics/0/100000"))
            .withHeader(HttpHeaders.USER_AGENT, equalTo("user agent"))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
            .willReturn(aResponse()
                .withBody(loadDataFile("/data/metrics.json"))
                .withStatus(200)
            ));

        uut = new MetricsRestServiceImpl(environment, new HttpComponentsClientHttpRequestFactory());

        assertEquals(5, uut.getMetrics(0, 100000L).size());
    }

    @Test
    void get_metrics_time_period_paged() throws IOException {
        stubFor(get(urlEqualTo("/rest/metrics/paged/0/100000/0/100"))
            .withHeader(HttpHeaders.USER_AGENT, equalTo("user agent"))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
            .willReturn(aResponse()
                .withBody(loadDataFile("/data/metrics.json"))
                .withStatus(200)
            ));

        uut = new MetricsRestServiceImpl(environment, new HttpComponentsClientHttpRequestFactory());

        assertEquals(5, uut.getMetrics(0, 100000L, 0, 100).size());
    }

    @Test
    void get_metrics_time_period_paged_exception() throws IOException {
        assertThrows(RuntimeException.class, () -> {
            stubFor(get(urlEqualTo("/rest/metrics/paged/0/100000/0/100"))
                .withHeader(HttpHeaders.USER_AGENT, equalTo("user agent"))
                .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
                .willReturn(aResponse()
                    .withBody("bad payload")
                    .withStatus(200)
                ));

            uut = new MetricsRestServiceImpl(environment, new HttpComponentsClientHttpRequestFactory());

            uut.getMetrics(0, 100000L, 0, 100).size();
        });
    }

    @Test
    void get_metrics_time_period_paged_returns_401_followed_by_authentication_and_successful_get() throws IOException {
        stubFor(get(urlEqualTo("/rest/metrics/paged/0/100000/0/100"))
            .withHeader(HttpHeaders.USER_AGENT, equalTo("user agent"))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
            .willReturn(aResponse()
                .withBody(loadDataFile("/data/metrics.json"))
                .withStatus(401)
            ));


        stubFor(post(urlEqualTo("/authenticate"))
            .withHeader(HttpHeaders.USER_AGENT, equalTo("testModule"))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
            .withRequestBody(containing("{\"username\":\"admin\",\"password\":\"admin\"}"))
            .willReturn(aResponse().withBody("{\"token\":\"msamsmsamsmas\"}")
                .withStatus(200)
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString())
            ));

        stubFor(get(urlEqualTo("/rest/metrics/paged/0/100000/0/100"))
            .withHeader(HttpHeaders.USER_AGENT, equalTo("user agent"))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
            .willReturn(aResponse()
                .withBody(loadDataFile("/data/metrics.json"))
                .withStatus(200)
            ));

        uut = new MetricsRestServiceImpl(environment, new HttpComponentsClientHttpRequestFactory());

        assertEquals(5, uut.getMetrics(0, 100000L, 0, 100).size());
    }

    @Test
    void get_metrics_time_period_count() throws IOException {
        stubFor(get(urlEqualTo("/rest/metrics/count/0/100000"))
            .withHeader(HttpHeaders.USER_AGENT, equalTo("user agent"))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
            .willReturn(aResponse()
                .withBody("5")
                .withStatus(200)
            ));

        uut = new MetricsRestServiceImpl(environment, new HttpComponentsClientHttpRequestFactory());

        assertEquals(5, uut.count(0, 100000L));
    }

    @Test
    void get_metrics_time_period_count_exception() throws IOException {
        assertThrows(RuntimeException.class, () -> {
            stubFor(get(urlEqualTo("/rest/metrics/count/0/100000"))
                .withHeader(HttpHeaders.USER_AGENT, equalTo("user agent"))
                .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
                .willReturn(aResponse()
                    .withBody("bad number")
                    .withStatus(200)
                ));

            uut = new MetricsRestServiceImpl(environment, new HttpComponentsClientHttpRequestFactory());

            uut.count(0, 100000L);
        });
    }

    @Test
    void get_metrics_time_period_count_returns_401_followed_by_authentication_and_successful_get() throws IOException {
        stubFor(get(urlEqualTo("/rest/metrics/count/0/100000"))
            .withHeader(HttpHeaders.USER_AGENT, equalTo("user agent"))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
            .willReturn(aResponse()
                .withBody("5")
                .withStatus(401)
            ));

        stubFor(post(urlEqualTo("/authenticate"))
            .withHeader(HttpHeaders.USER_AGENT, equalTo("testModule"))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
            .withRequestBody(containing("{\"username\":\"admin\",\"password\":\"admin\"}"))
            .willReturn(aResponse().withBody("{\"token\":\"msamsmsamsmas\"}")
                .withStatus(200)
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString())
            ));

        stubFor(get(urlEqualTo("/rest/metrics/count/0/100000"))
            .withHeader(HttpHeaders.USER_AGENT, equalTo("user agent"))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
            .willReturn(aResponse()
                .withBody("5")
                .withStatus(200)
            ));

        uut = new MetricsRestServiceImpl(environment, new HttpComponentsClientHttpRequestFactory());

        assertEquals(5, uut.count(0, 100000L));
    }

    @Test
    void get_metrics_time_period_and_module_name() throws IOException {
        stubFor(get(urlEqualTo("/rest/metrics/moduleName/0/100000"))
            .withHeader(HttpHeaders.USER_AGENT, equalTo("user agent"))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
            .willReturn(aResponse()
                .withBody(loadDataFile("/data/metrics.json"))
                .withStatus(201)
            ));

        uut = new MetricsRestServiceImpl(environment, new HttpComponentsClientHttpRequestFactory());

        assertEquals(5, uut.getMetrics("moduleName",0, 100000L).size());
    }

    @Test
    void get_metrics_time_period_and_module_name_exception() throws IOException {
        assertThrows(RuntimeException.class, () -> {
            stubFor(get(urlEqualTo("/rest/metrics/moduleName/0/100000"))
                .withHeader(HttpHeaders.USER_AGENT, equalTo("user agent"))
                .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
                .willReturn(aResponse()
                    .withBody("bad payload")
                    .withStatus(201)
                ));

            uut = new MetricsRestServiceImpl(environment, new HttpComponentsClientHttpRequestFactory());

            assertEquals(5, uut.getMetrics("moduleName", 0, 100000L).size());
        });
    }

    @Test
    void get_metrics_time_period_and_module_name_returns_401_followed_by_authentication_and_successful_get() throws IOException {
        stubFor(get(urlEqualTo("/rest/metrics/moduleName/0/100000"))
            .withHeader(HttpHeaders.USER_AGENT, equalTo("user agent"))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
            .willReturn(aResponse()
                .withBody(loadDataFile("/data/metrics.json"))
                .withStatus(401)
            ));

        stubFor(post(urlEqualTo("/authenticate"))
            .withHeader(HttpHeaders.USER_AGENT, equalTo("testModule"))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
            .withRequestBody(containing("{\"username\":\"admin\",\"password\":\"admin\"}"))
            .willReturn(aResponse().withBody("{\"token\":\"msamsmsamsmas\"}")
                .withStatus(200)
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString())
            ));

        stubFor(get(urlEqualTo("/rest/metrics/moduleName/0/100000"))
            .withHeader(HttpHeaders.USER_AGENT, equalTo("user agent"))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
            .willReturn(aResponse()
                .withBody(loadDataFile("/data/metrics.json"))
                .withStatus(201)
            ));

        uut = new MetricsRestServiceImpl(environment, new HttpComponentsClientHttpRequestFactory());

        assertEquals(5, uut.getMetrics("moduleName",0, 100000L).size());
    }

    @Test
    void get_metrics_time_period_and_module_name_paged() throws IOException {
        stubFor(get(urlEqualTo("/rest/metrics/paged/moduleName/0/100000/0/100"))
            .withHeader(HttpHeaders.USER_AGENT, equalTo("user agent"))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
            .willReturn(aResponse()
                .withBody(loadDataFile("/data/metrics.json"))
                .withStatus(201)
            ));

        uut = new MetricsRestServiceImpl(environment, new HttpComponentsClientHttpRequestFactory());

        assertEquals(5, uut.getMetrics("moduleName",0, 100000L, 0, 100).size());
    }

    @Test
    void get_metrics_time_period_and_module_name_paged_exception() throws IOException {
        assertThrows(RuntimeException.class, () -> {
            stubFor(get(urlEqualTo("/rest/metrics/paged/moduleName/0/100000/0/100"))
                .withHeader(HttpHeaders.USER_AGENT, equalTo("user agent"))
                .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
                .willReturn(aResponse()
                    .withBody("bad payload")
                    .withStatus(201)
                ));

            uut = new MetricsRestServiceImpl(environment, new HttpComponentsClientHttpRequestFactory());

            uut.getMetrics("moduleName", 0, 100000L, 0, 100);
        });
    }

    @Test
    void get_metrics_time_period_and_module_name_paged_returns_401_followed_by_authentication_and_successful_get() throws IOException {
        stubFor(get(urlEqualTo("/rest/metrics/paged/moduleName/0/100000/0/100"))
            .withHeader(HttpHeaders.USER_AGENT, equalTo("user agent"))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
            .willReturn(aResponse()
                .withBody(loadDataFile("/data/metrics.json"))
                .withStatus(401)
            ));

        stubFor(post(urlEqualTo("/authenticate"))
            .withHeader(HttpHeaders.USER_AGENT, equalTo("testModule"))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
            .withRequestBody(containing("{\"username\":\"admin\",\"password\":\"admin\"}"))
            .willReturn(aResponse().withBody("{\"token\":\"msamsmsamsmas\"}")
                .withStatus(200)
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString())
            ));

        stubFor(get(urlEqualTo("/rest/metrics/paged/moduleName/0/100000/0/100"))
            .withHeader(HttpHeaders.USER_AGENT, equalTo("user agent"))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
            .willReturn(aResponse()
                .withBody(loadDataFile("/data/metrics.json"))
                .withStatus(201)
            ));

        uut = new MetricsRestServiceImpl(environment, new HttpComponentsClientHttpRequestFactory());

        assertEquals(5, uut.getMetrics("moduleName",0, 100000L, 0, 100).size());
    }

    @Test
    void get_metrics_time_period_and_module_name_count() throws IOException {
        stubFor(get(urlEqualTo("/rest/metrics/count/moduleName/0/100000"))
            .withHeader(HttpHeaders.USER_AGENT, equalTo("user agent"))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
            .willReturn(aResponse()
                .withBody("5")
                .withStatus(201)
            ));

        uut = new MetricsRestServiceImpl(environment, new HttpComponentsClientHttpRequestFactory());

        assertEquals(5, uut.count("moduleName",0, 100000L));
    }

    @Test
    void get_metrics_time_period_and_module_name_count_exception() throws IOException {
        assertThrows(RuntimeException.class, () -> {
            stubFor(get(urlEqualTo("/rest/metrics/count/moduleName/0/100000"))
                .withHeader(HttpHeaders.USER_AGENT, equalTo("user agent"))
                .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
                .willReturn(aResponse()
                    .withBody("bad number")
                    .withStatus(201)
                ));

            uut = new MetricsRestServiceImpl(environment, new HttpComponentsClientHttpRequestFactory());

            uut.count("moduleName", 0, 100000L);
        });
    }

    @Test
    void get_metrics_time_period_and_module_name_count_returns_401_followed_by_authentication_and_successful_get() throws IOException {
        stubFor(get(urlEqualTo("/rest/metrics/count/moduleName/0/100000"))
            .withHeader(HttpHeaders.USER_AGENT, equalTo("user agent"))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
            .willReturn(aResponse()
                .withBody("5")
                .withStatus(401)
            ));

        stubFor(post(urlEqualTo("/authenticate"))
            .withHeader(HttpHeaders.USER_AGENT, equalTo("testModule"))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
            .withRequestBody(containing("{\"username\":\"admin\",\"password\":\"admin\"}"))
            .willReturn(aResponse().withBody("{\"token\":\"msamsmsamsmas\"}")
                .withStatus(200)
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString())
            ));

        stubFor(get(urlEqualTo("/rest/metrics/count/moduleName/0/100000"))
            .withHeader(HttpHeaders.USER_AGENT, equalTo("user agent"))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
            .willReturn(aResponse()
                .withBody("5")
                .withStatus(201)
            ));

        uut = new MetricsRestServiceImpl(environment, new HttpComponentsClientHttpRequestFactory());

        assertEquals(5, uut.count("moduleName",0, 100000L));
    }

    @Test
    void get_metrics_time_period_module_name_and_flow_name() throws IOException {
        stubFor(get(urlEqualTo("/rest/metrics/moduleName/flowName/0/100000"))
            .withHeader(HttpHeaders.USER_AGENT, equalTo("user agent"))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
            .willReturn(aResponse()
                .withBody(loadDataFile("/data/metrics.json"))
                .withStatus(201)
            ));

        uut = new MetricsRestServiceImpl(environment, new HttpComponentsClientHttpRequestFactory());

        assertEquals(5, uut.getMetrics("moduleName","flowName",0, 100000L).size());
    }

    @Test
    void get_metrics_time_period_module_name_and_flow_name_exception() throws IOException {
        assertThrows(RuntimeException.class, () -> {
            stubFor(get(urlEqualTo("/rest/metrics/moduleName/flowName/0/100000"))
                .withHeader(HttpHeaders.USER_AGENT, equalTo("user agent"))
                .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
                .willReturn(aResponse()
                    .withBody("bad payload")
                    .withStatus(201)
                ));

            uut = new MetricsRestServiceImpl(environment, new HttpComponentsClientHttpRequestFactory());

            assertEquals(5, uut.getMetrics("moduleName", "flowName", 0, 100000L).size());
        });
    }

    @Test
    void get_metrics_time_period_and_module_name_flow_name_returns_401_followed_by_authentication_and_successful_get() throws IOException {
        stubFor(get(urlEqualTo("/rest/metrics/moduleName/flowName/0/100000"))
            .withHeader(HttpHeaders.USER_AGENT, equalTo("user agent"))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
            .willReturn(aResponse()
                .withBody(loadDataFile("/data/metrics.json"))
                .withStatus(401)
            ));

        stubFor(post(urlEqualTo("/authenticate"))
            .withHeader(HttpHeaders.USER_AGENT, equalTo("testModule"))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
            .withRequestBody(containing("{\"username\":\"admin\",\"password\":\"admin\"}"))
            .willReturn(aResponse().withBody("{\"token\":\"msamsmsamsmas\"}")
                .withStatus(200)
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString())
            ));

        stubFor(get(urlEqualTo("/rest/metrics/moduleName/flowName/0/100000"))
            .withHeader(HttpHeaders.USER_AGENT, equalTo("user agent"))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
            .willReturn(aResponse()
                .withBody(loadDataFile("/data/metrics.json"))
                .withStatus(201)
            ));

        uut = new MetricsRestServiceImpl(environment, new HttpComponentsClientHttpRequestFactory());

        assertEquals(5, uut.getMetrics("moduleName","flowName",0, 100000L).size());
    }

    @Test
    void get_metrics_time_period_module_name_and_flow_name_paged() throws IOException {
        stubFor(get(urlEqualTo("/rest/metrics/paged/moduleName/flowName/0/100000/0/100"))
            .withHeader(HttpHeaders.USER_AGENT, equalTo("user agent"))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
            .willReturn(aResponse()
                .withBody(loadDataFile("/data/metrics.json"))
                .withStatus(201)
            ));

        uut = new MetricsRestServiceImpl(environment, new HttpComponentsClientHttpRequestFactory());

        assertEquals(5, uut.getMetrics("moduleName","flowName",0, 100000L, 0, 100).size());
    }

    @Test
    void get_metrics_time_period_module_name_and_flow_name_paged_exception() throws IOException {
        assertThrows(RuntimeException.class, () -> {
            stubFor(get(urlEqualTo("/rest/metrics/paged/moduleName/flowName/0/100000/0/100"))
                .withHeader(HttpHeaders.USER_AGENT, equalTo("user agent"))
                .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
                .willReturn(aResponse()
                    .withBody("bad payload")
                    .withStatus(201)
                ));

            uut = new MetricsRestServiceImpl(environment, new HttpComponentsClientHttpRequestFactory());

            uut.getMetrics("moduleName", "flowName", 0, 100000L, 0, 100);
        });
    }

    @Test
    void get_metrics_time_period_and_module_name_flow_name_paged_returns_401_followed_by_authentication_and_successful_get() throws IOException {
        stubFor(get(urlEqualTo("/rest/metrics/paged/moduleName/flowName/0/100000/0/100"))
            .withHeader(HttpHeaders.USER_AGENT, equalTo("user agent"))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
            .willReturn(aResponse()
                .withBody(loadDataFile("/data/metrics.json"))
                .withStatus(201)
            ));

        stubFor(post(urlEqualTo("/authenticate"))
            .withHeader(HttpHeaders.USER_AGENT, equalTo("testModule"))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
            .withRequestBody(containing("{\"username\":\"admin\",\"password\":\"admin\"}"))
            .willReturn(aResponse().withBody("{\"token\":\"msamsmsamsmas\"}")
                .withStatus(200)
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString())
            ));

        stubFor(get(urlEqualTo("/rest/metrics/paged/moduleName/flowName/0/100000/0/100"))
            .withHeader(HttpHeaders.USER_AGENT, equalTo("user agent"))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
            .willReturn(aResponse()
                .withBody(loadDataFile("/data/metrics.json"))
                .withStatus(201)
            ));

        uut = new MetricsRestServiceImpl(environment, new HttpComponentsClientHttpRequestFactory());

        assertEquals(5, uut.getMetrics("moduleName","flowName",0, 100000L, 0, 100).size());
    }

    @Test
    void get_metrics_time_period_module_name_and_flow_name_count() throws IOException {
        stubFor(get(urlEqualTo("/rest/metrics/count/moduleName/flowName/0/100000"))
            .withHeader(HttpHeaders.USER_AGENT, equalTo("user agent"))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
            .willReturn(aResponse()
                .withBody("5")
                .withStatus(201)
            ));

        uut = new MetricsRestServiceImpl(environment, new HttpComponentsClientHttpRequestFactory());

        assertEquals(5, uut.count("moduleName","flowName",0, 100000L));
    }

    @Test
    void get_metrics_time_period_module_name_and_flow_name_count_exception() throws IOException {
        assertThrows(RuntimeException.class, () -> {
            stubFor(get(urlEqualTo("/rest/metrics/count/moduleName/flowName/0/100000"))
                .withHeader(HttpHeaders.USER_AGENT, equalTo("user agent"))
                .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
                .willReturn(aResponse()
                    .withBody("bad number")
                    .withStatus(201)
                ));

            uut = new MetricsRestServiceImpl(environment, new HttpComponentsClientHttpRequestFactory());

            uut.count("moduleName", "flowName", 0, 100000L);
        });
    }

    @Test
    void get_metrics_time_period_and_module_name_flow_name_count_returns_401_followed_by_authentication_and_successful_get() throws IOException {
        stubFor(get(urlEqualTo("/rest/metrics/count/moduleName/flowName/0/100000"))
            .withHeader(HttpHeaders.USER_AGENT, equalTo("user agent"))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
            .willReturn(aResponse()
                .withBody("5")
                .withStatus(401)
            ));

        stubFor(post(urlEqualTo("/authenticate"))
            .withHeader(HttpHeaders.USER_AGENT, equalTo("testModule"))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
            .withRequestBody(containing("{\"username\":\"admin\",\"password\":\"admin\"}"))
            .willReturn(aResponse().withBody("{\"token\":\"msamsmsamsmas\"}")
                .withStatus(200)
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString())
            ));

        stubFor(get(urlEqualTo("/rest/metrics/count/moduleName/flowName/0/100000"))
            .withHeader(HttpHeaders.USER_AGENT, equalTo("user agent"))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
            .willReturn(aResponse()
                .withBody("5")
                .withStatus(201)
            ));

        uut = new MetricsRestServiceImpl(environment, new HttpComponentsClientHttpRequestFactory());

        assertEquals(5, uut.count("moduleName","flowName",0, 100000L));
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
