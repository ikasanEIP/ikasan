package org.ikasan.module.migration;

import org.ikasan.spec.metadata.ImportedResourceMetaData;
import org.ikasan.spec.metadata.ModuleManifestMetaData;

/**
 * Helper class to migrate imported resources within a ModuleManifestMetaData object.
 */
public class ImportedResourceMigrationHelper {

    /**
     * Migrates imported resources within a ModuleManifestMetaData object by updating specific resources.
     *
     * @param moduleManifestMetaData The module manifest metadata containing imported resources to be migrated.
     */
    public static void migrate(ModuleManifestMetaData moduleManifestMetaData) {
        moduleManifestMetaData.getImportedResourceMetaData().forEach(importedResourceMetaData -> {
            if(importedResourceMetaData.getResource().equals("classpath:filetransfer-service-conf.xml")) {
                importedResourceMetaData.setResource("org.ikasan.connector.basefiletransfer.BaseFileTransferAutoConfiguration");
                importedResourceMetaData.setResourceType(ImportedResourceMetaData.IMPORTED_CONFIGURATION_CLASS);
            }
        });
    }
}
