package org.ikasan.dashboard.ui.visualisation.view;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.data.provider.Query;
import org.apache.commons.io.IOUtils;
import org.ikasan.dashboard.ui.UITest;
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
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static com.github.mvysny.kaributesting.v10.LocatorJ._get;

public class BusinessStreamViewTest extends UITest
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

    @MockBean
    private ModuleMetaDataService moduleMetadataService;

    @MockBean
    private ConfigurationMetaDataService configurationMetadataService;

    @MockBean
    private ConfigurationService configurationService;

    @Override
    public void setup_expectations() {

    }

    @Test
    public void test_create_graph() throws IOException
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
