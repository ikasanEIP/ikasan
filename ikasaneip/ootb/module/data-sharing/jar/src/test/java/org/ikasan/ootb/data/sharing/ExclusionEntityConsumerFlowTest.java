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

import org.ikasan.spec.configuration.ConfiguredResource;
import org.ikasan.spec.configuration.ConfigurationParameter;
import org.ikasan.configurationService.service.ConfiguredResourceConfigurationService;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.github.tomakehurst.wiremock.stubbing.Scenario;
import org.awaitility.Awaitility;
import org.ikasan.bigqueue.IBigQueue;
import org.ikasan.dashboard.dto.exclusion.ExclusionEventImpl;
import org.ikasan.ootb.data.sharing.module.Application;
import org.ikasan.ootb.data.sharing.module.cache.EntityBigQueueCache;
import org.ikasan.ootb.data.sharing.module.component.endpoint.consumer.configuration.ExclusionConsumerConfiguration;
import org.ikasan.ootb.data.sharing.module.serialiser.ExclusionEventToBigQueueMessageSerialiser;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.module.Module;
import org.ikasan.spec.exclusion.ExclusionEvent;
import org.ikasan.testharness.flow.rule.IkasanFlowTestRule;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
 * This test class supports the <code>vanilla integration module</code> application.
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
public class ExclusionEntityConsumerFlowTest {

    private static final String REST_URL_PATH = "/rest/data-sharing/exclusions";
    private static final String CACHE_NAME = "DASHBOARD_1-Ikasan-ESB-Dashboard-EXCLUSION-queue";
    private static final String FLOW_NAME = "DASHBOARD_1 Ikasan ESB Dashboard Exclusion Entity Consumer Flow";

    @ClassRule
    public static WireMockRule wireMockRule = new WireMockRule(
        WireMockConfiguration.options().port(9080));

    @Autowired
    private Module<Flow> moduleUnderTest;

    @Autowired
    private ConfiguredResourceConfigurationService configurationService;

    @Value( "${big.queue.consumer.queueDir}" )
    private String queueDir;

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
        // These fixtures intentionally exercise historical windows beginning at the epoch.
        var consumer = (ConfiguredResource<ExclusionConsumerConfiguration>)
            moduleUnderTest.getFlow(FLOW_NAME).getFlowElement("Exclusion Entity Scheduled Consumer").getFlowComponent();
        consumer.getConfiguration().setQueryTimestamp(0L);
        consumer.getConfiguration().setSafetyLagMilliseconds(0L);
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
    public void test_exclusion_consume_success() throws IOException {
        // Setup wiremock rest service stubs
        this.createCountStub(257, 0, 100000);
        this.createCountStub(36, 100000, 200000);
        this.createQueryStub(100, 257, 0, 100000, 100, 0);
        this.createQueryStub(100, 257, 0, 100000, 100, 100);
        this.createQueryStub(57, 257, 0, 100000, 100, 200);
        this.createQueryStub(0, 257, 0, 100000, 100, 300);
        this.createQueryStub(36, 36, 100000, 200000, 100, 0);

        flowTestRule.withFlow(moduleUnderTest.getFlow(FLOW_NAME));

        flowTestRule.consumer("Exclusion Entity Scheduled Consumer")
            .producer("Exclusion Entity Big Queue Producer")
            .consumer("Exclusion Entity Scheduled Consumer")
            .producer("Exclusion Entity Big Queue Producer")
            .consumer("Exclusion Entity Scheduled Consumer")
            .producer("Exclusion Entity Big Queue Producer")
            .consumer("Exclusion Entity Scheduled Consumer")
            .producer("Exclusion Entity Big Queue Producer");

        flowTestRule.startFlow();
        flowTestRule.fireScheduledConsumer();

        assertEquals(Flow.RUNNING, flowTestRule.getFlowState());

        IBigQueue exclusionQueue = EntityBigQueueCache.instance().get(CACHE_NAME);

        Awaitility.await().atMost(30, TimeUnit.SECONDS)
            .untilAsserted(() -> Assert.assertEquals(3, exclusionQueue.size()));

        ExclusionEventToBigQueueMessageSerialiser exclusionEventToBigQueueMessageSerialiser
            = new ExclusionEventToBigQueueMessageSerialiser();

        byte[] exclusionEventsBytes = exclusionQueue.dequeue();

        List<ExclusionEvent<String>> exclusionEvents = exclusionEventToBigQueueMessageSerialiser
            .deserialise(exclusionEventsBytes);

        Assert.assertEquals(100, exclusionEvents.size());

        exclusionEventsBytes = exclusionQueue.dequeue();

        exclusionEvents = exclusionEventToBigQueueMessageSerialiser
            .deserialise(exclusionEventsBytes);

        Assert.assertEquals(100, exclusionEvents.size());

        exclusionEventsBytes = exclusionQueue.dequeue();

        exclusionEvents = exclusionEventToBigQueueMessageSerialiser
            .deserialise(exclusionEventsBytes);
        Assert.assertEquals(57, exclusionEvents.size());

        flowTestRule.fireScheduledConsumer();

        Awaitility.await().atMost(30, TimeUnit.SECONDS)
            .untilAsserted(() -> Assert.assertEquals(1, exclusionQueue.size()));

        exclusionEventsBytes = exclusionQueue.dequeue();

        exclusionEvents = exclusionEventToBigQueueMessageSerialiser
            .deserialise(exclusionEventsBytes);

        Assert.assertEquals(36, exclusionEvents.size());

        Awaitility.await().atMost(30, TimeUnit.SECONDS)
            .untilAsserted(() -> flowTestRule.assertIsSatisfied());

        flowTestRule.stopFlow();
        exclusionQueue.removeAll();
    }

