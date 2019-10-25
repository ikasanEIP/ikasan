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
import org.ikasan.dashboard.ui.home.view.HomeView;
import org.ikasan.dashboard.ui.layout.IkasanAppLayout;
import org.ikasan.dashboard.ui.search.view.SearchView;
import org.ikasan.spec.metadata.ConfigurationMetaDataService;
import org.ikasan.spec.metadata.ModuleMetaDataService;
import org.ikasan.topology.metadata.JsonFlowMetaDataProvider;
import org.ikasan.topology.metadata.JsonModuleMetaDataProvider;
import org.ikasan.vaadin.visjs.network.NetworkDiagram;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
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
public class GraphViewTest
{
    public static final String MODULE_JSON = "/data/graph/module.json";
    public static final String MODULE_FOUR_JSON = "/data/graph/module-four.json";
    public static final String SIMPLE_BOND_FLOW_JSON = "/data/graph/bondFlowsGraph.json";
    public static final String ELABORATE_BOND_FLOW_JSON = "/data/graph/bondFlowsGraphElaborate.json";
    public static final String BAD_JSON = "/data/graph/bad.json";
    public static final String BAD_XML = "/data/graph/bad.xml";


    @Autowired
    private ApplicationContext ctx;

    @MockBean
    private ModuleMetaDataService moduleMetadataService;

    @MockBean
    private ConfigurationMetaDataService configurationMetadataService;

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

        Assertions.assertEquals(10, myRoutes.size(), "Number of Routes is 1!");
        Assertions.assertEquals("", myRoutes.get(0).getUrl(), "URL is empty string!");
        Assertions.assertEquals(SearchView.class, myRoutes.get(0).getNavigationTarget(), "Navigation target is HomeView.class!");
    }

    @Test
    @Ignore
    public void testCreateGraph() throws IOException
    {
        UI.getCurrent().navigate("visualisation");

        GraphView graphView = _get(GraphView.class);

        Assertions.assertNotNull(graphView);

        JsonModuleMetaDataProvider provider = new JsonModuleMetaDataProvider(new JsonFlowMetaDataProvider());

        graphView.createGraph(provider.deserialiseModule(loadDataFile(MODULE_JSON)));

        NetworkDiagram networkDiagram = _get(NetworkDiagram.class);
        Assertions.assertNotNull(networkDiagram, "Network diagram should not be null!");

        Assertions.assertEquals(8, networkDiagram.getNodesDataProvider().size(new Query<>())
            , "There should be 12 nodes in the network diagram!");
        Assertions.assertEquals(7, networkDiagram.getEdgesDataProvider().size(new Query<>())
            , "There should be 12 nodes in the network diagram!");

//        graphView.createGraph(provider.deserialiseModule(loadDataFile(MODULE_FOUR_JSON)));
//
//        networkDiagram = _get(NetworkDiagram.class);
//        Assertions.assertNotNull(networkDiagram, "Network diagram should not be null!");
//
//        Assertions.assertEquals(18, networkDiagram.getNodesDataProvider().size(new Query<>())
//            , "There should be 28 nodes in the network diagram!");
//        Assertions.assertEquals(17, networkDiagram.getEdgesDataProvider().size(new Query<>())
//            , "There should be 27 nodes in the network diagram!");
    }


    @Test
    @Ignore
    public void testBadFileUpload() throws IOException
    {
//        GraphView upload = _get(GraphView.class);
//
//        Assertions.assertNotNull(upload);
//
//        upload.createGraph("application/json", loadDataFileStream(BAD_JSON));
//
//        Notification notification = getNotifications().get(0);
//
//        System.out.println(notification.getChildren());
//
//        VerticalLayout verticalLayout = (VerticalLayout)notification.getChildren().collect(Collectors.toList()).get(0);
//
//        Div div = (Div)verticalLayout.getChildren().findFirst().get();
//
//        Assertions.assertEquals("An error has occurred attempting to load graph JSON: Unrecognized token 'bad': was expecting ('true', 'false' or 'null')\n" +
//            " at [Source: (String)\"bad json\"; line: 1, column: 4]", div.getText(), "Error notification message must equal!");
//
//
//        clearNotifications();
    }

    @Test
    @Ignore
    public void testBadXmlFileUpload() throws IOException
    {
//        GraphView upload = _get(GraphView.class);
//
//        Assertions.assertNotNull(upload);
//
//        upload.createGraph("application/xml", loadDataFileStream(BAD_XML));
//
//        Notification notification = getNotifications().get(0);
//
//        System.out.println(notification.getChildren());
//
//        VerticalLayout verticalLayout = (VerticalLayout)notification.getChildren().collect(Collectors.toList()).get(0);
//
//        Div div = (Div)verticalLayout.getChildren().findFirst().get();
//
//        Assertions.assertEquals("File should be JSON!", div.getText(), "Error notification message must equal!");
//
//
//        clearNotifications();
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
