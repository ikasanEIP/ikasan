package org.ikasan.dashboard.component;

import org.ikasan.spec.component.transformation.Converter;
import org.ikasan.spec.component.transformation.TransformationException;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.metadata.ModuleMetaDataProvider;
import org.ikasan.spec.module.ModuleService;
import org.ikasan.spec.module.StartupControl;
import org.ikasan.spec.module.Module;

import java.util.HashMap;
import java.util.Map;

public class ModuleMetadataConverter implements Converter<Module<Flow>, String> {

    private ModuleMetaDataProvider<String> jsonModuleMetaDataProvider;
    private ModuleService moduleService;

    public ModuleMetadataConverter(ModuleMetaDataProvider<String> jsonModuleMetaDataProvider, ModuleService moduleService) {
        this.jsonModuleMetaDataProvider = jsonModuleMetaDataProvider;
        if(this.jsonModuleMetaDataProvider == null) {
            throw new IllegalArgumentException("jsonModuleMetaDataProvider cannot be null!");
        }
        this.moduleService = moduleService;
        if(this.moduleService == null) {
            throw new IllegalArgumentException("moduleService cannot be null!");
        }
    }

    @Override
    public String convert(Module<Flow> module) throws TransformationException {
        Map<String, StartupControl> stringStartupControlMap = new HashMap<>();

        module.getFlows().forEach(flow -> {
            StartupControl startupControl = moduleService.getStartupControl(module.getName(), flow.getName());
            if(startupControl != null) {
                stringStartupControlMap.put(flow.getName(), startupControl);
            }
        });

        return this.jsonModuleMetaDataProvider.describeModule(module, stringStartupControlMap);
    }
}