    @Test(timeout = 120000)
    @DirtiesContext
    public void test_exclusion_consume_stop_start_flow_to_assert_dynamic_configuration_persisted() throws IOException {
        // Setup wiremock rest service stubs
        this.createCountStub(257, 0, 100000);
        this.createCountStub(36, 100000, 200000);
        this.createQueryStub(100, 257, 0, 100000, 100, 0);
        this.createQueryStub(100, 257, 0, 100000, 100, 100);
        this.createQueryStub(57, 257, 0, 100000, 100, 200);
        this.createQueryStub(0, 257, 0, 100000, 100, 300);
        this.createQueryStub(36, 36, 100000, 200000, 100, 0);

        flowTestRule.withFlow(moduleUnderTest.getFlow(FLOW_NAME));

        flowTestRule.consumer("Exclusion Entity Scheduled Consumer")
            .producer("Exclusion Entity Big Queue Producer")
            .consumer("Exclusion Entity Scheduled Consumer")
            .producer("Exclusion Entity Big Queue Producer")
            .consumer("Exclusion Entity Scheduled Consumer")
            .producer("Exclusion Entity Big Queue Producer");

        flowTestRule.startFlow();
        flowTestRule.fireScheduledConsumer();

        assertEquals(Flow.RUNNING, flowTestRule.getFlowState());

        IBigQueue exclusionQueue = EntityBigQueueCache.instance().get(CACHE_NAME);

        Awaitility.await().atMost(30, TimeUnit.SECONDS)
            .untilAsserted(() -> Assert.assertEquals(3, exclusionQueue.size()));

        ExclusionEventToBigQueueMessageSerialiser exclusionEventToBigQueueMessageSerialiser
            = new ExclusionEventToBigQueueMessageSerialiser();

        byte[] exclusionEventsBytes = exclusionQueue.dequeue();

        List<ExclusionEvent<String>> exclusionEvents = exclusionEventToBigQueueMessageSerialiser
            .deserialise(exclusionEventsBytes);

        Assert.assertEquals(100, exclusionEvents.size());

        exclusionEventsBytes = exclusionQueue.dequeue();

        exclusionEvents = exclusionEventToBigQueueMessageSerialiser
            .deserialise(exclusionEventsBytes);

        Assert.assertEquals(100, exclusionEvents.size());

        exclusionEventsBytes = exclusionQueue.dequeue();

        exclusionEvents = exclusionEventToBigQueueMessageSerialiser
            .deserialise(exclusionEventsBytes);

        Assert.assertEquals(57, exclusionEvents.size());

        Awaitility.await().atMost(30, TimeUnit.SECONDS)
            .untilAsserted(() -> flowTestRule.assertIsSatisfied());

        flowTestRule.stopFlow();

        flowTestRule.startFlow();

        ExclusionConsumerConfiguration configuration = flowTestRule.getComponentConfig("Exclusion Entity Scheduled Consumer"
            , ExclusionConsumerConfiguration.class);

        // Assert that the dynamic configuration loaded as expected!
        assertEquals(100000L, configuration.getQueryTimestamp());

        flowTestRule.fireScheduledConsumer();
        flowTestRule.sleep(2000);

        // Finally, go ahead and confirm that the final batch of messages are picked up.
        Awaitility.await().atMost(30, TimeUnit.SECONDS)
            .untilAsserted(() -> Assert.assertEquals(1, exclusionQueue.size()));

        exclusionEventsBytes = exclusionQueue.dequeue();

        exclusionEvents = exclusionEventToBigQueueMessageSerialiser
            .deserialise(exclusionEventsBytes);

        Assert.assertEquals(36, exclusionEvents.size());

        flowTestRule.stopFlow();
        exclusionQueue.removeAll();
    }

