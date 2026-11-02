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
import org.awaitility.Awaitility;
import org.ikasan.bigqueue.IBigQueue;
import org.ikasan.dashboard.dto.exclusion.ExclusionEventImpl;
import org.ikasan.ootb.data.sharing.module.Application;
import org.ikasan.ootb.data.sharing.module.cache.EntityBigQueueCache;
import org.ikasan.ootb.data.sharing.module.serialiser.ExclusionEventToBigQueueMessageSerialiser;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.module.Module;
import org.ikasan.spec.exclusion.ExclusionEvent;
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

/** Integration tests for the assembled exclusion producer flow. */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {Application.class},
    properties = {
        "spring.main.allow-bean-definition-overriding=true",
        "data-sharing.upstreamDashboard=http://localhost:9080"
    },
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = {"/cleanDatabaseTables.sql"}, executionPhase = AFTER_TEST_METHOD)
public class ExclusionEntityProducerFlowTest {

    private static final String REST_URL_PATH = "/rest/harvest/exclusions";
    private static final String CACHE_NAME = "DASHBOARD_1-Ikasan-ESB-Dashboard-EXCLUSION-queue";
    private static final String FLOW_NAME = "DASHBOARD_1 Ikasan ESB Dashboard Exclusion Entity Producer Flow";

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
        flowTestRule.stopFlow();
        EntityBigQueueCache.instance().get(CACHE_NAME).removeAll();
    }

    @Test
    @DirtiesContext
    public void test_empty_checkpoint_batch_is_consumed_without_rest_request() throws IOException {
        IBigQueue exclusionQueue = EntityBigQueueCache.instance().get(CACHE_NAME);
        exclusionQueue.enqueue(new ExclusionEventToBigQueueMessageSerialiser().serialise(List.of()));
        flowTestRule.withFlow(moduleUnderTest.getFlow(FLOW_NAME));
        flowTestRule.consumer("Exclusion Entity BigQueue Consumer")
            .producer("Exclusion Entity REST Dashboard Publishing Producer");
        try {
            flowTestRule.startFlow();
            Awaitility.await().atMost(15, TimeUnit.SECONDS).untilAsserted(() -> {
                assertEquals(0L, exclusionQueue.size());
                flowTestRule.assertIsSatisfied();
            });
            verify(0, putRequestedFor(urlEqualTo(REST_URL_PATH)));
            assertEquals(Flow.RUNNING, flowTestRule.getFlowState());
        } finally {
            flowTestRule.stopFlow();
        }
    }

    @Test
    @DirtiesContext
    public void test_exclusion_produce_single_batch() throws IOException {
        // Setup: Create exclusion events and add to queue
        IBigQueue exclusionQueue = EntityBigQueueCache.instance().get(CACHE_NAME);

        List<ExclusionEvent<String>> events = createExclusionEvents(50);
        ExclusionEventToBigQueueMessageSerialiser serialiser = new ExclusionEventToBigQueueMessageSerialiser();
        exclusionQueue.enqueue(serialiser.serialise(events));

        // Setup WireMock stub for REST endpoint
        stubFor(put(urlEqualTo(REST_URL_PATH))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON_VALUE))
            .willReturn(aResponse()
                .withStatus(200)));

        // Setup flow test rule
        flowTestRule.withFlow(moduleUnderTest.getFlow(FLOW_NAME));
        flowTestRule.consumer("Exclusion Entity BigQueue Consumer")
            .producer("Exclusion Entity REST Dashboard Publishing Producer");

        flowTestRule.startFlow();

        // Wait for flow to process the queue
        Awaitility.await().atMost(30, TimeUnit.SECONDS)
            .untilAsserted(() -> Assert.assertEquals(0, exclusionQueue.size()));

        // Verify REST endpoint was called
        verify(1, putRequestedFor(urlEqualTo(REST_URL_PATH))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON_VALUE))
            .withRequestBody(equalToJson(objectMapper.writeValueAsString(events))));

        Awaitility.await().atMost(30, TimeUnit.SECONDS)
            .untilAsserted(() -> flowTestRule.assertIsSatisfied());

        flowTestRule.stopFlow();
    }

    @Test
    @DirtiesContext
    public void test_exclusion_produce_multiple_batches() throws IOException {
        // Setup: Create multiple batches of exclusion events
        IBigQueue exclusionQueue = EntityBigQueueCache.instance().get(CACHE_NAME);
        ExclusionEventToBigQueueMessageSerialiser serialiser = new ExclusionEventToBigQueueMessageSerialiser();

        // Add 3 batches to queue
        List<ExclusionEvent<String>> batch1 = createExclusionEvents(100);
        exclusionQueue.enqueue(serialiser.serialise(batch1));

        List<ExclusionEvent<String>> batch2 = createExclusionEvents(100);
        exclusionQueue.enqueue(serialiser.serialise(batch2));

        List<ExclusionEvent<String>> batch3 = createExclusionEvents(50);
        exclusionQueue.enqueue(serialiser.serialise(batch3));

        assertEquals(3, exclusionQueue.size());

        // Setup WireMock stub for REST endpoint
        stubFor(put(urlEqualTo(REST_URL_PATH))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON_VALUE))
            .willReturn(aResponse()
                .withStatus(200)
                .withBody("{\"success\": true}")));

        // Setup flow test rule
        flowTestRule.withFlow(moduleUnderTest.getFlow(FLOW_NAME));
        flowTestRule.consumer("Exclusion Entity BigQueue Consumer")
            .producer("Exclusion Entity REST Dashboard Publishing Producer")
            .consumer("Exclusion Entity BigQueue Consumer")
            .producer("Exclusion Entity REST Dashboard Publishing Producer")
            .consumer("Exclusion Entity BigQueue Consumer")
            .producer("Exclusion Entity REST Dashboard Publishing Producer");

        flowTestRule.startFlow();

        // Wait for all batches to be processed
        Awaitility.await().atMost(30, TimeUnit.SECONDS)
            .untilAsserted(() -> Assert.assertEquals(0, exclusionQueue.size()));

        // Verify REST endpoint was called 3 times
        verify(3, putRequestedFor(urlEqualTo(REST_URL_PATH))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON_VALUE)));

        Awaitility.await().atMost(30, TimeUnit.SECONDS)
            .untilAsserted(() -> flowTestRule.assertIsSatisfied());

        flowTestRule.stopFlow();
    }

    @Test
    @DirtiesContext
    public void test_exclusion_produce_with_rest_error_and_retry() throws IOException {
        // Setup: Create exclusion events
        IBigQueue exclusionQueue = EntityBigQueueCache.instance().get(CACHE_NAME);

        List<ExclusionEvent<String>> events = createExclusionEvents(50);
        ExclusionEventToBigQueueMessageSerialiser serialiser = new ExclusionEventToBigQueueMessageSerialiser();
        exclusionQueue.enqueue(serialiser.serialise(events));

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
        flowTestRule.consumer("Exclusion Entity BigQueue Consumer")
            .producer("Exclusion Entity REST Dashboard Publishing Producer")
            .consumer("Exclusion Entity BigQueue Consumer")
            .producer("Exclusion Entity REST Dashboard Publishing Producer");

        flowTestRule.startFlow();

        // Wait for retry and successful processing
        Awaitility.await().atMost(60, TimeUnit.SECONDS)
            .untilAsserted(() -> Assert.assertEquals(0, exclusionQueue.size()));

        // Verify REST endpoint was called at least twice (fail + retry)
        verify(moreThan(1), putRequestedFor(urlEqualTo(REST_URL_PATH))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON_VALUE)));

        Awaitility.await().atMost(30, TimeUnit.SECONDS)
            .untilAsserted(() -> flowTestRule.assertIsSatisfied());

        flowTestRule.stopFlow();
    }

    /**
     * Helper method to create test exclusion events
     */
    private List<ExclusionEvent<String>> createExclusionEvents(int count) {
        List<ExclusionEvent<String>> exclusionEvents = new ArrayList<>();
        long baseTimestamp = System.currentTimeMillis();
        for (int i = 0; i < count; i++) {
            ExclusionEvent<String> exclusionEvent = createExclusionEventTestFixture(i);
            exclusionEvent.setTimestamp(baseTimestamp + i);
            exclusionEvents.add(exclusionEvent);
        }
        return exclusionEvents;
    }

    private ExclusionEvent<String> createExclusionEventTestFixture(int index) {
        ExclusionEventImpl event = new ExclusionEventImpl();
        event.setId("exclusion-" + index);
        event.setModuleName("module" + (index % 2 + 1));
        event.setFlowName("flow-" + index);
        event.setIdentifier("event-" + index);
        event.setErrorUri("error-" + index);
        event.setTimestamp(1000L + index);
        event.setHarvested(false);
        event.setEvent(("payload-" + index).getBytes(StandardCharsets.UTF_8));
        return event;
    }
}
