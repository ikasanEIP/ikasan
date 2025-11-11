package org.ikasan.spec.metadata;

public interface ModulePomMetaData {

    /**
     * Set the pom group id.
     *
     * @param groupId
     */
    void setPomGroupId(String groupId);

    /**
     * Get the pom artefact id.
     *
     * @return
     */
    String getPomArtefactId();

    /**
     * Set the pom artefact id.
     *
     * @param artefactId
     */
    void setPomArtefactId(String artefactId);

    /**
     * Get the pom group id.
     *
     * @return
     */
    String getPomGroupId();

    /**
     * Set the module version
     *
     * @param version
     */
    void setVersion(String version);

    /**
     * Get the module version.
     *
     * @return
     */
    String getVersion();
}
