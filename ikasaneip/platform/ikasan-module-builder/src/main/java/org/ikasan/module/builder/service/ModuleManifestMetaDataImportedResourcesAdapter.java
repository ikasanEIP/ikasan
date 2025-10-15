package org.ikasan.module.builder.service;

import org.ikasan.spec.metadata.ImportedResourceMetaData;
import org.ikasan.spec.metadata.ModuleManifestMetaData;

import java.util.ArrayList;
import java.util.List;

public class ModuleManifestMetaDataImportedResourcesAdapter {

    /**
     * Filters ImportedResourceMetaData based on resource type and base module package.
     *
     * @param moduleManifestMetaData The module manifest metadata containing the imported resources
     * @param baseModulePackage The base package of the module
     * @param resourceType The type of the resource to filter by
     * @return List of ImportedResourceMetaData that match the resource type and base module package criteria
     */
    public List<ImportedResourceMetaData> adapt(ModuleManifestMetaData moduleManifestMetaData, String baseModulePackage, String resourceType) {
        List<ImportedResourceMetaData> importedResources = moduleManifestMetaData.getImportedResourceMetaData();
        List<ImportedResourceMetaData> filteredResources = new ArrayList<>();

        for (ImportedResourceMetaData importedResource : importedResources) {
            if (importedResource.getResourceType().equals(resourceType) && importedResource.getSource().startsWith(baseModulePackage)) {
                filteredResources.add(importedResource);
            }
        }

        return filteredResources;
    }
}
