package org.ikasan.ootb.data.sharing.module.component.splitter.configuration;

/** Controls the number of configurations sent in each downstream REST request. */
public class ConfigurationMetaDataSplitterConfiguration {
    private int batchSize = 100;

    public int getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }
}