    @Test(timeout = 120000)
    @DirtiesContext
    public void test_exclusion_entity_consume_with_service_error_bad_data_and_retry() throws Exception {
        flowTestRule.withFlow(moduleUnderTest.getFlow(FLOW_NAME));

        // Create stubs for a scenario where we have 250 exclusionEvents requiring paging
        // First count query returns 250
        stubFor(get(urlMatching(REST_URL_PATH + "/count\\?fromTimestamp=0&toTimestamp=99999.*"))
            .withHeader(HttpHeaders.USER_AGENT, equalTo(""))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
            .willReturn(aResponse()
                .withBody(objectMapper.writeValueAsString(Map.of("count", 250)))
                .withStatus(200)
            ));

        // First page: 100 exclusionEvents - will succeed
        stubFor(get(urlEqualTo(REST_URL_PATH + "?fromTimestamp=0&toTimestamp=99999&limit=100&offset=0&moduleNames=module1&moduleNames=module2"))
            .withHeader(HttpHeaders.USER_AGENT, equalTo(""))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
            .willReturn(aResponse()
                .withBody(this.serializeExclusionEvents(this.createExclusionEvents(100), 250, true))
                .withStatus(200)
            ));

        // Second page: 100 exclusionEvents - will be queried twice (once with error, once successfully)
        stubFor(get(urlEqualTo(REST_URL_PATH + "?fromTimestamp=0&toTimestamp=99999&limit=100&offset=100&moduleNames=module1&moduleNames=module2"))
            .inScenario("Retry Scenario")
            .whenScenarioStateIs(Scenario.STARTED)
            .withHeader(HttpHeaders.USER_AGENT, equalTo(""))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
            .willReturn(aResponse()
                .withBody("this is a bad message")
                .withStatus(200)
            )
            .willSetStateTo("Success State"));

        stubFor(get(urlEqualTo(REST_URL_PATH + "?fromTimestamp=0&toTimestamp=99999&limit=100&offset=100&moduleNames=module1&moduleNames=module2"))
            .inScenario("Retry Scenario")
            .whenScenarioStateIs("Success State")
            .withHeader(HttpHeaders.USER_AGENT, equalTo(""))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
            .willReturn(aResponse()
                .withBody(this.serializeExclusionEvents(this.createExclusionEvents(100), 250, true))
                .withStatus(200)
            ));

        // Third page: 50 exclusionEvents
        stubFor(get(urlEqualTo(REST_URL_PATH + "?fromTimestamp=0&toTimestamp=99999&limit=100&offset=200&moduleNames=module1&moduleNames=module2"))
            .withHeader(HttpHeaders.USER_AGENT, equalTo(""))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
            .willReturn(aResponse()
                .withBody(this.serializeExclusionEvents(this.createExclusionEvents(50), 250, false))
                .withStatus(200)
            ));

        IBigQueue exclusionQueue = EntityBigQueueCache.instance().get(CACHE_NAME);

        // Set up expectations: first attempt fails on second page, retry succeeds
        flowTestRule.consumer("Exclusion Entity Scheduled Consumer")
            .producer("Exclusion Entity Big Queue Producer") // First page succeeds
            .consumer("Exclusion Entity Scheduled Consumer")
            .producer("Exclusion Entity Big Queue Producer") // Second page fails - will retry
            .consumer("Exclusion Entity Scheduled Consumer")
            .producer("Exclusion Entity Big Queue Producer") // Second page retry succeeds
            .consumer("Exclusion Entity Scheduled Consumer")
            .producer("Exclusion Entity Big Queue Producer"); // Third page succeeds

        flowTestRule.startFlow();
        flowTestRule.fireScheduledConsumer();

        assertEquals(Flow.RUNNING, flowTestRule.getFlowState());

        ExclusionEventToBigQueueMessageSerialiser serialiser = new ExclusionEventToBigQueueMessageSerialiser();

        // Because the BigQueue producer is not LRCO the first page of data is written
        // to the BigQueue destination. This is fine bacause all entities have an immutable
        // identifier and they will simply be upserted into the destination datastore
        // if written more than one time.
        Awaitility.await().atMost(30, TimeUnit.SECONDS)
            .untilAsserted(() -> assertEquals(1, exclusionQueue.size()));

        // Confirm details of the first page.
        byte[] bytes = exclusionQueue.dequeue();
        List<ExclusionEvent<String>> exclusionEvents = serialiser.deserialise(bytes);
        Assert.assertEquals(100, exclusionEvents.size());

        // In the meantime the consumer has gone into recovery and subsequently
        // recovered. This time all 3 expected pages are written,
        Awaitility.await().atMost(30, TimeUnit.SECONDS)
            .untilAsserted(() -> Assert.assertEquals(3, exclusionQueue.size()));

        // Verify the details pages published to the downstream
        // BigQueue destination.
        bytes = exclusionQueue.dequeue();
        exclusionEvents = serialiser.deserialise(bytes);
        Assert.assertEquals(100, exclusionEvents.size());

        bytes = exclusionQueue.dequeue();
        exclusionEvents = serialiser.deserialise(bytes);
        Assert.assertEquals(100, exclusionEvents.size());

        bytes = exclusionQueue.dequeue();
        exclusionEvents = serialiser.deserialise(bytes);
        Assert.assertEquals(50, exclusionEvents.size());

        Awaitility.await().atMost(30, TimeUnit.SECONDS)
            .untilAsserted(() -> flowTestRule.assertIsSatisfied());

        flowTestRule.stopFlow();
        exclusionQueue.removeAll();

    }

