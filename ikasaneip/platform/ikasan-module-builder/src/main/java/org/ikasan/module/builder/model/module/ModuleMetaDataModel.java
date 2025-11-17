package org.ikasan.module.builder.model.module;

import org.ikasan.spec.metadata.FlowElementMetaData;
import org.ikasan.spec.metadata.ImportedResourceMetaData;
import org.ikasan.spec.metadata.ModuleMetaData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ModuleMetaDataModel {
    private String moduleBasePackage;
    private ModuleMetaData moduleMetaData;

    public ModuleMetaDataModel(String moduleBasePackage
        , ModuleMetaData moduleMetaData) {
        this.moduleBasePackage = moduleBasePackage;
        this.moduleMetaData = moduleMetaData;
    }

    public String getModuleBasePackage() {
        return moduleBasePackage;
    }

    public ModuleMetaData getModuleMetaData() {
        return moduleMetaData;
    }

    public List<FlowElementMetaData> getFlowElementMetaData() {
        return moduleMetaData.getFlows().stream()
            .flatMap(flowMetaData -> flowMetaData.getFlowElements().stream())
            .collect(Collectors.toMap(
                FlowElementMetaData::getComponentName, // Key: the field to be distinct by
                Function.identity(), // Value: the Person object itself
                (existing, replacement) -> existing // Merge function: keep the existing one if names are the same
            ))
            .values()
            .stream()
            .collect(Collectors.toList());
    }
}
