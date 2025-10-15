package org.ikasan.module.builder.model.module;

import org.ikasan.spec.metadata.ImportedResourceMetaData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModuleModel {
    String name;
    String moduleBasePackage;
    Map<String, FlowModel> flowModelMap;
    private List<ImportedResourceMetaData> importedClassConfigurationResources;
    private List<ImportedResourceMetaData> importedXmlResources;

    public ModuleModel(String name, String moduleBasePackage
        , List<ImportedResourceMetaData> importedClassConfigurationResources
        , List<ImportedResourceMetaData> importedXmlResources) {
        this.name = name;
        this.moduleBasePackage = moduleBasePackage;
        this.importedClassConfigurationResources = importedClassConfigurationResources;
        this.importedXmlResources = importedXmlResources;
        this.flowModelMap = new HashMap<>();
    }

    public void addFlow(FlowModel flowModel) {
        flowModelMap.put(flowModel.getName(), flowModel);
    }

    public String getName() {
        return name;
    }

    public String getModuleBasePackage() {
        return moduleBasePackage;
    }

    public Map<String, FlowModel> getFlowModelMap() {
        return flowModelMap;
    }

    public List<ImportedResourceMetaData> getImportedClassConfigurationResources() {
        return importedClassConfigurationResources;
    }

    public List<ImportedResourceMetaData> getImportedXmlResources() {
        return importedXmlResources;
    }
}
