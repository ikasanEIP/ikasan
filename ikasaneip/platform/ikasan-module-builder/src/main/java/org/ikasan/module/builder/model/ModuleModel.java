package org.ikasan.module.builder.model;

import java.util.HashMap;
import java.util.Map;

public class ModuleModel {
    String name;
    Map<String, FlowModel> flowModelMap;

    public ModuleModel(String name) {
        this.name = name;
        this.flowModelMap = new HashMap<>();
    }

    public void addFlow(FlowModel flowModel) {
        flowModelMap.put(flowModel.getName(), flowModel);
    }

    public String getName() {
        return name;
    }

    public Map<String, FlowModel> getFlowModelMap() {
        return flowModelMap;
    }
}
