package org.ikasan.ootb.data.sharing.module.model;

import org.ikasan.spec.metadata.model.ConfigurationMetaData;
import org.ikasan.spec.metadata.model.ModuleMetaData;
import java.util.ArrayList;
import java.util.List;

/** Module metadata and its referenced configurations, transported together through the queue. */
public class ModuleAndConfigMetaData {
    private List<ModuleMetaData> modules = new ArrayList<>();
    private List<ConfigurationMetaData> configurations = new ArrayList<>();

    public ModuleAndConfigMetaData() { }
    public ModuleAndConfigMetaData(List<ModuleMetaData> modules, List<ConfigurationMetaData> configurations) {
        this.modules = modules;
        this.configurations = configurations;
    }
    public List<ModuleMetaData> getModules() { return modules; }
    public void setModules(List<ModuleMetaData> modules) { this.modules = modules; }
    public List<ConfigurationMetaData> getConfigurations() { return configurations; }
    public void setConfigurations(List<ConfigurationMetaData> configurations) { this.configurations = configurations; }
}
