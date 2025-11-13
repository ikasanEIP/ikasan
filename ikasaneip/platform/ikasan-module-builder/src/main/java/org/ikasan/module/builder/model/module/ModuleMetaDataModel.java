package org.ikasan.module.builder.model.module;

import org.ikasan.spec.metadata.ImportedResourceMetaData;
import org.ikasan.spec.metadata.ModuleMetaData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
}
