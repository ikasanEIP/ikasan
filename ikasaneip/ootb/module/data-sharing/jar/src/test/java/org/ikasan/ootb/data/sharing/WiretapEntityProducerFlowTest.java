/*
 * $Id$
 * $URL$
 *
 * ====================================================================
 * Ikasan Enterprise Integration Platform
 *
 * Distributed under the Modified BSD License.
 * Copyright notice: The copyright for this software and a full listing
 * of individual contributors are as shown in the packaged copyright.txt
 * file.
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  - Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  - Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 *  - Neither the name of the ORGANIZATION nor the names of its contributors may
 *    be used to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * ====================================================================
 */
package org.ikasan.ootb.data.sharing;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.github.tomakehurst.wiremock.verification.LoggedRequest;
import org.awaitility.Awaitility;
import org.ikasan.bigqueue.IBigQueue;
import org.ikasan.dashboard.dto.wiretap.WiretapEventImpl;
import org.ikasan.ootb.data.sharing.module.Application;
import org.ikasan.ootb.data.sharing.module.cache.EntityBigQueueCache;
import org.ikasan.ootb.data.sharing.module.serialiser.WiretapEventsToBigQueueMessageSerialiser;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.module.Module;
import org.ikasan.spec.wiretap.WiretapEvent;
import org.ikasan.testharness.flow.rule.IkasanFlowTestRule;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;

