package org.ikasan.dashboard.ui.visualisation.component;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import com.github.mvysny.kaributesting.v10.MockVaadin;
import com.github.mvysny.kaributesting.v10.MockedUI;
import com.github.mvysny.kaributesting.v10.Routes;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.function.DeploymentConfiguration;
import com.vaadin.flow.server.*;
import com.vaadin.flow.server.startup.ApplicationRouteRegistry;
import com.vaadin.flow.spring.SpringServlet;
import com.vaadin.flow.spring.SpringVaadinServletService;
import elemental.json.JsonArray;
import elemental.json.JsonObject;
import elemental.json.JsonValue;
import org.apache.commons.io.IOUtils;
import org.ikasan.configurationService.metadata.JsonConfigurationMetaDataProvider;
import org.ikasan.dashboard.Application;
import org.ikasan.dashboard.ui.util.SecurityConstants;
import org.ikasan.dashboard.ui.visualisation.view.GraphView;
import org.ikasan.security.service.authentication.IkasanAuthentication;
import org.ikasan.solr.service.SolrGeneralServiceImpl;
import org.ikasan.spec.metadata.ConfigurationMetaData;
import org.ikasan.spec.metadata.ConfigurationMetaDataProvider;
import org.ikasan.spec.metadata.ConfigurationMetaDataService;
import org.ikasan.spec.metadata.ModuleMetaDataService;
import org.ikasan.spec.module.client.ConfigurationService;
import org.ikasan.topology.metadata.JsonFlowMetaDataProvider;
import org.ikasan.topology.metadata.JsonModuleMetaDataProvider;
import org.ikasan.vaadin.visjs.network.NetworkDiagram;
import org.ikasan.vaadin.visjs.network.Node;
import org.ikasan.vaadin.visjs.network.event.DoubleClickEvent;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.concurrent.Synchroniser;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.github.mvysny.kaributesting.v10.ButtonKt._click;
import static com.github.mvysny.kaributesting.v10.LocatorJ._get;
import static org.mockito.ArgumentMatchers.anyString;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class},
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ComponentConfigurationDialogTest {

    public static final String LARGE_COMPONENT_CONFIGURATION = "/data/graph/largeComponentConfiguration.json";
    public static final String MODULE_JSON = "/data/graph/module.json";

    private Mockery mockery = new Mockery()
    {{
        setImposteriser(ClassImposteriser.INSTANCE);
        setThreadingPolicy(new Synchroniser());
    }};

    ConfigurationMetaDataProvider<String> configurationMetaDataProvider = new JsonConfigurationMetaDataProvider(null);

    JsonObject jsonObject = mockery.mock(JsonObject.class);
    JsonObject pointer = mockery.mock(JsonObject.class, "pointer");
    JsonObject canvas = mockery.mock(JsonObject.class, "canvas");
    JsonArray jsonNodes = mockery.mock(JsonArray.class, "nodes");
    JsonValue jsonValue = mockery.mock(JsonValue.class, "node");

    @MockBean
    private SolrGeneralServiceImpl solrSearchService;

    @MockBean
    private IkasanAuthentication ikasanAuthentication;

    @MockBean
    private ConfigurationService configurationRestService;

    @MockBean
    private ModuleMetaDataService moduleMetadataService;

    @MockBean
    private ConfigurationMetaDataService configurationMetadataService;

    @Autowired
    private ApplicationContext ctx;

    @Before
    public void setup()
    {
        Logger rootLogger = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        rootLogger.setLevel(Level.WARN);

        final SpringServlet servlet = new SpringServlet(ctx, true) {
            @Override
            protected VaadinServletService createServletService(DeploymentConfiguration deploymentConfiguration) throws ServiceException
            {
                final VaadinServletService service = new SpringVaadinServletService(this, deploymentConfiguration, ctx) {
                    @Override
                    protected boolean isAtmosphereAvailable() {
                        return false;
                    }

                    @Override
                    protected RouteRegistry getRouteRegistry() {
                        new Routes().autoDiscoverViews("org.ikasan.dashboard.ui").register(this.getServlet().getServletContext());
                        RouteRegistry registry =  ApplicationRouteRegistry.getInstance(this.getServlet().getServletContext());
                        return registry;
                    }

                    @Override
                    public String getMainDivId(VaadinSession session, VaadinRequest request) {
                        return "ROOT-1";
                    }
                };
                service.init();
                return service;
            }
        };
        MockVaadin.setup(MockedUI::new, servlet);
    }

    @Test
    public void test_open_large_mixed_component_configuration() throws IOException {

        ConfigurationMetaData data = configurationMetaDataProvider.deserialiseMetadataConfiguration(loadDataFile(LARGE_COMPONENT_CONFIGURATION));

        Mockito.when(this.ikasanAuthentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY))
            .thenReturn(true);

        SecurityContextHolder.getContext().setAuthentication(this.ikasanAuthentication);

        UI.getCurrent().navigate("visualisation");

        GraphView graphView = _get(GraphView.class);

        JsonModuleMetaDataProvider provider = new JsonModuleMetaDataProvider(new JsonFlowMetaDataProvider());

        ReflectionTestUtils.invokeMethod(graphView, "createModuleVisualisation", provider.deserialiseModule(loadDataFile(MODULE_JSON)));

        NetworkDiagram networkDiagram = _get(NetworkDiagram.class);

        Set<Node> nodes = networkDiagram.getNodesDataProvider().fetch(new Query<>()).collect(Collectors.toSet());

        Optional<Node> consumer = nodes.stream().filter(node -> node.getId().equals("Test Consumer0")).findFirst();

        consumer.get().clickedOn(consumer.get().getX(), consumer.get().getY());

        mockery.checking(new Expectations()
        {
            {
                // set event factory
                oneOf(jsonObject).getObject(with(any(String.class)));
                will(returnValue(pointer));
                oneOf(pointer).getObject(with(any(String.class)));
                will(returnValue(canvas));
                oneOf(canvas).getNumber(with(any(String.class)));
                will(returnValue(new Double(consumer.get().getX())));
                oneOf(canvas).getNumber(with(any(String.class)));
                will(returnValue(new Double(consumer.get().getY())));
                oneOf(jsonObject).getArray(with(any(String.class)));
                will(returnValue(jsonNodes));
                oneOf(jsonNodes).length();
                will(returnValue(5));
                oneOf(jsonNodes).get(0);
                will(returnValue(jsonValue));
                oneOf(jsonValue).asString();
                will(returnValue("Test Consumer0"));
            }
        });

        ReflectionTestUtils.invokeMethod(networkDiagram, "fireEvent", new DoubleClickEvent(networkDiagram, true, jsonObject));

        ComponentOptionsDialog componentOptionsDialog = _get(ComponentOptionsDialog.class);

        componentOptionsDialog.getElement();

        Mockito.when(this.configurationRestService.getConfiguredResourceConfiguration(anyString(), anyString(), anyString(), anyString()))
            .thenReturn(data);

        _click(_get(Button.class, spec -> spec.withCaption("Component Configuration")));
    }

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
