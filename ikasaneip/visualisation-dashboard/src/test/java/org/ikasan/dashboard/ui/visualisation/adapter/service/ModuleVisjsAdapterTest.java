package org.ikasan.dashboard.ui.visualisation.adapter.service;

import org.apache.commons.io.IOUtils;
import org.ikasan.dashboard.ui.visualisation.model.flow.Module;
import org.ikasan.spec.metadata.ModuleMetaData;
import org.ikasan.topology.metadata.JsonFlowMetaDataProvider;
import org.ikasan.topology.metadata.JsonModuleMetaDataProvider;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;

public class ModuleVisjsAdapterTest
{
    public static final String MODULE_RESULT_JSON = "/data/graph/module.json";

    JsonModuleMetaDataProvider jsonModuleMetaDataProvider
        = new JsonModuleMetaDataProvider(new JsonFlowMetaDataProvider());


    @Test
    public void test() throws IOException
    {
        ModuleMetaData moduleMetaData = this.jsonModuleMetaDataProvider
            .deserialiseModule(loadDataFile(MODULE_RESULT_JSON));

        ModuleVisjsAdapter moduleVisjsAdapter = new ModuleVisjsAdapter();

        Module module = moduleVisjsAdapter.adapt(moduleMetaData);

        System.out.println(module);
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
