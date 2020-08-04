package org.ikasan.dashboard.ui.visualisation.layout;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
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
import org.slf4j.LoggerFactory;


import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class IkasanModuleLayoutManagerTest
{
    public static final String MODULE_FOUR_JSON = "/data/graph/module-four.json";


    private Mockery mockery = new Mockery()
    {{
        setImposteriser(ClassImposteriser.INSTANCE);
        setThreadingPolicy(new Synchroniser());
    }};

    /**
     * mocked container, listener and dao
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
    public void test_complex_module() throws IOException
    {
        mockery.checking(new Expectations()
        {
            {
                // set event factory
                oneOf(networkDiagram).drawFlow(-100, -100, 1400, 1100, "Inbound Routing Flow");
                oneOf(networkDiagram).setNodes(with(any(List.class)));
                oneOf(networkDiagram).setEdges(with(any(List.class)));
                oneOf(networkDiagram).drawFlow(-100, 1100, 2400, 2900, "E1 Flow");
                oneOf(networkDiagram).setNodes(with(any(List.class)));
                oneOf(networkDiagram).setEdges(with(any(List.class)));
                oneOf(networkDiagram).drawFlow(-100, 4100, 1000, 200, "E1 Stamping Flow");
                oneOf(networkDiagram).setNodes(with(any(List.class)));
                oneOf(networkDiagram).setEdges(with(any(List.class)));
                oneOf(networkDiagram).drawFlow(-100, 4400, 1200, 200, "E2 Create Flow");
                oneOf(networkDiagram).setNodes(with(any(List.class)));
                oneOf(networkDiagram).setEdges(with(any(List.class)));
                oneOf(networkDiagram).drawFlow(-100, 4700, 1600, 500, "E2 Amend Flow");
                oneOf(networkDiagram).setNodes(with(any(List.class)));
                oneOf(networkDiagram).setEdges(with(any(List.class)));
                oneOf(networkDiagram).drawFlow(-100, 5300, 1400, 350, "E2 Cancel Flow");
                oneOf(networkDiagram).setNodes(with(any(List.class)));
                oneOf(networkDiagram).setEdges(with(any(List.class)));
                oneOf(networkDiagram).drawFlow(-100, 5750, 400, 200, "E2 Allocation Flow");
                oneOf(networkDiagram).setNodes(with(any(List.class)));
                oneOf(networkDiagram).setEdges(with(any(List.class)));
                oneOf(networkDiagram).drawFlow(-100, 6050, 1200, 200, "E3 Create Flow");
                oneOf(networkDiagram).setNodes(with(any(List.class)));
                oneOf(networkDiagram).setEdges(with(any(List.class)));
                oneOf(networkDiagram).drawFlow(-100, 6350, 1600, 500, "E3 Amend Flow");
                oneOf(networkDiagram).setNodes(with(any(List.class)));
                oneOf(networkDiagram).setEdges(with(any(List.class)));
                oneOf(networkDiagram).drawFlow(-100, 6950, 1400, 350, "E3 Cancel Flow");
                oneOf(networkDiagram).setNodes(with(any(List.class)));
                oneOf(networkDiagram).setEdges(with(any(List.class)));
                oneOf(networkDiagram).drawFlow(-100, 7400, 600, 350, "E3 Allocation Flow");
                oneOf(networkDiagram).setNodes(with(any(List.class)));
                oneOf(networkDiagram).setEdges(with(any(List.class)));
                oneOf(networkDiagram).drawFlow(-100, 7850, 2600, 2000, "Ack Flow");
                oneOf(networkDiagram).setNodes(with(any(List.class)));
                oneOf(networkDiagram).setEdges(with(any(List.class)));
                oneOf(networkDiagram).drawFlow(-100, 9950, 2400, 500, "Some Trade Flow");
                oneOf(networkDiagram).setNodes(with(any(List.class)));
                oneOf(networkDiagram).setEdges(with(any(List.class)));

                oneOf(networkDiagram).drawModule(-200, -200, 3000, 10750, "electronicTrade");
            }
        });

        ModuleMetaData moduleMetaData = this.jsonModuleMetaDataProvider
            .deserialiseModule(loadDataFile(MODULE_FOUR_JSON));

        ModuleVisjsAdapter moduleVisjsAdapter = new ModuleVisjsAdapter();

        Module module = moduleVisjsAdapter.adapt(moduleMetaData, new ArrayList<>());

        IkasanModuleLayoutManager layoutManager = new IkasanModuleLayoutManager(module,
            networkDiagram, null);

        layoutManager.layout();

    }

    @Test
    public void test_complex_module_with_configured_x_and_y_spacing() throws IOException
    {
        mockery.checking(new Expectations()
        {
            {
                // set event factory
                oneOf(networkDiagram).drawFlow(-100, -100, 500, 500, "Inbound Routing Flow");
                oneOf(networkDiagram).setNodes(with(any(List.class)));
                oneOf(networkDiagram).setEdges(with(any(List.class)));
                oneOf(networkDiagram).drawFlow(-100, 500, 750, 1100, "E1 Flow");
                oneOf(networkDiagram).setNodes(with(any(List.class)));
                oneOf(networkDiagram).setEdges(with(any(List.class)));
                oneOf(networkDiagram).drawFlow(-100, 1700, 400, 200, "E1 Stamping Flow");
                oneOf(networkDiagram).setNodes(with(any(List.class)));
                oneOf(networkDiagram).setEdges(with(any(List.class)));
                oneOf(networkDiagram).drawFlow(-100, 2000, 450, 200, "E2 Create Flow");
                oneOf(networkDiagram).setNodes(with(any(List.class)));
                oneOf(networkDiagram).setEdges(with(any(List.class)));
                oneOf(networkDiagram).drawFlow(-100, 2300, 550, 300, "E2 Amend Flow");
                oneOf(networkDiagram).setNodes(with(any(List.class)));
                oneOf(networkDiagram).setEdges(with(any(List.class)));
                oneOf(networkDiagram).drawFlow(-100, 2700, 500, 250, "E2 Cancel Flow");
                oneOf(networkDiagram).setNodes(with(any(List.class)));
                oneOf(networkDiagram).setEdges(with(any(List.class)));
                oneOf(networkDiagram).drawFlow(-100, 3050, 250, 200, "E2 Allocation Flow");
                oneOf(networkDiagram).setNodes(with(any(List.class)));
                oneOf(networkDiagram).setEdges(with(any(List.class)));
                oneOf(networkDiagram).drawFlow(-100, 3350, 450, 200, "E3 Create Flow");
                oneOf(networkDiagram).setNodes(with(any(List.class)));
                oneOf(networkDiagram).setEdges(with(any(List.class)));
                oneOf(networkDiagram).drawFlow(-100, 3650, 550, 300, "E3 Amend Flow");
                oneOf(networkDiagram).setNodes(with(any(List.class)));
                oneOf(networkDiagram).setEdges(with(any(List.class)));
                oneOf(networkDiagram).drawFlow(-100, 4050, 500, 250, "E3 Cancel Flow");
                oneOf(networkDiagram).setNodes(with(any(List.class)));
                oneOf(networkDiagram).setEdges(with(any(List.class)));
                oneOf(networkDiagram).drawFlow(-100, 4400, 300, 250, "E3 Allocation Flow");
                oneOf(networkDiagram).setNodes(with(any(List.class)));
                oneOf(networkDiagram).setEdges(with(any(List.class)));
                oneOf(networkDiagram).drawFlow(-100, 4750, 800, 800, "Ack Flow");
                oneOf(networkDiagram).setNodes(with(any(List.class)));
                oneOf(networkDiagram).setEdges(with(any(List.class)));
                oneOf(networkDiagram).drawFlow(-100, 5650, 750, 300, "Some Trade Flow");
                oneOf(networkDiagram).setNodes(with(any(List.class)));
                oneOf(networkDiagram).setEdges(with(any(List.class)));

                oneOf(networkDiagram).drawModule(-200, -200, 1200, 6250, "electronicTrade");
            }
        });

        ModuleMetaData moduleMetaData = this.jsonModuleMetaDataProvider
            .deserialiseModule(loadDataFile(MODULE_FOUR_JSON));

        ModuleVisjsAdapter moduleVisjsAdapter = new ModuleVisjsAdapter();

        Module module = moduleVisjsAdapter.adapt(moduleMetaData, new ArrayList<>());

        IkasanModuleLayoutManager layoutManager = new IkasanModuleLayoutManager(module,
            networkDiagram, null);

        layoutManager.setxSpacing(50);
        layoutManager.setySpacing(50);

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
