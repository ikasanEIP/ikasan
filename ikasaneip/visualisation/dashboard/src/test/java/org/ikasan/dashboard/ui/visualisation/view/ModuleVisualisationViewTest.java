package org.ikasan.dashboard.ui.visualisation.view;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.router.RouteData;
import com.vaadin.flow.router.Router;
import com.vaadin.flow.router.RouterLayout;
import org.apache.commons.io.IOUtils;
import org.ikasan.dashboard.ui.UITest;
import org.ikasan.dashboard.ui.layout.IkasanAppLayout;
import org.ikasan.dashboard.ui.search.view.SearchView;
import org.ikasan.spec.metadata.ConfigurationMetaDataService;
import org.ikasan.spec.metadata.ModuleMetaDataService;
import org.ikasan.spec.module.client.ConfigurationService;
import org.ikasan.topology.metadata.JsonFlowMetaDataProvider;
import org.ikasan.topology.metadata.JsonModuleMetaDataProvider;
import org.ikasan.vaadin.visjs.network.NetworkDiagram;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import static com.github.mvysny.kaributesting.v10.LocatorJ._get;

public class ModuleVisualisationViewTest extends UITest
{
    public static final String MODULE_JSON = "/data/graph/module.json";
    public static final String MODULE_FOUR_JSON = "/data/graph/module-four.json";
    public static final String SIMPLE_BOND_FLOW_JSON = "/data/graph/bondFlowsGraph.json";
    public static final String ELABORATE_BOND_FLOW_JSON = "/data/graph/bondFlowsGraphElaborate.json";
    public static final String BAD_JSON = "/data/graph/bad.json";
    public static final String BAD_XML = "/data/graph/bad.xml";


   @MockBean
    private ModuleMetaDataService moduleMetadataService;

    @MockBean
    private ConfigurationMetaDataService configurationMetadataService;

    @MockBean
    private ConfigurationService configurationRestService;

    @Override
    public void setup_expectations() {

    }


    @Test
    public void test_create_graph() throws IOException
    {
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

        graphView.createModuleVisualisation(provider.deserialiseModule(loadDataFile(MODULE_FOUR_JSON)));

        networkDiagram = _get(NetworkDiagram.class);
        Assertions.assertNotNull(networkDiagram, "Network diagram should not be null!");

        Assertions.assertEquals(18, networkDiagram.getNodesDataProvider().size(new Query<>())
            , "There should be 28 nodes in the network diagram!");
        Assertions.assertEquals(17, networkDiagram.getEdgesDataProvider().size(new Query<>())
            , "There should be 27 nodes in the network diagram!");
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
