package org.ikasan.module.builder.model.manifest;

import org.ikasan.spec.metadata.ModuleManifestMetaData;

public class EnrichedModuleManifestMetaData {
    private String migrationProjectMavenGroupId;
    private ModuleManifestMetaData moduleManifestMetaData;

    public EnrichedModuleManifestMetaData(String migrationProjectMavenGroupId, ModuleManifestMetaData moduleManifestMetaData) {
        this.migrationProjectMavenGroupId = migrationProjectMavenGroupId;
        this.moduleManifestMetaData = moduleManifestMetaData;
    }

    public String getMigrationProjectMavenGroupId() {
        return migrationProjectMavenGroupId;
    }

    public ModuleManifestMetaData getModuleManifestMetaData() {
        return moduleManifestMetaData;
    }
}
