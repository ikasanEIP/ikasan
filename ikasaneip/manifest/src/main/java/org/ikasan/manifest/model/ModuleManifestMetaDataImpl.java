package org.ikasan.manifest.model;

import org.ikasan.spec.metadata.*;

import java.util.List;
import java.util.Objects;

public class ModuleManifestMetaDataImpl implements ModuleManifestMetaData {
    private ModuleMetaData moduleMetaData;
    private List<ConfigurationMetaData> configurationMetaData;
    private DependencyManagementMetaData dependencyManagement;
    private List<ParameterizedType> parameterizedTypes;
    private List<ConstructorMetaData> constructorMetaData;
    private List<BeanDefinitionMetaData> beanDefinitionMetaData;
    private List<ImportedResourceMetaData> importedResourceMetaData;
    private ModulePomMetaData modulePomMetaData;

    @Override
    public ModuleMetaData getModuleMetaData() {
        return moduleMetaData;
    }

    @Override
    public void setModuleMetaData(ModuleMetaData moduleMetaData) {
        this.moduleMetaData = moduleMetaData;
    }

    @Override
    public List<ConfigurationMetaData> getConfigurationMetaData() {
        return configurationMetaData;
    }

    @Override
    public void setConfigurationMetaData(List<ConfigurationMetaData> configurationMetaData) {
        this.configurationMetaData = configurationMetaData;
    }

    @Override
    public DependencyManagementMetaData getDependencyManagement() {
        return dependencyManagement;
    }

    @Override
    public void setDependencyManagement(DependencyManagementMetaData dependencyManagement) {
        this.dependencyManagement = dependencyManagement;
    }

    @Override
    public List<ParameterizedType> getParameterizedTypes() {
        return parameterizedTypes;
    }

    @Override
    public void setParameterizedTypes(List<ParameterizedType> parameterizedTypes) {
        this.parameterizedTypes = parameterizedTypes;
    }

    @Override
    public List<ConstructorMetaData> getConstructorMetaData() {
        return constructorMetaData;
    }

    @Override
    public void setConstructorMetaData(List<ConstructorMetaData> constructorMetaData) {
        this.constructorMetaData = constructorMetaData;
    }

    @Override
    public List<BeanDefinitionMetaData> getBeanDefinitionMetaData() {
        return beanDefinitionMetaData;
    }

    @Override
    public void setBeanDefinitionMetaData(List<BeanDefinitionMetaData> beanDefinitionMetaData) {
        this.beanDefinitionMetaData = beanDefinitionMetaData;
    }

    @Override
    public List<ImportedResourceMetaData> getImportedResourceMetaData() {
        return importedResourceMetaData;
    }

    @Override
    public void setImportedResourceMetaData(List<ImportedResourceMetaData> importedResourceMetaData) {
        this.importedResourceMetaData = importedResourceMetaData;
    }

    @Override
    public ModulePomMetaData getModulePomMetaData() {
        return modulePomMetaData;
    }

    @Override
    public void setModulePomMetaData(ModulePomMetaData modulePomMetaData) {
        this.modulePomMetaData = modulePomMetaData;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ModuleManifestMetaDataImpl that = (ModuleManifestMetaDataImpl) o;
        return Objects.equals(moduleMetaData, that.moduleMetaData)
            && Objects.equals(configurationMetaData, that.configurationMetaData)
            && Objects.equals(dependencyManagement, that.dependencyManagement)
            && Objects.equals(parameterizedTypes, that.parameterizedTypes)
            && Objects.equals(constructorMetaData, that.constructorMetaData)
            && Objects.equals(beanDefinitionMetaData, that.beanDefinitionMetaData)
            && Objects.equals(importedResourceMetaData, that.importedResourceMetaData);
    }

    @Override
    public int hashCode() {
        return Objects.hash(moduleMetaData, configurationMetaData, dependencyManagement
            , parameterizedTypes, constructorMetaData, beanDefinitionMetaData, importedResourceMetaData);
    }
}