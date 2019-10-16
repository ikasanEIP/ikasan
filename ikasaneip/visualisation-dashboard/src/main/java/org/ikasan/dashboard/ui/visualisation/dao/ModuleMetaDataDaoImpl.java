package org.ikasan.dashboard.ui.visualisation.dao;

import org.apache.commons.io.IOUtils;
import org.ikasan.spec.metadata.ModuleMetaData;
import org.ikasan.topology.metadata.JsonFlowMetaDataProvider;
import org.ikasan.topology.metadata.JsonModuleMetaDataProvider;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ModuleMetaDataDaoImpl implements ModuleMetaDataDao
{
    private static final String MODULE_ONE = "/data/graph/module-one.json";
    private static final String MODULE_TWO = "/data/graph/module-two.json";
    private static final String MODULE_THREE = "/data/graph/module-three.json";
    private static final String MODULE_FOUR = "/data/graph/module-four.json";
    private static final String MODULE_FIVE = "/data/graph/module-five.json";
    private static final String MODULE_SIX = "/data/graph/module-six.json";

    private Map<String, String> moduleMetadata;
    private JsonModuleMetaDataProvider provider;

    public ModuleMetaDataDaoImpl()
    {
        this.moduleMetadata = new HashMap<>();
        provider = new JsonModuleMetaDataProvider(new JsonFlowMetaDataProvider());

        init();
    }

    private void init()
    {
        try
        {
            this.moduleMetadata.put("module-one", IOUtils.toString(loadDataFileStream(MODULE_ONE), "UTF-8"));
            this.moduleMetadata.put("module-two", IOUtils.toString(loadDataFileStream(MODULE_TWO), "UTF-8"));
            this.moduleMetadata.put("module-three", IOUtils.toString(loadDataFileStream(MODULE_THREE), "UTF-8"));
            this.moduleMetadata.put("module-four", IOUtils.toString(loadDataFileStream(MODULE_FOUR), "UTF-8"));
            this.moduleMetadata.put("module-five", IOUtils.toString(loadDataFileStream(MODULE_FIVE), "UTF-8"));
            this.moduleMetadata.put("module-six", IOUtils.toString(loadDataFileStream(MODULE_SIX), "UTF-8"));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }


    @Override
    public List<String> getAllModuleName()
    {
        return new ArrayList<>(this.moduleMetadata.keySet());
    }

    @Override
    public List<ModuleMetaData> getAllModule()
    {
        return this.moduleMetadata.values().stream()
            .map(moduleMetadata -> provider.deserialiseModule(moduleMetadata))
            .collect(Collectors.toList());
    }

    @Override
    public String getModuleMetaData(String moduleName)
    {
        return this.moduleMetadata.get(moduleName);
    }

    protected InputStream loadDataFileStream(String fileName) throws IOException
    {
        return getClass().getResourceAsStream(fileName);
    }
}
