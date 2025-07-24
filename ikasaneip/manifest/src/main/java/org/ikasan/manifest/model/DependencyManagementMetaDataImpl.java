package org.ikasan.manifest.model;

import org.ikasan.spec.metadata.DependencyManagementMetaData;
import org.ikasan.spec.metadata.DependencyMetaData;
import org.ikasan.spec.metadata.RepositoryMetaData;

import java.util.List;

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
}