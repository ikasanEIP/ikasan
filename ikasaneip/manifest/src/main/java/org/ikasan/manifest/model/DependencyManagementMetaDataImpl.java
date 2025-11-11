package org.ikasan.manifest.model;

import org.ikasan.spec.metadata.DependencyManagementMetaData;
import org.ikasan.spec.metadata.DependencyMetaData;
import org.ikasan.spec.metadata.RepositoryMetaData;

import java.util.List;
import java.util.Objects;

public class DependencyManagementMetaDataImpl implements DependencyManagementMetaData {
    private List<RepositoryMetaData> repositories;
    private List<DependencyMetaData> dependencies;

    @Override
    public List<RepositoryMetaData> getRepositories() {
        return repositories;
    }

    @Override
    public void setRepositories(List<RepositoryMetaData> repositories) {
        this.repositories = repositories;
    }

    @Override
    public List<DependencyMetaData> getDependencies() {
        return dependencies;
    }

    @Override
    public void setDependencies(List<DependencyMetaData> dependencies) {
        this.dependencies = dependencies;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DependencyManagementMetaDataImpl that = (DependencyManagementMetaDataImpl) o;
        return Objects.equals(repositories, that.repositories)
            && Objects.equals(dependencies, that.dependencies);
    }

    @Override
    public int hashCode() {
        return Objects.hash(repositories, dependencies);
    }
}