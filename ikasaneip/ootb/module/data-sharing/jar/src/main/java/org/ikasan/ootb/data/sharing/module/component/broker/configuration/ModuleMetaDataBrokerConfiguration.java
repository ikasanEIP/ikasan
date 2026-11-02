package org.ikasan.ootb.data.sharing.module.component.broker.configuration;

import java.util.List;

public class ModuleMetaDataBrokerConfiguration {
    private List<String> moduleNames;   // The names of the modules we want to retrieve metadata for
    private int batchSize = 100;        // The number of IDs we attempt to retrieve from the REST service in a single call

    public List<String> getModuleNames() { return moduleNames; }
    public void setModuleNames(List<String> moduleNames) { this.moduleNames = moduleNames; }
    public int getBatchSize() { return batchSize; }
    public void setBatchSize(int batchSize) { this.batchSize = batchSize; }
}
