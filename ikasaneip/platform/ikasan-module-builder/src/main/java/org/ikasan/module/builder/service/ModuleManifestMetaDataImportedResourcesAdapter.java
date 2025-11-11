package org.ikasan.module.builder.service;

import org.ikasan.manifest.model.ImportedResourceMetaDataImpl;
import org.ikasan.spec.metadata.ImportedResourceMetaData;
import org.ikasan.spec.metadata.ModuleManifestMetaData;

import java.util.ArrayList;
import java.util.List;

public class ModuleManifestMetaDataImportedResourcesAdapter {

    private static List<ImportedResourceMetaData> MANDATORY_IMPORTED_RESOURCE_META_DATA;

    static {
        MANDATORY_IMPORTED_RESOURCE_META_DATA = new ArrayList<>();

        ImportedResourceMetaData importedResourceMetaData = new ImportedResourceMetaDataImpl();
        importedResourceMetaData.setResource("classpath:ikasan-transaction-pointcut-jms.xml");
        importedResourceMetaData.setResourceType("IMPORTED_XML_RESOURCE");

        MANDATORY_IMPORTED_RESOURCE_META_DATA.add(importedResourceMetaData);

        importedResourceMetaData = new ImportedResourceMetaDataImpl();
        importedResourceMetaData.setResource("classpath:h2-datasource-conf.xml");
        importedResourceMetaData.setResourceType("IMPORTED_XML_RESOURCE");

        MANDATORY_IMPORTED_RESOURCE_META_DATA.add(importedResourceMetaData);
    }

    /**
     * Filters ImportedResourceMetaData based on resource type and base module package.
     *
     * @param moduleManifestMetaData The module manifest metadata containing the imported resources
     * @param baseModulePackage The base package of the module
     * @param resourceType The type of the resource to filter by
     * @return List of ImportedResourceMetaData that match the resource type and base module package criteria
     */
    public List<ImportedResourceMetaData> adapt(ModuleManifestMetaData moduleManifestMetaData, String baseModulePackage, String resourceType) {
        List<ImportedResourceMetaData> filteredResources = new ArrayList<>();

        if(moduleManifestMetaData.getImportedResourceMetaData() == null) {
            moduleManifestMetaData.setImportedResourceMetaData(new ArrayList<>());
        }

        for (ImportedResourceMetaData importedResource : moduleManifestMetaData.getImportedResourceMetaData()) {
            if (importedResource.getResourceType().equals(resourceType) && importedResource.getSource().startsWith(baseModulePackage)) {
                filteredResources.add(importedResource);
            }
        }

        for (ImportedResourceMetaData importedResourceMetaData: MANDATORY_IMPORTED_RESOURCE_META_DATA) {
            if(!filteredResources.contains(importedResourceMetaData)
                && importedResourceMetaData.getResourceType().equals(resourceType)) {
                importedResourceMetaData.setSource(baseModulePackage);
                filteredResources.add(importedResourceMetaData);
            }
        }


        return filteredResources;
    }
}
