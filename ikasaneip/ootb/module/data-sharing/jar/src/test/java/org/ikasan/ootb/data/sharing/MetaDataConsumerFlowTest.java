package org.ikasan.ootb.data.sharing;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.awaitility.Awaitility;
import org.ikasan.bigqueue.IBigQueue;
import org.ikasan.ootb.data.sharing.module.Application;
import org.ikasan.ootb.data.sharing.module.cache.EntityBigQueueCache;
import org.ikasan.ootb.data.sharing.module.model.ModuleAndConfigMetaData;
import org.ikasan.ootb.data.sharing.module.serialiser.ModuleAndConfigMetaDataToBigQueueMessageSerialiser;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.module.Module;
import org.ikasan.testharness.flow.rule.IkasanFlowTestRule;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.ikasan.ootb.data.sharing.module.serialiser.ModuleAndConfigMetaDataTestFixtures.*;
import static org.junit.Assert.*;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import org.ikasan.ootb.data.sharing.module.component.broker.ModuleMetaDataBroker;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class, properties = {
    "spring.main.allow-bean-definition-overriding=true",
    "data-sharing.upstreamDashboard=http://localhost:9080",
    "data-sharing.downStreamDashboardEntities[DASHBOARD_1\\ Ikasan\\ ESB\\ Dashboard]=MODULE_METADATA",
    "data-sharing.downStreamDashboardEntityCronExpressions[DASHBOARD_1\\ Ikasan\\ ESB\\ Dashboard-MODULE_METADATA]=0 0 0 1 1 ? 2099"
}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Sql(scripts = "/cleanDatabaseTables.sql", executionPhase = AFTER_TEST_METHOD)
public class MetaDataConsumerFlowTest {
    private static final String FLOW_NAME = "DASHBOARD_1 Ikasan ESB Dashboard Module Metadata Consumer Flow";
    private static final String REST_URL_PATH = "/rest/data-sharing/module-metadata";
    private static final String CONFIGURATIONS = "/rest/data-sharing/configuration";
    private static final String CACHE = "DASHBOARD_1-Ikasan-ESB-Dashboard-MODULE_METADATA-queue";

    @ClassRule public static WireMockRule wireMockRule = new WireMockRule(
        WireMockConfiguration.options().port(9080));

    private final ObjectMapper mapper = new ObjectMapper();
    private final ModuleAndConfigMetaDataToBigQueueMessageSerialiser serialiser = new ModuleAndConfigMetaDataToBigQueueMessageSerialiser();
    private final IkasanFlowTestRule flowTestRule = new IkasanFlowTestRule();

    @Autowired private Module<Flow> moduleUnderTest;
    private IBigQueue queue;

    @Before
    public void setup() throws Exception {
        wireMockRule.resetAll();
        queue = EntityBigQueueCache.instance().get(CACHE);
        queue.removeAll();
        flowTestRule.withFlow(moduleUnderTest.getFlow(FLOW_NAME));
    }

    @After
    public void teardown() throws Exception {
        flowTestRule.stopFlow();
        queue.removeAll();
    }

    @Test(timeout = 120000)
    public void test_metadata_consume_paged_modules_and_configurations() throws Exception {
        var expected = createModuleAndConfigMetaData(2, 0);
        broker().getConfiguration().setBatchSize(1);
        stubPage(REST_URL_PATH, 0, expected.getModules().subList(0, 1), 2);
        stubPage(REST_URL_PATH, 1, expected.getModules().subList(1, 2), 2);
        for (var config : expected.getConfigurations()) {
            wireMockRule.stubFor(get(urlPathEqualTo(CONFIGURATIONS))
                .withQueryParam("configurationIdentifiers", equalTo(config.getConfigurationId()))
                .withQueryParam("offset", equalTo("0"))
                .withQueryParam("limit", equalTo("1"))
                .willReturn(ok().withBody(mapper.writeValueAsString(Map.of("data", List.of(config), "totalCount", 1, "hasMore", false)))));
        }
        runAndAssert(expected);
        wireMockRule.verify(2, getRequestedFor(urlPathEqualTo(REST_URL_PATH)));
        wireMockRule.verify(2, getRequestedFor(urlPathEqualTo(CONFIGURATIONS)));
    }

    @Test(timeout = 120000)
    public void test_metadata_consume_configuration_failure_keeps_modules() throws Exception {
        var expected = createModuleAndConfigMetaData(1);
        stubPage(REST_URL_PATH, 0, expected.getModules(), 1);
        wireMockRule.stubFor(get(urlPathEqualTo(CONFIGURATIONS)).willReturn(serverError()));
        expected.getConfigurations().clear();
        runAndAssert(expected);
        wireMockRule.verify(getRequestedFor(urlPathEqualTo(CONFIGURATIONS)));
    }

    @Test(timeout = 120000)
    public void test_metadata_consume_missing_configurations_keeps_modules() throws Exception {
        var expected = createModuleAndConfigMetaData(1);
        stubPage(REST_URL_PATH, 0, expected.getModules(), 1);
        stubPage(CONFIGURATIONS, 0, List.of(), 0);
        expected.getConfigurations().clear();
        runAndAssert(expected);
    }

    @Test(timeout = 120000)
    public void test_metadata_consume_empty_result_skips_configuration_lookup() throws Exception {
        stubPage(REST_URL_PATH, 0, List.of(), 0);
        runAndAssert(createModuleAndConfigMetaData(0));
        wireMockRule.verify(0, getRequestedFor(urlPathEqualTo(CONFIGURATIONS)));
    }

    private ModuleMetaDataBroker broker() {
        return (ModuleMetaDataBroker) moduleUnderTest.getFlow(FLOW_NAME).getFlowElement("Module Metadata Broker").getFlowComponent();
    }

    // Match existing entity flow stubs: raw JSON body without a Content-Type header.
    // The installed REST client currently misreads application/json responses requested as String.
    private void stubPage(String path, int offset, List<?> data, int total) throws Exception {
        wireMockRule.stubFor(get(urlPathEqualTo(path)).withQueryParam("offset", equalTo(Integer.toString(offset)))
            .willReturn(ok().withBody(mapper.writeValueAsString(Map.of("data", data, "totalCount", total,
                "hasMore", offset + data.size() < total)))));
    }

    private void runAndAssert(ModuleAndConfigMetaData expected) throws Exception {
        flowTestRule.consumer("Module Metadata Scheduled Consumer")
            .broker("Module Metadata Broker").producer("Module Metadata Big Queue Producer");
        flowTestRule.startFlow();
        flowTestRule.fireScheduledConsumer();
        Awaitility.await().atMost(15, TimeUnit.SECONDS).untilAsserted(() -> {
            assertEquals(1, queue.size());
            flowTestRule.assertIsSatisfied();
        });
        assertEquals(Flow.RUNNING, flowTestRule.getFlowState());
        assertModuleAndConfigMetaDataEqual(expected, serialiser.deserialise(queue.dequeue()));
    }
}
