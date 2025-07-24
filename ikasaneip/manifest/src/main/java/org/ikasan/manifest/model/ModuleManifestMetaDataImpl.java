package org.ikasan.manifest.model;

import org.ikasan.spec.metadata.*;

import java.util.List;

public class ModuleManifestMetaDataImpl implements ModuleManifestMetaData {
    private ModuleMetaData moduleMetaData;
    private List<ConfigurationMetaData> configurationMetaData;
    private DependencyManagementMetaData dependencyManagement;
    private List<ParameterizedType> parameterizedTypes;
    private List<ConstructorMetaData> constructorMetaData;
    private List<BeanDefinitionMetaData> beanDefinitionMetaData;

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
}