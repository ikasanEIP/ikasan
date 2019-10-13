package org.ikasan.dashboard.ui.visualisation.layout;

import org.apache.commons.io.IOUtils;
import org.ikasan.dashboard.ui.visualisation.adapter.service.ModuleVisjsAdapter;
import org.ikasan.dashboard.ui.visualisation.model.flow.Module;
import org.ikasan.spec.metadata.ModuleMetaData;
import org.ikasan.topology.metadata.JsonFlowMetaDataProvider;
import org.ikasan.topology.metadata.JsonModuleMetaDataProvider;
import org.ikasan.vaadin.visjs.network.NetworkDiagram;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.concurrent.Synchroniser;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class IkasanFlowLayoutManagerTest
{
    public static final String MODULE_JSON = "/data/graph/module.json";
    public static final String MODULE_FOUR_JSON = "/data/graph/module-four.json";

    private Mockery mockery = new Mockery()
    {{
        setImposteriser(ClassImposteriser.INSTANCE);
        setThreadingPolicy(new Synchroniser());
    }};

    /**
     * mocked container, service and dao
     */
    NetworkDiagram networkDiagram = mockery.mock(NetworkDiagram.class);

    JsonModuleMetaDataProvider jsonModuleMetaDataProvider
        = new JsonModuleMetaDataProvider(new JsonFlowMetaDataProvider());

    @Before
    public void setup()
    {
        Logger rootLogger = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        rootLogger.setLevel(Level.WARN);
    }

    @Test
    public void test_simple_module() throws IOException
    {
        mockery.checking(new Expectations()
        {
            {
                // set event factory
                oneOf(networkDiagram).drawFlow(100, -100, 1200, 200, "Simple Flow 1");
                oneOf(networkDiagram).setNodes(with(any(List.class)));
                oneOf(networkDiagram).setEdges(with(any(List.class)));
            }
        });

        ModuleMetaData moduleMetaData = this.jsonModuleMetaDataProvider
            .deserialiseModule(loadDataFile(MODULE_JSON));

        ModuleVisjsAdapter moduleVisjsAdapter = new ModuleVisjsAdapter();

        Module module = moduleVisjsAdapter.adapt(moduleMetaData, new ArrayList<>());

        IkasanFlowLayoutManager layoutManager = new IkasanFlowLayoutManager(module.getFlows().get(0),
            networkDiagram, null);

        layoutManager.layout();

        Assertions.assertEquals(1200, layoutManager.xExtent, "X extent equals!");
        Assertions.assertEquals(0, layoutManager.yExtent, "Y extent equals!");
        Assertions.assertEquals(7, layoutManager.edgeList.size(), "edge list size equals!");
        Assertions.assertEquals(8, layoutManager.nodeList.size(), "node list size equals!");
    }

    @Test
    public void test_complex_module() throws IOException
    {
        mockery.checking(new Expectations()
        {
            {
                // set event factory
                oneOf(networkDiagram).drawFlow(100, -100, 1400, 1100, "Inbound Routing Flow");
                oneOf(networkDiagram).setNodes(with(any(List.class)));
                oneOf(networkDiagram).setEdges(with(any(List.class)));
                oneOf(networkDiagram).drawFlow(100, -100, 2400, 2900, "E1 Flow");
                oneOf(networkDiagram).setNodes(with(any(List.class)));
                oneOf(networkDiagram).setEdges(with(any(List.class)));
                oneOf(networkDiagram).drawFlow(100, -100, 1000, 200, "E1 Stamping Flow");
                oneOf(networkDiagram).setNodes(with(any(List.class)));
                oneOf(networkDiagram).setEdges(with(any(List.class)));
                oneOf(networkDiagram).drawFlow(100, -100, 1200, 200, "E2 Create Flow");
                oneOf(networkDiagram).setNodes(with(any(List.class)));
                oneOf(networkDiagram).setEdges(with(any(List.class)));
                oneOf(networkDiagram).drawFlow(100, -100, 1600, 500, "E2 Amend Flow");
                oneOf(networkDiagram).setNodes(with(any(List.class)));
                oneOf(networkDiagram).setEdges(with(any(List.class)));
                oneOf(networkDiagram).drawFlow(100, -100, 1400, 1100, "Inbound Routing Flow");
                oneOf(networkDiagram).setNodes(with(any(List.class)));
                oneOf(networkDiagram).setEdges(with(any(List.class)));
                oneOf(networkDiagram).drawFlow(100, -100, 1400, 350, "E2 Cancel Flow");
                oneOf(networkDiagram).setNodes(with(any(List.class)));
                oneOf(networkDiagram).setEdges(with(any(List.class)));
                oneOf(networkDiagram).drawFlow(100, -100, 400, 200, "E2 Allocation Flow");
                oneOf(networkDiagram).setNodes(with(any(List.class)));
                oneOf(networkDiagram).setEdges(with(any(List.class)));
                oneOf(networkDiagram).drawFlow(100, -100, 1200, 200, "E3 Create Flow");
                oneOf(networkDiagram).setNodes(with(any(List.class)));
                oneOf(networkDiagram).setEdges(with(any(List.class)));
                oneOf(networkDiagram).drawFlow(100, -100, 1600, 500, "E3 Amend Flow");
                oneOf(networkDiagram).setNodes(with(any(List.class)));
                oneOf(networkDiagram).setEdges(with(any(List.class)));
                oneOf(networkDiagram).drawFlow(100, -100, 1400, 350, "E3 Cancel Flow");
                oneOf(networkDiagram).setNodes(with(any(List.class)));
                oneOf(networkDiagram).setEdges(with(any(List.class)));
                oneOf(networkDiagram).drawFlow(100, -100, 600, 350, "E3 Allocation Flow");
                oneOf(networkDiagram).setNodes(with(any(List.class)));
                oneOf(networkDiagram).setEdges(with(any(List.class)));
                oneOf(networkDiagram).drawFlow(100, -100, 2600, 2000, "Ack Flow");
                oneOf(networkDiagram).setNodes(with(any(List.class)));
                oneOf(networkDiagram).setEdges(with(any(List.class)));
            }
        });

        ModuleMetaData moduleMetaData = this.jsonModuleMetaDataProvider
            .deserialiseModule(loadDataFile(MODULE_FOUR_JSON));

        ModuleVisjsAdapter moduleVisjsAdapter = new ModuleVisjsAdapter();

        Module module = moduleVisjsAdapter.adapt(moduleMetaData, new ArrayList<>());

        IkasanFlowLayoutManager layoutManager = new IkasanFlowLayoutManager(module.getFlows().get(0),
            networkDiagram, null);

        layoutManager.layout();

        layoutManager = new IkasanFlowLayoutManager(module.getFlows().get(1),
            networkDiagram, null);

        layoutManager.layout();

        layoutManager = new IkasanFlowLayoutManager(module.getFlows().get(2),
            networkDiagram, null);

        layoutManager.layout();

        layoutManager = new IkasanFlowLayoutManager(module.getFlows().get(3),
            networkDiagram, null);

        layoutManager.layout();

        layoutManager = new IkasanFlowLayoutManager(module.getFlows().get(4),
            networkDiagram, null);

        layoutManager.layout();

        layoutManager = new IkasanFlowLayoutManager(module.getFlows().get(5),
            networkDiagram, null);

        layoutManager.layout();

        layoutManager = new IkasanFlowLayoutManager(module.getFlows().get(6),
            networkDiagram, null);

        layoutManager.layout();

        layoutManager = new IkasanFlowLayoutManager(module.getFlows().get(7),
            networkDiagram, null);

        layoutManager.layout();

        layoutManager = new IkasanFlowLayoutManager(module.getFlows().get(8),
            networkDiagram, null);

        layoutManager.layout();

        layoutManager = new IkasanFlowLayoutManager(module.getFlows().get(9),
            networkDiagram, null);

        layoutManager.layout();

        layoutManager = new IkasanFlowLayoutManager(module.getFlows().get(10),
            networkDiagram, null);

        layoutManager.layout();

        layoutManager = new IkasanFlowLayoutManager(module.getFlows().get(11),
            networkDiagram, null);

        layoutManager.layout();
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