    @Test(timeout = 120000)
    @DirtiesContext
    public void test_exclusion_entity_consume_with_service_http_error_and_retry() throws Exception {
        flowTestRule.withFlow(moduleUnderTest.getFlow(FLOW_NAME));

        // Create stubs for a scenario where we have 250 exclusionEvents requiring paging
        // First count query returns 250
        stubFor(get(urlMatching(REST_URL_PATH + "/count\\?fromTimestamp=0&toTimestamp=99999.*"))
            .withHeader(HttpHeaders.USER_AGENT, equalTo(""))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
            .willReturn(aResponse()
                .withBody(objectMapper.writeValueAsString(Map.of("count", 250)))
                .withStatus(200)
            ));

        // First page: 100 exclusionEvents - will succeed
        stubFor(get(urlEqualTo(REST_URL_PATH + "?fromTimestamp=0&toTimestamp=99999&limit=100&offset=0&moduleNames=module1&moduleNames=module2"))
            .withHeader(HttpHeaders.USER_AGENT, equalTo(""))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
            .willReturn(aResponse()
                .withBody(this.serializeExclusionEvents(this.createExclusionEvents(100), 250, true))
                .withStatus(200)
            ));

        // Second page: 100 exclusionEvents - will be queried twice (once with error, once successfully)
        stubFor(get(urlEqualTo(REST_URL_PATH + "?fromTimestamp=0&toTimestamp=99999&limit=100&offset=100&moduleNames=module1&moduleNames=module2"))
            .inScenario("Retry Scenario")
            .whenScenarioStateIs(Scenario.STARTED)
            .withHeader(HttpHeaders.USER_AGENT, equalTo(""))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
            .willReturn(aResponse()
                .withStatus(404)
            )
            .willSetStateTo("Success State"));

        stubFor(get(urlEqualTo(REST_URL_PATH + "?fromTimestamp=0&toTimestamp=99999&limit=100&offset=100&moduleNames=module1&moduleNames=module2"))
            .inScenario("Retry Scenario")
            .whenScenarioStateIs("Success State")
            .withHeader(HttpHeaders.USER_AGENT, equalTo(""))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
            .willReturn(aResponse()
                .withBody(this.serializeExclusionEvents(this.createExclusionEvents(100), 250, true))
                .withStatus(200)
            ));

        // Third page: 50 exclusionEvents
            stubFor(get(urlEqualTo(REST_URL_PATH + "?fromTimestamp=0&toTimestamp=99999&limit=100&offset=200&moduleNames=module1&moduleNames=module2"))
            .withHeader(HttpHeaders.USER_AGENT, equalTo(""))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
            .willReturn(aResponse()
                .withBody(this.serializeExclusionEvents(this.createExclusionEvents(50), 250, false))
                .withStatus(200)
            ));

        IBigQueue exclusionQueue = EntityBigQueueCache.instance().get(CACHE_NAME);

        // Set up expectations: first attempt fails on second page, retry succeeds
        flowTestRule.consumer("Exclusion Entity Scheduled Consumer")
            .producer("Exclusion Entity Big Queue Producer") // First page succeeds
            .consumer("Exclusion Entity Scheduled Consumer")
            .producer("Exclusion Entity Big Queue Producer") // Second page fails - will retry
            .consumer("Exclusion Entity Scheduled Consumer")
            .producer("Exclusion Entity Big Queue Producer") // Second page retry succeeds
            .consumer("Exclusion Entity Scheduled Consumer")
            .producer("Exclusion Entity Big Queue Producer"); // Third page succeeds

        flowTestRule.startFlow();
        flowTestRule.fireScheduledConsumer();

        assertEquals(Flow.RUNNING, flowTestRule.getFlowState());

        ExclusionEventToBigQueueMessageSerialiser serialiser = new ExclusionEventToBigQueueMessageSerialiser();

        // Because the BigQueue producer is not LRCO the first page of data is written
        // to the BigQueue destination. This is fine bacause all entities have an immutable
        // identifier and they will simply be upserted into the destination datastore
        // if written more than one time.
        Awaitility.await().atMost(30, TimeUnit.SECONDS)
            .untilAsserted(() -> Assert.assertEquals(1, exclusionQueue.size()));

        // Confirm details of the first page.
        byte[] bytes = exclusionQueue.dequeue();
        List<ExclusionEvent<String>> exclusionEvents = serialiser.deserialise(bytes);
        Assert.assertEquals(100, exclusionEvents.size());

        // In the meantime the consumer has gone into recovery and subsequently
        // recovered. This time all 3 expected pages are written,
        Awaitility.await().atMost(30, TimeUnit.SECONDS)
            .untilAsserted(() -> Assert.assertEquals(3, exclusionQueue.size()));

        // Verify the details pages published to the downstream
        // BigQueue destination.
        bytes = exclusionQueue.dequeue();
        exclusionEvents = serialiser.deserialise(bytes);
        Assert.assertEquals(100, exclusionEvents.size());

        bytes = exclusionQueue.dequeue();
        exclusionEvents = serialiser.deserialise(bytes);
        Assert.assertEquals(100, exclusionEvents.size());

        bytes = exclusionQueue.dequeue();
        exclusionEvents = serialiser.deserialise(bytes);
        Assert.assertEquals(50, exclusionEvents.size());

        Awaitility.await().atMost(30, TimeUnit.SECONDS)
            .untilAsserted(() -> flowTestRule.assertIsSatisfied());

        flowTestRule.stopFlow();
        exclusionQueue.removeAll();

    }

