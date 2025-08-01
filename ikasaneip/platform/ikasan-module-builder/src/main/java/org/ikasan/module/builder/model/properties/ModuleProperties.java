package org.ikasan.module.builder.model.properties;

import org.ikasan.module.builder.model.configuration.ComponentConfiguration;

import java.util.List;

public class ModuleProperties {
    private String moduleName;
    private List<ComponentConfiguration> componentConfigurations;

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public List<ComponentConfiguration> getComponentConfigurations() {
        return componentConfigurations;
    }

    public void setComponentConfigurations(List<ComponentConfiguration> componentConfigurations) {
        this.componentConfigurations = componentConfigurations;
    }
}
