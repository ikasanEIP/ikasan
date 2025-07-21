package org.ikasan.module.builder.model;

import java.util.HashMap;
import java.util.Map;

public class ModuleModel {
    String name;
    String moduleBasePackage;
    Map<String, FlowModel> flowModelMap;

    public ModuleModel(String name, String moduleBasePackage) {
        this.name = name;
        this.moduleBasePackage = moduleBasePackage;
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
}