    @Test(timeout = 120000)
    @DirtiesContext
    public void test_empty_window_checkpoint_survives_restart_with_empty_batches() throws Exception {
        createCountStub(0, 0, 100000);
        createCountStub(0, 100000, 200000);
        flowTestRule.withFlow(moduleUnderTest.getFlow(FLOW_NAME));
        var consumer = (ConfiguredResource<ExclusionConsumerConfiguration>)
            moduleUnderTest.getFlow(FLOW_NAME).getFlowElement("Exclusion Entity Scheduled Consumer").getFlowComponent();
        consumer.getConfiguration().setCronExpression("0 0 0 1 1 ? 2099");
        consumer.getConfiguration().setEager(false);
        try {
            flowTestRule.startFlow();
            fireEmptyPoll((org.ikasan.component.endpoint.quartz.consumer.ScheduledConsumer)
                flowTestRule.getComponent("Exclusion Entity Scheduled Consumer"));
            Awaitility.await().atMost(15, TimeUnit.SECONDS).untilAsserted(() -> {
                var saved = configurationService.getConfiguration(consumer.getConfiguredResourceId());
                Assert.assertNotNull(saved);
                ConfigurationParameter parameter =
                    (ConfigurationParameter) ((List<?>) saved.getParameters()).stream()
                        .filter(p -> ((ConfigurationParameter) p).getName().equals("queryTimestamp"))
                        .findFirst().orElseThrow();
                assertEquals(100000L, parameter.getValue());
            });
            flowTestRule.stopFlow();
            // Simulate losing all in-memory progress; start must reload the saved value.
            consumer.getConfiguration().setQueryTimestamp(-1L);
            flowTestRule.startFlow();
            assertEquals(100000L, consumer.getConfiguration().getQueryTimestamp());
            fireEmptyPoll((org.ikasan.component.endpoint.quartz.consumer.ScheduledConsumer)
                flowTestRule.getComponent("Exclusion Entity Scheduled Consumer"));
            Awaitility.await().atMost(15, TimeUnit.SECONDS).untilAsserted(() ->
                assertEquals(200000L, consumer.getConfiguration().getQueryTimestamp()));
            IBigQueue queue = EntityBigQueueCache.instance().get(CACHE_NAME);
            Awaitility.await().atMost(15, TimeUnit.SECONDS).untilAsserted(() -> assertEquals(2L, queue.size()));
            var serialiser = new ExclusionEventToBigQueueMessageSerialiser();
            Assert.assertTrue(serialiser.deserialise(queue.dequeue()).isEmpty());
            Assert.assertTrue(serialiser.deserialise(queue.dequeue()).isEmpty());
            verify(1, getRequestedFor(urlPathEqualTo(REST_URL_PATH + "/count"))
                .withQueryParam("fromTimestamp", equalTo("0")));
            verify(1, getRequestedFor(urlPathEqualTo(REST_URL_PATH + "/count"))
                .withQueryParam("fromTimestamp", equalTo("100000")));
        } finally {
            flowTestRule.stopFlow();
        }
    }

