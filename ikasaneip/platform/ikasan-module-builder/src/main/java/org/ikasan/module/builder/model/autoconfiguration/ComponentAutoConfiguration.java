package org.ikasan.module.builder.model.autoconfiguration;

import org.ikasan.module.builder.model.component.Component;
import org.ikasan.module.builder.model.configuration.ComponentConfiguration;

import java.util.List;

public class ComponentAutoConfiguration {
    private String packageName;
    private List<Component> components;
    private List<ComponentConfiguration> componentConfigurations;

    public ComponentAutoConfiguration(String packageName, List<Component> components
        , List<ComponentConfiguration> componentConfigurations) {
        this.packageName = packageName;
        this.components = components;
        this.componentConfigurations = componentConfigurations;
    }

    public String getPackageName() {
        return packageName;
    }

    public List<Component> getComponents() {
        return components;
    }

    public List<ComponentConfiguration> getComponentConfigurations() {
        return componentConfigurations;
    }
}
