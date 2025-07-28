package org.ikasan.module.builder.service;

import org.ikasan.module.builder.model.component.Component;
import org.ikasan.module.builder.model.component.ComponentConfigurationMetaData;
import org.ikasan.module.builder.model.component.ComponentTypeParameter;
import org.ikasan.spec.metadata.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ModuleManifestMetaDataComponentModelAdapter {


    /**
     * Adapt ModuleManifestMetaData to a list of Component objects based on the given module base package.
     *
     * @param moduleManifestMetaData The ModuleManifestMetaData to adapt.
     * @param moduleBasePackage The base package of the module to filter components by.
     * @return A list of Component objects filtered by the module base package.
     */
    public List<Component> adapt(ModuleManifestMetaData moduleManifestMetaData, String moduleBasePackage)
    {
        Map<String, ComponentConfigurationMetaData> configurationMetaDataMap
            = this.getConfigurationMetaDataMap(moduleManifestMetaData.getConfigurationMetaData());
        Map<String, ParameterizedType> parameterizedTypeMap
            = this.getParameterisedTypesMap(moduleManifestMetaData.getParameterizedTypes());
        Map<String, ConstructorMetaData> constructorMetaDataMap
            = this.getConstructorMetaDataMap(moduleManifestMetaData.getConstructorMetaData());

        List<Component> results = new ArrayList<>();
        for (FlowMetaData flowMetaData : moduleManifestMetaData.getModuleMetaData().getFlows()) {
            for (FlowElementMetaData flowElementMetaData : flowMetaData.getFlowElements()) {
                if(flowElementMetaData.getImplementingClass().startsWith(moduleBasePackage)) {
                    Component component = new Component();
                    component.setName(flowElementMetaData.getComponentName());
                    component.setImplementingClass(flowElementMetaData.getImplementingClass());
                    component.setClassName(flowElementMetaData.getImplementingClass().substring(
                        flowElementMetaData.getImplementingClass().lastIndexOf(".") + 1
                            ,  flowElementMetaData.getImplementingClass().length()));
                    component.setClassPackage(flowElementMetaData.getImplementingClass().substring(0,
                        flowElementMetaData.getImplementingClass().lastIndexOf(".")));
                    component.setComponentTypeClassName(flowElementMetaData.getComponentType().substring(
                        flowElementMetaData.getComponentType().lastIndexOf(".") + 1
                        ,  flowElementMetaData.getComponentType().length()));
                    component.setComponentTypePackage(flowElementMetaData.getComponentType().substring(0,
                        flowElementMetaData.getComponentType().lastIndexOf(".")));
                    component.setComponentType(flowElementMetaData.getComponentType());
                    component.setConfigured(flowElementMetaData.isConfigurable());
                    component.setParameterizedType(parameterizedTypeMap.get(flowElementMetaData.getImplementingClass()));
                    component.setConfigurationMetaData(configurationMetaDataMap.get(flowElementMetaData.getConfigurationId()));
                    if (constructorMetaDataMap.containsKey(flowElementMetaData.getComponentName())) {
                        component.setConstructorMetaData
                            (List.of(constructorMetaDataMap.get(flowElementMetaData.getComponentName())));
                    }
                    results.add(component);
                }
            }
        }
        return results;
    }

    /**
     * Retrieves a map of ComponentConfigurationMetaData objects based on the provided list of ConfigurationMetaData objects.
     *
     * @param configurationMetaDataList The list of ConfigurationMetaData objects to create ComponentConfigurationMetaData objects from.
     * @return A map of ComponentConfigurationMetaData objects where the key is the configuration id.
     */
    private Map<String, ComponentConfigurationMetaData> getConfigurationMetaDataMap(List<ConfigurationMetaData> configurationMetaDataList) {
        return configurationMetaDataList.stream()
            .map(configurationMetaData -> {
                ComponentConfigurationMetaData componentConfigurationMetaData = new ComponentConfigurationMetaData(configurationMetaData);
                componentConfigurationMetaData.setConfigurationClassName(configurationMetaData.getImplementingClass().substring(
                    configurationMetaData.getImplementingClass().lastIndexOf(".") + 1
                    ,  configurationMetaData.getImplementingClass().length()));
                componentConfigurationMetaData.setConfigurationPackageName(configurationMetaData.getImplementingClass().substring(0,
                    configurationMetaData.getImplementingClass().lastIndexOf(".")));

                return componentConfigurationMetaData;
            })
            .collect(Collectors.toMap(ComponentConfigurationMetaData::getConfigurationId, Function.identity()));
    }

    /**
     * Retrieves a map of implementating class names to their corresponding ParameterizedType objects.
     *
     * @param parameterizedTypes A List of ParameterizedType objects from which to extract information.
     * @return A Map<String, ParameterizedType> where the key is the implementing class name and the value
     *         is the associated ParameterizedType object.
     */
    private Map<String, ParameterizedType> getParameterisedTypesMap(List<ParameterizedType> parameterizedTypes) {
        return parameterizedTypes.stream()
            .map(parameterizedType -> {
                List<TypeParameter> typeParameters = new ArrayList<>();
                parameterizedType.getTypeParameters().forEach(typeParameter -> {
                    ComponentTypeParameter componentTypeParameter = new ComponentTypeParameter();
                    componentTypeParameter.setType(typeParameter.getType());
                    componentTypeParameter.setName(typeParameter.getName());
                    componentTypeParameter.setParameterClass(typeParameter.getType()
                        .substring(typeParameter.getType().lastIndexOf(".") + 1, typeParameter.getType().length()));
                    typeParameters.add(componentTypeParameter);
                });

                parameterizedType.setTypeParameters(typeParameters);
                return parameterizedType;
            })
            .collect(Collectors.toMap(ParameterizedType::getImplementingClassName, Function.identity(), (first, second) -> first));
    }

    /**
     * Retrieves a mapping of component names to ConstructorMetaData objects.
     *
     * @param constructorMetaDataList a list of ConstructorMetaData objects
     * @return a map where the key is the component name and the value is the corresponding ConstructorMetaData object
     */
    private Map<String, ConstructorMetaData> getConstructorMetaDataMap(List<ConstructorMetaData> constructorMetaDataList) {
        return constructorMetaDataList.stream()
            .collect(Collectors.toMap(ConstructorMetaData::getComponentName, Function.identity()));
    }
}