    private void fireEmptyPoll(org.ikasan.component.endpoint.quartz.consumer.ScheduledConsumer consumer)
        throws org.quartz.SchedulerException {
        // Trigger Quartz directly so the test can inspect persisted state and queued batches.
        var trigger = org.quartz.TriggerBuilder.newTrigger().forJob(consumer.getJobDetail())
            .usingJobData(org.ikasan.component.endpoint.quartz.consumer.ScheduledConsumer.CRON_EXPRESSION,
                consumer.getConfiguration().getConsolidatedCronExpressions().get(0))
            .build();
        consumer.scheduleAsEagerTrigger(trigger, 0);
    }

    /**
     * Creates a test stub to simulate an HTTP GET request for retrieving exclusionEvents.
     *
     * @param count the number of exclusionEvents to be simulated in the response
     * @param from the starting timestamp for filtering exclusionEvents
     * @param to the exclusive window end (the dashboard receives to - 1)
     * @throws IOException if an error occurs while creating the stub
     */
    private void createCountStub(int count, long from , long to) throws IOException {
        stubFor(get(urlEqualTo(REST_URL_PATH + "/count?fromTimestamp="+from+"&toTimestamp="+(to - 1)+"&" +
                "moduleNames=module1&moduleNames=module2"))
                .withHeader(HttpHeaders.USER_AGENT, equalTo(""))
                .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
                .willReturn(aResponse()
                    .withBody(objectMapper.writeValueAsString(Map.of("count", count)))
                    .withStatus(200)
                )
        );
    }

