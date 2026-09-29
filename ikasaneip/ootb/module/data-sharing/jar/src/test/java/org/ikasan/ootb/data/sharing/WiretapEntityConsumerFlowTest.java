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
import com.github.tomakehurst.wiremock.stubbing.Scenario;
import org.awaitility.Awaitility;
import org.ikasan.bigqueue.IBigQueue;
import org.ikasan.dashboard.dto.wiretap.WiretapEventImpl;
import org.ikasan.ootb.data.sharing.module.Application;
import org.ikasan.ootb.data.sharing.module.cache.EntityBigQueueCache;
import org.ikasan.ootb.data.sharing.module.component.endpoint.consumer.configuration.WiretapConsumerConfiguration;
import org.ikasan.ootb.data.sharing.module.serialiser.WiretapEventsToBigQueueMessageSerialiser;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.module.Module;
import org.ikasan.spec.wiretap.WiretapEvent;
import org.ikasan.testharness.flow.rule.IkasanFlowTestRule;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
public class WiretapEntityConsumerFlowTest {

    @ClassRule
    public static WireMockRule wireMockRule = new WireMockRule(
        WireMockConfiguration.options().port(9080));

    @Autowired
    private Module<Flow> moduleUnderTest;

    @Value( "${big.queue.consumer.queueDir}" )
    private String queueDir;

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
    }

    @After
    public void teardown() throws IOException {
    }

    @Test
    @DirtiesContext
    public void test_wiretap_consume_success() throws IOException {
        // Setup wiremock rest service stubs
        this.createCountStub(257, 0, 100000);
        this.createCountStub(36, 100000, 200000);


        this.createQueryStub(100, 257, 0, 100000, 100, 0);
        this.createQueryStub(100, 257, 0, 100000, 100, 100);
        this.createQueryStub(57, 257, 0, 100000, 100, 200);
        this.createQueryStub(0, 257, 0, 100000, 100, 300);

        this.createQueryStub(36, 36, 100000, 200000, 100, 0);

        flowTestRule.withFlow(moduleUnderTest.getFlow("DASHBOARD_1 Ikasan ESB Dashboard Wiretap Entity Consumer Flow"));

        flowTestRule.consumer("Wiretap Entity Scheduled Consumer")
            .producer("Wiretap Entity Big Queue Producer")
            .consumer("Wiretap Entity Scheduled Consumer")
            .producer("Wiretap Entity Big Queue Producer")
            .consumer("Wiretap Entity Scheduled Consumer")
            .producer("Wiretap Entity Big Queue Producer")
            .consumer("Wiretap Entity Scheduled Consumer")
            .producer("Wiretap Entity Big Queue Producer");

        flowTestRule.startFlow();
        flowTestRule.fireScheduledConsumer();

        assertEquals(Flow.RUNNING, flowTestRule.getFlowState());

        IBigQueue wiretapQueue = EntityBigQueueCache.instance().get("DASHBOARD_1-Ikasan-ESB-Dashboard-WIRETAP-queue");

        Awaitility.await().atMost(30, TimeUnit.SECONDS)
            .untilAsserted(() -> Assert.assertEquals(3, wiretapQueue.size()));

        WiretapEventsToBigQueueMessageSerialiser wiretapEventsToBigQueueMessageSerialiser
            = new WiretapEventsToBigQueueMessageSerialiser();

        byte[] wiretapEventBytes = wiretapQueue.dequeue();

        List<WiretapEvent> wiretapEvents = wiretapEventsToBigQueueMessageSerialiser
            .deserialise(wiretapEventBytes);

        Assert.assertEquals(100, wiretapEvents.size());

        wiretapEventBytes = wiretapQueue.dequeue();

        wiretapEvents = wiretapEventsToBigQueueMessageSerialiser
            .deserialise(wiretapEventBytes);

        Assert.assertEquals(100, wiretapEvents.size());

        wiretapEventBytes = wiretapQueue.dequeue();

        wiretapEvents = wiretapEventsToBigQueueMessageSerialiser
            .deserialise(wiretapEventBytes);

        Assert.assertEquals(57, wiretapEvents.size());

        flowTestRule.fireScheduledConsumer();

        Awaitility.await().atMost(30, TimeUnit.SECONDS)
            .untilAsserted(() -> Assert.assertEquals(1, wiretapQueue.size()));

        wiretapEventBytes = wiretapQueue.dequeue();

        wiretapEvents = wiretapEventsToBigQueueMessageSerialiser
            .deserialise(wiretapEventBytes);

        Assert.assertEquals(36, wiretapEvents.size());

        Awaitility.await().atMost(30, TimeUnit.SECONDS)
            .untilAsserted(() -> flowTestRule.assertIsSatisfied());

        flowTestRule.stopFlow();
        wiretapQueue.removeAll();
    }

    @Test
    @DirtiesContext
    public void test_wiretap_consume_stop_start_flow_to_assert_dynamic_configuration_persisted() throws IOException {
        // Setup wiremock rest service stubs
        this.createCountStub(257, 0, 100000);
        this.createCountStub(36, 100000, 200000);


        this.createQueryStub(100, 257, 0, 100000, 100, 0);
        this.createQueryStub(100, 257, 0, 100000, 100, 100);
        this.createQueryStub(57, 257, 0, 100000, 100, 200);
        this.createQueryStub(0, 257, 0, 100000, 100, 300);

        this.createQueryStub(36, 36, 100000, 200000, 100, 0);

        flowTestRule.withFlow(moduleUnderTest.getFlow("DASHBOARD_1 Ikasan ESB Dashboard Wiretap Entity Consumer Flow"));

        flowTestRule.consumer("Wiretap Entity Scheduled Consumer")
            .producer("Wiretap Entity Big Queue Producer")
            .consumer("Wiretap Entity Scheduled Consumer")
            .producer("Wiretap Entity Big Queue Producer")
            .consumer("Wiretap Entity Scheduled Consumer")
            .producer("Wiretap Entity Big Queue Producer");

        flowTestRule.startFlow();
        flowTestRule.fireScheduledConsumer();

        assertEquals(Flow.RUNNING, flowTestRule.getFlowState());

        IBigQueue wiretapQueue = EntityBigQueueCache.instance().get("DASHBOARD_1-Ikasan-ESB-Dashboard-WIRETAP-queue");

        Awaitility.await().atMost(3000000, TimeUnit.SECONDS)
            .untilAsserted(() -> Assert.assertEquals(3, wiretapQueue.size()));

        WiretapEventsToBigQueueMessageSerialiser wiretapEventsToBigQueueMessageSerialiser
            = new WiretapEventsToBigQueueMessageSerialiser();

        byte[] wiretapEventBytes = wiretapQueue.dequeue();

        List<WiretapEvent> wiretapEvents = wiretapEventsToBigQueueMessageSerialiser
            .deserialise(wiretapEventBytes);

        Assert.assertEquals(100, wiretapEvents.size());

        wiretapEventBytes = wiretapQueue.dequeue();

        wiretapEvents = wiretapEventsToBigQueueMessageSerialiser
            .deserialise(wiretapEventBytes);

        Assert.assertEquals(100, wiretapEvents.size());

        wiretapEventBytes = wiretapQueue.dequeue();

        wiretapEvents = wiretapEventsToBigQueueMessageSerialiser
            .deserialise(wiretapEventBytes);

        Assert.assertEquals(57, wiretapEvents.size());

        Awaitility.await().atMost(30, TimeUnit.SECONDS)
            .untilAsserted(() -> flowTestRule.assertIsSatisfied());

        flowTestRule.stopFlow();

        flowTestRule.startFlow();

        WiretapConsumerConfiguration configuration = flowTestRule.getComponentConfig("Wiretap Entity Scheduled Consumer"
            , WiretapConsumerConfiguration.class);

        // Assert that the dynamic configuration loaded as expected!
        assertEquals(100000L, configuration.getQueryTimestamp());

        flowTestRule.fireScheduledConsumer();
        flowTestRule.sleep(2000);


        // Finally go ahead and confirm that the final batch of messages are picked up.
        Awaitility.await().atMost(30, TimeUnit.SECONDS)
            .untilAsserted(() -> Assert.assertEquals(1, wiretapQueue.size()));

        wiretapEventBytes = wiretapQueue.dequeue();

        wiretapEvents = wiretapEventsToBigQueueMessageSerialiser
            .deserialise(wiretapEventBytes);

        Assert.assertEquals(36, wiretapEvents.size());

        flowTestRule.stopFlow();
        wiretapQueue.removeAll();
    }

    @Test
    @DirtiesContext
    public void test_wiretap_consume_with_service_error_bad_data_and_retry() throws Exception {
        flowTestRule.withFlow(moduleUnderTest.getFlow("DASHBOARD_1 Ikasan ESB Dashboard Wiretap Entity Consumer Flow"));

        // Create stubs for a scenario where we have 250 events requiring paging
        // First count query returns 250
        stubFor(get(urlMatching("/rest/data-sharing/wiretap/count\\?fromTimestamp=0&toTimestamp=100000.*"))
            .withHeader(HttpHeaders.USER_AGENT, equalTo(""))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
            .willReturn(aResponse()
                .withBody(this.serializeWiretapEvents(List.of(), 250, false))
                .withStatus(200)
            ));

        // First page: 100 events - will succeed
        stubFor(get(urlEqualTo("/rest/data-sharing/wiretap?fromTimestamp=0&toTimestamp=100000&limit=100&offset=0&moduleNames=module1&moduleNames=module2"))
            .withHeader(HttpHeaders.USER_AGENT, equalTo(""))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
            .willReturn(aResponse()
                .withBody(this.serializeWiretapEvents(this.createWiretapEvents(100), 250, true))
                .withStatus(200)
            ));

        // Second page: 100 events - will be queried twice (once with error, once successfully)
        stubFor(get(urlEqualTo("/rest/data-sharing/wiretap?fromTimestamp=0&toTimestamp=100000&limit=100&offset=100&moduleNames=module1&moduleNames=module2"))
            .inScenario("Retry Scenario")
            .whenScenarioStateIs(Scenario.STARTED)
            .withHeader(HttpHeaders.USER_AGENT, equalTo(""))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
            .willReturn(aResponse()
                .withBody("this is a bad message")
                .withStatus(200)
            )
            .willSetStateTo("Success State"));

        stubFor(get(urlEqualTo("/rest/data-sharing/wiretap?fromTimestamp=0&toTimestamp=100000&limit=100&offset=100&moduleNames=module1&moduleNames=module2"))
            .inScenario("Retry Scenario")
            .whenScenarioStateIs("Success State")
            .withHeader(HttpHeaders.USER_AGENT, equalTo(""))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
            .willReturn(aResponse()
                .withBody(this.serializeWiretapEvents(this.createWiretapEvents(100), 250, true))
                .withStatus(200)
            ));

        // Third page: 50 events
        stubFor(get(urlEqualTo("/rest/data-sharing/wiretap?fromTimestamp=0&toTimestamp=100000&limit=100&offset=200&moduleNames=module1&moduleNames=module2"))
            .withHeader(HttpHeaders.USER_AGENT, equalTo(""))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
            .willReturn(aResponse()
                .withBody(this.serializeWiretapEvents(this.createWiretapEvents(50), 250, false))
                .withStatus(200)
            ));


        IBigQueue wiretapQueue = EntityBigQueueCache.instance().get("DASHBOARD_1-Ikasan-ESB-Dashboard-WIRETAP-queue");

        // Set up expectations: first attempt fails on second page, retry succeeds
        flowTestRule.consumer("Wiretap Entity Scheduled Consumer")
            .producer("Wiretap Entity Big Queue Producer") // First page succeeds
            .consumer("Wiretap Entity Scheduled Consumer")
            .producer("Wiretap Entity Big Queue Producer") // Second page fails - will retry
            .consumer("Wiretap Entity Scheduled Consumer")
            .producer("Wiretap Entity Big Queue Producer") // Second page retry succeeds
            .consumer("Wiretap Entity Scheduled Consumer")
            .producer("Wiretap Entity Big Queue Producer"); // Third page succeeds

        flowTestRule.startFlow();
        flowTestRule.fireScheduledConsumer();

        assertEquals(Flow.RUNNING, flowTestRule.getFlowState());

        WiretapEventsToBigQueueMessageSerialiser serialiser = new WiretapEventsToBigQueueMessageSerialiser();

        // Because the BigQueue producer is not LRCO the first page of data is written
        // to the BigQueue destination. This is fine bacause all entities have an immutable
        // identifier and they will simply be upserted into the destination datastore
        // if written more than one time.
        Awaitility.await().atMost(30, TimeUnit.SECONDS)
            .untilAsserted(() -> Assert.assertTrue(wiretapQueue.size() == 1));

        // Confirm details of the first page.
        byte[] bytes = wiretapQueue.dequeue();
        List<WiretapEvent> events = serialiser.deserialise(bytes);
        Assert.assertEquals(100, events.size());

        // In the meantime the consumer has gone into recovery and subsequently
        // recovered. This time all 3 expected pages are written,
        Awaitility.await().atMost(30, TimeUnit.SECONDS)
            .untilAsserted(() -> Assert.assertEquals(3, wiretapQueue.size()));

        // Verify the details pages published to the downstream
        // BigQueue destination.
        bytes = wiretapQueue.dequeue();
        events = serialiser.deserialise(bytes);
        Assert.assertEquals(100, events.size());

        bytes = wiretapQueue.dequeue();
        events = serialiser.deserialise(bytes);
        Assert.assertEquals(100, events.size());

        bytes = wiretapQueue.dequeue();
        events = serialiser.deserialise(bytes);
        Assert.assertEquals(50, events.size());

        Awaitility.await().atMost(30, TimeUnit.SECONDS)
            .untilAsserted(() -> flowTestRule.assertIsSatisfied());

        flowTestRule.stopFlow();
        wiretapQueue.removeAll();

    }

    @Test
    @DirtiesContext
    public void test_wiretap_consume_with_service_http_error_and_retry() throws Exception {
        flowTestRule.withFlow(moduleUnderTest.getFlow("DASHBOARD_1 Ikasan ESB Dashboard Wiretap Entity Consumer Flow"));

        // Create stubs for a scenario where we have 250 events requiring paging
        // First count query returns 250
        stubFor(get(urlMatching("/rest/data-sharing/wiretap/count\\?fromTimestamp=0&toTimestamp=100000.*"))
            .withHeader(HttpHeaders.USER_AGENT, equalTo(""))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
            .willReturn(aResponse()
                .withBody(this.serializeWiretapEvents(List.of(), 250, false))
                .withStatus(200)
            ));

        // First page: 100 events - will succeed
        stubFor(get(urlEqualTo("/rest/data-sharing/wiretap?fromTimestamp=0&toTimestamp=100000&limit=100&offset=0&moduleNames=module1&moduleNames=module2"))
            .withHeader(HttpHeaders.USER_AGENT, equalTo(""))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
            .willReturn(aResponse()
                .withBody(this.serializeWiretapEvents(this.createWiretapEvents(100), 250, true))
                .withStatus(200)
            ));

        // Second page: 100 events - will be queried twice (once with error, once successfully)
        stubFor(get(urlEqualTo("/rest/data-sharing/wiretap?fromTimestamp=0&toTimestamp=100000&limit=100&offset=100&moduleNames=module1&moduleNames=module2"))
            .inScenario("Retry Scenario")
            .whenScenarioStateIs(Scenario.STARTED)
            .withHeader(HttpHeaders.USER_AGENT, equalTo(""))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
            .willReturn(aResponse()
                .withStatus(404)
            )
            .willSetStateTo("Success State"));

        stubFor(get(urlEqualTo("/rest/data-sharing/wiretap?fromTimestamp=0&toTimestamp=100000&limit=100&offset=100&moduleNames=module1&moduleNames=module2"))
            .inScenario("Retry Scenario")
            .whenScenarioStateIs("Success State")
            .withHeader(HttpHeaders.USER_AGENT, equalTo(""))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
            .willReturn(aResponse()
                .withBody(this.serializeWiretapEvents(this.createWiretapEvents(100), 250, true))
                .withStatus(200)
            ));

        // Third page: 50 events
        stubFor(get(urlEqualTo("/rest/data-sharing/wiretap?fromTimestamp=0&toTimestamp=100000&limit=100&offset=200&moduleNames=module1&moduleNames=module2"))
            .withHeader(HttpHeaders.USER_AGENT, equalTo(""))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
            .willReturn(aResponse()
                .withBody(this.serializeWiretapEvents(this.createWiretapEvents(50), 250, false))
                .withStatus(200)
            ));


        IBigQueue wiretapQueue = EntityBigQueueCache.instance().get("DASHBOARD_1-Ikasan-ESB-Dashboard-WIRETAP-queue");

        // Set up expectations: first attempt fails on second page, retry succeeds
        flowTestRule.consumer("Wiretap Entity Scheduled Consumer")
            .producer("Wiretap Entity Big Queue Producer") // First page succeeds
            .consumer("Wiretap Entity Scheduled Consumer")
            .producer("Wiretap Entity Big Queue Producer") // Second page fails - will retry
            .consumer("Wiretap Entity Scheduled Consumer")
            .producer("Wiretap Entity Big Queue Producer") // Second page retry succeeds
            .consumer("Wiretap Entity Scheduled Consumer")
            .producer("Wiretap Entity Big Queue Producer"); // Third page succeeds

        flowTestRule.startFlow();
        flowTestRule.fireScheduledConsumer();

        assertEquals(Flow.RUNNING, flowTestRule.getFlowState());

        WiretapEventsToBigQueueMessageSerialiser serialiser = new WiretapEventsToBigQueueMessageSerialiser();

        // Because the BigQueue producer is not LRCO the first page of data is written
        // to the BigQueue destination. This is fine bacause all entities have an immutable
        // identifier and they will simply be upserted into the destination datastore
        // if written more than one time.
        Awaitility.await().atMost(30, TimeUnit.SECONDS)
            .untilAsserted(() -> Assert.assertEquals(1, wiretapQueue.size()));

        // Confirm details of the first page.
        byte[] bytes = wiretapQueue.dequeue();
        List<WiretapEvent> events = serialiser.deserialise(bytes);
        Assert.assertEquals(100, events.size());

        // In the meantime the consumer has gone into recovery and subsequently
        // recovered. This time all 3 expected pages are written,
        Awaitility.await().atMost(30, TimeUnit.SECONDS)
            .untilAsserted(() -> Assert.assertEquals(3, wiretapQueue.size()));

        // Verify the details pages published to the downstream
        // BigQueue destination.
        bytes = wiretapQueue.dequeue();
        events = serialiser.deserialise(bytes);
        Assert.assertEquals(100, events.size());

        bytes = wiretapQueue.dequeue();
        events = serialiser.deserialise(bytes);
        Assert.assertEquals(100, events.size());

        bytes = wiretapQueue.dequeue();
        events = serialiser.deserialise(bytes);
        Assert.assertEquals(50, events.size());

        Awaitility.await().atMost(30, TimeUnit.SECONDS)
            .untilAsserted(() -> flowTestRule.assertIsSatisfied());

        flowTestRule.stopFlow();
        wiretapQueue.removeAll();

    }

    /**
     * Creates a test stub to simulate an HTTP GET request for retrieving wiretap event counts.
     *
     * @param count the number of wiretap events to be simulated in the response
     * @param from the starting timestamp for filtering events
     * @param to the ending timestamp for filtering events
     * @throws IOException if an error occurs while creating the stub
     */
    private void createCountStub(int count, long from , long to) throws IOException {
        stubFor(get(urlEqualTo("/rest/data-sharing/wiretap/count?fromTimestamp="+from+"&toTimestamp="+to+"&" +
                "moduleNames=module1&moduleNames=module2"))
                .withHeader(HttpHeaders.USER_AGENT, equalTo(""))
                .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
                .willReturn(aResponse()
                    .withBody(this.serializeWiretapEvents(List.of(), count))
                    .withStatus(200)
                )
        );
    }

    /**
     * Creates a test stub to simulate an HTTP GET request for retrieving wiretap events with pagination support.
     *
     * @param resultSize the number of wiretap events to be included in the simulated response
     * @param totalCount the total count of events available across all pages
     * @param from the starting timestamp used for filtering events
     * @param to the ending timestamp used for filtering events
     * @param limit the maximum number of events to be included in a single page of the response
     * @param offset the starting position of the events in the total count, used for pagination
     * @throws IOException if an error occurs during the creation of the stub
     */
    private void createQueryStub(int resultSize, int totalCount, long from , long to, int limit, int offset) throws IOException {
        stubFor(get(urlEqualTo("/rest/data-sharing/wiretap?fromTimestamp="+from+"&toTimestamp="+to+"&" +
                "limit="+limit+"&offset="+offset+"&moduleNames=module1&moduleNames=module2"))
                .withHeader(HttpHeaders.USER_AGENT, equalTo(""))
                .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
                .willReturn(aResponse()
                    .withBody(this.serializeWiretapEvents(this.createWiretapEvents(resultSize), totalCount))
                    .withStatus(200)
                )

        );
    }


    /**
     * Creates a WiretapEvent for testing purposes.
     *
     * @param identifier unique identifier for the event
     * @param moduleName the module name
     * @param flowName the flow name
     * @param componentName the component name
     * @param event the event payload
     * @param timestamp the timestamp in milliseconds
     * @return WiretapEvent instance
     */
    private WiretapEvent<String> createWiretapEvent(String identifier, String moduleName, String flowName,
                                                    String componentName, String event, long timestamp) {
        WiretapEventImpl wiretapEvent = new WiretapEventImpl();
        wiretapEvent.setId(identifier);
        wiretapEvent.setModuleName(moduleName);
        wiretapEvent.setFlowName(flowName);
        wiretapEvent.setComponentName(componentName);
        wiretapEvent.setEvent(event);
        wiretapEvent.setTimestamp(timestamp);
        wiretapEvent.setExpiry(timestamp + 86400000); // 24 hours expiry
        wiretapEvent.setEventId("event-" + identifier);
        return wiretapEvent;
    }

    /**
     * Creates a list of WiretapEvent entities for testing purposes.
     *
     * @param count number of events to create
     * @return List of WiretapEvent instances
     */
    private List<WiretapEvent<String>> createWiretapEvents(int count) {
        List<WiretapEvent<String>> events = new ArrayList<>();
        long baseTimestamp = System.currentTimeMillis();

        for (int i = 0; i < count; i++) {
            events.add(createWiretapEvent(
                String.valueOf(i + 1),
                "test-module-" + (i % 3),
                "test-flow-" + (i % 2),
                "test-component-" + (i % 4),
                "{\"data\":\"test event " + i + "\"}",
                baseTimestamp + (i * 1000)
            ));
        }

        return events;
    }

    /**
     * Creates a list of WiretapEvent entities with custom parameters for testing purposes.
     *
     * @param count number of events to create
     * @param moduleName the module name to use for all events
     * @param flowName the flow name to use for all events
     * @return List of WiretapEvent instances
     */
    private List<WiretapEvent<String>> createWiretapEvents(int count, String moduleName, String flowName) {
        List<WiretapEvent<String>> events = new ArrayList<>();
        long baseTimestamp = System.currentTimeMillis();

        for (int i = 0; i < count; i++) {
            events.add(createWiretapEvent(
                String.valueOf(i + 1),
                moduleName,
                flowName,
                "component-" + i,
                "{\"data\":\"test event " + i + "\",\"index\":" + i + "}",
                baseTimestamp + (i * 1000)
            ));
        }

        return events;
    }

    /**
     * Serializes a list of WiretapEvents to JSON string in the shape of wiretap-response.json.
     * The response includes data array, totalCount, and hasMore fields.
     *
     * @param wiretapEvents the list of events to serialize
     * @return JSON string representation matching wiretap-response.json format
     * @throws IOException if serialization fails
     */
    private String serializeWiretapEvents(List<WiretapEvent<String>> wiretapEvents, int totalCount) throws IOException {
        Map<String, Object> response = new HashMap<>();
        response.put("data", wiretapEvents);
        response.put("totalCount", totalCount);
        response.put("hasMore", false);
        return objectMapper.writeValueAsString(response);
    }

    /**
     * Serializes a list of WiretapEvents to JSON string in the shape of wiretap-response.json
     * with pagination support.
     *
     * @param wiretapEvents the list of events to serialize
     * @param totalCount the total count of events available
     * @param hasMore whether there are more events available
     * @return JSON string representation matching wiretap-response.json format
     * @throws IOException if serialization fails
     */
    private String serializeWiretapEvents(List<WiretapEvent<String>> wiretapEvents, long totalCount, boolean hasMore) throws IOException {
        Map<String, Object> response = new HashMap<>();
        response.put("data", wiretapEvents);
        response.put("totalCount", totalCount);
        response.put("hasMore", hasMore);
        return objectMapper.writeValueAsString(response);
    }

}
