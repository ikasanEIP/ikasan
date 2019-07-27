package org.ikasan.dashboard.ui.visualisation.view;

import com.github.mvysny.kaributesting.v10.MockVaadin;
import com.github.mvysny.kaributesting.v10.Routes;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.router.RouteData;
import com.vaadin.flow.router.Router;
import com.vaadin.flow.router.RouterLayout;
import org.apache.commons.io.IOUtils;
import org.ikasan.dashboard.ui.layout.IkasanAppLayout;
import org.ikasan.vaadin.visjs.network.NetworkDiagram;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.github.mvysny.kaributesting.v10.LocatorJ.*;
import static com.github.mvysny.kaributesting.v10.NotificationsKt.*;

public class GraphViewTest
{
    public static final String SIMPLE_BOND_FLOW_JSON = "/data/graph/bondFlowsGraph.json";
    public static final String ELABORATE_BOND_FLOW_JSON = "/data/graph/bondFlowsGraphElaborate.json";
    public static final String BAD_JSON = "/data/graph/bad.json";
    public static final String BAD_XML = "/data/graph/bad.xml";

    private static Routes routes;

    @BeforeAll
    public static void createRoutes()
    {
        // initialize routes only once, to avoid view auto-detection before every test and to speed up the tests
        routes = new Routes().autoDiscoverViews("org.ikasan.dashboard.ui");
    }

    @BeforeEach
    public void setupVaadin()
    {
        MockVaadin.setup(routes);
    }

    @Test
    public void testGraphView()
    {
        final IkasanAppLayout main = (IkasanAppLayout) UI.getCurrent().getChildren().findFirst().get();
        Router router = UI.getCurrent().getRouter();
        Map<Class<? extends RouterLayout>, List<RouteData>> routesByParent = router.getRoutesByParent();
        List<RouteData> myRoutes = routesByParent.get(IkasanAppLayout.class);

        Assertions.assertEquals(1, myRoutes.size(), "Number of Routes is 1!");
        Assertions.assertEquals("", myRoutes.get(0).getUrl(), "URL is empty string!");
        Assertions.assertEquals(GraphView.class, myRoutes.get(0).getNavigationTarget(), "Navigation target is GraphView.class!");
    }

    @Test
    public void testCreateGraph() throws IOException
    {
        GraphView upload = _get(GraphView.class);

        Assertions.assertNotNull(upload);

//        upload.createGraph("application/json", loadDataFileStream(SIMPLE_BOND_FLOW_JSON));
//
//        NetworkDiagram networkDiagram = _get(NetworkDiagram.class);
//        Assertions.assertNotNull(networkDiagram, "Network diagram should not be null!");
//
//        Assertions.assertEquals(12, networkDiagram.getNodesDataProvider().size(new Query<>())
//            , "There should be 12 nodes in the network diagram!");
//        Assertions.assertEquals(12, networkDiagram.getEdgesDataProvider().size(new Query<>())
//            , "There should be 12 nodes in the network diagram!");
//
//        upload.createGraph("application/json", loadDataFileStream(ELABORATE_BOND_FLOW_JSON));
//
//        networkDiagram = _get(NetworkDiagram.class);
//        Assertions.assertNotNull(networkDiagram, "Network diagram should not be null!");
//
//        Assertions.assertEquals(28, networkDiagram.getNodesDataProvider().size(new Query<>())
//            , "There should be 28 nodes in the network diagram!");
//        Assertions.assertEquals(27, networkDiagram.getEdgesDataProvider().size(new Query<>())
//            , "There should be 27 nodes in the network diagram!");
    }

    @Test
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
