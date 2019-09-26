package org.ikasan.dashboard.ui.visualisation.dao;

import org.ikasan.spec.metadata.ModuleMetaData;

import java.util.List;

public interface ModuleMetaDataDao
{
    public List<String> getAllModuleName();

    public String getModuleMetaData(String moduleName);

    public List<ModuleMetaData> getAllModule();
}
