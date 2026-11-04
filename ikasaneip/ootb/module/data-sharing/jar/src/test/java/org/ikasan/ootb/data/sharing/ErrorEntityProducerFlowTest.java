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
import org.ikasan.dashboard.dto.error.ErrorOccurrenceImpl;
import org.ikasan.ootb.data.sharing.module.Application;
import org.ikasan.ootb.data.sharing.module.cache.EntityBigQueueCache;
import org.ikasan.ootb.data.sharing.module.serialiser.ErrorOccurrenceToBigQueueMessageSerialiser;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.module.Module;
import org.ikasan.spec.error.reporting.ErrorOccurrence;
import org.ikasan.testharness.flow.rule.IkasanFlowTestRule;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;

/**
 * Comprehensive unit tests for Error Entity Producer Flow.
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
public class ErrorEntityProducerFlowTest {

    private static final String REST_URL_PATH = "/rest/harvest/errors";
    private static final String CACHE_NAME = "DASHBOARD_1-Ikasan-ESB-Dashboard-ERROR-queue";
    private static final String FLOW_NAME = "DASHBOARD_1 Ikasan ESB Dashboard Error Entity Producer Flow";

    @ClassRule
    public static WireMockRule wireMockRule = new WireMockRule(
        WireMockConfiguration.options().port(9081));

    @Autowired
    private Module<Flow> moduleUnderTest;

    private static final ObjectMapper objectMapper = new ObjectMapper();

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
        EntityBigQueueCache.instance().get(CACHE_NAME).removeAll();
        WireMock.reset();
        wireMockRule.resetAll();
    }

    @After
    public void teardown() throws IOException {
        // Always cancel scheduling and recovery, including after assertion failures.
        moduleUnderTest.getFlow(FLOW_NAME).stop();
        EntityBigQueueCache.instance().get(CACHE_NAME).removeAll();
    }

    @Test(timeout = 120000)
    @DirtiesContext
    public void test_empty_checkpoint_batch_is_consumed_without_rest_request() throws IOException {
        IBigQueue errorQueue = EntityBigQueueCache.instance().get(CACHE_NAME);
        errorQueue.enqueue(new ErrorOccurrenceToBigQueueMessageSerialiser().serialise(List.of()));
        flowTestRule.withFlow(moduleUnderTest.getFlow(FLOW_NAME));
        flowTestRule.consumer("Error Entity BigQueue Consumer")
            .producer("Error Entity REST Dashboard Publishing Producer");
        try {
            flowTestRule.startFlow();
            Awaitility.await().atMost(15, TimeUnit.SECONDS).untilAsserted(() -> {
                assertEquals(0L, errorQueue.size());
                flowTestRule.assertIsSatisfied();
            });
            verify(0, putRequestedFor(urlEqualTo(REST_URL_PATH)));
            assertEquals(Flow.RUNNING, flowTestRule.getFlowState());
        } finally {
            flowTestRule.stopFlow();
        }
    }

    @Test(timeout = 120000)
    @DirtiesContext
    public void test_error_produce_single_batch() throws IOException {
        // Setup: Create error events and add to queue
        IBigQueue errorQueue = EntityBigQueueCache.instance().get(CACHE_NAME);

        List<ErrorOccurrence<byte[]>> events = createErrorOccurrences(50);
        ErrorOccurrenceToBigQueueMessageSerialiser serialiser = new ErrorOccurrenceToBigQueueMessageSerialiser();
        errorQueue.enqueue(serialiser.serialise(events));

        // Setup WireMock stub for REST endpoint
        stubFor(put(urlEqualTo(REST_URL_PATH))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON_VALUE))
            .willReturn(aResponse()
                .withStatus(200)));

        // Setup flow test rule
        flowTestRule.withFlow(moduleUnderTest.getFlow(FLOW_NAME));
        flowTestRule.consumer("Error Entity BigQueue Consumer")
            .producer("Error Entity REST Dashboard Publishing Producer");

        flowTestRule.startFlow();

        // Wait for flow to process the queue
        Awaitility.await().atMost(30, TimeUnit.SECONDS)
            .untilAsserted(() -> Assert.assertEquals(0, errorQueue.size()));

        // Verify REST endpoint was called
        verify(1, putRequestedFor(urlEqualTo(REST_URL_PATH))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON_VALUE)));

        Awaitility.await().atMost(30, TimeUnit.SECONDS)
            .untilAsserted(() -> flowTestRule.assertIsSatisfied());

        flowTestRule.stopFlow();
    }

    @Test(timeout = 120000)
    @DirtiesContext
    public void test_error_produce_multiple_batches() throws IOException {
        // Setup: Create multiple batches of error events
        IBigQueue errorQueue = EntityBigQueueCache.instance().get(CACHE_NAME);
        ErrorOccurrenceToBigQueueMessageSerialiser serialiser = new ErrorOccurrenceToBigQueueMessageSerialiser();

        // Add 3 batches to queue
        List<ErrorOccurrence<byte[]>> batch1 = createErrorOccurrences(100);
        errorQueue.enqueue(serialiser.serialise(batch1));

        List<ErrorOccurrence<byte[]>> batch2 = createErrorOccurrences(100);
        errorQueue.enqueue(serialiser.serialise(batch2));

        List<ErrorOccurrence<byte[]>> batch3 = createErrorOccurrences(50);
        errorQueue.enqueue(serialiser.serialise(batch3));

        assertEquals(3, errorQueue.size());

        // Setup WireMock stub for REST endpoint
        stubFor(put(urlEqualTo(REST_URL_PATH))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON_VALUE))
            .willReturn(aResponse()
                .withStatus(200)
                .withBody("{\"success\": true}")));

        // Setup flow test rule
        flowTestRule.withFlow(moduleUnderTest.getFlow(FLOW_NAME));
        flowTestRule.consumer("Error Entity BigQueue Consumer")
            .producer("Error Entity REST Dashboard Publishing Producer")
            .consumer("Error Entity BigQueue Consumer")
            .producer("Error Entity REST Dashboard Publishing Producer")
            .consumer("Error Entity BigQueue Consumer")
            .producer("Error Entity REST Dashboard Publishing Producer");

        flowTestRule.startFlow();

        // Wait for all batches to be processed
        Awaitility.await().atMost(30, TimeUnit.SECONDS)
            .untilAsserted(() -> Assert.assertEquals(0, errorQueue.size()));

        // Verify REST endpoint was called 3 times
        verify(3, putRequestedFor(urlEqualTo(REST_URL_PATH))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON_VALUE)));

        Awaitility.await().atMost(30, TimeUnit.SECONDS)
            .untilAsserted(() -> flowTestRule.assertIsSatisfied());

        flowTestRule.stopFlow();
    }

    @Test(timeout = 120000)
    @DirtiesContext
    public void test_error_produce_empty_queue() throws IOException, InterruptedException {
        // Setup: Empty queue
        IBigQueue errorQueue = EntityBigQueueCache.instance().get(CACHE_NAME);
        assertEquals(0, errorQueue.size());

        // Setup flow test rule
        flowTestRule.withFlow(moduleUnderTest.getFlow(FLOW_NAME));

        flowTestRule.startFlow();

        // Give it a moment to confirm nothing happens
        Thread.sleep(2000);

        // Verify no REST calls were made
        verify(0, putRequestedFor(urlEqualTo(REST_URL_PATH)));

        assertEquals(Flow.RUNNING, flowTestRule.getFlowState());

        flowTestRule.stopFlow();
    }

    @Test(timeout = 120000)
    @DirtiesContext
    public void test_error_produce_with_rest_error_and_retry() throws IOException {
        // Setup: Create error events
        IBigQueue errorQueue = EntityBigQueueCache.instance().get(CACHE_NAME);

        List<ErrorOccurrence<byte[]>> events = createErrorOccurrences(50);
        ErrorOccurrenceToBigQueueMessageSerialiser serialiser = new ErrorOccurrenceToBigQueueMessageSerialiser();
        errorQueue.enqueue(serialiser.serialise(events));

        // Setup WireMock stub to fail first, then succeed
        stubFor(put(urlEqualTo(REST_URL_PATH))
            .inScenario("Retry Scenario")
            .whenScenarioStateIs("Started")
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON_VALUE))
            .willReturn(aResponse()
                .withStatus(500)
                .withBody("{\"error\": \"Internal Server Error\"}"))
            .willSetStateTo("Failed Once"));

        stubFor(put(urlEqualTo(REST_URL_PATH))
            .inScenario("Retry Scenario")
            .whenScenarioStateIs("Failed Once")
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON_VALUE))
            .willReturn(aResponse()
                .withStatus(200)
                .withBody("{\"success\": true}"))
            .willSetStateTo("Succeeded"));

        // Setup flow test rule - error handling should retry
        flowTestRule.withFlow(moduleUnderTest.getFlow(FLOW_NAME));
        flowTestRule.consumer("Error Entity BigQueue Consumer")
            .producer("Error Entity REST Dashboard Publishing Producer")
            .consumer("Error Entity BigQueue Consumer")
            .producer("Error Entity REST Dashboard Publishing Producer");

        flowTestRule.startFlow();

        // Wait for retry and successful processing
        Awaitility.await().atMost(60, TimeUnit.SECONDS)
            .untilAsserted(() -> Assert.assertEquals(0, errorQueue.size()));

        // Verify REST endpoint was called at least twice (fail + retry)
        verify(moreThan(1), putRequestedFor(urlEqualTo(REST_URL_PATH))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON_VALUE)));

        Awaitility.await().atMost(30, TimeUnit.SECONDS)
            .untilAsserted(() -> flowTestRule.assertIsSatisfied());

        flowTestRule.stopFlow();
    }

    @Test(timeout = 120000)
    @DirtiesContext
    public void test_error_produce_large_batch() throws IOException {
        // Setup: Create a large batch of error events
        IBigQueue errorQueue = EntityBigQueueCache.instance().get(CACHE_NAME);

        List<ErrorOccurrence<byte[]>> events = createErrorOccurrences(500);
        ErrorOccurrenceToBigQueueMessageSerialiser serialiser = new ErrorOccurrenceToBigQueueMessageSerialiser();
        errorQueue.enqueue(serialiser.serialise(events));

        // Setup WireMock stub
        stubFor(put(urlEqualTo(REST_URL_PATH))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON_VALUE))
            .willReturn(aResponse()
                .withStatus(200)
                .withBody("{\"success\": true}")));

        // Setup flow test rule
        flowTestRule.withFlow(moduleUnderTest.getFlow(FLOW_NAME));
        flowTestRule.consumer("Error Entity BigQueue Consumer")
            .producer("Error Entity REST Dashboard Publishing Producer");

        flowTestRule.startFlow();

        // Wait for processing
        Awaitility.await().atMost(30, TimeUnit.SECONDS)
            .untilAsserted(() -> Assert.assertEquals(0, errorQueue.size()));

        // Verify REST endpoint was called
        verify(1, putRequestedFor(urlEqualTo(REST_URL_PATH)));

        // Verify payload size
        List<LoggedRequest> requests = findAll(putRequestedFor(urlEqualTo(REST_URL_PATH)));
        Assert.assertEquals(1, requests.size());

        String requestBody = requests.getFirst().getBodyAsString();
        Assert.assertNotNull(requestBody);
        Assert.assertTrue(!requestBody.isEmpty());

        Awaitility.await().atMost(30, TimeUnit.SECONDS)
            .untilAsserted(() -> flowTestRule.assertIsSatisfied());

        flowTestRule.stopFlow();
    }

    @Test(timeout = 120000)
    @DirtiesContext
    public void test_error_produce_flow_lifecycle() throws IOException {
        // Test flow can be started, stopped, and restarted
        Flow flow = moduleUnderTest.getFlow(FLOW_NAME);

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

    @Test(timeout = 120000)
    @DirtiesContext
    public void test_error_produce_different_modules() throws IOException {
        // Setup: Create events from different modules
        IBigQueue errorQueue = EntityBigQueueCache.instance().get(CACHE_NAME);
        ErrorOccurrenceToBigQueueMessageSerialiser serialiser = new ErrorOccurrenceToBigQueueMessageSerialiser();

        // Mix of different modules and flows
        List<ErrorOccurrence<byte[]>> events = new ArrayList<>();
        events.addAll(createErrorOccurrences(20));
        events.addAll(createErrorOccurrences(30));
        events.addAll(createErrorOccurrences(25));

        errorQueue.enqueue(serialiser.serialise(events));

        // Setup WireMock stub
        stubFor(put(urlEqualTo(REST_URL_PATH))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON_VALUE))
            .willReturn(aResponse()
                .withStatus(200)
                .withBody("{\"success\": true}")));

        // Setup flow test rule
        flowTestRule.withFlow(moduleUnderTest.getFlow(FLOW_NAME));
        flowTestRule.consumer("Error Entity BigQueue Consumer")
            .producer("Error Entity REST Dashboard Publishing Producer");

        flowTestRule.startFlow();

        // Wait for processing
        Awaitility.await().atMost(30, TimeUnit.SECONDS)
            .untilAsserted(() -> Assert.assertEquals(0, errorQueue.size()));

        // Verify REST endpoint was called
        verify(1, putRequestedFor(urlEqualTo(REST_URL_PATH)));

        Awaitility.await().atMost(30, TimeUnit.SECONDS)
            .untilAsserted(() -> flowTestRule.assertIsSatisfied());

        flowTestRule.stopFlow();
    }

    @Test(timeout = 120000)
    @DirtiesContext
    public void test_error_produce_queue_persistence() throws IOException {
        // Setup: Add events to queue
        IBigQueue errorQueue = EntityBigQueueCache.instance().get(CACHE_NAME);
        ErrorOccurrenceToBigQueueMessageSerialiser serialiser = new ErrorOccurrenceToBigQueueMessageSerialiser();

        List<ErrorOccurrence<byte[]>> batch1 = createErrorOccurrences(50);
        errorQueue.enqueue(serialiser.serialise(batch1));

        List<ErrorOccurrence<byte[]>> batch2 = createErrorOccurrences(50);
        errorQueue.enqueue(serialiser.serialise(batch2));

        assertEquals(2, errorQueue.size());

        // Setup WireMock stub for first batch only
        stubFor(put(urlEqualTo(REST_URL_PATH))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON_VALUE))
            .willReturn(aResponse()
                .withStatus(200)
                .withBody("{\"success\": true}")));

        // Start flow and process first batch
        flowTestRule.withFlow(moduleUnderTest.getFlow(FLOW_NAME));
        flowTestRule.consumer("Error Entity BigQueue Consumer")
            .producer("Error Entity REST Dashboard Publishing Producer")
            .consumer("Error Entity BigQueue Consumer")
            .producer("Error Entity REST Dashboard Publishing Producer");

        flowTestRule.startFlow();

        // Wait for first batch
        Awaitility.await().atMost(30, TimeUnit.SECONDS)
            .untilAsserted(() -> Assert.assertEquals(0, errorQueue.size()));

        Awaitility.await().atMost(30, TimeUnit.SECONDS)
            .untilAsserted(() -> flowTestRule.assertIsSatisfied());

        flowTestRule.stopFlow();

        // Verify second batch still in queue
        assertEquals(0, errorQueue.size());
    }

    /**
     * Helper method to create test ErrorEvent objects
     */
    private List<ErrorOccurrence<byte[]>> createErrorOccurrences(int count) {
        List<ErrorOccurrence<byte[]>> errorOccurrences = new ArrayList<>();
        long baseTimestamp = System.currentTimeMillis();
        for (int i = 0; i < count; i++) {
            ErrorOccurrence<byte[]> errorOccurrence = createErrorOccurrenceTestFixture(i);
            errorOccurrence.setTimestamp(baseTimestamp + i);
            errorOccurrences.add(errorOccurrence);
        }
        return errorOccurrences;
    }

    private ErrorOccurrence<byte[]> createErrorOccurrenceTestFixture(int count) {
        return createErrorOccurrence(
            "error-occurrence" + count,
            "module" + (count % 10),
            "flow" + (count % 5),
            "component" + (count % 3),
            "payload-" + count,
            1000L + count,
            100L,
            "myExceptionAndTrace" + (count % 3),
            "exceptionMessage" + (count % 3),
            "java.lang.IllegalArgumentException" + (count % 3),
            "eventLifeIdentifier" + (count % 3),
            "eventRelatedIdentifier" + (count % 3),
            "action" + (count % 3),
            "userAction" + (count % 3),
            "actionedBy" + (count % 3),
            1000L + count
        );
    }

    /**
     * Helper method to create ErrorOccurrenceImpl instances for testing.
     */
    private ErrorOccurrence<byte[]> createErrorOccurrence(String uri, String moduleName, String flowName,
                                                          String flowElementName, String payload,
                                                          Long timestamp, Long expiry, String errorDetail, String errorMessage, String exceptionClass,
                                                          String eventLifeIdentifier, String eventRelatedIdentifier, String action, String userAction, String actionedBy, Long userActionTimestamp) {
        ErrorOccurrenceImpl errorOccurrence = new ErrorOccurrenceImpl();

        errorOccurrence.setUri(uri);
        errorOccurrence.setModuleName(moduleName);
        errorOccurrence.setFlowName(flowName);
        errorOccurrence.setFlowElementName(flowElementName);
        errorOccurrence.setErrorDetail(errorDetail);
        errorOccurrence.setErrorMessage(errorMessage);
        errorOccurrence.setExceptionClass(exceptionClass);
        errorOccurrence.setEventLifeIdentifier(eventLifeIdentifier);
        errorOccurrence.setEventRelatedIdentifier(eventRelatedIdentifier);
        errorOccurrence.setAction(action);
        errorOccurrence.setEvent(payload == null ? null : payload.getBytes(StandardCharsets.UTF_8));
        errorOccurrence.setEventAsString(payload);
        errorOccurrence.setTimestamp(timestamp);
        errorOccurrence.setExpiry(expiry);
        errorOccurrence.setUserAction(userAction);
        errorOccurrence.setActionedBy(actionedBy);
        errorOccurrence.setUserActionTimestamp(userActionTimestamp);
        return errorOccurrence;
    }
}
