package org.ikasan.module.builder.model.manifest;

import org.ikasan.spec.metadata.ModuleManifestMetaData;

public class EnrichedModuleManifestMetaData {
    private String migrationProjectMavenGroupId;
    private String migrationProjectMavenArtefactId;
    private ModuleManifestMetaData moduleManifestMetaData;

    /**
     * Constructs an EnrichedModuleManifestMetaData object with the provided migration project Maven group ID
     * and module manifest metadata.
     *
     * @param moduleManifestMetaData The metadata about the module manifest.
     */
    public EnrichedModuleManifestMetaData(ModuleManifestMetaData moduleManifestMetaData) {
        this.moduleManifestMetaData = moduleManifestMetaData;
        if(this.moduleManifestMetaData.getModulePomMetaData() != null) {
            this.migrationProjectMavenGroupId = this.moduleManifestMetaData.getModulePomMetaData().getPomGroupId();
            this.migrationProjectMavenArtefactId = this.moduleManifestMetaData.getModulePomMetaData().getPomArtefactId();
        }
    }

    /**
     * Retrieves the Maven group ID of the migration project associated with this metadata.
     *
     * @return The Maven group ID of the migration project.
     */
    public String getMigrationProjectMavenGroupId() {
        return migrationProjectMavenGroupId;
    }

    /**
     * Retrieves the module manifest metadata associated with this EnrichedModuleManifestMetaData object.
     * This metadata represents the top-level manifest for an Ikasan module.
     *
     * @return ModuleManifestMetaData containing detailed information about the module manifest, including module metadata,
     * configuration metadata, dependency management, parameterized types, constructors, bean definitions,
     * imported resources metadata, and module POM metadata.
     */
    public ModuleManifestMetaData getModuleManifestMetaData() {
        return moduleManifestMetaData;
    }

    /**
     * Retrieves the Maven artifact ID of the migration project associated with this metadata.
     *
     * @return The Maven artifact ID of the migration project.
     */
    public String getMigrationProjectMavenArtefactId() {
        return migrationProjectMavenArtefactId;
    }
}
