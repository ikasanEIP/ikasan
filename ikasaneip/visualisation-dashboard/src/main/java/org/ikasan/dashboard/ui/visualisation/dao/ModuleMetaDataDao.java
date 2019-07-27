package org.ikasan.dashboard.ui.visualisation.dao;

import java.util.List;

public interface ModuleMetaDataDao
{
    public List<String> getAllModuleName();

    public String getModuleMetaData(String moduleName);
}
