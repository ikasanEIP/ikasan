package org.ikasan.spec.metadata;

/**
 * Represents a Maven dependency.
 */
public interface DependencyMetaData {

    /**
     * Get the dependency group.
     * @return The dependency group.
     */
    String getGroup();

    /**
     * Set the dependency group.
     * @param group The dependency group.
     */
    void setGroup(String group);

    /**
     * Get the dependency artefact.
     * @return The dependency artefact.
     */
    String getArtefact();

    /**
     * Set the dependency artefact.
     * @param artefact The dependency artefact.
     */
    void setArtefact(String artefact);

    /**
     * Get the dependency version.
     * @return The dependency version.
     */
    String getVersion();

    /**
     * Set the dependency version.
     * @param version The dependency version.
     */
    void setVersion(String version);
}
