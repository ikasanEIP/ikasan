package org.ikasan.spec.module.client;

import org.ikasan.spec.metadata.FlowMetaData;
import org.ikasan.spec.metadata.ModuleMetaData;

import java.util.Optional;

public interface MetaDataService {

    /**
     * Get a flow metadata from a module.
     *
     * @param contextUrl
     * @param moduleName
     * @param flowName
     * @return
     */
    public Optional<FlowMetaData> getFlowMetadata(String contextUrl, String moduleName, String flowName);

    /**
     * Get a module metadata from a module.
     *
     * @param contextUrl
     * @param moduleName
     * @return
     */
    public Optional<ModuleMetaData> getModuleMetadata(String contextUrl, String moduleName);

}
