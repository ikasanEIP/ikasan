package org.ikasan.dashboard.ui.visualisation.dao;

import org.junit.jupiter.api.Test;

public class ModuleMataDataDaoImplTest
{
    @Test
    public void test()
    {
        ModuleMetaDataDaoImpl dao = new ModuleMetaDataDaoImpl();

        System.out.println(dao.getModuleMetaData("blbgToms-mhiTrade"));
    }
}
