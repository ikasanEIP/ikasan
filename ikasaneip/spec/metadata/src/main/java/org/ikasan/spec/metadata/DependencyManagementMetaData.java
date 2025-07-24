package org.ikasan.spec.metadata;

import java.util.List;

/**
 * Represents the dependency management information for an Ikasan module.
 */
public interface DependencyManagementMetaData {

    /**
     * Get the list of Maven repositories.
     * @return A list of repository metadata.
     */
    List<RepositoryMetaData> getRepositories();

    /**
     * Set the list of Maven repositories.
     * @param repositories A list of repository metadata.
     */
    void setRepositories(List<RepositoryMetaData> repositories);

    /**
     * Get the list of dependencies.
     * @return A list of dependency metadata.
     */
    List<DependencyMetaData> getDependencies();

    /**
     * Set the list of dependencies.
     * @param dependencies A list of dependency metadata.
     */
    void setDependencies(List<DependencyMetaData> dependencies);
}
