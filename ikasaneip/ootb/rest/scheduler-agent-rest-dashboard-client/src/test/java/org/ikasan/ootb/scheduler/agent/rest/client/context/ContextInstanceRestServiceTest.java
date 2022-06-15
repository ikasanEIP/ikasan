package org.ikasan.ootb.scheduler.agent.rest.client.context;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.ikasan.spec.component.endpoint.EndpointException;
import org.ikasan.spec.dashboard.DashboardRestService;
import org.ikasan.spec.scheduled.instance.model.ContextInstance;
import org.ikasan.spec.scheduled.instance.model.ContextParameterInstance;
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

import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit.WireMockRule;

public class ContextInstanceRestServiceTest {

    private static final String MODULE_NAME = "module name";
    private Mockery mockery = new Mockery() {{
        setImposteriser(ClassImposteriser.INSTANCE);
        setThreadingPolicy(new Synchroniser());
    }};

    Environment environment = mockery.mock(Environment.class);

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(
        WireMockConfiguration.options().dynamicPort()); // No-args constructor defaults to port 8080

    ContextInstanceRestServiceImpl uut;

    @Before
    public void setup() {
        String dashboardBaseUrl = "http://localhost:" + wireMockRule.port();

        mockery.checking(new Expectations() {{
            atLeast(2).of(environment).getProperty(DashboardRestService.DASHBOARD_BASE_URL_PROPERTY);
            will(returnValue(dashboardBaseUrl));
            oneOf(environment).getProperty(DashboardRestService.DASHBOARD_USERNAME_PROPERTY);
            will(returnValue(null));
            oneOf(environment).getProperty(DashboardRestService.DASHBOARD_PASSWORD_PROPERTY);
            will(returnValue(null));
            oneOf(environment).getProperty(DashboardRestService.MODULE_NAME_PROPERTY);
            will(returnValue(MODULE_NAME));
        }});

        uut = new ContextInstanceRestServiceImpl(environment, new HttpComponentsClientHttpRequestFactory(), "/rest/jobContext");
    }

    @Test
    public void get_all_contexts() throws IOException {
        stubFor(get(urlEqualTo("/rest/jobContext/getAll"))
            .withHeader(HttpHeaders.USER_AGENT, equalTo(MODULE_NAME))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
            .willReturn(aResponse()
                .withBody(loadDataFile("/data/context/job-context-instance-all.json"))
                .withStatus(200)
            ));

        Map<String, ContextInstance> all = uut.getAll();
        assertEquals(2, all.size());

        ContextInstance contextInstance = all.get("COMPLEX_CONTEXT-1");
        List<ContextParameterInstance> params = contextInstance.getContextParameters();
        assertEquals(3, params.size());
        contextInstance = all.get("COMPLEX_CONTEXT-2");
        params = contextInstance.getContextParameters();
        assertEquals(3, params.size());
    }

    @Test(expected = EndpointException.class)
    public void get_all_contexts_returns_error() {
        stubFor(get(urlEqualTo("/rest/jobContext/getAll"))
            .withHeader(HttpHeaders.USER_AGENT, equalTo(MODULE_NAME))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
            .willReturn(aResponse()
                .withBody("{500 Error}")
                .withStatus(500)
            ));

        uut.getAll();
    }

    @Test
    public void get_context() throws IOException {
        stubFor(get(urlEqualTo("/rest/jobContext/getByContextName?contextName=COMPLEX_CONTEXT"))
            .withHeader(HttpHeaders.USER_AGENT, equalTo(MODULE_NAME))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
            .willReturn(aResponse()
                .withBody(loadDataFile("/data/context/job-context-instance.json"))
                .withStatus(200)
            ));

        Map<String, ContextInstance> complex_context = uut.getByContextName("COMPLEX_CONTEXT");
        assertEquals(1, complex_context.size());

        ContextInstance contextInstance = complex_context.get("COMPLEX_CONTEXT");
        List<ContextParameterInstance> params = contextInstance.getContextParameters();
        assertEquals(3, params.size());
    }

    @Test(expected = EndpointException.class)
    public void get_context_with_error() {
        stubFor(get(urlEqualTo("/rest/jobContext/getByContextName?contextName=COMPLEX_CONTEXT"))
            .withHeader(HttpHeaders.USER_AGENT, equalTo(MODULE_NAME))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
            .willReturn(aResponse()
                .withBody("{403 Error}")
                .withStatus(403)
            ));

        uut.getByContextName("COMPLEX_CONTEXT");
    }

    protected String loadDataFile(String fileName) throws IOException {
        String contentToSend = IOUtils.toString(loadDataFileStream(fileName), "UTF-8");

        return contentToSend;
    }

    protected InputStream loadDataFileStream(String fileName) throws IOException {
        return getClass().getResourceAsStream(fileName);
    }

}