    /**
     * Creates a test stub to simulate an HTTP GET request for retrieving exclusionEvents with pagination support.
     *
     * @param resultSize the number of exclusionEvents to be included in the simulated response
     * @param totalCount the total count of exclusionEvents available across all pages
     * @param from the starting timestamp used for filtering exclusionEvents
     * @param to the exclusive window end (the dashboard receives to - 1)
     * @param limit the maximum number of exclusionEvents to be included in a single page of the response
     * @param offset the starting position of the exclusionEvents in the total count, used for pagination
     * @throws IOException if an error occurs during the creation of the stub
     */
    private void createQueryStub(int resultSize, int totalCount, long from , long to, int limit, int offset) throws IOException {
        stubFor(get(urlEqualTo(REST_URL_PATH + "?fromTimestamp="+from+"&toTimestamp="+(to - 1)+"&" +
                "limit="+limit+"&offset="+offset+"&moduleNames=module1&moduleNames=module2"))
                .withHeader(HttpHeaders.USER_AGENT, equalTo(""))
                .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
                .willReturn(aResponse()
                    .withBody(this.serializeExclusionEvents(this.createExclusionEvents(resultSize), totalCount))
                    .withStatus(200)
                )

        );
    }

    /**
     * Creates a list of ExclusionEvent<String> entities for testing purposes.
     *
     * @param count number of exclusionEvents to create
     * @return List of ExclusionEvent<String> instances
     */
    private List<ExclusionEvent<String>> createExclusionEvents(int count) {
        List<ExclusionEvent<String>> exclusionEvents = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            exclusionEvents.add(createExclusionEventTestFixture(i));
        }

        return exclusionEvents;
    }

    /**
     * Serializes a list of ExclusionEvent<String> to JSON string in the shape of the dashboard response.
     * The response includes data array, totalCount, and hasMore fields.
     *
     * @param exclusionEventsBytes the list of exclusionEvents to serialize
     * @return JSON string representation matching the dashboard response format
     * @throws IOException if serialization fails
     */
    private String serializeExclusionEvents(List<ExclusionEvent<String>> exclusionEventsBytes, int totalCount) throws IOException {
        Map<String, Object> response = new HashMap<>();
        response.put("data", exclusionEventsBytes);
        response.put("totalCount", totalCount);
        response.put("count", totalCount);
        response.put("hasMore", false);
        return objectMapper.writeValueAsString(response);
    }

    /**
     * Serializes a list of ExclusionEvent<String> to JSON string in the shape of the dashboard response
     * with pagination support.
     *
     * @param exclusionEvents the list of exclusionEvents to serialize
     * @param totalCount the total count of exclusionEvents available
     * @param hasMore whether there are more exclusionEvents available
     * @return JSON string representation matching the dashboard response format
     * @throws IOException if serialization fails
     */
    private String serializeExclusionEvents(List<ExclusionEvent<String>> exclusionEvents, long totalCount, boolean hasMore) throws IOException {
        Map<String, Object> response = new HashMap<>();
        response.put("data", exclusionEvents);
        response.put("totalCount", totalCount);
        response.put("count", totalCount);
        response.put("hasMore", hasMore);
        return objectMapper.writeValueAsString(response);
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
