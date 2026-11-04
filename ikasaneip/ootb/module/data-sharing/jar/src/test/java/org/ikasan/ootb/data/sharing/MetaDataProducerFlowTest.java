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
import java.util.concurrent.TimeUnit;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.ikasan.ootb.data.sharing.module.serialiser.ModuleAndConfigMetaDataTestFixtures.*;
import static org.junit.Assert.*;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import org.ikasan.ootb.data.sharing.module.component.splitter.ConfigurationMetaDataSplitter;
import org.ikasan.ootb.data.sharing.module.component.splitter.configuration.ConfigurationMetaDataSplitterConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class, properties = {
    "spring.main.allow-bean-definition-overriding=true",
    "data-sharing.upstreamDashboard=http://localhost:9080",
    "data-sharing.downStreamDashboardEntities[DASHBOARD_1\\ Ikasan\\ ESB\\ Dashboard]=MODULE_METADATA",
    "data-sharing.downStreamDashboardEntityCronExpressions[DASHBOARD_1\\ Ikasan\\ ESB\\ Dashboard-MODULE_METADATA]=0 0 0 1 1 ? 2099"
}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Sql(scripts = "/cleanDatabaseTables.sql", executionPhase = AFTER_TEST_METHOD)
public class MetaDataProducerFlowTest {
    private static final String FLOW_NAME = "DASHBOARD_1 Ikasan ESB Dashboard Metadata Producer Flow";
    private static final String REST_URL_PATH = "/rest/module/metadata";
    private static final String CONFIGURATIONS = "/rest/configuration/metadata";
    private static final String CACHE = "DASHBOARD_1-Ikasan-ESB-Dashboard-MODULE_METADATA-queue";

    @ClassRule public static WireMockRule wireMockRule = new WireMockRule(
        WireMockConfiguration.options().port(9081));

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
    public void test_metadata_produce_modules_individually_and_configurations_in_batches() throws Exception {
        publishAndAssert(createModuleAndConfigMetaData(2), 5);
    }

    @Test(timeout = 120000)
    public void test_metadata_produce_without_configurations_retains_message_on_failure() throws Exception {
        var payload = createModuleAndConfigMetaData(2);
        payload.getConfigurations().clear();
        assertEmptyRouteFailure(payload, 2);
    }

    @Test(timeout = 120000)
    public void test_metadata_produce_empty_payload_stops_without_rest_calls() throws Exception {
        assertEmptyRouteFailure(createModuleAndConfigMetaData(0), 0);
    }

    @Test(timeout = 120000)
    public void test_metadata_produce_configuration_only_payload_stops_at_module_splitter() throws Exception {
        var payload = createModuleAndConfigMetaData(1);
        payload.getModules().clear();
        assertEmptyRouteFailure(payload, 0);
    }

    // Characterise the current flow: Ikasan rejects empty splitter results.
    // In particular, missing configurations are not yet safely skipped downstream.
    private void assertEmptyRouteFailure(ModuleAndConfigMetaData payload, int moduleRequests) throws Exception {
        wireMockRule.stubFor(put(urlEqualTo(REST_URL_PATH)).willReturn(ok()));
        wireMockRule.stubFor(put(urlEqualTo(CONFIGURATIONS)).willReturn(ok()));
        queue.enqueue(serialiser.serialise(payload));
        flowTestRule.startFlow();
        Awaitility.await().atMost(15, TimeUnit.SECONDS).untilAsserted(() ->
            assertEquals(Flow.STOPPED_IN_ERROR, flowTestRule.getFlowState()));
        assertEquals(1, queue.size());
        assertModuleAndConfigMetaDataEqual(payload, serialiser.deserialise(queue.peek()));
        wireMockRule.verify(moduleRequests, putRequestedFor(urlEqualTo(REST_URL_PATH)));
        wireMockRule.verify(0, putRequestedFor(urlEqualTo(CONFIGURATIONS)));
    }

    private void publishAndAssert(ModuleAndConfigMetaData payload, int batchSize) throws Exception {
        var splitter = (ConfigurationMetaDataSplitter) moduleUnderTest.getFlow(FLOW_NAME)
            .getFlowElement("Configuration Batch Splitter").getFlowComponent();
        var configuration = new ConfigurationMetaDataSplitterConfiguration();
        configuration.setBatchSize(batchSize);
        splitter.setConfiguration(configuration);
        wireMockRule.stubFor(put(urlEqualTo(REST_URL_PATH)).willReturn(ok()));
        wireMockRule.stubFor(put(urlEqualTo(CONFIGURATIONS)).willReturn(ok()));
        queue.enqueue(serialiser.serialise(payload));

        flowTestRule.consumer("Metadata BigQueue Consumer")
            .multiRecipientRouter("Seperate Module and Configuration routes").splitter("Module Splitter");
        for (var module : payload.getModules()) {
            flowTestRule.producer("Module REST Producer");
        }
        flowTestRule.splitter("Configuration Batch Splitter");
        int batches = (payload.getConfigurations().size() + batchSize - 1) / batchSize;
        for (int i = 0; i < batches; i++) {
            flowTestRule.producer("Configuration REST Producer");
        }
        flowTestRule.startFlow();
        Awaitility.await().atMost(15, TimeUnit.SECONDS).untilAsserted(() -> {
            assertEquals(0, queue.size());
            flowTestRule.assertIsSatisfied();
            wireMockRule.verify(payload.getModules().size(), putRequestedFor(urlEqualTo(REST_URL_PATH)));
            wireMockRule.verify(batches, putRequestedFor(urlEqualTo(CONFIGURATIONS)));
        });
        for (var module : payload.getModules()) {
            wireMockRule.verify(1, putRequestedFor(urlEqualTo(REST_URL_PATH))
                .withRequestBody(equalToJson(mapper.writeValueAsString(module))));
        }
        for (int start = 0; start < payload.getConfigurations().size(); start += batchSize) {
            var batch = payload.getConfigurations().subList(start, Math.min(start + batchSize, payload.getConfigurations().size()));
            wireMockRule.verify(1, putRequestedFor(urlEqualTo(CONFIGURATIONS))
                .withRequestBody(equalToJson(mapper.writeValueAsString(batch))));
        }
        assertEquals(Flow.RUNNING, flowTestRule.getFlowState());
    }
}
