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
import org.ikasan.dashboard.dto.error.ErrorOccurrenceImpl;
import org.ikasan.ootb.data.sharing.module.Application;
import org.ikasan.ootb.data.sharing.module.cache.EntityBigQueueCache;
import org.ikasan.ootb.data.sharing.module.component.endpoint.consumer.configuration.ErrorConsumerConfiguration;
import org.ikasan.ootb.data.sharing.module.serialiser.ErrorOccurrenceToBigQueueMessageSerialiser;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.module.Module;
import org.ikasan.spec.error.reporting.ErrorOccurrence;
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
public class ErrorEntityConsumerFlowTest {

    private static final String REST_URL_PATH = "/rest/data-sharing/errors";
    private static final String CACHE_NAME = "DASHBOARD_1-Ikasan-ESB-Dashboard-ERROR-queue";
    private static final String FLOW_NAME = "DASHBOARD_1 Ikasan ESB Dashboard Error Entity Consumer Flow";

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
        var consumer = (ConfiguredResource<ErrorConsumerConfiguration>)
            moduleUnderTest.getFlow(FLOW_NAME).getFlowElement("Error Entity Scheduled Consumer").getFlowComponent();
        consumer.getConfiguration().setQueryTimestamp(0L);
        consumer.getConfiguration().setSafetyLagMilliseconds(0L);
    }

    @After
    public void teardown() throws IOException {
    }

    @Test
    @DirtiesContext
    public void test_error_consume_success() throws IOException {
        // Setup wiremock rest service stubs
        this.createCountStub(257, 0, 100000);
        this.createCountStub(36, 100000, 200000);
        this.createQueryStub(100, 257, 0, 100000, 100, 0);
        this.createQueryStub(100, 257, 0, 100000, 100, 100);
        this.createQueryStub(57, 257, 0, 100000, 100, 200);
        this.createQueryStub(0, 257, 0, 100000, 100, 300);
        this.createQueryStub(36, 36, 100000, 200000, 100, 0);

        flowTestRule.withFlow(moduleUnderTest.getFlow(FLOW_NAME));

        flowTestRule.consumer("Error Entity Scheduled Consumer")
            .producer("Error Entity Big Queue Producer")
            .consumer("Error Entity Scheduled Consumer")
            .producer("Error Entity Big Queue Producer")
            .consumer("Error Entity Scheduled Consumer")
            .producer("Error Entity Big Queue Producer")
            .consumer("Error Entity Scheduled Consumer")
            .producer("Error Entity Big Queue Producer");

        flowTestRule.startFlow();
        flowTestRule.fireScheduledConsumer();

        assertEquals(Flow.RUNNING, flowTestRule.getFlowState());

        IBigQueue errorQueue = EntityBigQueueCache.instance().get(CACHE_NAME);

        Awaitility.await().atMost(30, TimeUnit.SECONDS)
            .untilAsserted(() -> Assert.assertEquals(3, errorQueue.size()));

        ErrorOccurrenceToBigQueueMessageSerialiser errorOccurrenceToBigQueueMessageSerialiser
            = new ErrorOccurrenceToBigQueueMessageSerialiser();

        byte[] errorOccurrencesBytes = errorQueue.dequeue();

        List<ErrorOccurrence<byte[]>> errorOccurrences = errorOccurrenceToBigQueueMessageSerialiser
            .deserialise(errorOccurrencesBytes);

        Assert.assertEquals(100, errorOccurrences.size());

        errorOccurrencesBytes = errorQueue.dequeue();

        errorOccurrences = errorOccurrenceToBigQueueMessageSerialiser
            .deserialise(errorOccurrencesBytes);

        Assert.assertEquals(100, errorOccurrences.size());

        errorOccurrencesBytes = errorQueue.dequeue();

        errorOccurrences = errorOccurrenceToBigQueueMessageSerialiser
            .deserialise(errorOccurrencesBytes);
        Assert.assertEquals(57, errorOccurrences.size());

        flowTestRule.fireScheduledConsumer();

        Awaitility.await().atMost(30, TimeUnit.SECONDS)
            .untilAsserted(() -> Assert.assertEquals(1, errorQueue.size()));

        errorOccurrencesBytes = errorQueue.dequeue();

        errorOccurrences = errorOccurrenceToBigQueueMessageSerialiser
            .deserialise(errorOccurrencesBytes);

        Assert.assertEquals(36, errorOccurrences.size());

        Awaitility.await().atMost(30, TimeUnit.SECONDS)
            .untilAsserted(() -> flowTestRule.assertIsSatisfied());

        flowTestRule.stopFlow();
        errorQueue.removeAll();
    }

    @Test
    @DirtiesContext
    public void test_error_consume_stop_start_flow_to_assert_dynamic_configuration_persisted() throws IOException {
        // Setup wiremock rest service stubs
        this.createCountStub(257, 0, 100000);
        this.createCountStub(36, 100000, 200000);
        this.createQueryStub(100, 257, 0, 100000, 100, 0);
        this.createQueryStub(100, 257, 0, 100000, 100, 100);
        this.createQueryStub(57, 257, 0, 100000, 100, 200);
        this.createQueryStub(0, 257, 0, 100000, 100, 300);
        this.createQueryStub(36, 36, 100000, 200000, 100, 0);

        flowTestRule.withFlow(moduleUnderTest.getFlow(FLOW_NAME));

        flowTestRule.consumer("Error Entity Scheduled Consumer")
            .producer("Error Entity Big Queue Producer")
            .consumer("Error Entity Scheduled Consumer")
            .producer("Error Entity Big Queue Producer")
            .consumer("Error Entity Scheduled Consumer")
            .producer("Error Entity Big Queue Producer");

        flowTestRule.startFlow();
        flowTestRule.fireScheduledConsumer();

        assertEquals(Flow.RUNNING, flowTestRule.getFlowState());

        IBigQueue errorQueue = EntityBigQueueCache.instance().get(CACHE_NAME);

        Awaitility.await().atMost(3000000, TimeUnit.SECONDS)
            .untilAsserted(() -> Assert.assertEquals(3, errorQueue.size()));

        ErrorOccurrenceToBigQueueMessageSerialiser errorOccurrenceToBigQueueMessageSerialiser
            = new ErrorOccurrenceToBigQueueMessageSerialiser();

        byte[] errorOccurrencesBytes = errorQueue.dequeue();

        List<ErrorOccurrence<byte[]>> errorOccurrences = errorOccurrenceToBigQueueMessageSerialiser
            .deserialise(errorOccurrencesBytes);

        Assert.assertEquals(100, errorOccurrences.size());

        errorOccurrencesBytes = errorQueue.dequeue();

        errorOccurrences = errorOccurrenceToBigQueueMessageSerialiser
            .deserialise(errorOccurrencesBytes);

        Assert.assertEquals(100, errorOccurrences.size());

        errorOccurrencesBytes = errorQueue.dequeue();

        errorOccurrences = errorOccurrenceToBigQueueMessageSerialiser
            .deserialise(errorOccurrencesBytes);

        Assert.assertEquals(57, errorOccurrences.size());

        Awaitility.await().atMost(30, TimeUnit.SECONDS)
            .untilAsserted(() -> flowTestRule.assertIsSatisfied());

        flowTestRule.stopFlow();

        flowTestRule.startFlow();

        ErrorConsumerConfiguration configuration = flowTestRule.getComponentConfig("Error Entity Scheduled Consumer"
            , ErrorConsumerConfiguration.class);

        // Assert that the dynamic configuration loaded as expected!
        assertEquals(100000L, configuration.getQueryTimestamp());

        flowTestRule.fireScheduledConsumer();
        flowTestRule.sleep(2000);

        // Finally, go ahead and confirm that the final batch of messages are picked up.
        Awaitility.await().atMost(30, TimeUnit.SECONDS)
            .untilAsserted(() -> Assert.assertEquals(1, errorQueue.size()));

        errorOccurrencesBytes = errorQueue.dequeue();

        errorOccurrences = errorOccurrenceToBigQueueMessageSerialiser
            .deserialise(errorOccurrencesBytes);

        Assert.assertEquals(36, errorOccurrences.size());

        flowTestRule.stopFlow();
        errorQueue.removeAll();
    }

    @Test
    @DirtiesContext
    public void test_error_entity_consume_with_service_error_bad_data_and_retry() throws Exception {
        flowTestRule.withFlow(moduleUnderTest.getFlow(FLOW_NAME));

        // Create stubs for a scenario where we have 250 errorOccurrences requiring paging
        // First count query returns 250
        stubFor(get(urlMatching(REST_URL_PATH + "/count\\?fromTimestamp=0&toTimestamp=99999.*"))
            .withHeader(HttpHeaders.USER_AGENT, equalTo(""))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
            .willReturn(aResponse()
                .withBody(this.serializeErrorOccurrences(List.of(), 250, false))
                .withStatus(200)
            ));

        // First page: 100 errorOccurrences - will succeed
        stubFor(get(urlEqualTo(REST_URL_PATH + "?fromTimestamp=0&toTimestamp=99999&limit=100&offset=0&moduleNames=module1&moduleNames=module2"))
            .withHeader(HttpHeaders.USER_AGENT, equalTo(""))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
            .willReturn(aResponse()
                .withBody(this.serializeErrorOccurrences(this.createErrorOccurrences(100), 250, true))
                .withStatus(200)
            ));

        // Second page: 100 errorOccurrences - will be queried twice (once with error, once successfully)
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
                .withBody(this.serializeErrorOccurrences(this.createErrorOccurrences(100), 250, true))
                .withStatus(200)
            ));

        // Third page: 50 errorOccurrences
        stubFor(get(urlEqualTo(REST_URL_PATH + "?fromTimestamp=0&toTimestamp=99999&limit=100&offset=200&moduleNames=module1&moduleNames=module2"))
            .withHeader(HttpHeaders.USER_AGENT, equalTo(""))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
            .willReturn(aResponse()
                .withBody(this.serializeErrorOccurrences(this.createErrorOccurrences(50), 250, false))
                .withStatus(200)
            ));


        IBigQueue errorQueue = EntityBigQueueCache.instance().get(CACHE_NAME);

        // Set up expectations: first attempt fails on second page, retry succeeds
        flowTestRule.consumer("Error Entity Scheduled Consumer")
            .producer("Error Entity Big Queue Producer") // First page succeeds
            .consumer("Error Entity Scheduled Consumer")
            .producer("Error Entity Big Queue Producer") // Second page fails - will retry
            .consumer("Error Entity Scheduled Consumer")
            .producer("Error Entity Big Queue Producer") // Second page retry succeeds
            .consumer("Error Entity Scheduled Consumer")
            .producer("Error Entity Big Queue Producer"); // Third page succeeds

        flowTestRule.startFlow();
        flowTestRule.fireScheduledConsumer();

        assertEquals(Flow.RUNNING, flowTestRule.getFlowState());

        ErrorOccurrenceToBigQueueMessageSerialiser serialiser = new ErrorOccurrenceToBigQueueMessageSerialiser();

        // Because the BigQueue producer is not LRCO the first page of data is written
        // to the BigQueue destination. This is fine bacause all entities have an immutable
        // identifier and they will simply be upserted into the destination datastore
        // if written more than one time.
        Awaitility.await().atMost(30, TimeUnit.SECONDS)
            .untilAsserted(() -> assertEquals(1, errorQueue.size()));

        // Confirm details of the first page.
        byte[] bytes = errorQueue.dequeue();
        List<ErrorOccurrence<byte[]>> errorOccurrences = serialiser.deserialise(bytes);
        Assert.assertEquals(100, errorOccurrences.size());

        // In the meantime the consumer has gone into recovery and subsequently
        // recovered. This time all 3 expected pages are written,
        Awaitility.await().atMost(30, TimeUnit.SECONDS)
            .untilAsserted(() -> Assert.assertEquals(3, errorQueue.size()));

        // Verify the details pages published to the downstream
        // BigQueue destination.
        bytes = errorQueue.dequeue();
        errorOccurrences = serialiser.deserialise(bytes);
        Assert.assertEquals(100, errorOccurrences.size());

        bytes = errorQueue.dequeue();
        errorOccurrences = serialiser.deserialise(bytes);
        Assert.assertEquals(100, errorOccurrences.size());

        bytes = errorQueue.dequeue();
        errorOccurrences = serialiser.deserialise(bytes);
        Assert.assertEquals(50, errorOccurrences.size());

        Awaitility.await().atMost(30, TimeUnit.SECONDS)
            .untilAsserted(() -> flowTestRule.assertIsSatisfied());

        flowTestRule.stopFlow();
        errorQueue.removeAll();

    }

    @Test
    @DirtiesContext
    public void test_error_entity_consume_with_service_http_error_and_retry() throws Exception {
        flowTestRule.withFlow(moduleUnderTest.getFlow(FLOW_NAME));

        // Create stubs for a scenario where we have 250 errorOccurrences requiring paging
        // First count query returns 250
        stubFor(get(urlMatching(REST_URL_PATH + "/count\\?fromTimestamp=0&toTimestamp=99999.*"))
            .withHeader(HttpHeaders.USER_AGENT, equalTo(""))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
            .willReturn(aResponse()
                .withBody(this.serializeErrorOccurrences(List.of(), 250, false))
                .withStatus(200)
            ));

        // First page: 100 errorOccurrences - will succeed
        stubFor(get(urlEqualTo(REST_URL_PATH + "?fromTimestamp=0&toTimestamp=99999&limit=100&offset=0&moduleNames=module1&moduleNames=module2"))
            .withHeader(HttpHeaders.USER_AGENT, equalTo(""))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
            .willReturn(aResponse()
                .withBody(this.serializeErrorOccurrences(this.createErrorOccurrences(100), 250, true))
                .withStatus(200)
            ));

        // Second page: 100 errorOccurrences - will be queried twice (once with error, once successfully)
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
                .withBody(this.serializeErrorOccurrences(this.createErrorOccurrences(100), 250, true))
                .withStatus(200)
            ));

        // Third page: 50 errorOccurrences
            stubFor(get(urlEqualTo(REST_URL_PATH + "?fromTimestamp=0&toTimestamp=99999&limit=100&offset=200&moduleNames=module1&moduleNames=module2"))
            .withHeader(HttpHeaders.USER_AGENT, equalTo(""))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
            .willReturn(aResponse()
                .withBody(this.serializeErrorOccurrences(this.createErrorOccurrences(50), 250, false))
                .withStatus(200)
            ));

        IBigQueue errorQueue = EntityBigQueueCache.instance().get(CACHE_NAME);

        // Set up expectations: first attempt fails on second page, retry succeeds
        flowTestRule.consumer("Error Entity Scheduled Consumer")
            .producer("Error Entity Big Queue Producer") // First page succeeds
            .consumer("Error Entity Scheduled Consumer")
            .producer("Error Entity Big Queue Producer") // Second page fails - will retry
            .consumer("Error Entity Scheduled Consumer")
            .producer("Error Entity Big Queue Producer") // Second page retry succeeds
            .consumer("Error Entity Scheduled Consumer")
            .producer("Error Entity Big Queue Producer"); // Third page succeeds

        flowTestRule.startFlow();
        flowTestRule.fireScheduledConsumer();

        assertEquals(Flow.RUNNING, flowTestRule.getFlowState());

        ErrorOccurrenceToBigQueueMessageSerialiser serialiser = new ErrorOccurrenceToBigQueueMessageSerialiser();

        // Because the BigQueue producer is not LRCO the first page of data is written
        // to the BigQueue destination. This is fine bacause all entities have an immutable
        // identifier and they will simply be upserted into the destination datastore
        // if written more than one time.
        Awaitility.await().atMost(30, TimeUnit.SECONDS)
            .untilAsserted(() -> Assert.assertEquals(1, errorQueue.size()));

        // Confirm details of the first page.
        byte[] bytes = errorQueue.dequeue();
        List<ErrorOccurrence<byte[]>> errorOccurrences = serialiser.deserialise(bytes);
        Assert.assertEquals(100, errorOccurrences.size());

        // In the meantime the consumer has gone into recovery and subsequently
        // recovered. This time all 3 expected pages are written,
        Awaitility.await().atMost(30, TimeUnit.SECONDS)
            .untilAsserted(() -> Assert.assertEquals(3, errorQueue.size()));

        // Verify the details pages published to the downstream
        // BigQueue destination.
        bytes = errorQueue.dequeue();
        errorOccurrences = serialiser.deserialise(bytes);
        Assert.assertEquals(100, errorOccurrences.size());

        bytes = errorQueue.dequeue();
        errorOccurrences = serialiser.deserialise(bytes);
        Assert.assertEquals(100, errorOccurrences.size());

        bytes = errorQueue.dequeue();
        errorOccurrences = serialiser.deserialise(bytes);
        Assert.assertEquals(50, errorOccurrences.size());

        Awaitility.await().atMost(30, TimeUnit.SECONDS)
            .untilAsserted(() -> flowTestRule.assertIsSatisfied());

        flowTestRule.stopFlow();
        errorQueue.removeAll();

    }

    @Test
    @DirtiesContext
    public void test_empty_window_checkpoint_survives_restart_with_empty_batches() throws Exception {
        createCountStub(0, 0, 100000);
        createCountStub(0, 100000, 200000);
        flowTestRule.withFlow(moduleUnderTest.getFlow(FLOW_NAME));
        var consumer = (ConfiguredResource<ErrorConsumerConfiguration>)
            moduleUnderTest.getFlow(FLOW_NAME).getFlowElement("Error Entity Scheduled Consumer").getFlowComponent();
        consumer.getConfiguration().setCronExpression("0 0 0 1 1 ? 2099");
        consumer.getConfiguration().setEager(false);
        try {
            flowTestRule.startFlow();
            fireEmptyPoll((org.ikasan.component.endpoint.quartz.consumer.ScheduledConsumer)
                flowTestRule.getComponent("Error Entity Scheduled Consumer"));
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
                flowTestRule.getComponent("Error Entity Scheduled Consumer"));
            Awaitility.await().atMost(15, TimeUnit.SECONDS).untilAsserted(() ->
                assertEquals(200000L, consumer.getConfiguration().getQueryTimestamp()));
            IBigQueue queue = EntityBigQueueCache.instance().get(CACHE_NAME);
            Awaitility.await().atMost(15, TimeUnit.SECONDS).untilAsserted(() -> assertEquals(2L, queue.size()));
            var serialiser = new ErrorOccurrenceToBigQueueMessageSerialiser();
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
     * Creates a test stub to simulate an HTTP GET request for retrieving errorOccurrences.
     *
     * @param count the number of errorOccurrences to be simulated in the response
     * @param from the starting timestamp for filtering errorOccurrences
     * @param to the exclusive window end (the dashboard receives to - 1)
     * @throws IOException if an error occurs while creating the stub
     */
    private void createCountStub(int count, long from , long to) throws IOException {
        stubFor(get(urlEqualTo(REST_URL_PATH + "/count?fromTimestamp="+from+"&toTimestamp="+(to - 1)+"&" +
                "moduleNames=module1&moduleNames=module2"))
                .withHeader(HttpHeaders.USER_AGENT, equalTo(""))
                .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
                .willReturn(aResponse()
                    .withBody(this.serializeErrorOccurrences(List.of(), count))
                    .withStatus(200)
                )
        );
    }

    /**
     * Creates a test stub to simulate an HTTP GET request for retrieving errorOccurrences with pagination support.
     *
     * @param resultSize the number of errorOccurrences to be included in the simulated response
     * @param totalCount the total count of errorOccurrences available across all pages
     * @param from the starting timestamp used for filtering errorOccurrences
     * @param to the exclusive window end (the dashboard receives to - 1)
     * @param limit the maximum number of errorOccurrences to be included in a single page of the response
     * @param offset the starting position of the errorOccurrences in the total count, used for pagination
     * @throws IOException if an error occurs during the creation of the stub
     */
    private void createQueryStub(int resultSize, int totalCount, long from , long to, int limit, int offset) throws IOException {
        stubFor(get(urlEqualTo(REST_URL_PATH + "?fromTimestamp="+from+"&toTimestamp="+(to - 1)+"&" +
                "limit="+limit+"&offset="+offset+"&moduleNames=module1&moduleNames=module2"))
                .withHeader(HttpHeaders.USER_AGENT, equalTo(""))
                .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
                .willReturn(aResponse()
                    .withBody(this.serializeErrorOccurrences(this.createErrorOccurrences(resultSize), totalCount))
                    .withStatus(200)
                )

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

    /**
     * Creates a list of ErrorOccurrence<byte[]> entities for testing purposes.
     *
     * @param count number of errorOccurrences to create
     * @return List of ErrorOccurrence<byte[]> instances
     */
    private List<ErrorOccurrence<byte[]>> createErrorOccurrences(int count) {
        List<ErrorOccurrence<byte[]>> errorOccurrences = new ArrayList<>();
        long baseTimestamp = System.currentTimeMillis();

        for (int i = 0; i < count; i++) {
            errorOccurrences.add(createErrorOccurrenceTestFixture(i));
        }

        return errorOccurrences;
    }

    /**
     * Serializes a list of ErrorOccurrence<byte[]> to JSON string in the shape of error-response.json.
     * The response includes data array, totalCount, and hasMore fields.
     *
     * @param errorOccurrencesBytes the list of errorOccurrences to serialize
     * @return JSON string representation matching error-response.json format
     * @throws IOException if serialization fails
     */
    private String serializeErrorOccurrences(List<ErrorOccurrence<byte[]>> errorOccurrencesBytes, int totalCount) throws IOException {
        Map<String, Object> response = new HashMap<>();
        response.put("data", errorOccurrencesBytes);
        response.put("totalCount", totalCount);
        response.put("hasMore", false);
        return objectMapper.writeValueAsString(response);
    }

    /**
     * Serializes a list of ErrorOccurrence<byte[]> to JSON string in the shape of error-response.json
     * with pagination support.
     *
     * @param errorOccurrences the list of errorOccurrences to serialize
     * @param totalCount the total count of errorOccurrences available
     * @param hasMore whether there are more errorOccurrences available
     * @return JSON string representation matching error-response.json format
     * @throws IOException if serialization fails
     */
    private String serializeErrorOccurrences(List<ErrorOccurrence<byte[]>> errorOccurrences, long totalCount, boolean hasMore) throws IOException {
        Map<String, Object> response = new HashMap<>();
        response.put("data", errorOccurrences);
        response.put("totalCount", totalCount);
        response.put("hasMore", hasMore);
        return objectMapper.writeValueAsString(response);
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
}
