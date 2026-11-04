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
import org.ikasan.dashboard.dto.replay.ReplayEventImpl;
import org.ikasan.ootb.data.sharing.module.Application;
import org.ikasan.ootb.data.sharing.module.cache.EntityBigQueueCache;
import org.ikasan.ootb.data.sharing.module.serialiser.ReplayEventToBigQueueMessageSerialiser;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.module.Module;
import org.ikasan.spec.replay.ReplayEvent;
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

/** Integration tests for the assembled replay producer flow. */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {Application.class},
    properties = {
        "spring.main.allow-bean-definition-overriding=true",
        "data-sharing.upstreamDashboard=http://localhost:9080"
    },
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = {"/cleanDatabaseTables.sql"}, executionPhase = AFTER_TEST_METHOD)
public class ReplayEntityProducerFlowTest {

    private static final String REST_URL_PATH = "/rest/harvest/replay";
    private static final String CACHE_NAME = "DASHBOARD_1-Ikasan-ESB-Dashboard-REPLAY-queue";
    private static final String FLOW_NAME = "DASHBOARD_1 Ikasan ESB Dashboard Replay Entity Producer Flow";

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
        flowTestRule.stopFlow();
        EntityBigQueueCache.instance().get(CACHE_NAME).removeAll();
    }

    @Test(timeout = 120000)
    @DirtiesContext
    public void test_empty_checkpoint_batch_is_consumed_without_rest_request() throws IOException {
        IBigQueue replayQueue = EntityBigQueueCache.instance().get(CACHE_NAME);
        replayQueue.enqueue(new ReplayEventToBigQueueMessageSerialiser().serialise(List.of()));
        flowTestRule.withFlow(moduleUnderTest.getFlow(FLOW_NAME));
        flowTestRule.consumer("Replay Entity BigQueue Consumer")
            .producer("Replay Entity REST Dashboard Publishing Producer");
        try {
            flowTestRule.startFlow();
            Awaitility.await().atMost(15, TimeUnit.SECONDS).untilAsserted(() -> {
                assertEquals(0L, replayQueue.size());
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
    public void test_replay_produce_single_batch() throws IOException {
        // Setup: Create replay events and add to queue
        IBigQueue replayQueue = EntityBigQueueCache.instance().get(CACHE_NAME);

        List<ReplayEvent> events = createReplayEvents(50);
        ReplayEventToBigQueueMessageSerialiser serialiser = new ReplayEventToBigQueueMessageSerialiser();
        replayQueue.enqueue(serialiser.serialise(events));

        // Setup WireMock stub for REST endpoint
        stubFor(put(urlEqualTo(REST_URL_PATH))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON_VALUE))
            .willReturn(aResponse()
                .withStatus(200)));

        // Setup flow test rule
        flowTestRule.withFlow(moduleUnderTest.getFlow(FLOW_NAME));
        flowTestRule.consumer("Replay Entity BigQueue Consumer")
            .producer("Replay Entity REST Dashboard Publishing Producer");

        flowTestRule.startFlow();

        // Wait for flow to process the queue
        Awaitility.await().atMost(30, TimeUnit.SECONDS)
            .untilAsserted(() -> Assert.assertEquals(0, replayQueue.size()));

        // Verify REST endpoint was called
        verify(1, putRequestedFor(urlEqualTo(REST_URL_PATH))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON_VALUE))
            .withRequestBody(equalToJson(objectMapper.writeValueAsString(events))));

        Awaitility.await().atMost(30, TimeUnit.SECONDS)
            .untilAsserted(() -> flowTestRule.assertIsSatisfied());

        flowTestRule.stopFlow();
    }

    @Test(timeout = 120000)
    @DirtiesContext
    public void test_replay_produce_multiple_batches() throws IOException {
        // Setup: Create multiple batches of replay events
        IBigQueue replayQueue = EntityBigQueueCache.instance().get(CACHE_NAME);
        ReplayEventToBigQueueMessageSerialiser serialiser = new ReplayEventToBigQueueMessageSerialiser();

        // Add 3 batches to queue
        List<ReplayEvent> batch1 = createReplayEvents(100);
        replayQueue.enqueue(serialiser.serialise(batch1));

        List<ReplayEvent> batch2 = createReplayEvents(100);
        replayQueue.enqueue(serialiser.serialise(batch2));

        List<ReplayEvent> batch3 = createReplayEvents(50);
        replayQueue.enqueue(serialiser.serialise(batch3));

        assertEquals(3, replayQueue.size());

        // Setup WireMock stub for REST endpoint
        stubFor(put(urlEqualTo(REST_URL_PATH))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON_VALUE))
            .willReturn(aResponse()
                .withStatus(200)
                .withBody("{\"success\": true}")));

        // Setup flow test rule
        flowTestRule.withFlow(moduleUnderTest.getFlow(FLOW_NAME));
        flowTestRule.consumer("Replay Entity BigQueue Consumer")
            .producer("Replay Entity REST Dashboard Publishing Producer")
            .consumer("Replay Entity BigQueue Consumer")
            .producer("Replay Entity REST Dashboard Publishing Producer")
            .consumer("Replay Entity BigQueue Consumer")
            .producer("Replay Entity REST Dashboard Publishing Producer");

        flowTestRule.startFlow();

        // Wait for all batches to be processed
        Awaitility.await().atMost(30, TimeUnit.SECONDS)
            .untilAsserted(() -> Assert.assertEquals(0, replayQueue.size()));

        // Verify REST endpoint was called 3 times
        verify(3, putRequestedFor(urlEqualTo(REST_URL_PATH))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON_VALUE)));

        Awaitility.await().atMost(30, TimeUnit.SECONDS)
            .untilAsserted(() -> flowTestRule.assertIsSatisfied());

        flowTestRule.stopFlow();
    }

    @Test(timeout = 120000)
    @DirtiesContext
    public void test_replay_produce_with_rest_error_and_retry() throws IOException {
        // Setup: Create replay events
        IBigQueue replayQueue = EntityBigQueueCache.instance().get(CACHE_NAME);

        List<ReplayEvent> events = createReplayEvents(50);
        ReplayEventToBigQueueMessageSerialiser serialiser = new ReplayEventToBigQueueMessageSerialiser();
        replayQueue.enqueue(serialiser.serialise(events));

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
        flowTestRule.consumer("Replay Entity BigQueue Consumer")
            .producer("Replay Entity REST Dashboard Publishing Producer")
            .consumer("Replay Entity BigQueue Consumer")
            .producer("Replay Entity REST Dashboard Publishing Producer");

        flowTestRule.startFlow();

        // Wait for retry and successful processing
        Awaitility.await().atMost(60, TimeUnit.SECONDS)
            .untilAsserted(() -> Assert.assertEquals(0, replayQueue.size()));

        // Verify REST endpoint was called at least twice (fail + retry)
        verify(moreThan(1), putRequestedFor(urlEqualTo(REST_URL_PATH))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON_VALUE)));

        Awaitility.await().atMost(30, TimeUnit.SECONDS)
            .untilAsserted(() -> flowTestRule.assertIsSatisfied());

        flowTestRule.stopFlow();
    }

    /**
     * Helper method to create test replay events
     */
    private List<ReplayEvent> createReplayEvents(int count) {
        List<ReplayEvent> replayEvents = new ArrayList<>();
        long baseTimestamp = System.currentTimeMillis();
        for (int i = 0; i < count; i++) {
            ReplayEvent replayEvent = createReplayEventTestFixture(i);
            replayEvent.setTimestamp(baseTimestamp + i);
            replayEvents.add(replayEvent);
        }
        return replayEvents;
    }

    private ReplayEvent createReplayEventTestFixture(int index) {
        ReplayEventImpl event = new ReplayEventImpl();
        event.setId("replay-" + index);
        event.setModuleName("module" + (index % 2 + 1));
        event.setFlowName("flow-" + index);
        event.setEventId("event-" + index);
        event.setTimestamp(1000L + index);
        event.setEvent(("payload-" + index).getBytes(StandardCharsets.UTF_8));
        return event;
    }
}
