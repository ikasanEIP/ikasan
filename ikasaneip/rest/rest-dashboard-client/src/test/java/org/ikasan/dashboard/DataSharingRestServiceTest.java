package org.ikasan.dashboard;

import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.apache.commons.io.IOUtils;
import org.ikasan.spec.error.reporting.ErrorOccurrence;
import org.ikasan.spec.exclusion.ExclusionEvent;
import org.ikasan.spec.flow.FlowState;
import org.ikasan.spec.metadata.model.ConfigurationMetaData;
import org.ikasan.spec.metadata.model.ModuleMetaData;
import org.ikasan.spec.replay.ReplayEvent;
import org.ikasan.spec.search.SearchResults;
import org.ikasan.spec.wiretap.WiretapEvent;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.imposters.ByteBuddyClassImposteriser;
import org.jmock.lib.concurrent.Synchroniser;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class DataSharingRestServiceTest {

    private Mockery mockery = new Mockery()
    {{
        setImposteriser(ByteBuddyClassImposteriser.INSTANCE);
        setThreadingPolicy(new Synchroniser());
    }};

    Environment environment = mockery.mock(Environment.class);

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(
        WireMockConfiguration.options().dynamicPort());

    DataSharingRestServiceImpl uut;

    @Before
    public void setup()
    {
        String dashboardBaseUrl = "http://localhost:" + wireMockRule.port();
        mockery.checking(new Expectations()
        {{
            atLeast(2).of(environment).getProperty(DataSharingRestServiceImpl.DASHBOARD_BASE_URL_PROPERTY);
            will(returnValue(dashboardBaseUrl));
            oneOf(environment).getProperty(DataSharingRestServiceImpl.DASHBOARD_USERNAME_PROPERTY);
            will(returnValue(null));
            oneOf(environment).getProperty(DataSharingRestServiceImpl.DASHBOARD_PASSWORD_PROPERTY);
            will(returnValue(null));
            oneOf(environment).getProperty(DataSharingRestServiceImpl.DASHBOARD_REST_USERAGENT);
            will(returnValue("user agent"));
        }});
    }

    // ========== WIRETAP QUERY TESTS ==========

    @Test
    public void query_wiretap_success() throws IOException {
        stubFor(get(urlEqualTo("/rest/data-sharing/wiretap?fromTimestamp=0&toTimestamp=100000&limit=100&offset=0"))
            .withHeader(HttpHeaders.USER_AGENT, equalTo("user agent"))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
            .willReturn(aResponse()
                .withBody(loadDataFile("/data/wiretap-response.json"))
                .withStatus(200)
            ));

        uut = new DataSharingRestServiceImpl(environment, new HttpComponentsClientHttpRequestFactory());

        SearchResults<WiretapEvent> results = uut.queryWiretap(0, 100000L, null, 100, 0);

        assertNotNull(results);
        assertEquals(3, results.getResultList().size());
        assertEquals(3, results.getTotalNumberOfResults());
        results.getResultList().forEach(wiretapEvent -> Assert.assertTrue(wiretapEvent instanceof WiretapEvent));
    }

    @Test
    public void query_wiretap_with_module_names() throws IOException {
        stubFor(get(urlMatching("/rest/data-sharing/wiretap\\?fromTimestamp=0&toTimestamp=100000&limit=100&offset=0&moduleNames=.*"))
            .withHeader(HttpHeaders.USER_AGENT, equalTo("user agent"))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
            .willReturn(aResponse()
                .withBody(loadDataFile("/data/wiretap-response.json"))
                .withStatus(200)
            ));

        uut = new DataSharingRestServiceImpl(environment, new HttpComponentsClientHttpRequestFactory());

        List<String> moduleNames = Arrays.asList("module1", "module2");
        SearchResults<WiretapEvent> results = uut.queryWiretap(0, 100000L, moduleNames, 100, 0);

        assertNotNull(results);
        assertEquals(3, results.getResultList().size());
        assertEquals(3, results.getTotalNumberOfResults());
        results.getResultList().forEach(wiretapEvent -> Assert.assertTrue(wiretapEvent instanceof WiretapEvent));
    }

    @Test(expected = RuntimeException.class)
    public void query_wiretap_exception() throws IOException {
        stubFor(get(urlEqualTo("/rest/data-sharing/wiretap?fromTimestamp=0&toTimestamp=100000&limit=100&offset=0"))
            .withHeader(HttpHeaders.USER_AGENT, equalTo("user agent"))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
            .willReturn(aResponse()
                .withBody("bad payload")
                .withStatus(200)
            ));

        uut = new DataSharingRestServiceImpl(environment, new HttpComponentsClientHttpRequestFactory());

        uut.queryWiretap(0, 100000L, null, 100, 0);
    }

    @Test
    public void query_wiretap_returns_401_followed_by_authentication_and_successful_get() throws IOException {
        stubFor(get(urlEqualTo("/rest/data-sharing/wiretap?fromTimestamp=0&toTimestamp=100000&limit=100&offset=0"))
            .withHeader(HttpHeaders.USER_AGENT, equalTo("user agent"))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
            .willReturn(aResponse()
                .withBody(loadDataFile("/data/wiretap-response.json"))
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

        stubFor(get(urlEqualTo("/rest/data-sharing/wiretap?fromTimestamp=0&toTimestamp=100000&limit=100&offset=0"))
            .withHeader(HttpHeaders.USER_AGENT, equalTo("user agent"))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
            .willReturn(aResponse()
                .withBody(loadDataFile("/data/wiretap-response.json"))
                .withStatus(200)
            ));

        uut = new DataSharingRestServiceImpl(environment, new HttpComponentsClientHttpRequestFactory());

        SearchResults results = uut.queryWiretap(0, 100000L, null, 100, 0);
        assertEquals(3, results.getResultList().size());
    }

    // ========== ERRORS QUERY TESTS ==========

    @Test
    public void query_errors_success() throws IOException {
        stubFor(get(urlEqualTo("/rest/data-sharing/errors?fromTimestamp=0&toTimestamp=100000&limit=100&offset=0"))
            .withHeader(HttpHeaders.USER_AGENT, equalTo("user agent"))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
            .willReturn(aResponse()
                .withBody(loadDataFile("/data/errors-response.json"))
                .withStatus(200)
            ));

        uut = new DataSharingRestServiceImpl(environment, new HttpComponentsClientHttpRequestFactory());

        SearchResults<ErrorOccurrence> results = uut.queryErrors(0, 100000L, null, 100, 0);

        assertNotNull(results);
        assertEquals(2, results.getResultList().size());
        assertEquals(2, results.getTotalNumberOfResults());
        results.getResultList().forEach(errorOccurrence
            -> Assert.assertTrue(errorOccurrence instanceof ErrorOccurrence<?>));
    }

    @Test(expected = RuntimeException.class)
    public void query_errors_exception() throws IOException {
        stubFor(get(urlEqualTo("/rest/data-sharing/errors?fromTimestamp=0&toTimestamp=100000&limit=100&offset=0"))
            .withHeader(HttpHeaders.USER_AGENT, equalTo("user agent"))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
            .willReturn(aResponse()
                .withBody("bad payload")
                .withStatus(200)
            ));

        uut = new DataSharingRestServiceImpl(environment, new HttpComponentsClientHttpRequestFactory());

        uut.queryErrors(0, 100000L, null, 100, 0);
    }

    // ========== EXCLUSIONS QUERY TESTS ==========

    @Test
    public void query_exclusions_success() throws IOException {
        stubFor(get(urlEqualTo("/rest/data-sharing/exclusions?fromTimestamp=0&toTimestamp=100000&limit=100&offset=0"))
            .withHeader(HttpHeaders.USER_AGENT, equalTo("user agent"))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
            .willReturn(aResponse()
                .withBody(loadDataFile("/data/exclusions-response.json"))
                .withStatus(200)
            ));

        uut = new DataSharingRestServiceImpl(environment, new HttpComponentsClientHttpRequestFactory());

        SearchResults<ExclusionEvent> results = uut.queryExclusions(0, 100000L, null, 100, 0);

        assertNotNull(results);
        assertEquals(3, results.getResultList().size());
        assertEquals(3, results.getTotalNumberOfResults());

        results.getResultList().forEach(exclusionEvent
            -> Assert.assertTrue(exclusionEvent instanceof ExclusionEvent<?>));
    }

    @Test(expected = RuntimeException.class)
    public void query_exclusions_exception() throws IOException {
        stubFor(get(urlEqualTo("/rest/data-sharing/exclusions?fromTimestamp=0&toTimestamp=100000&limit=100&offset=0"))
            .withHeader(HttpHeaders.USER_AGENT, equalTo("user agent"))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
            .willReturn(aResponse()
                .withBody("bad payload")
                .withStatus(200)
            ));

        uut = new DataSharingRestServiceImpl(environment, new HttpComponentsClientHttpRequestFactory());

        uut.queryExclusions(0, 100000L, null, 100, 0);
    }

    // ========== REPLAYS QUERY TESTS ==========

    @Test
    public void query_replays_success() throws IOException {
        stubFor(get(urlEqualTo("/rest/data-sharing/replays?fromTimestamp=0&toTimestamp=100000&limit=100&offset=0"))
            .withHeader(HttpHeaders.USER_AGENT, equalTo("user agent"))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
            .willReturn(aResponse()
                .withBody(loadDataFile("/data/replays-response.json"))
                .withStatus(200)
            ));

        uut = new DataSharingRestServiceImpl(environment, new HttpComponentsClientHttpRequestFactory());

        SearchResults<ReplayEvent> results = uut.queryReplays(0, 100000L, null, 100, 0);

        assertNotNull(results);
        assertEquals(2, results.getResultList().size());
        assertEquals(2, results.getTotalNumberOfResults());

        results.getResultList().forEach(replayEvent
            -> Assert.assertTrue(replayEvent instanceof ReplayEvent));
    }

    @Test(expected = RuntimeException.class)
    public void query_replays_exception() throws IOException {
        stubFor(get(urlEqualTo("/rest/data-sharing/replays?fromTimestamp=0&toTimestamp=100000&limit=100&offset=0"))
            .withHeader(HttpHeaders.USER_AGENT, equalTo("user agent"))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
            .willReturn(aResponse()
                .withBody("bad payload")
                .withStatus(200)
            ));

        uut = new DataSharingRestServiceImpl(environment, new HttpComponentsClientHttpRequestFactory());

        uut.queryReplays(0, 100000L, null, 100, 0);
    }

    // ========== MODULE METADATA QUERY TESTS ==========

    @Test
    public void query_module_metadata_success() throws IOException {
        stubFor(get(urlMatching("/rest/data-sharing/module-metadata\\?limit=100&offset=0&moduleNames=.*"))
            .withHeader(HttpHeaders.USER_AGENT, equalTo("user agent"))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
            .willReturn(aResponse()
                .withBody(loadDataFile("/data/topology-response.json"))
                .withStatus(200)
            ));

        uut = new DataSharingRestServiceImpl(environment, new HttpComponentsClientHttpRequestFactory());

        List<String> moduleNames = Arrays.asList("module1", "module2");
        SearchResults<ModuleMetaData> results = uut.queryModuleMetadata(moduleNames, 100, 0);

        assertNotNull(results);
        assertEquals(1, results.getResultList().size());
        assertEquals(1, results.getTotalNumberOfResults());

        results.getResultList().forEach(moduleMetaData
            -> Assert.assertTrue(moduleMetaData instanceof ModuleMetaData));
    }

    @Test(expected = RuntimeException.class)
    public void query_module_metadata_exception() throws IOException {
        stubFor(get(urlMatching("/rest/data-sharing/module-metadata\\?limit=100&offset=0&moduleNames=.*"))
            .withHeader(HttpHeaders.USER_AGENT, equalTo("user agent"))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
            .willReturn(aResponse()
                .withBody("bad payload")
                .withStatus(200)
            ));

        uut = new DataSharingRestServiceImpl(environment, new HttpComponentsClientHttpRequestFactory());

        List<String> moduleNames = Arrays.asList("module1", "module2");
        uut.queryModuleMetadata(moduleNames, 100, 0);
    }

    // ========== CONFIGURATION QUERY TESTS ==========

    @Test
    public void query_configuration_success() throws IOException {
        stubFor(get(urlMatching("/rest/data-sharing/configuration\\?limit=100&offset=0&configurationIdentifiers=.*"))
            .withHeader(HttpHeaders.USER_AGENT, equalTo("user agent"))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
            .willReturn(aResponse()
                .withBody(loadDataFile("/data/configuration-response.json"))
                .withStatus(200)
            ));

        uut = new DataSharingRestServiceImpl(environment, new HttpComponentsClientHttpRequestFactory());

        List<String> configurationIdentifiers = Arrays.asList("config1", "config2");
        SearchResults<ConfigurationMetaData> results = uut.queryConfiguration(configurationIdentifiers, 100, 0);

        assertNotNull(results);
        assertEquals(2, results.getResultList().size());
        assertEquals(2, results.getTotalNumberOfResults());

        results.getResultList().forEach(configurationMetaData
            -> Assert.assertTrue(configurationMetaData instanceof ConfigurationMetaData<?>));
    }

    @Test(expected = RuntimeException.class)
    public void query_configuration_exception() throws IOException {
        stubFor(get(urlMatching("/rest/data-sharing/configuration\\?limit=100&offset=0&configurationIdentifiers=.*"))
            .withHeader(HttpHeaders.USER_AGENT, equalTo("user agent"))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
            .willReturn(aResponse()
                .withBody("bad payload")
                .withStatus(200)
            ));

        uut = new DataSharingRestServiceImpl(environment, new HttpComponentsClientHttpRequestFactory());

        List<String> configurationIdentifiers = Arrays.asList("config1", "config2");
        uut.queryConfiguration(configurationIdentifiers, 100, 0);
    }

    // ========== COUNT WIRETAP TESTS ==========

    @Test
    public void count_wiretap_success() throws IOException {
        stubFor(get(urlEqualTo("/rest/data-sharing/wiretap/count?fromTimestamp=0&toTimestamp=100000"))
            .withHeader(HttpHeaders.USER_AGENT, equalTo("user agent"))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
            .willReturn(aResponse()
                .withBody(loadDataFile("/data/data-sharing-count-response.json"))
                .withStatus(200)
            ));

        uut = new DataSharingRestServiceImpl(environment, new HttpComponentsClientHttpRequestFactory());

        assertEquals(10, uut.countWiretap(0, 100000L, null));
    }

    @Test
    public void count_wiretap_with_module_names() throws IOException {
        stubFor(get(urlMatching("/rest/data-sharing/wiretap/count\\?fromTimestamp=0&toTimestamp=100000&moduleNames=.*"))
            .withHeader(HttpHeaders.USER_AGENT, equalTo("user agent"))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
            .willReturn(aResponse()
                .withBody(loadDataFile("/data/data-sharing-count-response.json"))
                .withStatus(200)
            ));

        uut = new DataSharingRestServiceImpl(environment, new HttpComponentsClientHttpRequestFactory());

        List<String> moduleNames = Arrays.asList("module1", "module2");
        assertEquals(10, uut.countWiretap(0, 100000L, moduleNames));
    }

    @Test(expected = RuntimeException.class)
    public void count_wiretap_exception() throws IOException {
        stubFor(get(urlEqualTo("/rest/data-sharing/wiretap/count?fromTimestamp=0&toTimestamp=100000"))
            .withHeader(HttpHeaders.USER_AGENT, equalTo("user agent"))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
            .willReturn(aResponse()
                .withBody("bad payload")
                .withStatus(200)
            ));

        uut = new DataSharingRestServiceImpl(environment, new HttpComponentsClientHttpRequestFactory());

        uut.countWiretap(0, 100000L, null);
    }

    @Test
    public void count_wiretap_returns_401_followed_by_authentication_and_successful_get() throws IOException {
        stubFor(get(urlEqualTo("/rest/data-sharing/wiretap/count?fromTimestamp=0&toTimestamp=100000"))
            .withHeader(HttpHeaders.USER_AGENT, equalTo("user agent"))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
            .willReturn(aResponse()
                .withBody(loadDataFile("/data/data-sharing-count-response.json"))
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

        stubFor(get(urlEqualTo("/rest/data-sharing/wiretap/count?fromTimestamp=0&toTimestamp=100000"))
            .withHeader(HttpHeaders.USER_AGENT, equalTo("user agent"))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
            .willReturn(aResponse()
                .withBody(loadDataFile("/data/data-sharing-count-response.json"))
                .withStatus(200)
            ));

        uut = new DataSharingRestServiceImpl(environment, new HttpComponentsClientHttpRequestFactory());

        assertEquals(10, uut.countWiretap(0, 100000L, null));
    }

    // ========== COUNT ERRORS TESTS ==========

    @Test
    public void count_errors_success() throws IOException {
        stubFor(get(urlEqualTo("/rest/data-sharing/errors/count?fromTimestamp=0&toTimestamp=100000"))
            .withHeader(HttpHeaders.USER_AGENT, equalTo("user agent"))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
            .willReturn(aResponse()
                .withBody(loadDataFile("/data/data-sharing-count-response.json"))
                .withStatus(200)
            ));

        uut = new DataSharingRestServiceImpl(environment, new HttpComponentsClientHttpRequestFactory());

        assertEquals(10, uut.countErrors(0, 100000L, null));
    }

    @Test(expected = RuntimeException.class)
    public void count_errors_exception() throws IOException {
        stubFor(get(urlEqualTo("/rest/data-sharing/errors/count?fromTimestamp=0&toTimestamp=100000"))
            .withHeader(HttpHeaders.USER_AGENT, equalTo("user agent"))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
            .willReturn(aResponse()
                .withBody("bad payload")
                .withStatus(200)
            ));

        uut = new DataSharingRestServiceImpl(environment, new HttpComponentsClientHttpRequestFactory());

        uut.countErrors(0, 100000L, null);
    }

    // ========== COUNT EXCLUSIONS TESTS ==========

    @Test
    public void count_exclusions_success() throws IOException {
        stubFor(get(urlEqualTo("/rest/data-sharing/exclusions/count?fromTimestamp=0&toTimestamp=100000"))
            .withHeader(HttpHeaders.USER_AGENT, equalTo("user agent"))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
            .willReturn(aResponse()
                .withBody(loadDataFile("/data/data-sharing-count-response.json"))
                .withStatus(200)
            ));

        uut = new DataSharingRestServiceImpl(environment, new HttpComponentsClientHttpRequestFactory());

        assertEquals(10, uut.countExclusions(0, 100000L, null));
    }

    @Test(expected = RuntimeException.class)
    public void count_exclusions_exception() throws IOException {
        stubFor(get(urlEqualTo("/rest/data-sharing/exclusions/count?fromTimestamp=0&toTimestamp=100000"))
            .withHeader(HttpHeaders.USER_AGENT, equalTo("user agent"))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
            .willReturn(aResponse()
                .withBody("bad payload")
                .withStatus(200)
            ));

        uut = new DataSharingRestServiceImpl(environment, new HttpComponentsClientHttpRequestFactory());

        uut.countExclusions(0, 100000L, null);
    }

    // ========== COUNT REPLAYS TESTS ==========

    @Test
    public void count_replays_success() throws IOException {
        stubFor(get(urlEqualTo("/rest/data-sharing/replays/count?fromTimestamp=0&toTimestamp=100000"))
            .withHeader(HttpHeaders.USER_AGENT, equalTo("user agent"))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
            .willReturn(aResponse()
                .withBody(loadDataFile("/data/data-sharing-count-response.json"))
                .withStatus(200)
            ));

        uut = new DataSharingRestServiceImpl(environment, new HttpComponentsClientHttpRequestFactory());

        assertEquals(10, uut.countReplays(0, 100000L, null));
    }

    @Test(expected = RuntimeException.class)
    public void count_replays_exception() throws IOException {
        stubFor(get(urlEqualTo("/rest/data-sharing/replays/count?fromTimestamp=0&toTimestamp=100000"))
            .withHeader(HttpHeaders.USER_AGENT, equalTo("user agent"))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
            .willReturn(aResponse()
                .withBody("bad payload")
                .withStatus(200)
            ));

        uut = new DataSharingRestServiceImpl(environment, new HttpComponentsClientHttpRequestFactory());

        uut.countReplays(0, 100000L, null);
    }

    // ========== FLOW STATES TESTS ==========

    @Test
    public void get_flow_states_success() throws IOException {
        stubFor(get(urlEqualTo("/rest/data-sharing/flowstates"))
            .withHeader(HttpHeaders.USER_AGENT, equalTo("user agent"))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
            .willReturn(aResponse()
                .withBody(loadDataFile("/data/flowstates-response.json"))
                .withStatus(200)
            ));

        uut = new DataSharingRestServiceImpl(environment, new HttpComponentsClientHttpRequestFactory());

        List<FlowState> results = uut.getFlowStates(null);

        assertNotNull(results);
        assertEquals(3, results.size());

        // Verify first flow state
        FlowState flowState1 = results.get(0);
        assertEquals("module1", flowState1.getModuleName());
        assertEquals("flow1", flowState1.getFlowName());
        assertEquals("running", flowState1.getState());

        // Verify second flow state
        FlowState flowState2 = results.get(1);
        assertEquals("module1", flowState2.getModuleName());
        assertEquals("flow2", flowState2.getFlowName());
        assertEquals("stopped", flowState2.getState());

        // Verify third flow state
        FlowState flowState3 = results.get(2);
        assertEquals("module2", flowState3.getModuleName());
        assertEquals("flow1", flowState3.getFlowName());
        assertEquals("running", flowState3.getState());
    }

    @Test
    public void get_flow_states_with_module_names() throws IOException {
        stubFor(get(urlMatching("/rest/data-sharing/flowstates\\?moduleNames=.*"))
            .withHeader(HttpHeaders.USER_AGENT, equalTo("user agent"))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
            .willReturn(aResponse()
                .withBody(loadDataFile("/data/flowstates-response.json"))
                .withStatus(200)
            ));

        uut = new DataSharingRestServiceImpl(environment, new HttpComponentsClientHttpRequestFactory());

        List<String> moduleNames = Arrays.asList("module1", "module2");
        List<FlowState> results = uut.getFlowStates(moduleNames);

        assertNotNull(results);
        assertEquals(3, results.size());
        results.forEach(flowState -> Assert.assertTrue(flowState instanceof FlowState));
    }

    @Test
    public void get_flow_states_with_single_module_name() throws IOException {
        stubFor(get(urlEqualTo("/rest/data-sharing/flowstates?moduleNames=module1"))
            .withHeader(HttpHeaders.USER_AGENT, equalTo("user agent"))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
            .willReturn(aResponse()
                .withBody("[{\"moduleName\":\"module1\",\"flowName\":\"flow1\",\"state\":\"running\"}]")
                .withStatus(200)
            ));

        uut = new DataSharingRestServiceImpl(environment, new HttpComponentsClientHttpRequestFactory());

        List<String> moduleNames = Arrays.asList("module1");
        List<FlowState> results = uut.getFlowStates(moduleNames);

        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals("module1", results.get(0).getModuleName());
        assertEquals("flow1", results.get(0).getFlowName());
        assertEquals("running", results.get(0).getState());
    }

    @Test
    public void get_flow_states_empty_list() throws IOException {
        stubFor(get(urlEqualTo("/rest/data-sharing/flowstates"))
            .withHeader(HttpHeaders.USER_AGENT, equalTo("user agent"))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
            .willReturn(aResponse()
                .withBody("[]")
                .withStatus(200)
            ));

        uut = new DataSharingRestServiceImpl(environment, new HttpComponentsClientHttpRequestFactory());

        List<FlowState> results = uut.getFlowStates(null);

        assertNotNull(results);
        assertEquals(0, results.size());
    }

    @Test(expected = RuntimeException.class)
    public void get_flow_states_exception() throws IOException {
        stubFor(get(urlEqualTo("/rest/data-sharing/flowstates"))
            .withHeader(HttpHeaders.USER_AGENT, equalTo("user agent"))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
            .willReturn(aResponse()
                .withBody("bad payload")
                .withStatus(200)
            ));

        uut = new DataSharingRestServiceImpl(environment, new HttpComponentsClientHttpRequestFactory());

        uut.getFlowStates(null);
    }

    @Test
    public void get_flow_states_returns_401_followed_by_authentication_and_successful_get() throws IOException {
        stubFor(get(urlEqualTo("/rest/data-sharing/flowstates"))
            .withHeader(HttpHeaders.USER_AGENT, equalTo("user agent"))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
            .willReturn(aResponse()
                .withBody(loadDataFile("/data/flowstates-response.json"))
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

        stubFor(get(urlEqualTo("/rest/data-sharing/flowstates"))
            .withHeader(HttpHeaders.USER_AGENT, equalTo("user agent"))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
            .willReturn(aResponse()
                .withBody(loadDataFile("/data/flowstates-response.json"))
                .withStatus(200)
            ));

        uut = new DataSharingRestServiceImpl(environment, new HttpComponentsClientHttpRequestFactory());

        List<FlowState> results = uut.getFlowStates(null);
        assertEquals(3, results.size());
    }

    @Test
    public void get_flow_states_with_multiple_module_names_url_encoding() throws IOException {
        stubFor(get(urlMatching("/rest/data-sharing/flowstates\\?moduleNames=module1&moduleNames=module2"))
            .withHeader(HttpHeaders.USER_AGENT, equalTo("user agent"))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
            .willReturn(aResponse()
                .withBody(loadDataFile("/data/flowstates-response.json"))
                .withStatus(200)
            ));

        uut = new DataSharingRestServiceImpl(environment, new HttpComponentsClientHttpRequestFactory());

        List<String> moduleNames = Arrays.asList("module1", "module2");
        List<FlowState> results = uut.getFlowStates(moduleNames);

        assertNotNull(results);
        assertEquals(3, results.size());
    }

    @Test(expected = RuntimeException.class)
    public void get_flow_states_http_error_500() throws IOException {
        stubFor(get(urlEqualTo("/rest/data-sharing/flowstates"))
            .withHeader(HttpHeaders.USER_AGENT, equalTo("user agent"))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
            .willReturn(aResponse()
                .withBody("Internal server error")
                .withStatus(500)
            ));

        uut = new DataSharingRestServiceImpl(environment, new HttpComponentsClientHttpRequestFactory());

        uut.getFlowStates(null);
    }

    @Test
    public void get_flow_states_with_null_state_values() throws IOException {
        stubFor(get(urlEqualTo("/rest/data-sharing/flowstates"))
            .withHeader(HttpHeaders.USER_AGENT, equalTo("user agent"))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
            .willReturn(aResponse()
                .withBody("[{\"moduleName\":\"module1\",\"flowName\":\"flow1\",\"state\":null}]")
                .withStatus(200)
            ));

        uut = new DataSharingRestServiceImpl(environment, new HttpComponentsClientHttpRequestFactory());

        List<FlowState> results = uut.getFlowStates(null);

        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals("module1", results.get(0).getModuleName());
        assertEquals("flow1", results.get(0).getFlowName());
        Assert.assertNull(results.get(0).getState());
    }

    // ========== HELPER METHODS ==========

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
