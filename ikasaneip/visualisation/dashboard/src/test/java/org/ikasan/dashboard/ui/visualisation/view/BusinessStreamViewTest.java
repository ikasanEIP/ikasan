package org.ikasan.dashboard.ui.visualisation.view;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import com.github.mvysny.kaributesting.v10.MockVaadin;
import com.github.mvysny.kaributesting.v10.MockedUI;
import com.github.mvysny.kaributesting.v10.Routes;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.function.DeploymentConfiguration;
import com.vaadin.flow.router.RouteData;
import com.vaadin.flow.router.Router;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.server.*;
import com.vaadin.flow.server.startup.ApplicationRouteRegistry;
import com.vaadin.flow.spring.SpringServlet;
import com.vaadin.flow.spring.SpringVaadinServletService;
import org.apache.commons.io.IOUtils;
import org.ikasan.dashboard.Application;
import org.ikasan.dashboard.ui.layout.IkasanAppLayout;
import org.ikasan.dashboard.ui.search.view.SearchView;
import org.ikasan.dashboard.ui.util.SecurityConstants;
import org.ikasan.rest.client.ConfigurationRestServiceImpl;
import org.ikasan.spec.metadata.ConfigurationMetaData;
import org.ikasan.spec.metadata.ConfigurationMetaDataService;
import org.ikasan.spec.metadata.ConfigurationParameterMetaData;
import org.ikasan.spec.metadata.ModuleMetaDataService;
import org.ikasan.spec.module.client.ConfigurationService;
import org.ikasan.topology.metadata.JsonFlowMetaDataProvider;
import org.ikasan.topology.metadata.JsonModuleMetaDataProvider;
import org.ikasan.vaadin.visjs.network.NetworkDiagram;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.concurrent.Synchroniser;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import static com.github.mvysny.kaributesting.v10.LocatorJ._get;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class},
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class BusinessStreamViewTest
{
    public static final String MODULE_JSON = "/data/graph/module.json";

    private Mockery mockery = new Mockery()
    {{
        setImposteriser(ClassImposteriser.INSTANCE);
        setThreadingPolicy(new Synchroniser());
    }};

    /**
     * mocked container, listener and dao
     */
    ConfigurationMetaData configurationMetaData = mockery.mock(ConfigurationMetaData.class);

    ConfigurationParameterMetaData configurationParameterMetaData = mockery.mock(ConfigurationParameterMetaData.class);

    @Autowired
    private ApplicationContext ctx;

    @MockBean
    private ModuleMetaDataService moduleMetadataService;

    @MockBean
    private ConfigurationMetaDataService configurationMetadataService;

    @MockBean
    private ConfigurationService configurationService;

    @Before
    public void setup() throws Exception
    {
        Logger rootLogger = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        rootLogger.setLevel(Level.WARN);

        final SpringServlet servlet = new SpringServlet(ctx, true) {
            @Override
            protected VaadinServletService createServletService(DeploymentConfiguration deploymentConfiguration) throws ServiceException {
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

    @After
    public void tearDown() {
        MockVaadin.tearDown();
    }


    @Test
    public void testGraphView()
    {
        final IkasanAppLayout main = (IkasanAppLayout) UI.getCurrent().getChildren().findFirst().get();
        Router router = UI.getCurrent().getRouter();
        Map<Class<? extends RouterLayout>, List<RouteData>> routesByParent = router.getRoutesByParent();
        List<RouteData> myRoutes = routesByParent.get(IkasanAppLayout.class);

        Assertions.assertEquals(10, myRoutes.size(), "Number of Routes is 1!0");
        Assertions.assertEquals("", myRoutes.get(0).getUrl(), "URL is empty string!");
        Assertions.assertEquals(SearchView.class, myRoutes.get(0).getNavigationTarget(), "Navigation target is SearchView.class!");
    }

    @Test
    public void testCreateGraph() throws IOException
    {
        Mockito.when(this.configurationService.getFlowConfiguration(Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
            .thenReturn(configurationMetaData);

        mockery.checking(new Expectations() {
             {
                 // set event factory
                 oneOf(configurationMetaData).getParameters();
                 will(returnValue(List.of(configurationParameterMetaData)));

                 oneOf(configurationParameterMetaData).getName();
                 will(returnValue("isRecording"));

                 oneOf(configurationParameterMetaData).getValue();
                 will(returnValue(true));
             }
         });

        UI.getCurrent().navigate("visualisation");

        GraphView graphView = _get(GraphView.class);

        Assertions.assertNotNull(graphView);

        JsonModuleMetaDataProvider provider = new JsonModuleMetaDataProvider(new JsonFlowMetaDataProvider());

        graphView.createModuleVisualisation(provider.deserialiseModule(loadDataFile(MODULE_JSON)));

        NetworkDiagram networkDiagram = _get(NetworkDiagram.class);
        Assertions.assertNotNull(networkDiagram, "Network diagram should not be null!");

        Assertions.assertEquals(8, networkDiagram.getNodesDataProvider().size(new Query<>())
            , "There should be 12 nodes in the network diagram!");
        Assertions.assertEquals(7, networkDiagram.getEdgesDataProvider().size(new Query<>())
            , "There should be 12 nodes in the network diagram!");
    }


    protected String loadDataFile(String fileName) throws IOException
    {
        String contentToSend = IOUtils.toString(loadDataFileStream(fileName));

        return contentToSend;
    }

    protected InputStream loadDataFileStream(String fileName) throws IOException
    {
        return getClass().getResourceAsStream(fileName);
    }
}