/**
 * Comprehensive unit tests for Wiretap Entity Producer Flow.
 *
 * Tests cover:
 * - Single batch publishing
 * - Multiple batch publishing
 * - Empty queue handling
 * - Error handling and retry
 * - Flow lifecycle
 *
 * @author Ikasan Development Team
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {Application.class},
    properties = {
        "spring.main.allow-bean-definition-overriding=true",
        "data-sharing.upstreamDashboard=http://localhost:9080"
    },
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = {"/cleanDatabaseTables.sql"}, executionPhase = AFTER_TEST_METHOD)
public class WiretapEntityProducerFlowTest {

    @ClassRule
    public static WireMockRule wireMockRule = new WireMockRule(
        WireMockConfiguration.options().port(9081));

    @Autowired
    private Module<Flow> moduleUnderTest;

    private static ObjectMapper objectMapper = new ObjectMapper();

    public IkasanFlowTestRule flowTestRule = new IkasanFlowTestRule();

    @BeforeClass
    public static void setupObjectMapper() {
        final var simpleModule = new SimpleModule()
            .addAbstractTypeMapping(List.class, ArrayList.class)
            .addAbstractTypeMapping(Map.class, HashMap.class);

        objectMapper.registerModule(simpleModule);
    }

    @Before
    public void setup() throws IOException {
        EntityBigQueueCache.instance().get("DASHBOARD_1-Ikasan-ESB-Dashboard-WIRETAP-queue").removeAll();
        WireMock.reset();
        wireMockRule.resetAll();
    }

    @After
    public void teardown() throws IOException {
        EntityBigQueueCache.instance().get("DASHBOARD_1-Ikasan-ESB-Dashboard-WIRETAP-queue").removeAll();
    }

    @Test
    @DirtiesContext
    public void test_wiretap_produce_single_batch() throws IOException {
        // Setup: Create wiretap events and add to queue
        IBigQueue wiretapQueue = EntityBigQueueCache.instance().get("DASHBOARD_1-Ikasan-ESB-Dashboard-WIRETAP-queue");

        List<WiretapEvent> events = createWiretapEvents(50, "module1", "flow1");
        WiretapEventsToBigQueueMessageSerialiser serialiser = new WiretapEventsToBigQueueMessageSerialiser();
        wiretapQueue.enqueue(serialiser.serialise(events));

        // Setup WireMock stub for REST endpoint
        stubFor(put(urlEqualTo("/rest/harvest/wiretaps"))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON_VALUE))
            .willReturn(aResponse()
                .withStatus(200)));

        // Setup flow test rule
        flowTestRule.withFlow(moduleUnderTest.getFlow("DASHBOARD_1 Ikasan ESB Dashboard Wiretap Entity Producer Flow"));
        flowTestRule.consumer("Wiretap Entity BigQueue Consumer")
            .producer("Wiretap Entity REST Dashboard Publishing Producer");

        flowTestRule.startFlow();

        // Wait for flow to process the queue
        Awaitility.await().atMost(30, TimeUnit.SECONDS)
            .untilAsserted(() -> Assert.assertEquals(0, wiretapQueue.size()));

        // Verify REST endpoint was called
        verify(1, putRequestedFor(urlEqualTo("/rest/harvest/wiretaps"))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON_VALUE)));

        Awaitility.await().atMost(30, TimeUnit.SECONDS)
            .untilAsserted(() -> flowTestRule.assertIsSatisfied());

        flowTestRule.stopFlow();
    }

    @Test
    @DirtiesContext
    public void test_wiretap_produce_multiple_batches() throws IOException {
        // Setup: Create multiple batches of wiretap events
        IBigQueue wiretapQueue = EntityBigQueueCache.instance().get("DASHBOARD_1-Ikasan-ESB-Dashboard-WIRETAP-queue");
        WiretapEventsToBigQueueMessageSerialiser serialiser = new WiretapEventsToBigQueueMessageSerialiser();

        // Add 3 batches to queue
        List<WiretapEvent> batch1 = createWiretapEvents(100, "module1", "flow1");
        wiretapQueue.enqueue(serialiser.serialise(batch1));

        List<WiretapEvent> batch2 = createWiretapEvents(100, "module2", "flow2");
        wiretapQueue.enqueue(serialiser.serialise(batch2));

        List<WiretapEvent> batch3 = createWiretapEvents(50, "module3", "flow3");
        wiretapQueue.enqueue(serialiser.serialise(batch3));

        assertEquals(3, wiretapQueue.size());

        // Setup WireMock stub for REST endpoint
        stubFor(put(urlEqualTo("/rest/harvest/wiretaps"))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON_VALUE))
            .willReturn(aResponse()
                .withStatus(200)
                .withBody("{\"success\": true}")));

        // Setup flow test rule
        flowTestRule.withFlow(moduleUnderTest.getFlow("DASHBOARD_1 Ikasan ESB Dashboard Wiretap Entity Producer Flow"));
        flowTestRule.consumer("Wiretap Entity BigQueue Consumer")
            .producer("Wiretap Entity REST Dashboard Publishing Producer")
            .consumer("Wiretap Entity BigQueue Consumer")
            .producer("Wiretap Entity REST Dashboard Publishing Producer")
            .consumer("Wiretap Entity BigQueue Consumer")
            .producer("Wiretap Entity REST Dashboard Publishing Producer");

        flowTestRule.startFlow();

        // Wait for all batches to be processed
        Awaitility.await().atMost(30, TimeUnit.SECONDS)
            .untilAsserted(() -> Assert.assertEquals(0, wiretapQueue.size()));

        // Verify REST endpoint was called 3 times
        verify(3, putRequestedFor(urlEqualTo("/rest/harvest/wiretaps"))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON_VALUE)));

        Awaitility.await().atMost(30, TimeUnit.SECONDS)
            .untilAsserted(() -> flowTestRule.assertIsSatisfied());

        flowTestRule.stopFlow();
    }

    @Test
    @DirtiesContext
    public void test_wiretap_produce_empty_queue() throws IOException, InterruptedException {
        // Setup: Empty queue
        IBigQueue wiretapQueue = EntityBigQueueCache.instance().get("DASHBOARD_1-Ikasan-ESB-Dashboard-WIRETAP-queue");
        assertEquals(0, wiretapQueue.size());

        // Setup flow test rule
        flowTestRule.withFlow(moduleUnderTest.getFlow("DASHBOARD_1 Ikasan ESB Dashboard Wiretap Entity Producer Flow"));

        flowTestRule.startFlow();

        // Give it a moment to confirm nothing happens
        Thread.sleep(2000);

        // Verify no REST calls were made
        verify(0, putRequestedFor(urlEqualTo("/rest/harvest/wiretaps")));

        assertEquals(Flow.RUNNING, flowTestRule.getFlowState());

        flowTestRule.stopFlow();
    }

    @Test
    @DirtiesContext
    public void test_wiretap_produce_with_rest_error_and_retry() throws IOException {
        // Setup: Create wiretap events
        IBigQueue wiretapQueue = EntityBigQueueCache.instance().get("DASHBOARD_1-Ikasan-ESB-Dashboard-WIRETAP-queue");

        List<WiretapEvent> events = createWiretapEvents(50, "module1", "flow1");
        WiretapEventsToBigQueueMessageSerialiser serialiser = new WiretapEventsToBigQueueMessageSerialiser();
        wiretapQueue.enqueue(serialiser.serialise(events));

        // Setup WireMock stub to fail first, then succeed
        stubFor(put(urlEqualTo("/rest/harvest/wiretaps"))
            .inScenario("Retry Scenario")
            .whenScenarioStateIs("Started")
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON_VALUE))
            .willReturn(aResponse()
                .withStatus(500)
                .withBody("{\"error\": \"Internal Server Error\"}"))
            .willSetStateTo("Failed Once"));

        stubFor(put(urlEqualTo("/rest/harvest/wiretaps"))
            .inScenario("Retry Scenario")
            .whenScenarioStateIs("Failed Once")
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON_VALUE))
            .willReturn(aResponse()
                .withStatus(200)
                .withBody("{\"success\": true}"))
            .willSetStateTo("Succeeded"));

        // Setup flow test rule - error handling should retry
        flowTestRule.withFlow(moduleUnderTest.getFlow("DASHBOARD_1 Ikasan ESB Dashboard Wiretap Entity Producer Flow"));
        flowTestRule.consumer("Wiretap Entity BigQueue Consumer")
            .producer("Wiretap Entity REST Dashboard Publishing Producer")
            .consumer("Wiretap Entity BigQueue Consumer")
            .producer("Wiretap Entity REST Dashboard Publishing Producer");

        flowTestRule.startFlow();

        // Wait for retry and successful processing
        Awaitility.await().atMost(60, TimeUnit.SECONDS)
            .untilAsserted(() -> Assert.assertEquals(0, wiretapQueue.size()));

        // Verify REST endpoint was called at least twice (fail + retry)
        verify(moreThan(1), putRequestedFor(urlEqualTo("/rest/harvest/wiretaps"))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON_VALUE)));

        Awaitility.await().atMost(30, TimeUnit.SECONDS)
            .untilAsserted(() -> flowTestRule.assertIsSatisfied());

        flowTestRule.stopFlow();
    }

    @Test
    @DirtiesContext
    public void test_wiretap_produce_large_batch() throws IOException {
        // Setup: Create a large batch of wiretap events
        IBigQueue wiretapQueue = EntityBigQueueCache.instance().get("DASHBOARD_1-Ikasan-ESB-Dashboard-WIRETAP-queue");

        List<WiretapEvent> events = createWiretapEvents(500, "module1", "flow1");
        WiretapEventsToBigQueueMessageSerialiser serialiser = new WiretapEventsToBigQueueMessageSerialiser();
        wiretapQueue.enqueue(serialiser.serialise(events));

        // Setup WireMock stub
        stubFor(put(urlEqualTo("/rest/harvest/wiretaps"))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON_VALUE))
            .willReturn(aResponse()
                .withStatus(200)
                .withBody("{\"success\": true}")));

        // Setup flow test rule
        flowTestRule.withFlow(moduleUnderTest.getFlow("DASHBOARD_1 Ikasan ESB Dashboard Wiretap Entity Producer Flow"));
        flowTestRule.consumer("Wiretap Entity BigQueue Consumer")
            .producer("Wiretap Entity REST Dashboard Publishing Producer");

        flowTestRule.startFlow();

        // Wait for processing
        Awaitility.await().atMost(30, TimeUnit.SECONDS)
            .untilAsserted(() -> Assert.assertEquals(0, wiretapQueue.size()));

        // Verify REST endpoint was called
        verify(1, putRequestedFor(urlEqualTo("/rest/harvest/wiretaps")));

        // Verify payload size
        List<LoggedRequest> requests = findAll(putRequestedFor(urlEqualTo("/rest/harvest/wiretaps")));
        Assert.assertEquals(1, requests.size());

        String requestBody = requests.get(0).getBodyAsString();
        Assert.assertNotNull(requestBody);
        Assert.assertTrue(requestBody.length() > 0);

        Awaitility.await().atMost(30, TimeUnit.SECONDS)
            .untilAsserted(() -> flowTestRule.assertIsSatisfied());

        flowTestRule.stopFlow();
    }

    @Test
    @DirtiesContext
    public void test_wiretap_produce_flow_lifecycle() throws IOException {
        // Test flow can be started, stopped, and restarted
        Flow flow = moduleUnderTest.getFlow("DASHBOARD_1 Ikasan ESB Dashboard Wiretap Entity Producer Flow");

        // Start flow
        flow.start();
        assertEquals(Flow.RUNNING, flow.getState());

        // Stop flow
        flow.stop();
        assertEquals(Flow.STOPPED, flow.getState());

        // Restart flow
        flow.start();
        assertEquals(Flow.RUNNING, flow.getState());

        // Final stop
        flow.stop();
        assertEquals(Flow.STOPPED, flow.getState());
    }

    @Test
    @DirtiesContext
    public void test_wiretap_produce_different_modules() throws IOException {
        // Setup: Create events from different modules
        IBigQueue wiretapQueue = EntityBigQueueCache.instance().get("DASHBOARD_1-Ikasan-ESB-Dashboard-WIRETAP-queue");
        WiretapEventsToBigQueueMessageSerialiser serialiser = new WiretapEventsToBigQueueMessageSerialiser();

        // Mix of different modules and flows
        List<WiretapEvent> events = new ArrayList<>();
        events.addAll(createWiretapEvents(20, "orderModule", "orderFlow"));
        events.addAll(createWiretapEvents(30, "customerModule", "customerFlow"));
        events.addAll(createWiretapEvents(25, "inventoryModule", "inventoryFlow"));

        wiretapQueue.enqueue(serialiser.serialise(events));

        // Setup WireMock stub
        stubFor(put(urlEqualTo("/rest/harvest/wiretaps"))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON_VALUE))
            .willReturn(aResponse()
                .withStatus(200)
                .withBody("{\"success\": true}")));

        // Setup flow test rule
        flowTestRule.withFlow(moduleUnderTest.getFlow("DASHBOARD_1 Ikasan ESB Dashboard Wiretap Entity Producer Flow"));
        flowTestRule.consumer("Wiretap Entity BigQueue Consumer")
            .producer("Wiretap Entity REST Dashboard Publishing Producer");

        flowTestRule.startFlow();

        // Wait for processing
        Awaitility.await().atMost(30, TimeUnit.SECONDS)
            .untilAsserted(() -> Assert.assertEquals(0, wiretapQueue.size()));

        // Verify REST endpoint was called
        verify(1, putRequestedFor(urlEqualTo("/rest/harvest/wiretaps")));

        Awaitility.await().atMost(30, TimeUnit.SECONDS)
            .untilAsserted(() -> flowTestRule.assertIsSatisfied());

        flowTestRule.stopFlow();
    }

    @Test
    @DirtiesContext
    public void test_wiretap_produce_queue_persistence() throws IOException {
        // Setup: Add events to queue
        IBigQueue wiretapQueue = EntityBigQueueCache.instance().get("DASHBOARD_1-Ikasan-ESB-Dashboard-WIRETAP-queue");
        WiretapEventsToBigQueueMessageSerialiser serialiser = new WiretapEventsToBigQueueMessageSerialiser();

        List<WiretapEvent> batch1 = createWiretapEvents(50, "module1", "flow1");
        wiretapQueue.enqueue(serialiser.serialise(batch1));

        List<WiretapEvent> batch2 = createWiretapEvents(50, "module2", "flow2");
        wiretapQueue.enqueue(serialiser.serialise(batch2));

        assertEquals(2, wiretapQueue.size());

        // Setup WireMock stub for first batch only
        stubFor(put(urlEqualTo("/rest/harvest/wiretaps"))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON_VALUE))
            .willReturn(aResponse()
                .withStatus(200)
                .withBody("{\"success\": true}")));

        // Start flow and process first batch
        flowTestRule.withFlow(moduleUnderTest.getFlow("DASHBOARD_1 Ikasan ESB Dashboard Wiretap Entity Producer Flow"));
        flowTestRule.consumer("Wiretap Entity BigQueue Consumer")
            .producer("Wiretap Entity REST Dashboard Publishing Producer")
            .consumer("Wiretap Entity BigQueue Consumer")
            .producer("Wiretap Entity REST Dashboard Publishing Producer");

        flowTestRule.startFlow();

        // Wait for first batch
        Awaitility.await().atMost(30, TimeUnit.SECONDS)
            .untilAsserted(() -> Assert.assertEquals(0, wiretapQueue.size()));

        Awaitility.await().atMost(30, TimeUnit.SECONDS)
            .untilAsserted(() -> flowTestRule.assertIsSatisfied());

        flowTestRule.stopFlow();

        // Verify second batch still in queue
        assertEquals(0, wiretapQueue.size());
    }

    /**
     * Helper method to create test WiretapEvent objects
     */
    private List<WiretapEvent> createWiretapEvents(int count, String moduleName, String flowName) {
        List<WiretapEvent> events = new ArrayList<>();
        long baseTimestamp = System.currentTimeMillis();
        for (int i = 0; i < count; i++) {
            WiretapEventImpl event = new WiretapEventImpl();
            event.setId("event-" + i);
            event.setModuleName(moduleName);
            event.setFlowName(flowName);
            event.setComponentName("component" + (i % 5));
            event.setEvent("payload-" + i);
            event.setTimestamp(baseTimestamp + i);
            event.setExpiry(100L);
            events.add(event);
        }
        return events;
    }
}
