package org.ikasan.ootb.data.sharing.module.component.splitter;

import org.ikasan.ootb.data.sharing.module.model.ModuleAndConfigMetaData;
import org.ikasan.ootb.data.sharing.module.component.splitter.configuration.ConfigurationMetaDataSplitterConfiguration;
import org.ikasan.spec.component.splitting.Splitter;
import org.ikasan.spec.component.splitting.SplitterException;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.ikasan.spec.metadata.model.ConfigurationMetaData;

import java.util.List;
import java.util.ArrayList;

public class ConfigurationMetaDataSplitter implements Splitter<ModuleAndConfigMetaData, List<ConfigurationMetaData>>,
    ConfiguredResource<ConfigurationMetaDataSplitterConfiguration> {
    private ConfigurationMetaDataSplitterConfiguration configuration = new ConfigurationMetaDataSplitterConfiguration();
    private String configurationId;

    @Override
    public List<List<ConfigurationMetaData>> split(ModuleAndConfigMetaData moduleAndConfigMetaData) {
        if (configuration == null || configuration.getBatchSize() <= 0) {
            throw new SplitterException("Configuration metadata splitter requires a positive batchSize");
        }
        if (moduleAndConfigMetaData == null) {
            throw new SplitterException("Module and configuration metadata must not be null");
        }
        int batchSize = configuration.getBatchSize();
        List<ConfigurationMetaData> configurations = moduleAndConfigMetaData.getConfigurations();
        List<List<ConfigurationMetaData>> batches = new ArrayList<>();
        if (configurations == null) {
            return batches;
        }
        for (int start = 0; start < configurations.size();) {
            int end = start + Math.min(batchSize, configurations.size() - start);
            batches.add(new ArrayList<>(configurations.subList(start, end)));
            start = end;
        }
        return batches;
    }

    @Override
    public ConfigurationMetaDataSplitterConfiguration getConfiguration() {
        return configuration;
    }

    @Override
    public void setConfiguration(ConfigurationMetaDataSplitterConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    public String getConfiguredResourceId() {
        return configurationId;
    }

    @Override
    public void setConfiguredResourceId(String id) {
        this.configurationId = id;
    }
}
